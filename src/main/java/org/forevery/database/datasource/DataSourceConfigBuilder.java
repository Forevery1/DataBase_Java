package org.forevery.database.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.forevery.database.config.ConfigInfo;

import javax.sql.DataSource;

/**
 * DataSourceConfig类的工厂类，用于创建多种类别的数据源.
 * Created by Forevery
 */
public final class DataSourceConfigBuilder {

    /**
     * 得到新的实例.
     *
     * @return DataSourceConfigFactory实例
     */
    public static DataSourceConfigBuilder newInstance() {
        return new DataSourceConfigBuilder();
    }

    public HikariDataSource buildDataSource(HikariConfig config) {
        HikariDataSourceConfig hdsConfig = HikariDataSourceConfig.newInstance();
        return this.saveDataSource(hdsConfig, hdsConfig.buildDataSource(config));
    }

    /**
     * HikariDataSource数据源
     *
     * @param driver   JDBC驱动
     * @param url      url
     * @param user     用户名
     * @param password 密码
     */
    public HikariDataSource buildHikariDataSource(String driver, String url, String user, String password) {
        HikariDataSourceConfig hdsConfig = HikariDataSourceConfig.newInstance();
        return this.saveDataSource(hdsConfig, hdsConfig.buildDataSource(driver, url, user, password));
    }

    /**
     * 将HikariDataSource数据源保存到配置信息中.
     *
     * @param dataSource 数据源
     * @return HikariDataSource实例
     */
    public HikariDataSource buildHikariDataSource(HikariDataSource dataSource) {
        return this.saveDataSource(HikariDataSourceConfig.newInstance(), dataSource);
    }

    /**
     * 将`DataSourceConfig`和`DataSource`数据源保存到配置信息中.
     *
     * @param dsConfig   数据源配置实例
     * @param dataSource 数据源实例
     * @param <C>        数据源配置的泛型C
     * @param <D>        数据源的泛型D
     * @return 数据源
     */
    private <C extends DataSourceConfig<D>, D extends DataSource> D saveDataSource(C dsConfig, D dataSource) {
        dsConfig.setDataSource(dataSource);
        ConfigInfo.getInstance().setDsConfig(dsConfig);
        return dataSource;
    }
}
