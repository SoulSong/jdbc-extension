package com.shf.jdbc.extension.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shf.jdbc.extension.holder.SomethingHolder;
import com.shf.jdbc.extension.mybatis.entity.User;
import com.shf.jdbc.extension.mybatis.interceptor.LogInnerInterceptor;
import com.shf.jdbc.extension.mybatis.mapper.DatabaseMapper;
import com.shf.jdbc.extension.mybatis.mapper.SqlMapper;
import com.shf.jdbc.extension.mybatis.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/26 14:56
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.shf.jdbc.extension.mybatis",
        "com.shf.jdbc.extension.datasource.bean.enhance"})
@MapperScan("com.shf.jdbc.extension.mybatis.mapper")
@Slf4j
public class MybatisExtensionApplication implements CommandLineRunner {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DatabaseMapper databaseMapper;

    @Autowired
    private SqlMapper sqlMapper;

    public static void main(String[] args) {
        SpringApplication.run(MybatisExtensionApplication.class, args);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        interceptor.addInnerInterceptor(new LogInnerInterceptor());
        return interceptor;
    }

    @Override
    public void run(String... args) throws Exception {
        SomethingHolder.set("var from threadLocal");
        System.out.println("**********************************************************");

        databaseMapper.dropTable("user");
        databaseMapper.createTable("CREATE TABLE user (id INT, name VARCHAR(20))");

        log.info("batch insert {} users.", userMapper.batchInsert(Arrays.asList(User.builder().id(1).name("foo").build(),
                User.builder().id(2).name("bar").build(),
                User.builder().id(3).name("FOO").build(),
                User.builder().id(4).name("BAR").build())));

        log.info("find all {} users.", userMapper.selectList(null).size());
        log.info("find {} users named foo.", userMapper.findByName("foo"));
        log.info("find {} users named foo.", userMapper.findByColumn("name", "foo"));
        log.info("load {} user with id(1).", userMapper.loadUserById(1));
        List<Map<String, Object>> result = sqlMapper.query("SELECT * FROM user");
        result.forEach(value -> {
            try {
                log.info("{}", objectMapper.writeValueAsString(value));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        userMapper.delete(null);
        System.out.println("**********************************************************");
        SomethingHolder.remove();
    }
}
