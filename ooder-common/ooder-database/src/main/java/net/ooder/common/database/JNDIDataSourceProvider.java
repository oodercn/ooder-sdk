/**
 * $RCSfile: JNDIDataSourceProvider.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:49 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: JNDIDataSourceProvider.java,v $
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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description:
 * An implementation of ConnectionProvider that utilizes a JDBC 2.0 DataSource
 * made available via JNDI. This is useful for application servers where a pooled
 * data connection is already provided so Jive can share the pool with the
 * other applications.<p>
 *
 * The JNDI location of the DataSource stored as the Jive property
 * <code>database.JNDIProvider.name</code>. This can be overridden by setting
 * the provider's <code>name</code> property if required.
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @see ConnectionProvider
 * @author wenzhang li
 * @version 1.0
 */
public class JNDIDataSourceProvider implements ConnectionProvider {

    private String dataSourceName;
    private DataSource dataSource;
    private String configKey = null;


    @Override
    public ProviderConfig getProviderConfig() {
        return new ProviderConfig(this.configKey);
    }

    /**
     * Keys of JNDI properties to query PropertyManager for.
     */
    private static final String[] jndiPropertyKeys =
            {
                    Context.APPLET,
                    Context.AUTHORITATIVE,
                    Context.BATCHSIZE,
                    Context.DNS_URL,
                    Context.INITIAL_CONTEXT_FACTORY,
                    Context.LANGUAGE,
                    Context.OBJECT_FACTORIES,
                    Context.PROVIDER_URL,
                    Context.REFERRAL,
                    Context.SECURITY_AUTHENTICATION,
                    Context.SECURITY_CREDENTIALS,
                    Context.SECURITY_PRINCIPAL,
                    Context.SECURITY_PROTOCOL,
                    Context.STATE_FACTORIES,
                    Context.URL_PKG_PREFIXES};

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
        dataSourceName = CommonConfig.getValue(configKey + ".database.JNDIProvider.name");
    }

    public String getName() {
        return "JNDI DataSource Connection Provider";
    }

    public String getDescription() {
        return "Connection Provider for Jive to lookup pooled " + "DataSource from JNDI location. Requires 'name' " + "property with JNDI location. This can be set in " + "the properties file as 'JNDIDataSource.name'";
    }

    public String getAuthor() {
        return "Andy - wuym@spk.com.cn";
    }

    public int getMajorVersion() {
        return 2;
    }

    public int getMinorVersion() {
        return 1;
    }

    public boolean isPooled() {
        return true;
    }

    public void start() {
        if (dataSourceName == null || dataSourceName.equals("")) {
            error("No name specified for DataSource. JNDI lookup will fail", null);
            return;
        }
        try {
            Properties contextProperties = new Properties();
            for (int i = 0; i < jndiPropertyKeys.length; i++) {
                String k = jndiPropertyKeys[i];
                String v = CommonConfig.getValue(configKey + "." + k);
                if (v != null) {
                    contextProperties.setProperty(k, v);
                }
            }
            Context context = null;
            if (contextProperties.size() > 0) {
                context = new InitialContext(contextProperties);
            } else {
                context = new InitialContext();
            }
            dataSource = (DataSource) context.lookup(dataSourceName);

        } catch (Exception e) {
            error("Could not lookup DataSource at '" + dataSourceName + "'", e);
        }
    }

    public void restart() {
        destroy();
        start();
    }

    public void destroy() {
    }

    public Connection getConnection() {
        if (dataSource == null) {
            error("DataSource has not been initialized.", null);
            return null;
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            error("Could not retrieve Connection from DataSource", e);
            return null;
        }
    }

    public String getProperty(String name) {
        if ("name".equals(name)) {
            return dataSourceName;
        } else {
            return null;
        }
    }

    public Iterator propertyNames() {
        List list = new ArrayList();
        list.add("name");
        return Collections.unmodifiableList(list).iterator();
    }

    public String getPropertyDescription(String name) {
        if ("name".equals(name)) {
            return "JNDI name to lookup. eg: java:comp/env/jdbc/MyDataSource";
        } else {
            return null;
        }
    }

    /**
     * Log an error.
     *
     * @param msg Description of error
     * @param e Exception to printStackTrace (may be null)
     */
    private final void error(String msg, Exception e) {
        System.out.println(msg);
        e.printStackTrace();
    }
}

