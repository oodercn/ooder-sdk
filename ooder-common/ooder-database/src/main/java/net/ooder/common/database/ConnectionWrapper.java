/**
 * $RCSfile: ConnectionWrapper.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:49 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: ConnectionWrapper.java,v $
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

import java.sql.Connection;
import java.sql.SQLException;

import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.Constants;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description:
 * An implementation of the Connection interface that wraps an underlying
 * Connection object. It releases the connection back to a connection pool
 * when Connection.close() is called. 
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @author wenzhang li
 * @version 1.0
 */
public class ConnectionWrapper extends ConnectionAdapter {

    /**
     * Commons Logging instance.
     */
    protected static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, ConnectionWrapper.class);

    public ConnectionPool pool;
    public boolean checkedout = false;
    public long createTime;
    public long lockTime;
    public long checkinTime;
    public Exception exception;
    public boolean hasLoggedException = false;

    public ConnectionWrapper(Connection connection, ConnectionPool pool) {
        super(connection);

        this.pool = pool;
        createTime = System.currentTimeMillis();
        lockTime = createTime;
        checkinTime = lockTime;
    }

    public void setConnection(Connection connection) {
        super.connection = connection;
    }

    /**
     * Instead of closing the underlying connection, we simply release
     * it back into the pool.
     */
    public void close() throws SQLException {
        synchronized (this) {
            checkedout = false;
          
            checkinTime = System.currentTimeMillis();
           
            if (log.isDebugEnabled()) {
                exception = null;
                hasLoggedException = false;
            }
        }

        pool.freeConnection();

        // Release object references. Any further method calls on the connection will fail.
        // super.connection = null;
    }

    public String toString() {
        if (connection != null) {
            return connection.toString();
        }
        else {
            return "Jive Software Connection Wrapper";
        }
    }

    public synchronized boolean isCheckedOut() {
        return checkedout;
    }
}

