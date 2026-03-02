/**
 * $RCSfile: DBBeanBase.java,v $
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

import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.Constants;
import net.ooder.annotation.MethodChinaName;

/**
 * 数据库访问
 * 通过JNDI从Appliction Server连接池中取得数据
 * Creation date: (2002-6-24 14:15:23)
 * @author: JinDun
 *
 */

/***
 * 不建议再使用
 * @author wenzhang
 *
 */
public class DBBeanBase {
    /**
     * Commons Logging instance.
     */
    protected static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, DBBeanBase.class);


    protected static int nConCount = 0;


    protected static synchronized void changeCount(int nChange) {
        nConCount += nChange;
    }

    protected String dataSourceName;
    protected Connection con = null;
    //private Statement statm;
    public java.sql.Statement statm = null; //当前使用的Statement
    public java.sql.PreparedStatement prestatm = null; //当前正在使用的PreparedStatement
    protected ResultSet resultset = null;
    protected long tm; //操作ID
    protected boolean isTransaction = false;
    protected int resultrows = 0; //查询集合的记录数
    protected int resultcolumns = 0;
    protected DBResult dbResult = null;

    protected String strSql; //正在执行的SQL



    public DBBeanBase(String configKey) {
        this(configKey, false);
    }


    public DBBeanBase(String configKey, boolean value) {
        try {
            con = getConnection(configKey);
            statm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            //设置超时时间
            //statm.setQueryTimeout(Integer.parseInt(ZHCXConfig.getValue(ZHCXConfig.QUERYTIMEOUT_KEY, "60")));
            isTransaction = value;
            if (isTransaction) {
                con.setAutoCommit(false);
            } else {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("SQL error during DBBeanBase construction", e);
        }

    }

    public DBBeanBase(Connection con, boolean value) {
        try {
            this.con = con;
            // con = getConnection(configKey);
            statm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            //设置超时时间
            //statm.setQueryTimeout(Integer.parseInt(ZHCXConfig.getValue(ZHCXConfig.QUERYTIMEOUT_KEY, "60")));
            isTransaction = value;
            if (isTransaction) {
                con.setAutoCommit(false);
            } else {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("SQL error during DBBeanBase construction with Connection", e);
        }

    }

    /**
     * 得到数据库的连接
     */
    private Connection getConnection_Direct() {
        Context context = null;
        Connection conn = null;

        try {
            // create parameter list to access naming system
            Hashtable parms = new Hashtable();
            //初始化JNDI参数环境
//			parms.put(Context.INITIAL_CONTEXT_FACTORY, CNInitialContextFactory.class.getName());
            //初始化数据源的服务器地址
            parms.put(Context.PROVIDER_URL, "iiop:///");

            // access naming system
            context = new InitialContext(parms);

            // get DataSource factory object from naming system
            DataSource dataSource = (DataSource) context.lookup(dataSourceName);
            log.debug("DataSource lookup: " + dataSourceName);
            conn = dataSource.getConnection();
        } catch (Exception t) {
            log.error("Error getting direct connection", t);
        } finally {
            try {
                if (context != null) {
                    context.close();
                }
            } catch (Exception e) {
                log.error("Context close error.", e);
            }
            String strThread = Thread.currentThread().getName();
            changeCount(1);
            tm = System.currentTimeMillis();
            log.debug("Application log(" + new java.sql.Timestamp(tm) + "): Call getConnection() Connection Count: " + nConCount + " in Thread: " + strThread);
        }

        return conn;
    }


    protected Connection getConnection(String configKey) {
        Connection conn = null;

        try {
            conn = ConnectionManagerFactory.getInstance().getConnectionManager(configKey).getConnection();
            //连接成功
            String strThread = Thread.currentThread().getName();
            changeCount(1);
            tm = System.currentTimeMillis();
            log.info("Application log(" + new java.sql.Timestamp(tm) +
                    "): Call getConnection() Connection Count: " + nConCount +
                    " in Thread: " + strThread);

        } catch (Exception t) {
            log.error("Error getting connection", t);
            return null;
        }
        return conn;
    }

    /**
     * Execute query.
     * Creation date: (2001-7-6 16:25:27)
     * @return int
     * 0 - failed
     * 1 - successful
     * 2 - user define
     * 3 - user define
     * ....
     */
    @MethodChinaName("执行查询")
    public int executeQuery(String strSql) {
        int result = 0;
        try {
            if (resultset != null) {
                resultset.close();
                resultset = null;
                dbResult = null;
            }
            log.debug(strSql);
            resultset = statm.executeQuery(strSql);
            createDBResult();
            resultset.close();
            resultset = null;
        } catch (SQLException e) {
//            if(e.getErrorCode() == 1013)//query timeout
//            {
//                throw new QueryTimeoutException("查询超时");
//            }
            result = -1;
            log.error("Catch DataException in executeQuery of " +
                    getClass().getName());
            log.error("SQL:" + strSql);
            log.error("Error in DBBeanBase ", e);
        }
        return result;
    }

    /**
     * Execute update.
     * Creation date: (2001-7-6 16:25:27)
     * @return int
     * -1 - failed
     * >=0 - affected rows
     * ....
     */
    @MethodChinaName("执行更新")
    public int executeUpdate(String strSql) {
        int result = 1;
        try {
            if (resultset != null) {
                resultset.close();
                resultset = null;
                dbResult = null;
            }
            log.debug(strSql);
            result = statm.executeUpdate(strSql);
        } catch (SQLException e) {
            result = -1;
            log.error("Catch DataException in executeUpdate of " +
                    getClass().getName());
            log.error("SQL:" + strSql);
            log.error("Error in DBBeanBase ", e);
        }
        return result;
    }

    protected void createDBResult() {
        try {
            ArrayList v = new ArrayList();
            resultrows = 0;
            resultcolumns = 0;
            ResultSetMetaData metadata = resultset.getMetaData();
            resultcolumns = metadata.getColumnCount();

            while (resultset.next()) {
                for (int i = 1; i <= resultcolumns; i++) {
                    Object o = resultset.getObject(i);
                    v.add(o);
                }
                resultrows++;
            }

            if (resultrows == 0) {
                resultcolumns = 0;
            }
            dbResult = new DBResult(v, resultrows, resultcolumns);
            //设置各列名称
            for (int i = 1; i <= resultcolumns; i++) {
                dbResult.setColumnName(i - 1, metadata.getColumnName(i));
            }
        } catch (Exception e) {
            log.error("Catch SQLException in createDBResult() of " +
                    getClass().getName());
            log.error("SQL:" + strSql);
            log.error("Error in DBBeanBase ", e);
        }
    }


    @MethodChinaName("获取查询结果")
    public DBResult getSelectDBResult() {

        return dbResult;

    }

    @MethodChinaName("设置查询结果")
    public void setSelectDBResult(DBResult rs) {

        this.dbResult = rs;

    }

    /*****************************************************************************
     * Utility method to get the value at a specific row and column index
     *
     * @param column the column containing the desired data
     * @param row the row containing the desired data
     * @return the value of the column at the specified row
     * @exception ArrayIndexOutOfBoundsException thrown when there is no data at the specified row
     */
    protected Object valueAtColumnRow(int column, int row) throws
            ArrayIndexOutOfBoundsException {

        // Handle an empty result set by throwing an exception
        if (resultset == null) {
            throw new ArrayIndexOutOfBoundsException("Result set is empty.");
        }

        try {
            // Handle an out of bounds index by throwing an exception
            if (row > resultrows) {
                throw new ArrayIndexOutOfBoundsException(
                        "Row is out of bounds.");
            }

            // Adjust the current row to the desired row index
            resultset.absolute(row);
        } catch (Exception e) {
            log.error("Error occurred in " + getClass().getName() +
                    " when goto row " + row);
            log.error("Error in DBBeanBase ", e);
        }

        // Return the indexed property element
        try {
            return resultset.getObject(column);
        } catch (Exception e) {
            log.error("Error occurred in " + getClass().getName() +
                    " when get value in (" + row + "," + column + ")");
            log.error("Error in DBBeanBase ", e);
            return null;
        }
    }

    /**
     * Close the result set and release resources
     */
    @MethodChinaName("关闭连接")
    public void close() {

        try {
            // Release the SQL statement resources
            if (resultset != null) {
                resultset.close();
                resultset = null;
            }
        } catch (SQLException e) {
            log.error("Catch SQLException " + getClass().getName() +
                    "when close reslutset");
            log.error("Error in DBBeanBase ", e);
        }

        //
        try {
            if (con != null) {
                String strThread = Thread.currentThread().getName();
                changeCount(-1);
                log.debug("Application log(" + new java.sql.Timestamp(tm) +
                        "): Call close() Connection Count: " + nConCount +
                        " in Thread: " + strThread);

                if (statm != null) {
                    statm.close();
                    statm = null;
                }

                if (prestatm != null) {
                    prestatm.close();
                    prestatm = null;
                }

                // Close the App Server V3 connection
                con.close();
                con = null;
            }
        } catch (SQLException e) {
            log.error("Catch SQLException in java.sql.Connection.close in" +
                    getClass().getName());
            log.error("This error maybe cause connection doesn't close.");
            log.error("Error in DBBeanBase ", e);
        }

        return;
    }

    /**
     * Insert the method's description here.
     * Creation date: (01-9-25 15:13:54)
     * @return boolean
     */
    @MethodChinaName("提交事务")
    public boolean commit() {
        boolean success = true;
        try {
            con.commit();
        } catch (SQLException e) {
            success = false;
            log.error("Catch exception in commit() in " + getClass().getName());
            log.error("Error in DBBeanBase ", e);
        }

        return success;
    }

    /**
     * Insert the method's description here.
     * Creation date: (01-9-25 15:16:23)
     * @return boolean
     */
    @MethodChinaName("回滚事务")
    public boolean rollback() {
        boolean success = true;
        try {
            con.rollback();
            log.debug("Application log(" + new java.sql.Timestamp(tm) +
                    "): Call rollback() in " + getClass().getName());
        } catch (SQLException e) {
            success = false;
            log.error("Catch exception in rollback() in " + getClass().getName());
            log.error("Error in DBBeanBase ", e);
        }

        return success;
    }

    /**
     * 准备SQL
     */
    @MethodChinaName("准备SQL语句")
    public boolean prepareSql(String strPreSql) {
        try {
            if (prestatm != null) {
                prestatm.close();
                prestatm = null;
            }
            prestatm = con.prepareStatement(strPreSql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            strSql = strPreSql;
            log.debug(strSql);
            return true;
        } catch (SQLException e) {
            log.error("Catch SQLException in prepareSql of " +
                    getClass().getName());
            log.error("Sql: " + strSql);
            log.error("Error in DBBeanBase ", e);
            return false;
        }
    }


    /*  	public boolean setClob(Clob c, String clob)
     {
      try
      {
       oracle.sql.CLOB oraC = (oracle.sql.CLOB) c;
       Writer out = oraC.getCharacterOutputStream();
       out.write(clob);
       out.close();
       return true;
      }
      catch(Exception e)
      {
         System.err.println("Catch SQLException in setClob of " + getClass().getName());
                e.printStackTrace();
       return false;
      }
     }
     */

    /**
     * 执行准备好的SQL
     */
    @MethodChinaName("执行预编译查询")
    public int executePreparedQuery() {
        if (prestatm == null) {
            return -1;
        }
        int result = 0;
        try {
            if (resultset != null) {
                resultset.close();
                resultset = null;
                dbResult = null;
            }
            resultset = prestatm.executeQuery();

            createDBResult();
            resultset.close();
            resultset = null;
        } catch (SQLException e) {

            result = -1;
            log.error("Catch SqlException in executePreparedQuery of " +
                    getClass().getName());
            log.error("Sql: " + strSql);
            log.error("Error in DBBeanBase ", e);
        } finally {
        }
        return result;
    }

    /**
     * 执行准备好的SQL
     */
    @MethodChinaName("执行预编译更新")
    public int executePreparedUpdate() {
        if (prestatm == null) {
            return -1;
        }
        int result = 1;
        try {
            if (resultset != null) {
                resultset.close();
                resultset = null;
                dbResult = null;
            }
            result = prestatm.executeUpdate();

        } catch (SQLException e) {
            result = -1;
            log.error("Catch SqlException in executePreparedUpdate of " +
                    getClass().getName());
            log.error("Sql: " + strSql);
            log.error("Error in DBBeanBase ", e);
        } finally {
        }
        return result;
    }

    @MethodChinaName("设置整型参数")
    public void setInt(int index, int value) {
        try {
            prestatm.setInt(index, value);
        } catch (Exception e) {
            log.error("Catch SQLException in setInt of " + getClass().getName());
            log.error("Error in DBBeanBase ", e);
            return;
        }
    }

    @MethodChinaName("设置字符串参数")
    public void setString(int index, String value) {
        try {
            prestatm.setString(index, value);
        } catch (Exception e) {
            log.error("Catch SQLException in setString of " + getClass().getName());
            log.error("Error in DBBeanBase ", e);
            return;
        }
    }

    @MethodChinaName("设置日期参数")
    public void setDate(int index, java.sql.Date value) {
        try {
            prestatm.setDate(index, value);
        } catch (Exception e) {
            log.error("Catch SQLException in setString of " +
                    getClass().getName(), e);
            return;
        }
    }

    @MethodChinaName("设置浮点型参数")
    public void setFloat(int index, float value) {
        try {
            prestatm.setFloat(index, value);
        } catch (Exception e) {
            log.error("Catch SQLException in setString of " +
                    getClass().getName(), e);
            return;
        }
    }

    @MethodChinaName("设置时间戳参数")
    public void setTime(int index, java.sql.Timestamp value) {
        try {
            prestatm.setTimestamp(index, value);
        } catch (Exception e) {
            log.error("Catch SQLException in setString of " +
                    getClass().getName(), e);
            return;
        }
    }

    @MethodChinaName("设置长整型参数")
    public void setLong(int index, long value) {
        try {
            prestatm.setLong(index, value);
        } catch (Exception e) {
            log.error("Catch SQLException in setString of " +
                    getClass().getName(), e);
            return;
        }
    }

    @MethodChinaName("设置空值参数")
    public void setNull(int index, int sqlType) {
        try {
            prestatm.setNull(index, sqlType);
        } catch (Exception e) {
            log.error("Catch SQLException in setString of " +
                    getClass().getName(), e);
            return;
        }
    }

    @MethodChinaName("设置字符流参数")
    public void setCharacterStream(int parameterIndex,
                                   Reader reader,
                                   int length) {
        try {
            prestatm.setCharacterStream(parameterIndex, reader, length);
        } catch (Exception e) {
            log.error("Catch SQLException in setString of " +
                    getClass().getName(), e);
            return;
        }
    }

    @MethodChinaName("检查连接是否关闭")
    public boolean isClosed() {
        try {
            if (con == null) {
                return true;
            } else {
                return con.isClosed()
                        ;
            }
        } catch (Exception e) {
            log.error("Error in isClosed()");
            log.error("Error in DBBeanBase ", e);
            return false;
        }
    }


    /**
     * 取得DBBean内的连接，可能是null
     * @return
     */
    @MethodChinaName("获取数据库连接")
    public Connection getConn() {
        return con;
    }

}


