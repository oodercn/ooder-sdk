/**
 * $RCSfile: DBAgent.java,v $
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.Constants;
import net.ooder.annotation.MethodChinaName;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description: </p>
 * <p>这个类作为进行数据库操作的代理，它能进行的数据库操作有select、update、insert和delete
 * 具体操作步骤如下：<br>
 * 1.实例化一个DBAgent<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;DBAgent dba = new DBAgent();<br>
 * 2.执行sql脚本<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;int status = dba.execute("select * from ...");<br>
 * 其中status返回值为-1表示执行失败，如果为select查询,返回1表示成功.如果为其他类型数据库操作,则返回发生影响的结果数<br></p>
 * 3.得到sql执行结果<br>
 * 如果为查询操作，调用getQueryResult()获得结果：<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;ResultSet rs = dba.getQueryResult();<br>
 * 如果为其他操作，调用getUpdateResult()获得发生效果的记录数<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;int result = dba.getUpdateResult();<br>
 * 4.关闭<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;dba.close();<br>
 * <p>以上所有的数据库操作任何时候都不会抛出异常，因此在调用时不必进行异常捕捉。是否发生错误可以
 * 通过execute()方法的返回值进行判断。</p>
 * <p>如果SQL语句为PrepareStatement类型，则需要通过调用setParamVector()方法初始化输入参数。</p>
 * <p>该类还支持事务处理。在实例化DBAgent时传入是否自动提交参数，true为自动提交，此时不能手工控制事务；为false时需要手工控制
 * 数据库事务，此时可以调用的事务控制方法有rollback(),commit()
 * </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
  * @author wenzhang li
 * @version 2.0
 */

public class DBAgent {

