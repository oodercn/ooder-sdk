/**
 * $RCSfile: ConnectionProvider.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:49 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: ConnectionProvider.java,v $
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

import net.ooder.common.database.metadata.ProviderConfig;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description:
 * Abstract class that defines the connection provider framework. Other classes
 * extend this abstract class to make connection to actual data sources.<p>
 *
 * It is expected that each subclass be a JavaBean, so that properties of
 * the connection provider are exposed through bean introspection.
 * <p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @author wenzhang li
 * @version 1.0
 */
public interface ConnectionProvider {

    /**
     * Returns true if this connection provider provides connections out
     * of a connection pool. Implementing and using connection providers that
     * are pooled is strongly recommended, as they greatly increase the speed
     * of Jive.
     *
     * @return true if the Connection objects returned by this provider are
     *      pooled.
     */
    public boolean isPooled();


    public ProviderConfig getProviderConfig();


    /**
     * Returns a database connection. When a Jive component is done with a
     * connection, it will call the close method of that connection. Therefore,
     * connection pools with special release methods are not directly
     * supported by the connection provider infrastructure. Instead, connections
     * from those pools should be wrapped such that calling the close method
     * on the wrapper class will release the connection from the pool.
     *
     * @return a Connection object.
     */
    public Connection getConnection() throws SQLException;

    /**
     * Starts the connection provider. For some connection providers, this
     * will be a no-op. However, connection provider users should always call
     * this method to make sure the connection provider is started.
     */
    public void start();

    /**
     * This method should be called whenever properties have been changed so
     * that the changes will take effect.
     */
    public void restart();

    /**
     * Tells the connection provider to destroy itself. For many connection
     * providers, this will essentially result in a no-op. However,
     * connection provider users should always call this method when changing
     * from one connection provider to another to ensure that there are no
     * dangling database connections.
     */
    public void destroy();
}

