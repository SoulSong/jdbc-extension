package com.shf.jdbc.extension.jpa.interceptor;

import com.shf.jdbc.extension.holder.SomethingHolder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.regex.Pattern;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/27 13:27
 */
@Slf4j
public class LogStatementInspector implements StatementInspector {
    private static final Pattern SQL_COMMENT_PATTERN = Pattern
            .compile(
                    "\\/\\*.*?\\*\\/\\s*"
            );


    @Override
    public String inspect(String sql) {
        log.info("Executing SQL query: {}, threadlocal : {}", sql, SomethingHolder.get());

        return SQL_COMMENT_PATTERN
                .matcher(sql)
                .replaceAll("");
    }
}
