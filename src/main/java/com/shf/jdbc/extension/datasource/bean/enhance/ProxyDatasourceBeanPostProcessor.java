package com.shf.jdbc.extension.datasource.bean.enhance;

import com.shf.jdbc.extension.datasource.interceptor.BaseDataSourceMethodInterceptor;
import com.shf.jdbc.extension.holder.SomethingHolder;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.logging.DefaultJsonQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;


/**
 * description :
 * http://ttddyy.github.io/datasource-proxy/docs/current/user-guide/index.html
 * refer: https://github.com/ttddyy/datasource-proxy-examples/blob/master/springboot-autoconfig-example/src/main/java/net/ttddyy/dsproxy/example/DatasourceProxyBeanPostProcessor.java
 *
 * @author songhaifeng
 * @date 2021/4/25 16:55
 */
@Component
@Slf4j
@Profile("datasource-proxy")
public class ProxyDatasourceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof DataSource && !(bean instanceof ProxyDataSource)) {
            // Instead of directly returning a less specific datasource bean
            // (e.g.: HikariDataSource -> DataSource), return a proxy object.
            // See following links for why:
            //   https://stackoverflow.com/questions/44237787/how-to-use-user-defined-database-proxy-in-datajpatest
            //   https://gitter.im/spring-projects/spring-boot?at=5983602d2723db8d5e70a904
            //   http://blog.arnoldgalovics.com/2017/06/26/configuring-a-datasource-proxy-in-spring-boot/
            final ProxyFactory factory = new ProxyFactory(bean);
            factory.setProxyTargetClass(true);
            factory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean));
            return factory.getProxy();
        }
        return bean;
    }

    /**
     * listener(**): get called before/after query execution, like: execute, executeBatch, executeQuery.
     * 等价于beforeQuery((execInfo, queryInfoList) -> {}) & afterQuery((execInfo, queryInfoList) -> {})
     * methodListener(**)：invoked every interaction with JDBC API internaction.
     * 等价于beforeMethod(callback -> {}) & .afterMethod(executionContext -> {})
     */
    private class ProxyDataSourceInterceptor extends BaseDataSourceMethodInterceptor {

        public ProxyDataSourceInterceptor(final DataSource dataSource) {
            super(dataSource);
        }

        @Override
        protected DataSource setDataSource(DataSource dataSource) {
            CustomJsonQueryLogEntryCreator creator = new CustomJsonQueryLogEntryCreator();
            return ProxyDataSourceBuilder.create(dataSource)
                    .name("datasource-proxy")
//                    .multiline()
//                    .asJson()
//                    .logQueryBySlf4j(SLF4JLogLevel.INFO)
//                    .logSlowQueryBySlf4j(3, TimeUnit.MILLISECONDS)
                    // retrieve by QueryCountHolder.getGrandTotal();
                    .countQuery()
                    // prints out all JDBC API interaction.
                    .traceMethods(log::debug)
                    // By default, datasource-proxy does NOT proxy ResultSet.
                    // However, in some case, you want to return a proxied ResultSet
                    // - for example, apply MethodExecutionListener on ResultSet.
                    .proxyResultSet()
//                    .listener(new CustomQueryLoggingListener())
                    .beforeQuery((execInfo, queryInfoList) -> {
                        // mock rule checker
                        if (queryInfoList.stream().anyMatch(queryInfo -> queryInfo.getQuery().contains("DELETE") && !queryInfo.getQuery().contains("WHERE"))) {
                            throw new UnsupportedOperationException();
                        }
                    })
                    .afterQuery((execInfo, queryInfoList) -> {
                        log.info(creator.getLogEntry(execInfo, queryInfoList, true, true));
                    })
//                    .beforeMethod(callback -> {
//                    })
//                    .afterMethod(executionContext -> {
//                        // print out JDBC API calls to console
//                        Method method = executionContext.getMethod();
//                        Class<?> targetClass = executionContext.getTarget().getClass();
//                        log.info("JDBC: " + targetClass.getSimpleName() + "#" + method.getName());
//                    })

                    .build();
        }
    }

    private class CustomQueryLoggingListener extends SystemOutQueryLoggingListener {
        public CustomQueryLoggingListener() {
            CustomJsonQueryLogEntryCreator creator = new CustomJsonQueryLogEntryCreator();
            setQueryLogEntryCreator(creator);
        }
    }

    private class CustomJsonQueryLogEntryCreator extends DefaultJsonQueryLogEntryCreator {
        @Override
        public String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName, boolean writeConnectionId) {
            StringBuilder sb = new StringBuilder();

            sb.append("{");
            if (writeDataSourceName) {
                writeDataSourceNameEntry(sb, execInfo, queryInfoList);
            }

            if (writeConnectionId) {
                writeConnectionIdEntry(sb, execInfo, queryInfoList);
            }

            // threadLocal
            writeThreadLocal(sb);

            // Time
            writeTimeEntry(sb, execInfo, queryInfoList);

            // Success
            writeResultEntry(sb, execInfo, queryInfoList);

            // Type
            writeTypeEntry(sb, execInfo, queryInfoList);

            // Batch
            writeBatchEntry(sb, execInfo, queryInfoList);

            // QuerySize
            writeQuerySizeEntry(sb, execInfo, queryInfoList);

            // BatchSize
            writeBatchSizeEntry(sb, execInfo, queryInfoList);

            // Queries
            writeQueriesEntry(sb, execInfo, queryInfoList);

            // Params
            writeParamsEntry(sb, execInfo, queryInfoList);

            return sb.toString();
        }

        private void writeThreadLocal(StringBuilder sb) {
            sb.append("\"threadlocal\":\"");
            sb.append(SomethingHolder.get());
            sb.append("\", ");
        }
    }

}