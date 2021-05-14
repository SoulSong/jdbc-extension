package com.shf.jdbc.extension.datasource.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/25 23:58
 */
public abstract class BaseDataSourceMethodInterceptor implements MethodInterceptor {
    private final DataSource dataSource;

    public BaseDataSourceMethodInterceptor(DataSource dataSource) {
        this.dataSource = setDataSource(dataSource);
    }

    @Override
    public Object invoke( MethodInvocation methodInvocation) throws Throwable {
        final Method proxyMethod = ReflectionUtils.findMethod(this.dataSource.getClass(),
                methodInvocation.getMethod().getName());
        if (proxyMethod != null) {
            return proxyMethod.invoke(this.dataSource, methodInvocation.getArguments());
        }
        return methodInvocation.proceed();
    }

    protected abstract DataSource setDataSource(DataSource dataSource);
}
