/**
 * $RCSfile: ConnectionManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:48 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: ConnectionManager.java,v $
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
import net.ooder.common.database.enums.DataBaseType;
import net.ooder.common.database.metadata.ProviderConfig;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.Constants;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: 常用代码打包
 * </p>
 * <p>
 * description: Central manager of database connections. All methods are static so that they can be easily accessed
 * throughout the classes in the database package.
 * <p>
 * <p>
 * This class also provides a set of utility methods that abstract out operations that may not work on all databases
 * such as setting the max number or rows that a query should return.
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 * @see ConnectionProvider
 */
public class ConnectionManager {

    private static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, ConnectionManager.class);

    private InheritableThreadLocal all_conn = new InheritableThreadLocal();
    private String configKey;

    // private String configKey = null;

    private ConnectionProvider connectionProvider;
    private Object providerLock = new Object();

    // True if connection profiling is turned on. Always false by default.
    private boolean profilingEnabled = false;
    // True if connection will be recorded in a InheritableThreadLocal.
    private boolean recordConnectionEnabled = false;

    // True if the database support transactions.
    private boolean transactionsSupported;
    // True if the database requires large text fields to be streamed.
    private boolean streamTextRequired;
    // True if the database supports the Statement.setMaxRows() method.
    private boolean maxRowsSupported;
    // True if the database supports the Statement.setFetchSize() method.
    private boolean fetchSizeSupported;
    // True if the database supports correlated subqueries.
    private boolean subqueriesSupported;
    // True if the database supports scroll-insensitive results.
    private boolean scrollResultsSupported;
    // True if the database supports batch updates.
    private boolean batchUpdatesSupported;


    public ProviderConfig providerConfig;

    protected ConnectionManager(String configKey) {
        this.configKey = configKey;
        this.providerConfig = new ProviderConfig(configKey);
    }

    protected ConnectionManager(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;



    }

    /**
     * Returns a database connection from the currently active connection provider. (auto commit is set to true).
     */
    public Connection getConnection() throws SQLException {
        connectionProvider = this.getConnectionProvider();
        Connection con = connectionProvider.getConnection();
        if (recordConnectionEnabled) {
            recordConn(con);
        }
        if (con == null) {
            log.error("WARNING: ConnectionManager.getConnection() failed to obtain a connection.");
        }

        // See if profiling is enabled. If yes, wrap the connection with a
        // profiled connection.
        if (profilingEnabled) {
            return new ProfiledConnection(con);
        } else {
            return con;
        }
    }

    private void recordConn(Connection conn) {
        List list = (List) all_conn.get();
        if (list == null) {
            list = new ArrayList();
        }
        list.add(conn);
        all_conn.set(list);
    }

    public List getRecordedConnction() {
        return (List) all_conn.get();
    }

    public void clearRecordedConnection() {
        List list = (List) all_conn.get();
        if (list == null) {
            return;
        }
        list.clear();
    }

    /**
     * Returns a Connection from the currently active connection provider that is ready to participate in transactions
     * (auto commit is set to false).
     */
    public Connection getTransactionConnection() throws SQLException {
        Connection con = getConnection();
        if (isTransactionsSupported()) {
            con.setAutoCommit(false);
        }
        return con;
    }

    /**
     * Closes a Connection. However, it first rolls back the transaction or commits it depending on the value of
     * <code>abortTransaction</code>.
     */
    public void closeTransactionConnection(Connection con, boolean abortTransaction) {
        // test to see if the connection passed in is null
        if (con == null) {
            return;
        }

        // Rollback or commit the transaction
        if (isTransactionsSupported()) {
            try {
                if (abortTransaction) {
                    con.rollback();
                } else {
                    con.commit();
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        try {
            // Reset the connection to auto-commit mode.
            if (isTransactionsSupported()) {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            log.error(e);
        }
        try {
            // Close the db connection.
            con.close();
        } catch (Exception e) {
            log.error(e);
        }

    }

    /**
     * Creates a scroll insensitive Statement if the JDBC driver supports it, or a normal Statement otherwise.
     *
     * @param con the database connection.
     * @return a Statement
     * @throws SQLException if an error occurs.
     */
    public Statement createScrollableStatement(Connection con) throws SQLException {
        if (isScrollResultsSupported()) {
            return con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } else {
            return con.createStatement();
        }
    }

    /**
     * Creates a scroll insensitive PreparedStatement if the JDBC driver supports it, or a normal PreparedStatement
     * otherwise.
     *
     * @param con the database connection.
     * @param sql the SQL to create the PreparedStatement with.
     * @return a PreparedStatement
     * @throws SQLException if an error occurs.
     */
    public PreparedStatement createScrollablePreparedStatement(Connection con, String sql) throws SQLException {
        if (isScrollResultsSupported()) {
            return con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } else {
            return con.prepareStatement(sql);
        }
    }

    /**
     * Scrolls forward in a result set to the specified row number. If the JDBC driver supports the feature, the cursor
     * will be moved directly. Otherwise, we scroll through results one by one manually by calling <tt>rs.next()</tt>.
     *
     * @param rs        the ResultSet object to scroll.
     * @param rowNumber the row number to scroll forward to.
     * @throws SQLException if an error occurs.
     */
    public void scrollResultSet(ResultSet rs, int rowNumber) throws SQLException {
        // If the driver supports scrollable result sets, use that feature.
        if (isScrollResultsSupported()) {
            if (rowNumber > 0) {
                rs.setFetchDirection(ResultSet.FETCH_FORWARD);
                rs.absolute(rowNumber);
            }
        }
        // Otherwise, manually scroll to the correct row.
        else {
            for (int i = 0; i < rowNumber; i++) {
                rs.next();
            }
        }
    }

    /**
     * Returns the current connection provider. The only case in which this method should be called is if more
     * information about the current connection provider is needed. Database connections should always be obtained by
     * calling the getConnection method of this class.
     */
    public ConnectionProvider getConnectionProvider() {
        if (connectionProvider == null) {
            synchronized (providerLock) {
                if (connectionProvider == null) {
                    // Attempt to load the connection provider classname as
                    // a common property.
                    ConnectionProvider tmpProvider = null;

                    if (configKey != null) {
                        String className = CommonConfig.getValue(configKey + ".database.connectionProvider.className");
                        if (className != null) {
                            // Attempt to load the class.
                            try {
                                Class conClass = ClassUtility.loadClass(className);
                                Constructor constructor = conClass.getConstructor(new Class[]{ProviderConfig.class});
                                tmpProvider = (ConnectionProvider) constructor.newInstance(providerConfig);
                            } catch (Exception e) {
                                log.error("Warning: failed to create the connection provider specified by connection Provider.className. Using the default pool.", e);
                                tmpProvider = new HikariCPConnectionProvider(providerConfig);
                            }
                        } else {
                            tmpProvider = new HikariCPConnectionProvider(providerConfig);
                        }
                    } else {
                        tmpProvider = new HikariCPConnectionProvider(providerConfig);
                    }
                    setConnectionProvider(tmpProvider);
                }
            }
        }

        return connectionProvider;
    }

    /**
     * Sets the connection provider. The old provider (if it exists) is shut down before the new one is started. A
     * connection provider <b>should not</b> be started before being passed to the connection manager because the
     * manager will call the start() method automatically.
     *
     * @param provider the ConnectionProvider that the manager should obtain connections from.
     */
    public void setConnectionProvider(ConnectionProvider provider) {
        synchronized (providerLock) {
            if (connectionProvider != null) {
                connectionProvider.destroy();
                connectionProvider = null;
            }
            connectionProvider = provider;
            connectionProvider.start();
            // Now, get a connection to determine meta data.
            Connection con = null;
            try {
                con = connectionProvider.getConnection();
                setMetaData(con);
            } catch (Exception e) {
                log.error(e);
            } finally {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
        // Remember what connection provider we want to use for restarts.
        // JiveGlobals.setJiveProperty("connectionProvider.className",
        // provider.getClass().getName());
    }

    /**
     * Retrives a large text column from a result set, automatically performing streaming if the JDBC driver requires
     * it. This is necessary because different JDBC drivers have different capabilities and methods for retrieving large
     * text values.
     *
     * @param rs          the ResultSet to retrieve the text field from.
     * @param columnIndex the column in the ResultSet of the text field.
     * @return the String value of the text field.
     */
    public String getLargeTextField(ResultSet rs, int columnIndex) throws SQLException {
        if (isStreamTextRequired()) {
            Reader bodyReader = null;
            String value = null;
            try {
                bodyReader = rs.getCharacterStream(columnIndex);
                if (bodyReader == null) {
                    return null;
                }
                char[] buf = new char[256];
                int len;
                StringWriter out = new StringWriter(256);
                while ((len = bodyReader.read(buf)) >= 0) {
                    out.write(buf, 0, len);
                }
                value = out.toString();
                out.close();
            } catch (Exception e) {
                log.error(e);
                throw new SQLException("Failed to load text field");
            } finally {
                try {
                    bodyReader.close();
                } catch (Exception e) {
                }
            }
            return value;
        } else {
            return rs.getString(columnIndex);
        }
    }

    /**
     * Sets a large text column in a result set, automatically performing streaming if the JDBC driver requires it. This
     * is necessary because different JDBC drivers have different capabilities and methods for setting large text
     * values.
     *
     * @param pstmt          the PreparedStatement to set the text field in.
     * @param parameterIndex the index corresponding to the text field.
     * @param value          the String to set.
     */
    public void setLargeTextField(PreparedStatement pstmt, int parameterIndex, String value) throws SQLException {
        if (isStreamTextRequired()) {
            Reader bodyReader = null;
            try {
                bodyReader = new StringReader(value);
                pstmt.setCharacterStream(parameterIndex, bodyReader, value.length());
            } catch (Exception e) {
                log.error(e);
                throw new SQLException("Failed to set text field.");
            }
            // Leave bodyReader open so that the db can read from it. It *should*
            // be garbage collected after it's done without needing to call close.
        } else {
            pstmt.setString(parameterIndex, value);
        }
    }

    /**
     * Sets the max number of rows that should be returned from executing a statement. The operation is automatically
     * bypassed if Jive knows that the the JDBC driver or database doesn't support it.
     *
     * @param stmt    the Statement to set the max number of rows for.
     * @param maxRows the max number of rows to return.
     */
    public void setMaxRows(Statement stmt, int maxRows) {
        if (isMaxRowsSupported()) {
            try {
                stmt.setMaxRows(maxRows);
            } catch (Throwable t) {
                // Ignore. Exception may happen if the driver doesn't support
                // this operation and we didn't set meta-data correctly.
                // However, it is a good idea to update the meta-data so that
                // we don't have to incur the cost of catching an exception
                // each time.
                maxRowsSupported = false;
            }
        }
    }

    /**
     * Sets the number of rows that the JDBC driver should buffer at a time. The operation is automatically bypassed if
     * Jive knows that the the JDBC driver or database doesn't support it.
     *
     * @param rs        the ResultSet to set the fetch size for.
     * @param fetchSize the fetchSize.
     */
    public void setFetchSize(ResultSet rs, int fetchSize) {
        if (isFetchSizeSupported()) {
            try {
                rs.setFetchSize(fetchSize);
            } catch (Throwable t) {
                fetchSizeSupported = false;
            }
        }
    }

    /**
     * Uses a connection from the database to set meta data information about what different JDBC drivers and databases
     * support.
     */
    private void setMetaData(Connection con) throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
        // Supports transactions?
        transactionsSupported = metaData.supportsTransactions();
        // Supports subqueries?
        subqueriesSupported = metaData.supportsCorrelatedSubqueries();
        // Supports scroll insensitive result sets?
        scrollResultsSupported = metaData.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
        // Supports batch updates
        batchUpdatesSupported = metaData.supportsBatchUpdates();

        // Set defaults for other meta properties
        streamTextRequired = false;
        maxRowsSupported = true;
        fetchSizeSupported = true;


        DataBaseType dataBaseType = DataBaseType.getDataBaseType(metaData);

        switch (dataBaseType) {
            case db2:
                break;
            case mssql:
                fetchSizeSupported = true;
                maxRowsSupported = false;
                break;
            case interbase:
                fetchSizeSupported = true;
                maxRowsSupported = false;
                break;
            case oracle:
                streamTextRequired = true;
                break;
            case other:
                break;
        }

    }

    /**
     * Returns true if connection profiling is turned on. You can collect profiling statistics by using the static
     * methods of the ProfiledConnection class.
     *
     * @return true if connection profiling is enabled.
     */
    public boolean isProfilingEnabled() {
        return profilingEnabled;
    }

    public boolean isTransactionsSupported() {
        return transactionsSupported;
    }

    public boolean isStreamTextRequired() {
        return streamTextRequired;
    }

    public boolean isMaxRowsSupported() {
        return maxRowsSupported;
    }

    public boolean isFetchSizeSupported() {
        return fetchSizeSupported;
    }

    public boolean isSubqueriesSupported() {
        return subqueriesSupported;
    }

    public boolean isScrollResultsSupported() {
        return scrollResultsSupported;
    }

    public boolean isBatchUpdatesSupported() {
        return batchUpdatesSupported;
    }

    /**
     * Proxy method for ConnectionProvider
     *
     * @return true if the Connection objects are pooled.
     * @see ConnectionProvider
     */
    public boolean isPooled() {
        return connectionProvider.isPooled();
    }

    /**
     * Proxy method for ConnectionProvider
     *
     * @see ConnectionProvider
     */
    public void start() {
        connectionProvider.start();
    }

    /**
     * Proxy method for ConnectionProvider
     *
     * @see ConnectionProvider
     */
    public void restart() {
        connectionProvider.restart();
    }

    /**
     * Proxy method for ConnectionProvider
     *
     * @see ConnectionProvider
     */
    public void destroy() {
        connectionProvider.destroy();
    }

    /**
     * @return Returns the recordConnectionEnabled.
     */
    public boolean isRecordConnectionEnabled() {
        return recordConnectionEnabled;
    }

    /**
     * @param recordConnectionEnabled The recordConnectionEnabled to set.
     */
    public void setRecordConnectionEnabled(boolean recordConnectionEnabled) {
        this.recordConnectionEnabled = recordConnectionEnabled;
    }

    /**
     * Turns connection profiling on or off. You can collect profiling statistics by using the static methods of the
     * ProfiledConnection class.
     *
     * @param enable true to enable profiling; false to disable.
     */
    public void setProfilingEnabled(boolean enable) {
        // If enabling profiling, call the start method on ProfiledConnection
        if (!profilingEnabled && enable) {
            ProfiledConnection.start();
        }
        // Otherwise, if turning off, call stop method.
        else if (profilingEnabled && !enable) {
            ProfiledConnection.stop();
        }
        profilingEnabled = enable;
    }

}