    /**
     * Commons Logging instance.
     */
    private static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, DBAgent.class);

    protected Connection conn = null;
    protected ResultSet query_result = null;
    protected int update_result;
    protected String SQLString;
    protected List params;
    protected int rows = -1;
    protected HashSet psSet;
    protected List batchedSql;
    protected String configKey;

    protected DBAgent() {}

    /**
     * 默认构造函数，启用自动提交
     * 并自动创建一个新的数据库连接Connnection
     */
    public DBAgent(String configKey) throws SQLException, JDSException {
        this(configKey, null, true);
    }

    /**
     * 构造函数，启用自动提交
     * 如果传入的连接为null，则自动创建一个新的数据库连接
     * @param conn - 数据库连接
     */
    public DBAgent(String configKey, Connection conn) throws SQLException, JDSException {
        this(configKey, conn, true);
    }

    /**
     * 构造函数，接收参数为是否进行自动提交
     * 自动创建一个新的数据库连接
     * @param isAutoCommit - 是否使用自动提交
     */
    public DBAgent(String configKey, boolean isAutoCommit) throws SQLException, JDSException {
        this(configKey, null, isAutoCommit);
    }

    /**
     * 构造函数，接收参数为是否进行自动提交
     * 如果传入的连接为null，则自动创建一个新的数据库连接
     * @param conn - 数据库连接
     * @param isAutoCommit - 是否使用自动提交
     */
    public DBAgent(String configKey, Connection conn, boolean isAutoCommit) throws SQLException, JDSException {

            this.configKey = configKey;

            //从PoolManager应用类中获得数据库连接
            if (conn == null) {
                this.conn = ConnectionManagerFactory.getInstance().getConnectionManager(configKey).getConnection();
            } else {
                this.conn = conn;
            }
            if  (this.conn==null){
                log.error("configKey is " +configKey+"  conn is null!");
                throw new JDSException("configKey is " +configKey+"  conn is null!");
            }else {
                this.conn.setAutoCommit(isAutoCommit);
                psSet = new HashSet();
                params = new ArrayList();
            }

    }

    /**
     * 执行sql脚本
     *
     * @return -1表示执行失败<br>
     *         如果为select查询,返回1表示成功.<br>
     *         如果为其他类型数据库操作,则返回发生影响的结果数
     */
    @MethodChinaName("执行SQL语句")
    public int execute(String sqlString) {
        setSQLString(sqlString);
        return execute();
    }

    /**
     * 执行sql脚本
     *
     * @return -1表示执行失败<br>
     *         如果为select查询,返回1表示成功.<br>
     *         如果为其他类型数据库操作,则返回发生影响的结果数
     */
    @MethodChinaName("执行SQL语句")
    public int execute() {
        //判断SQL语句是否为空
        if (getSQLString() == null) {
            log.error("There is not a executable sql.");
            return -1;
        }
        //判断是否成功得到数据库连接
        if (conn == null)
            return -1;

        int status;
        String sqlStr = getSQLString().trim().substring(0, 6).toLowerCase();
        //判断SQL语句类型，类型为select,update,delete和insert中的一种
        if (sqlStr.startsWith("select")) {
            status = query(getSQLString());
        } else if (sqlStr.startsWith("update") || sqlStr.startsWith("delete") || sqlStr.startsWith("insert")) {
            status = update(getSQLString());
        } else {
            log.error("Sql type is not regonized. It should be one of the select,insert,delete or update");
            return -1;
        }
        SQLString = null;
        params = new ArrayList();
        return status;
    }

    private int query(String SQLString) {
        PreparedStatement pst = null;
        List inputParams = getParams();
        try {
            pst = conn.prepareStatement(SQLString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            psSet.add(pst);

            Iterator inputParamsIte = inputParams.iterator();
            int index = 1;
            while (inputParamsIte.hasNext()) {
                pst.setObject(index, inputParamsIte.next());
                index++;
            }
            query_result = pst.executeQuery();
            return 1;
        } catch (SQLException e) {
            log.error("Query execution fail: " + SQLString, e);
            return -1;
        }
    }

    private int update(String SQLString) {
        PreparedStatement pst = null;
        List inputParams = getParams();
        try {
            pst = conn.prepareStatement(SQLString);
            psSet.add(pst);
            Iterator inputParamsIte = inputParams.iterator();
            int index = 1;
            while (inputParamsIte.hasNext()) {
                pst.setObject(index, inputParamsIte.next());
                index++;
            }
            update_result = pst.executeUpdate();
            query_result = null;
            rows = -1;
            return update_result;
        } catch (Exception e) {
            log.error("Update execution fail: " + SQLString, e);
            return -1;
        }
    }

    /**
     * @return 获得准备执行的sql语句
     */
    @MethodChinaName("获取SQL语句")
    public String getSQLString() {
        return SQLString;
    }

    /**
     * @return 封装到Vector中的参数列表。添加参数的顺序需要和sql语句中的'?'出现的顺序一致
     */
    @MethodChinaName("获取参数列表")
    public List getParams() {
        return params;
    }

    /**
     * @param SQLString 设置准备执行的sql语句
     */
    @MethodChinaName("设置SQL语句")
    public void setSQLString(String SQLString) {
        this.SQLString = SQLString;
    }

    /**
     * @param paramVector 封装到Vector中的参数列表。添加参数的顺序需要和sql语句中的'?'出现的顺序一致
     */
    @MethodChinaName("设置参数列表")
    public void setParams(List params) {
        this.params = params;
    }

    /**
     * 获得查询结果集
     * @return 查询结果集
     */
    @MethodChinaName("获取查询结果")
    public ResultSet getQueryResult() {
        return query_result;
    }

    /**
     * 获得更改的记录数
     * @return 更改的记录数
     */
    @MethodChinaName("获取更新结果")
    public int getUpdateResult() {
        return update_result;
    }

    /**
     * 释放数据库连接
     */
    @MethodChinaName("关闭数据库连接")
    public void close() {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit())
                    conn.commit();
                Iterator ite = psSet.iterator();
                while (ite.hasNext()) {
                    ((PreparedStatement)ite.next()).close();
                }
                if (!conn.isClosed())
                    conn.close();
            } catch (Exception e) {}
        }
    }

    /**
     * 得到结果集的记录数
     */
    @MethodChinaName("获取结果集行数")
    public int getRows() {
        if (query_result != null) {
            int i = 0;
            try {
                query_result.last();
                i = query_result.getRow();
                query_result.beforeFirst();
            } catch (SQLException e) {
                log.error("", e);
            }
            rows = i;
            return i;
        } else
            return -1;
    }

    /**
     * 事务提交。只在自动提交设置为false时生效
     */
    @MethodChinaName("提交事务")
    public void commit() {
        try {
            if (!conn.getAutoCommit())
                conn.commit();
        } catch (SQLException e) {
            log.error("", e);
        }
    }

    /**
     * 事务回滚。只在自动提交设置为false时生效
     */
    @MethodChinaName("回滚事务")
    public void rollback() {
        try {
            if (!conn.getAutoCommit())
                conn.rollback();
        } catch (SQLException e) {
            log.error("", e);
        }
    }

    /**
     * 清除Batch
     */
    @MethodChinaName("清除批量操作")
    public void clearBatch() {
        if (batchedSql != null)
            batchedSql.clear();
    }

    /**
     * 添加Batch
     *
     * @param sql 要添加到Batch中的sql
     */
    @MethodChinaName("添加批量SQL语句")
    public void addBatch(String sql) {
        if (batchedSql == null)
            batchedSql = new ArrayList();

        batchedSql.add(sql);
    }

    /**
     * 执行Batch
     *
     * @return -1表示执行失败<br>
     *         如果成功,则返回发生影响的记录数
     */
    @MethodChinaName("执行批量操作")
    public int executeBatch() {
        int result;
        Statement stm = null;
        if (batchedSql == null || batchedSql.size() == 0) {
            log.error("No batched sql found, you should addBatch(String sql) first.");
            result = -1;
        } else {
            try {
                stm = conn.createStatement();

                Iterator batchedSqlIte = batchedSql.iterator();
                while (batchedSqlIte.hasNext()) {
                    stm.addBatch((String)batchedSqlIte.next());
                }
                result = stm.executeBatch().length;
            } catch (SQLException e) {
                log.error("Batch execution fail: " + SQLString, e);
                result = -1;
            }
        }

        if (stm != null) {
            try {
                stm.close();
            } catch (Exception e) {
                log.error("", e);
            }
        }

        return result;
    }
    /**
     * 取得DBBean内的连接，可能是null
     * @return
     */
    @MethodChinaName("获取数据库连接")
    public Connection getConn()
    {
        return conn;
    }


}

