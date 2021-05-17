package com.shf.jdbc.extension.datasource.bean.enhance;

import com.p6spy.engine.spy.P6DataSource;
import com.shf.jdbc.extension.datasource.interceptor.BaseDataSourceMethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * description :
 * https://p6spy.readthedocs.io/en/latest/index.html
 *
 * @author songhaifeng
 * @date 2021/4/25 23:33
 */
@Component
@Profile("P6Spy")
public class P6DataSourceBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof DataSource && !(bean instanceof P6DataSource)) {
            final ProxyFactory factory = new ProxyFactory(bean);
            factory.setProxyTargetClass(true);
            factory.addAdvice(new P6DataSourceInterceptor((DataSource) bean));
            return factory.getProxy();
        }
        return bean;
    }

    private class P6DataSourceInterceptor extends BaseDataSourceMethodInterceptor {

        public P6DataSourceInterceptor(DataSource dataSource) {
            super(dataSource);
        }

        @Override
        protected DataSource setDataSource(DataSource dataSource) {
            return new P6DataSource(dataSource);
        }
    }
}
