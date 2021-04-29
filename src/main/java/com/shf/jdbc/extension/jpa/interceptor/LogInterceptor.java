package com.shf.jdbc.extension.jpa.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/27 11:29
 */
@Slf4j
public class LogInterceptor extends EmptyInterceptor {

    @Override
    public String onPrepareStatement(String sql) {
        log.info(sql);
        return sql;
    }
}
