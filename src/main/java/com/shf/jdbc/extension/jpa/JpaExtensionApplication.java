package com.shf.jdbc.extension.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shf.jdbc.extension.holder.SomethingHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.Map;

/**
 * description :
 * https://www.baeldung.com/jpa-query-parameters
 *
 * @author songhaifeng
 * @date 2021/4/26 14:56
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.shf.jdbc.extension.datasource.bean.enhance"})
@Slf4j
public class JpaExtensionApplication implements CommandLineRunner {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(JpaExtensionApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        SomethingHolder.set("var from threadLocal");
        System.out.println("**********************************************************");
        userService.execute();
        System.out.println("**********************************************************");
        SomethingHolder.remove();
    }

    @Service
    public class UserService {
        @Transactional(rollbackFor = Exception.class)
        public void execute() {
            entityManager.createNativeQuery("DROP TABLE IF EXISTS user").executeUpdate();
            entityManager.createNativeQuery("CREATE TABLE user (id INT, name VARCHAR(20))").executeUpdate();

            entityManager.createNativeQuery("INSERT INTO user (id, name) VALUES (?, ?)")
                    .setParameter(1, 1)
                    .setParameter(2, "foo")
                    .executeUpdate();
            entityManager.persist(new User(2, "bar"));
            entityManager.persist(new User(3, "FOO"));
            entityManager.persist(new User(4, "BAR"));

            List<Map<String, Object>> result = entityManager.createNativeQuery("SELECT id,name FROM user where name=:name")
                    .setParameter("name", "foo")
                    .unwrap(NativeQuery.class)
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                    .getResultList();
            result.forEach(value -> {
                try {
                    log.info("{}", objectMapper.writeValueAsString(value));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "user")
    public class User {
        @Id
        private int id;
        private String name;
    }
}
