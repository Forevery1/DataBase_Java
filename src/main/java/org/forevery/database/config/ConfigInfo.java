package org.forevery.database.config;

import org.forevery.database.datasource.DataSourceConfig;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 配置
 */
public final class ConfigInfo {

    /* ConfigInfo的唯一实例 */
    private static final ConfigInfo configInfo = new ConfigInfo();

    /**
     * Connection 连接对象
     */
    private Connection connection = null;

    /**
     * 数据源配置信息.
     */
    private DataSourceConfig<? extends DataSource> dsConfig;

    /**
     * 得到ConfigInfo的唯一实例.
     *
     * @return ConfigInfo实例
     */
    public static ConfigInfo getInstance() {
        return configInfo;
    }

    /**
     * 关闭数据源，清除数据源配置信息.
     */
    public void clear() {
        if (dsConfig != null) {
            if (dsConfig.getDataSource() != null) {
                dsConfig.close();
            }
            dsConfig = null;
        }
    }

    /**
     * 获取连接对象
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * 获取连接对象
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * 获取数据源.
     *
     * @return 数据源
     */
    public DataSource getDataSource() {
        return dsConfig.getDataSource();
    }

    /**
     * 通过setter方法,设置数据源配置信息实例.
     *
     * @param dsConfig DataSourceConfig多态实例
     */
    public <D extends DataSource> void setDsConfig(DataSourceConfig<D> dsConfig) {
        this.dsConfig = dsConfig;
    }
}
