/**
 * $RCSfile: DBDAOUtil.java,v $
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
package net.ooder.common.database.dao;

import net.ooder.common.database.DbManager;
import net.ooder.common.database.metadata.TableInfo;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.CaselessStringKeyHashMap;
import net.sf.cglib.beans.BeanMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DBDAOUtil<T> {

    protected transient static final Log logger = LogFactory.getLog(
            "DAO", DBDAOUtil.class);


    private TableInfo tableInfo;
    private Connection connection;
    private Class<T> tClass;


    DBDAOUtil(TableInfo tableInfo, Class<T> tClass) throws SQLException {

        this.tableInfo = tableInfo;
        this.connection = DbManager.getInstance(tableInfo.getUrl()).getConnection();
        this.tClass = tClass;

    }


    public void close() {
        DbManager.getInstance(tableInfo.getUrl()).releaseConnection(this.connection);
    }

    public void update(Map<String, Object> valueMap) throws DAOException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            String pkName = this.tableInfo.getPkName();

            Object pkVlaue = valueMap.get(tableInfo.getCoInfoByName(pkName).getFieldname());
            if (pkVlaue == null || pkVlaue.equals("")) {
                pkVlaue = UUID.randomUUID().toString();
                this.create(pkVlaue.toString());
                valueMap.put(tableInfo.getCoInfoByName(pkName).getFieldname(), pkVlaue);
            } else {
                if (!isInDB(pkVlaue.toString())) {
                    this.create(pkVlaue.toString());
                }
            }

            sql = SqlUtil.getUpdateSQLNoParas(this.tableInfo, valueMap, null);
            if (sql == null || sql.length() == 0) {
                if (logger.isWarnEnabled()) logger.warn("WARNING: Nothing to be updated!");
                return;
            }
            ps = connection.prepareStatement(sql);

            Integer k = SqlUtil.prepareFields(tableInfo, ps, valueMap);
            SqlUtil.preparePrimaryKeys(tableInfo, ps, k, pkVlaue);
            ps.executeUpdate();
        } catch (Throwable e) {

            e.printStackTrace();

            throw new DAOException("Update failed!msg: " + e.getMessage(), e);
        } finally {

            try {
                ps.close();
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    public void update(Map<String, Object> valueMap, String where) throws DAOException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            String pkName = this.tableInfo.getPkName();
            Object pkVlaue = valueMap.get(pkName);
            if (pkVlaue == null) {
                pkVlaue = UUID.randomUUID().toString();
            }
            sql = SqlUtil.getUpdateSQLNoParas(this.tableInfo, valueMap, where);
            if (sql == null || sql.length() == 0) {
                if (logger.isWarnEnabled()) logger.warn("WARNING: Nothing to be updated!");
                return;
            }
            ps = connection.prepareStatement(sql);


            SqlUtil.prepareFields(tableInfo, ps, valueMap);

            ps.executeUpdate();

        } catch (Throwable e) {

            e.printStackTrace();

            throw new DAOException("Update failed!msg: " + e.getMessage(), e);
        } finally {

            try {
                ps.close();
                connection.close();
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    public boolean isInDB(String uuid) throws DAOException {
        PreparedStatement st = null;
        ResultSet result = null;
        String sqlString = null;
        try {
            StringBuffer sql = new StringBuffer("select count(*) AS DAO_ROWCOUNT ");
            sql.append(" FROM ");
            sql.append(tableInfo.getName());
            sql.append(" where (");

            Set<String> _allKeys = tableInfo.getPkNames();

            if (_allKeys.size() == 0) {
                sql.append(tableInfo.getPkName()).append("=?,");
            } else {
                for(String key:_allKeys){
                    sql.append(key).append("=?,");
                }

            }


            sqlString = sql.toString();

            if (sqlString.endsWith(",")) {
                sqlString = sqlString.substring(0, sqlString.length() - 1);
            }

            sqlString = sqlString + ")";
            st = connection.prepareStatement(sqlString);

            SqlUtil.preparePrimaryKeys(tableInfo, st, 1, uuid);

            result = st.executeQuery();

            int rowCount = 0;
            // while (result.next() && (curRow <= endRow))
            if (result.next()) {
                rowCount = result.getInt("DAO_ROWCOUNT");
            }

            if (rowCount > 0) {

                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {

            e.fillInStackTrace();

            throw new DAOException("isInDB failed! msg:" + e.getMessage(), e);
        } finally {
            try {
                result.close();
                st.close();
            } catch (Exception e) {
                // do nothing
            }
        }


    }

    public int deleteByKeys(String[] uuids) throws DAOException {
        String where = SqlUtil.getWhereSqlbyIds(uuids, tableInfo.getPkName());
        return this.deleteByWhere(where);
    }

    public int deleteByKey(String uuid) throws DAOException {
        String deleteSql = SqlUtil.getDeleteSQLByKey(this.tableInfo);
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(deleteSql);
            SqlUtil.preparePrimaryKeys(tableInfo, ps, 1, uuid);
            int deletCount = ps.executeUpdate();
            return deletCount;
        } catch (Exception e) {
            e.printStackTrace();

            throw new DAOException("Delete failed! msg:" + e.getMessage(), e);
        }
    }


    public int deleteByWhere(String where) throws DAOException {
        String deleteSql = SqlUtil.getDeleteSQLByWhereClause(this.tableInfo, where);
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(deleteSql);
            int deletCount = ps.executeUpdate();
            return deletCount;
        } catch (Exception e) {
            e.fillInStackTrace();
            throw new DAOException("Delete failed! msg:" + e.getMessage(), e);
        } finally {

            try {
                ps.close();
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    public T create() throws DAOException {
        return DBBeanUtil.genBean(this.tableInfo, UUID.randomUUID().toString(), tClass);
    }

    public T create(String uuid) throws DAOException {
        PreparedStatement ps = null;
        String sql = null;
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        T dao = DBBeanUtil.genBean(this.tableInfo, uuid, tClass);
        try {
            Map<String, Object> valueMap = BeanMap.create(dao);
            sql = SqlUtil.getInsertSQLNoParas(this.tableInfo);
            ps = connection.prepareStatement(sql);
            SqlUtil.prepareAllFields(tableInfo, ps, uuid, valueMap);

//
//            SqlUtil.prepareFields(tableInfo, ps, valueMap);
            ps.executeUpdate();
        } catch (Exception e) {
            e.fillInStackTrace();
            throw new DAOException("Create failed! msg:" + e.getMessage(), e);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                // do nothing
            }
        }
        return dao;

    }


    public synchronized List<T> findAll(String custWhereClause) throws DAOException {
        PreparedStatement st = null;
        ResultSet result = null;
        StringBuffer sql = new StringBuffer();
        try {
            String sSql = SqlUtil.getFullSearchSql(tableInfo, custWhereClause);
            st = connection.prepareStatement(sSql);
            result = st.executeQuery();
            List dbBeans = new ArrayList();

            while (result.next()) {
                Object newBean = fetchResultSet(result);
                dbBeans.add(newBean);
            }
            return dbBeans;
        } catch (SQLException e) {
            e.fillInStackTrace();

            throw new DAOException("find failed! msg: " + e.getMessage(), e);
        } finally {
            try {
                result.close();
                st.close();
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    public synchronized List<T> load(String[] uuids) throws DAOException {
        String where = SqlUtil.getWhereSqlbyIds(uuids, tableInfo.getPkName());
        List<T> objs = this.findAll(where);
        return objs;
    }


    public synchronized List<T> findPrimaryKey(String uuid) throws DAOException {
        PreparedStatement st = null;
        ResultSet result = null;
        StringBuffer sql = new StringBuffer();
        try {
            String sSql = SqlUtil.getFindByPrimaryKeySQLNoParas(tableInfo);
            st = connection.prepareStatement(sSql);
            SqlUtil.preparePrimaryKeys(tableInfo, st, 1, uuid);
            result = st.executeQuery();
            List<T> dbBeans = new ArrayList<T>();

            while (result.next()) {
                T newBean = (T) fetchResultSet(result);
                dbBeans.add(newBean);
            }
            return dbBeans;
        } catch (SQLException e) {
            e.printStackTrace();

            throw new DAOException("find failed! msg: " + e.getMessage(), e);
        } finally {
            try {
                result.close();
                st.close();
            } catch (Exception e) {
                // do nothing
            }
        }
    }


    protected T fetchResultSet(ResultSet result) {
        CaselessStringKeyHashMap valuemap = SqlUtil.fetchResultSet(this.tableInfo, result);
        T bean = DBBeanUtil.genBean(tableInfo, valuemap, tClass);
        return bean;
    }


}


