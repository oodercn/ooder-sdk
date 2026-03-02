/**
 * HikariCP连接池提供者
 * <p>
 * Title: ooder系统管理系统
 * </p>
 * <p>
 * Description: HikariCP连接池实现，提供数据库连接池管理功能
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 *
 * @author ooder team
 * @version 1.0
 * @since 2025-08-25
 */
package net.ooder.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ooder.common.database.metadata.ProviderConfig;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.Constants;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * HikariCP连接池提供者，实现ConnectionProvider接口
 */
public class HikariCPConnectionProvider implements ConnectionProvider {

    /**
     * Commons Logging instance.
     */
    protected static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, HikariCPConnectionProvider.class);

    private ProviderConfig providerConfig;
    private HikariDataSource dataSource;
    private final Object initLock = new Object();

    /**
     * 构造函数ConfigReader
     * @param providerConfig 连接提供者配置
     */
    public HikariCPConnectionProvider(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
    }

    @Override
    public boolean isPooled() {
        return true;
    }

    @Override
    public ProviderConfig getProviderConfig() {
        return providerConfig;
    }

    /**
     * 设置连接提供者配置
     * @param providerConfig 连接提供者配置
     */
    public void setProviderConfig(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
    }

    /**
     * 创建HikariCP数据源
     * @return HikariDataSource
     */
    private HikariDataSource createDataSource() {
        if (providerConfig.getServerURL() == null || providerConfig.getServerURL().length() == 0) {
            log.error("jdbcUrl is null");
            return null;
        }

        if (providerConfig.getDriver() == null || providerConfig.getDriver().length() == 0) {
            log.error("Driver is null");
            return null;
        }

        try {
            ClassUtility.loadClass(providerConfig.getDriver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        HikariConfig config = new HikariConfig();
        
        // 基本配置
        config.setJdbcUrl(providerConfig.getServerURL());
        log.info("jdbcUrl is :" + providerConfig.getServerURL());
        config.setUsername(providerConfig.getUsername());
        config.setPassword(providerConfig.getPassword());
        
        // 连接池配置
        config.setMaximumPoolSize(providerConfig.getMaxConnections());
        log.info("maxConnections is :" + providerConfig.getMaxConnections());
        config.setMinimumIdle(providerConfig.getMinConnections());
        log.info("minConnections is :" + providerConfig.getMinConnections());
        config.setInitializationFailTimeout(10000); // 初始化失败超时时间
        
        // 连接超时配置
        config.setConnectionTimeout(providerConfig.getConnectionTimeout());
        log.info("connectionTimeout is :" + providerConfig.getConnectionTimeout());
        
        // 连接空闲时间配置
        config.setIdleTimeout(providerConfig.getMaxIdleTime() * 1000L); // HikariCP使用毫秒
        log.info("maxIdleTime is :" + providerConfig.getMaxIdleTime());
        
        // 连接最大生命周期
        config.setMaxLifetime((providerConfig.getMaxIdleTime() + 300) * 1000L);
        
        // 连接测试配置
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        
        // 其他配置
        config.setPoolName("ooder-hikari-pool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        
        return new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            synchronized (initLock) {
                if (dataSource == null) {
                    log.error("Warning: HikariCPConnectionProvider.getConnection() was " + 
                            "called before the internal pool has been initialized.");
                    return null;
                }
            }
        }
        return dataSource.getConnection();
    }

    @Override
    public void start() {
        synchronized (initLock) {
            dataSource = createDataSource();
        }
    }

    @Override
    public void restart() {
        destroy();
        start();
    }

    @Override
    public void destroy() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (Exception e) {
                log.error("Error closing HikariDataSource", e);
            }
        }
        dataSource = null;
    }

    @Override
    protected void finalize() {
        destroy();
    }
}