# DataBase_Java
Java数据库操作工具
```
Forevery:
  database:
    driver: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql:///os?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false
    username: root
    password: root
    charset: utf-8
    prefix: sys_
    maxPoolSize: 15
    timeStamp:
      autoTimeStamp: true
      type: String
      createTimeColumn: create_time
      updateTimeColumn: update_time
  debug: true
  showSql: true
  dateFormat: yyyy-MM-dd HH:mm:ss
  scanPackage: org.forevery.database
```
