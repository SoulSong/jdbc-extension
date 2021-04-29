package com.shf.jdbc.extension.mybatis.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.shf.jdbc.extension.holder.SomethingHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/27 1:45
 */
@Slf4j
public class LogInnerInterceptor implements InnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        log.info("sql : {} ; threadlocal : {}", boundSql.getSql().replaceAll("\n", ""), SomethingHolder.get());
    }
}
