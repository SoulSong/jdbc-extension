# Mybatis
- 默认的日志打印无法将Preparing与Parameters整合在单行，对日志解析不是很友好。并且无法扩展增强自定义日志项：
```text
<==    Updates: 0
==>  Preparing: insert into user (id,name) values (?, ?) , (?, ?) , (?, ?) , (?, ?)
==> Parameters: 1(Integer), foo(String), 2(Integer), bar(String), 3(Integer), FOO(String), 4(Integer), BAR(String)
```
- 提供了自定义拦截能力，通过实现InnerInterceptor接口即可自定义扩展。如自定义SQL打印，添加自定义日志项即非常方便。
- 提供了DELETE ALL暴力操作的拦截能力，直接装配BlockAttackInnerInterceptor至Plugin即可。

# Jpa
- 通过修改Hibernate相关类的日志级别可打印执行SQL，但仍然无法解决多行打印造成的日志解析不友好，以及扩展自定义日志项等难题。
- 通过实现EmptyInterceptor即可扩展自定义sql打印，目前其已经在5.0废弃，并建议使用StatementInspector代替。
- 通过实现StatementInspector接口扩展实现SQL的打印、重写等能力，但其无法获取Parameters信息，非常不友好。

# JdbcTemplate
- 通过修改日志等级，即可打印默认的sql语句，但无展示并不友好，不利于日志采集，且扩展自定义日志项较麻烦。

# P6Spy
其核心能力即为打印log，提供了自定义日志格式扩展能力，实现其MessageFormattingStrategy接口即可。但无法实现查询拦截等能力。


# ProxyDatasource
提供了丰富的扩展能力，不仅能够自定义扩展日志格式、内容，还提供了query、JDBC API method级别调用的拦截扩展。
其在sql日志打印层面已经非常优秀，通过继承其实现，可以继续增加自定义的日志项以满足业务需求。
```text
{"name":"datasource-proxy", "connection":7, "threadlocal":"var from threadLocal", "time":14, "success":true, "type":"Prepared", "batch":true, "querySize":1, "batchSize":2, "query":["INSERT INTO user (id, name) VALUES (?, ?)"], "params":[["1","foo"],["2","bar"]]}
{"name":"datasource-proxy", "connection":8, "threadlocal":"var from threadLocal", "time":15, "success":true, "type":"Prepared", "batch":true, "querySize":1, "batchSize":2, "query":["INSERT INTO user (id, name) VALUES (?, ?)"], "params":[["3","FOO"],["4","BAR"]]}
```