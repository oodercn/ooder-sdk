/**
 * $RCSfile: ConnectionManagerFactory.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:49 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: ConnectionManagerFactory.java,v $
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
import net.ooder.common.database.metadata.MetadataFactory;
import net.ooder.common.database.metadata.ProviderConfig;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.Constants;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description:
 * Central manager of database connections. All methods are static so that they
 * can be easily accessed throughout the classes in the database package.<p>
 * <p>
 * This class also provides a set of utility methods that abstract out
 * operations that may not work on all databases such as setting the max number
 * or rows that a query should return.
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 *
 * @author wenzhang li
 * @version 1.0
 * @see ConnectionProvider
 */
public class ConnectionManagerFactory {

    private static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, ConnectionManagerFactory.class);

    private static ConnectionManagerFactory instance = null;
    private static Map<String, ConnectionManager> connectionManagers;
    private static Map<String, ProviderConfig> providerConfigMap;
    public static final String THREAD_LOCK = "Thread Lock";

    private ConnectionManagerFactory() {

        connectionManagers = new HashMap();
        providerConfigMap = new HashMap<>();
    }

    public Map getConnectionManagers() {
        return connectionManagers;
    }

    public static ConnectionManagerFactory getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                instance = new ConnectionManagerFactory();
            }
        }
        return instance;
    }

    public void updateProviderConfig(ProviderConfig config) {

        providerConfigMap.put(config.getConfigKey().trim(), config);
        providerConfigMap.put(config.getServerURL().trim(), config);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionManager con = getConnectionManager(config);
                MetadataFactory metadataFactory = MetadataFactory.getInstance(config.getConfigKey());
                metadataFactory.clearAll();
                if (con != null) {
                    con.clearRecordedConnection();
                }
            }
        }).start();

    }


    public ProviderConfig getProviderConfig(String configKey) {
        return providerConfigMap.get(configKey);
    }



    public ConnectionManager getConnectionManager(ProviderConfig schema) {
        ConnectionManager manager = connectionManagers.get(schema.getConfigKey());
        if (manager == null) {
            manager = connectionManagers.get(schema.getServerURL());
        }
        if (manager == null) {
            manager = new ConnectionManager(schema);
            connectionManagers.put(schema.getServerURL(), manager);
            connectionManagers.put(schema.getConfigKey(), manager);
        }
        return manager;
    }

    ;

    public ConnectionManager getConnectionManager(String configKey) {
        if (configKey == null) {
            throw new IllegalArgumentException("Parameters 'configKey' can't be null.");
        }
        ProviderConfig config = providerConfigMap.get(configKey);
        ConnectionManager manager = null;
        if (config == null) {
            synchronized (configKey.intern()) {
                manager = connectionManagers.get(configKey);
                if (manager == null) {
                    String ref = CommonConfig.getValue(configKey + ".database.ref");
                    if (ref != null) {
                        manager = connectionManagers.get(ref);
                        if (manager == null) {
                            manager = getConnectionManager(ref);
                        }
                    }
                    if (manager == null) {
                        manager = new ConnectionManager(configKey);
                    }

                    if (manager != null) {
                        connectionManagers.put(configKey, manager);
                        connectionManagers.put(manager.getConnectionProvider().getProviderConfig().getServerURL(), manager);
                    }
                }
            }
        } else {
            manager = this.getConnectionManager(config);
        }

        return manager;

    }

    public static Connection getConnection(String configKey) {
        try {
            return getInstance().getConnectionManager(configKey).getConnection();
        } catch (SQLException e) {
            log.error("can't get the connection with " + configKey, e);
            return null;
        }
    }

}

