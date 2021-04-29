package com.shf.jdbc.extension.datasource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.shf.jdbc.extension.holder.SomethingHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author songhaifeng
 */
@SpringBootApplication
@Slf4j
public class DataSourceExtensionApplication {

    @Autowired
    private ObjectMapper objectMapper;

    public static void main(String[] args) {
        SpringApplication.run(DataSourceExtensionApplication.class, args);
    }

    @Bean
    CommandLineRunner init(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return args -> {
            SomethingHolder.set("var from threadLocal");

            System.out.println("**********************************************************");

            jdbcTemplate.execute("DROP TABLE IF EXISTS user");
            jdbcTemplate.execute("CREATE TABLE user (id INT, name VARCHAR(20))");

            jdbcTemplate.batchUpdate("INSERT INTO user (id, name) VALUES (?, ?)",
                    Arrays.asList(new Object[][]{{1, "foo"}, {2, "bar"}}));

            PreparedStatement preparedStatement = jdbcTemplate.getDataSource().getConnection()
                    .prepareStatement("INSERT INTO user (id, name) VALUES (?, ?)");
            preparedStatement.setString(2, "FOO");
            preparedStatement.setInt(1, 3);
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 4);
            preparedStatement.setString(2, "BAR");
            preparedStatement.addBatch();
            preparedStatement.executeBatch();

            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Integer.class);
            List<Map<String, Object>> result = namedParameterJdbcTemplate.queryForList("SELECT id,name FROM user where id=:id", ImmutableMap.of("id", 1));
            result.forEach(value -> {
                try {
                    log.info("{}", objectMapper.writeValueAsString(value));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
            jdbcTemplate.update("DELETE FROM user");
            System.out.println("**********************************************************");
            SomethingHolder.remove();
        };
    }
}
