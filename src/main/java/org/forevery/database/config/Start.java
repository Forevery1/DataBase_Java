package org.forevery.database.config;

import com.zaxxer.hikari.HikariConfig;
import org.forevery.database.bean.Config;
import org.forevery.database.datasource.DataSourceConfigBuilder;
import org.forevery.database.utils.Log;

/**
 * 入口初始化配置
 */
public class Start {

    /**
     * 初始化配置
     */
    public synchronized static void Init() {
        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName(Config.DRIVER);
            hikariConfig.setJdbcUrl(Config.URL);
            hikariConfig.setUsername(Config.USERNAME);
            hikariConfig.setPassword(Config.PASSWORD);
            hikariConfig.setMaximumPoolSize(Config.maxPoolSize);
            DataSourceConfigBuilder.newInstance().buildDataSource(hikariConfig);
            ConfigInfo.getInstance().setConnection(ConfigInfo.getInstance().getDataSource().getConnection());
            if (Config.DEBUG) Log.i("数据库连接成功!");
        } catch (Exception e) {
            if (Config.DEBUG) Log.e("数据库连接失败!" + "[" + e.getMessage() + "]");
        }
    }
}
