/**
 * $RCSfile: MiniConnectionProvider.java,v $
 * $Revision: 1.2 $
 * $Date: 2016/10/22 14:54:54 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: MiniConnectionProvider.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.common.database;

import net.ooder.common.CommonConfig;
import net.ooder.common.database.metadata.ProviderConfig;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.Constants;

import javax.sql.ConnectionPoolDataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description:
 * Default connection provider, which uses an internal connection pool.
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 *
 * @author wenzhang li
 * @version 2.0
 */
public class MiniConnectionProvider implements ConnectionProvider {

    /**
     * Commons Logging instance.
     */
    protected static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, MiniConnectionProvider.class);

    private String configKey = null;

    private String driver;
    private String serverURL;
    private String username;
    private String password;
    private int minConnections = 3;
    private int maxConnections = 10;

    /**
     * Maximum time a connection can be open before it's reopened (in days)
     */
    private int connectionTimeout = 30 * 1000;

    /**
     * MySQL doesn't currently support Unicode. However, a workaround is
     * implemented in the mm.mysql JDBC driver. Setting the Jive property
     * database.mysql.useUnicode to true will turn this feature on.
     */
    private boolean mysqlUseUnicode;

    private String encoding;

    ProviderConfig providerConfig;

    MiniConnectionPoolManager miniConnectionPoolManager;

    @Override
    public ProviderConfig getProviderConfig() {
        return providerConfig;
    }

    private Object initLock = new Object();

    private String datasource;

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }


    public MiniConnectionProvider(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
        configKey = providerConfig.getConfigKey();
        this.loadProperties();
    }

    public boolean isPooled() {
        return true;
    }

    public ConnectionPoolDataSource createDataSource() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        ConnectionPoolDataSource dataSource = null;
        if (datasource != null && !datasource.equals("")) {
            Class driverClass = ClassUtility.loadClass(datasource);
            dataSource = (ConnectionPoolDataSource) driverClass.newInstance();
            Method setUrlMethod = driverClass.getMethod("setURL", new Class[]{String.class});
            setUrlMethod.invoke(dataSource, serverURL);
            Method setUserMethod = driverClass.getMethod("setUser", new Class[]{String.class});
            setUserMethod.invoke(dataSource, username);
            Method setPassword = driverClass.getMethod("setPassword", new Class[]{String.class});
            setPassword.invoke(dataSource, password);
        } else {
            log.error("Could not load JDBC driver class: " + driver);
        }


        return dataSource;
    }


    public Connection getConnection() throws SQLException {

        if (miniConnectionPoolManager == null) {
            // block until the init has been done
            synchronized (initLock) {
                // if still null, something has gone wrong
                if (miniConnectionPoolManager == null) {
                    log.error("Warning: DbConnectionDefaultPool.getConnection() was " + "called before the internal pool has been initialized.");

                    return null;
                }
            }
        }

        return miniConnectionPoolManager.getConnection();
    }

    public void start() {
        // load properties
        loadProperties();

        // acquire lock so that no connections can be returned.
        synchronized (initLock) {

            ConnectionPoolDataSource source = null;
            try {
                source = createDataSource();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            miniConnectionPoolManager = new MiniConnectionPoolManager(source, maxConnections, connectionTimeout / 60000);

        }
    }

    public void restart() {
        // Kill off pool.
        destroy();
        // Reload properties.
        loadProperties();
        // Start a new pool.
        start();
    }

    public void destroy() {
        if (miniConnectionPoolManager != null) {
            try {
                miniConnectionPoolManager.dispose();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        // Release reference to connectionPool
        miniConnectionPoolManager = null;
    }

    public void finalize() {
        destroy();
    }

    /**
     * Returns the JDBC driver classname used to make database connections.
     * For example: com.mysql.jdbc.Driver
     *
     * @return the JDBC driver classname.
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Returns the JDBC connection URL used to make database connections.
     *
     * @return the JDBC connection URL.
     */
    public String getServerURL() {
        return serverURL;
    }

    /**
     * Returns the username used to connect to the database. In some cases,
     * a username is not needed so this method will return null.
     *
     * @return the username used to connect to the datbase.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password used to connect to the database. In some cases,
     * a password is not needed so this method will return null.
     *
     * @return the password used to connect to the database.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the minimum number of connections that the pool will use. This
     * should probably be at least three.
     *
     * @return the minimum number of connections in the pool.
     */
    public int getMinConnections() {
        return minConnections;
    }

    /**
     * Returns the maximum number of connections that the pool will use. The
     * actual number of connections in the pool will vary between this value
     * and the minimum based on the current load.
     *
     * @return the max possible number of connections in the pool.
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * Returns the amount of time between connection recycles in days. For
     * example, a value of .5 would correspond to recycling the connections
     * in the pool once every half day.
     *
     * @return the amount of time in days between connection recycles.
     */
    public double getConnectionTimeout() {
        return connectionTimeout;
    }

    public boolean isMysqlUseUnicode() {
        return mysqlUseUnicode;
    }

    /**
     * Load properties that already exist from Jive properties.
     */
    private void loadProperties() {
        driver = CommonConfig.getValue(configKey + ".database.driver");
        serverURL = CommonConfig.getValue(configKey + ".database.serverURL");
        username = CommonConfig.getValue(configKey + ".database.username");
        password = CommonConfig.getValue(configKey + ".database.password");
        String minCons = CommonConfig.getValue(configKey + ".database.minConnections");
        String maxCons = CommonConfig.getValue(configKey + ".database.maxConnections");
        String conTimeout = CommonConfig.getValue(configKey + ".database.connectionTimeout");
        mysqlUseUnicode = Boolean.valueOf(CommonConfig.getValue(configKey + ".database.mysql.useUnicode")).booleanValue();
        encoding = CommonConfig.getValue(configKey + ".database.mysql.characterEncoding");
        try {
            if (minCons != null) {
                minConnections = Integer.parseInt(minCons);
            }
            if (maxCons != null) {
                maxConnections = Integer.parseInt(maxCons);
            }
            if (conTimeout != null) {
                connectionTimeout = Integer.parseInt(conTimeout);
            }
        } catch (Exception e) {
            log.error("Error: could not parse default pool properties. " + "Make sure the values exist and are correct.", e);
            e.printStackTrace();
        }

    }
}

