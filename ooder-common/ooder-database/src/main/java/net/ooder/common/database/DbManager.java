/**
 * $RCSfile: DbManager.java,v $
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

import net.ooder.common.database.metadata.MetadataFactory;
import net.ooder.common.database.metadata.ProviderConfig;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.org.conf.OrgConstants;
import net.ooder.annotation.MethodChinaName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * The Manager provides connections and manage transaction transparently. The Manager is a singleton, you get its
 * instance with the getInstance() method. It is used by all the XxxxManager to get database connection. Before doing
 * any operation, you must pass to the manager either a datasource or a jdbc driver/url/username/password You may extend
 * it and use setInstance() method to make sure your implementation is used as a singleton.
 */
public class DbManager {
    private static Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), DbManager.class);

    private static Map<String, DbManager> managerMap = new HashMap<String, DbManager>();

    private static InheritableThreadLocal trans_conn = new InheritableThreadLocal();

    public static final String THREAD_LOCK = "Thread Lock";
    private final String configKey;

    @MethodChinaName("获取数据库管理器实例")
    public static DbManager getInstance(String configKey) {
        DbManager manager = managerMap.get(configKey);
        if (manager == null) {
            synchronized (THREAD_LOCK) {
                if (manager == null) {
                    manager = new DbManager(configKey);
                    managerMap.put(configKey, manager);
                }
            }
        }
        return manager;
    }

    DbManager(String configKey) {
        this.configKey = configKey;
    }


    @MethodChinaName("清空连接")
    public DbManager clear() {
        trans_conn.remove();

        DbManager manager = new DbManager(configKey);
        managerMap.put(configKey, manager);
        return manager;

    }

    /**
     * Get an auto commit connection. Normally you you not need this method that much ;-)
     *
     * @return an auto commit connection.
     */
    @MethodChinaName("获取数据库连接")
    public Connection getConnection() throws SQLException {
        Connection tc = (Connection) trans_conn.get();
        if (tc != null) {
            return tc;
        }
        return ConnectionManagerFactory.getConnection(configKey);
        /*
         * if (ds!=null) { return ds.getConnection(); } else if (jdbc_driver != null && jdbc_url != null &&
         * jdbc_username != null && jdbc_password != null) { return DriverManager.getConnection(jdbc_url, jdbc_username,
         * jdbc_password); } else { throw new IllegalStateException("Please set a datasource or a jdbc
         * driver/url/username/password"); }
         */
    }

    /**
     * Release the connection. Normally you should not need this method ;-)
     */
    @MethodChinaName("释放数据库连接")
    public synchronized void releaseConnection(Connection c) {
        Connection tc = (Connection) trans_conn.get();
        if (tc != null) {
            return;
        }

        try {
            if (c != null && !c.isClosed()) {
                c.setAutoCommit(true);
                c.close();
            }
        } catch (SQLException x) {
            log.error("Could not release the connection: ", x);
        }
    }

    /**
     * When working within a transaction, you should invoke this method first. The connection is returned just in case
     * you need to set the isolation level or else.
     *
     * @return a non-auto commit connection with the default transaction isolation level.
     */
    @MethodChinaName("开始事务")
    public Connection beginTransaction() throws SQLException {
        Connection c = getConnection();
        c.setAutoCommit(false);
        trans_conn.set(c);
        return c;
    }

    /**
     * Release connection used for the transaction and perform a commit or rollback.
     *
     * @param commit tells whether this connection should be committed: true for commit(), false for rollback()
     */
    @MethodChinaName("结束事务")
    public void endTransaction(boolean commit) throws SQLException {
        Connection c = (Connection) trans_conn.get();
        if (c == null) {
            return;
        }

        try {
            if (commit) {
                c.commit();
            } else {
                c.rollback();
            }
        } finally {
            trans_conn.set(null);
            releaseConnection(c);
        }
    }

    // //////////////////////////////////////////////////
    // Utils method
    // //////////////////////////////////////////////////

    /**
     * Log a message using the underlying logwriter, if not null.
     */
    @MethodChinaName("记录日志")
    public void log(String message) {
        log.trace(message);
    }

    /**
     * Close the passed Statement.
     */
    @MethodChinaName("关闭语句")
    public void close(Statement s) {
        try {
            if (s != null)
                s.close();
        } catch (SQLException x) {
            log.error("Could not close statement!: ", x);
        }
        ;
    }

    /**
     * Close the passed ResultSet.
     */
    @MethodChinaName("关闭结果集")
    public void close(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException x) {
            log.error("Could not close result set!: ", x);
        }
        ;
    }

    /**
     * Close the passed Statement and ResultSet.
     */
    @MethodChinaName("关闭语句和结果集")
    public void close(Statement s, ResultSet rs) {
        close(rs);
        close(s);
    }

}


