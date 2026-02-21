/**
 * $RCSfile: MetadataFactory.java,v $
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
package net.ooder.common.database.metadata;

import net.ooder.annotation.ColType;
import net.ooder.annotation.DBField;
import net.ooder.annotation.DBTable;
import net.ooder.annotation.MethodChinaName;
import net.ooder.common.SystemStatus;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.database.ConnectionManagerFactory;
import net.ooder.common.database.DbManager;
import net.ooder.common.database.ProfiledConnection;
import net.ooder.common.database.ProfiledConnectionEntry;
import net.ooder.common.database.dao.DAOException;
import net.ooder.common.database.util.TypeMapping;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.CaselessStringKeyHashMap;
import net.ooder.common.util.StringUtility;
import net.ooder.esb.util.ESBConstants;
import net.ooder.thread.JDSThreadFactory;
import net.ooder.web.util.AnnotationUtil;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executors;

public class MetadataFactory {

    Cache<String, TableInfo> tableCache = CacheManagerFactory.createCache(ESBConstants.CONFIG_KEY, "TableInfo");

    protected transient static final Log log = LogFactory.getLog("MetadataFactory", MetadataFactory.class);

    public static final String THREAD_LOCK = "MetadataFactory Thread Lock";

    private String url;

    private String configKey;


    static Map<String, MetadataFactory> factoryMap = new HashMap<>();

    DbManager dbManager;


    public static MetadataFactory getInstance(String key) {
        MetadataFactory factory = factoryMap.get(key);
        if (factory == null) {
            synchronized (THREAD_LOCK) {
                if (factory == null) {
                    factory = new MetadataFactory(key);
                    factoryMap.put(key, factory);
                    Executors.newCachedThreadPool(new JDSThreadFactory(key)).execute(new DBReConnection(factory));
                }
            }
        }
        return factory;
    }


    MetadataFactory(ProviderConfig config) {
        ConnectionManagerFactory.getInstance().updateProviderConfig(config);
        init(config);
    }

    MetadataFactory(String configKey) {
        this.configKey = configKey;
        reSetConfig();
    }

    void init(ProviderConfig config) {
        this.configKey = config.getConfigKey().toLowerCase();
        this.url = config.getServerURL().trim();
    }

    public void connect() {
        ProviderConfig connectionProvider = ConnectionManagerFactory.getInstance().getProviderConfig(configKey);
        if (dbManager == null && connectionProvider != null && !connectionProvider.getStatus().equals(SystemStatus.ONLINE)) {
            this.reConnect(connectionProvider);
        }
    }

    public void updateProviderConfig(ProviderConfig connectionProvider) {
        ConnectionManagerFactory.getInstance().updateProviderConfig(connectionProvider);
        dbManager = DbManager.getInstance(configKey).clear();
        reConnect(connectionProvider);
    }


    private void reConnect(ProviderConfig connectionProvider) {
        if (connectionProvider == null) {
            connectionProvider = ConnectionManagerFactory.getInstance().getProviderConfig(configKey);
        }

        if (connectionProvider != null) {
            this.url = connectionProvider.getServerURL();
            SystemStatus status = connectionProvider.getStatus();
            if (!status.equals(SystemStatus.DISABLE) && !status.equals(SystemStatus.DELETE) && !status.equals(SystemStatus.FAULT)) {
                Connection conn = null;
                try {
                    if (dbManager != null && dbManager.getConnection() != null) {
                        conn = dbManager.getConnection();
                        DatabaseMetaData dbmd = conn.getMetaData();
                        this.url = dbmd.getURL();
                        connectionProvider.setStatus(SystemStatus.ONLINE);
                    }
                } catch (SQLException e) {
                    log.error("SQL error during reConnect", e);
                    connectionProvider.setStatus(SystemStatus.FAULT);
                } finally {
                    if (dbManager != null) {
                        dbManager.releaseConnection(conn);
                    }

                }

            }
        }
    }


    public String getDataTypeJsonStr() throws DAOException {
        Set<String> set = getDataType();
        String str = set.toString().replaceAll("(\\w[^],]+)", "'$1'");
        return str;
    }

    public List<SqlExcuteInfo> getSqlCountInfo() {
        List<SqlExcuteInfo> infos = new ArrayList<SqlExcuteInfo>();
        SqlType[] types = SqlType.values();
        for (SqlType type : types) {
            infos.add(new SqlExcuteInfo(this.configKey, type));
        }
        return infos;
    }

    public List<ProfiledConnectionEntry> getSqlExcuteInfos(SqlType type) {
        ProfiledConnectionEntry[] entrieArr = ProfiledConnection.getSortedQueries(type.getType(), true);
        List<ProfiledConnectionEntry> entries = new ArrayList<ProfiledConnectionEntry>();
        if (entrieArr != null && entrieArr.length > 0) {
            entries = Arrays.asList(entrieArr);
        }
        return entries;
    }

    public void startSqlMonitor() {
        ConnectionManagerFactory.getInstance().getConnectionManager(configKey).setProfilingEnabled(true);
    }

    public void stopSqlMonitor() {
        ConnectionManagerFactory.getInstance().getConnectionManager(configKey).setProfilingEnabled(false);
    }


    public void resetStatistics() {
        ProfiledConnection.resetStatistics();
    }


    /**
     * 根据表信息取得生成数据库表的sql
     *
     * @param info
     * @return
     */
    private List<String> getCreateTableSqlByInfo(TableInfo info) {
        List<String> rtn = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        //   String tabName = info.getName().toLowerCase();
        String tabName = info.getName();
        sb.append("create table ").append(tabName);
        String tabCom = info.getCnname();


        sb.append("(");
        String keyName = info.getPkName();
        List<ColInfo> colInfos = info.getColList();
        for (ColInfo colInfo : colInfos) {

            String name = colInfo.getName();
            String typeStr = getDbTypeStr(colInfo);
            sb.append(name).append(" ").append(typeStr);

            if (name.toLowerCase().equals(keyName.toLowerCase())) {
                sb.append(" primary key ");
            } else if (!colInfo.isCanNull()) {
                sb.append(" not null ");
            }

            if (isMySql()) {
                sb.append("  comment  '").append(colInfo.getCnname()).append("'");
            } else {
                sb.append(colInfo.getName()).append(" is '").append(colInfo.getCnname()).append("'");
            }

            sb.append(",");
        }

        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");

        StringBuilder tablesb = new StringBuilder();
        if (isMySql()) {
            tablesb.append(" alter table ").append(info.getName()).append(" comment '").append(info.getCnname()).append("'");
        } else {
            tablesb.append("comment on table ").append(tabName).append(" is '").append(tabCom).append("'");
        }

        ;
        rtn.add(0, sb.toString());

        rtn.add(tablesb.toString());
        return rtn;
    }

    public void reLoadTableInfo(String tableName) throws DAOException {
        this.clear(tableName);
    }

    /**
     * 根据表信息创建数据库"
     *
     * @param info
     * @throws SQLException
     */
    public void createTableByInfo(TableInfo info) throws DAOException {
        Connection conn = null;
        if (getDbManager() == null) {
            throw new DAOException("数据库未连接");
        }
        try {
            conn = dbManager.getConnection();
            TableInfo tableInfo = this.loadTableInfoFromDb(info.getName());

            if (tableInfo == null) {
                List<ColInfo> cols = info.getColList();
                if (cols.size() == 0) {
                    String pkName = info.getPkName();
                    if (pkName == null || pkName.equals("")) {
                        pkName = "UUID";
                    }
                    ColInfo collInfo = new ColInfo();
                    collInfo.setName(pkName);
                    if (isMySql()) {
                        collInfo.setColType(ColType.VARCHAR);
                    } else {
                        collInfo.setColType(ColType.VARCHAR2);
                    }
                    collInfo.setCanNull(false);
                    collInfo.setLength(64);
                    collInfo.setPk(true);
                    collInfo.setUrl(info.getUrl());
                    info.addCol(collInfo);
                    info.setPkName(pkName);
                }

                List<String> sqls = getCreateTableSqlByInfo(info);
                Statement stmt = conn.createStatement();
                boolean flag = conn.getAutoCommit();
                conn.setAutoCommit(false);
                for (int i = 0, c = sqls.size(); i < c; i++) {
                    stmt.addBatch(sqls.get(i));
                }
                stmt.executeBatch();
                conn.commit();

            } else {
                List<TableInfo> tableList = new ArrayList<TableInfo>();
                tableList.add(info);
                modifyTableCnname(tableList);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            dbManager.releaseConnection(conn);
        }


    }


    /**
     * 修改数据库表,增加info2比info1多的"只考虑info1.getName()==info2.getName()并且info2的列比info1的列多的情况
     *
     * @throws SQLException
     */

    private TableInfo getTableInfoByDAO(Class clazz) throws DAOException {
        TableInfo info = new TableInfo();
        Map<String, ColInfo> fieldMap = new HashMap<String, ColInfo>();
        DBTable cAnn = AnnotationUtil.getClassAnnotation(clazz, DBTable.class);
        String tableName = null;

        if (cAnn != null) {
            //  tableName = cAnn.tableName().toLowerCase();
            tableName = cAnn.tableName();
            info = this.getTableInfo(tableName);
            if (info == null && cAnn != null) {
                info = new TableInfo();
                info.setName(cAnn.tableName());
                info.setPkName(cAnn.primaryKey());
                info.setCnname(cAnn.cname());
                info.setConfigKey(cAnn.configKey());
                Field ms[] = clazz.getDeclaredFields();

                Method methods[] = clazz.getDeclaredMethods();

                for (int i = 0; i < ms.length; i++) {
                    Field m = ms[i];
                    DBField ma = m.getAnnotation(DBField.class);
                    if (ma != null) {
                        ColInfo col = new ColInfo();
                        col.setName(m.getName());
                        col.setFieldname(ma.dbFieldName());
                        col.setColType(ma.dbType());
                        col.setCnname(ma.cnName());
                        col.setLength(ma.length());
                        col.setCanNull(ma.isNull());
                        col.setEnums(ma.enums());
                        col.setEnumClass(ma.enumClass());
                        col.setTablename(tableName);
                        col.setUrl(info.getUrl());
                        MethodChinaName mcn = m.getAnnotation(MethodChinaName.class);
                        if (mcn != null) {
                            col.setCnname(mcn.cname());
                        }
                        if (cAnn.primaryKey().equals(col.getName())
                                || cAnn.primaryKey().equals(col.getFieldname())
                                ) {
                            col.setPk(true);
                        }

                        if (col.getCnname() == null) {
                            col.setCnname(col.getName());
                        }
                        fieldMap.put(col.getFieldname(), col);
                        info.addCol(col);
                    }
                }


                for (Method method : methods) {
                    if (method.getName().startsWith("get") ||
                            (method.getName().startsWith("is") && (method.getReturnType().equals(boolean.class) || method.getReturnType().equals(Boolean.class)))
                                    && method.getParameterTypes().length == 0) {
                        String fieldName = method.getName().substring("get".length());
                        if (method.getName().startsWith("is")) {
                            fieldName = method.getName().substring("is".length());
                        }
                        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                        DBField ma = AnnotationUtil.getMethodAnnotation(method, DBField.class);
                        if (ma != null) {
                            ColInfo col = fieldMap.get(fieldName);

                            if (col == null) {
                                col = new ColInfo();
                                col.setName(fieldName);
                                col.setFieldname(ma.dbFieldName());
                                col.setColType(ma.dbType());
                                col.setEnums(ma.enums());
                                col.setEnumClass(ma.enumClass());
                                col.setLength(ma.length());
                                col.setCanNull(ma.isNull());
                                col.setUrl(info.getUrl());
                                MethodChinaName mcn = AnnotationUtil.getMethodAnnotation(method, MethodChinaName.class);
                                if (mcn != null) {
                                    col.setCnname(mcn.cname());
                                } else {
                                    col.setCnname(ma.cnName());
                                }
                                if (cAnn.primaryKey().equals(col.getName())
                                        || cAnn.primaryKey().equals(col.getFieldname())
                                        ) {
                                    col.setPk(true);
                                }
                                if (col.getCnname() == null) {
                                    col.setCnname(col.getName());
                                }
                                fieldMap.put(col.getFieldname(), col);
                                info.addCol(col);
                            }

                        }

                    }
                }

            }
        } else {
            info = fillByJPA(clazz);
        }
        this.initTable(info);

        return info;
    }


    private TableInfo fillByJPA(Class clazz) throws DAOException {
        TableInfo info = new TableInfo();
        Map<String, ColInfo> fieldMap = new HashMap<String, ColInfo>();
        Table cAnn = AnnotationUtil.getClassAnnotation(clazz, Table.class);
        String tableName = null;
        if (cAnn != null) {
            tableName = cAnn.name();
            info = this.getTableInfo(tableName);
            if (info == null && cAnn != null) {
                info = new TableInfo();
                info.setName(cAnn.name());
                info.setUniqueConstraint(cAnn.uniqueConstraints());
                info.setCnname(cAnn.name());
                info.setConfigKey(cAnn.schema());
                Field ms[] = clazz.getDeclaredFields();
                Method methods[] = clazz.getDeclaredMethods();


                for (int i = 0; i < ms.length; i++) {
                    Field m = ms[i];
                    Column ma = m.getAnnotation(Column.class);
                    if (ma != null) {
                        ColInfo col = new ColInfo();
                        col.setName(m.getName());
                        col.setFieldname(ma.name());
                        col.setColType(ColType.fromJPAType(ma.columnDefinition()));
                        col.setLength(ma.length());
                        col.setCanNull(ma.nullable());
                        col.setTablename(tableName);
                        col.setUrl(info.getUrl());
                        MethodChinaName mcn = m.getAnnotation(MethodChinaName.class);
                        if (mcn != null) {
                            col.setCnname(mcn.cname());
                        }
                        if (info.getPkName().contains(col.getName())
                                || info.getPkName().equals(col.getFieldname())
                                ) {
                            col.setPk(true);
                        }

                        if (col.getCnname() == null) {
                            col.setCnname(col.getName());
                        }
                        fieldMap.put(col.getFieldname(), col);
                        info.addCol(col);
                    }
                }


                for (Method method : methods) {
                    if (method.getName().startsWith("get") ||
                            (method.getName().startsWith("is") && (method.getReturnType().equals(boolean.class) || method.getReturnType().equals(Boolean.class)))
                                    && method.getParameterTypes().length == 0) {
                        String fieldName = method.getName().substring("get".length());
                        if (method.getName().startsWith("is")) {
                            fieldName = method.getName().substring("is".length());
                        }
                        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                        Column ma = AnnotationUtil.getMethodAnnotation(method, Column.class);

                        if (ma != null) {
                            ColInfo col = fieldMap.get(fieldName);

                            if (col == null) {
                                col = new ColInfo();
                                col.setName(fieldName);
                                col.setFieldname(ma.name());
                                col.setColType(ColType.fromJPAType(ma.columnDefinition()));
                                col.setLength(ma.length());
                                col.setCanNull(ma.nullable());
                                col.setTablename(tableName);
                                col.setUrl(info.getUrl());
                                MethodChinaName mcn = AnnotationUtil.getMethodAnnotation(method, MethodChinaName.class);
                                if (mcn != null) {
                                    col.setCnname(mcn.cname());
                                } else {
                                    col.setCnname(ma.name());
                                }
                                if (info.getPkName().contains(col.getName())
                                        || info.getPkName().equals(col.getFieldname())
                                        ) {
                                    col.setPk(true);
                                }
                                if (col.getCnname() == null) {
                                    col.setCnname(col.getName());
                                }
                                fieldMap.put(col.getFieldname(), col);
                                info.addCol(col);
                            }


                        }

                    }
                }

            }
        }
        this.initTable(info);

        return info;
    }

    /**
     * 取所有数据类"
     *
     * @return
     * @throws SQLException
     */
    private Set<String> getDataType() throws DAOException {
        if (getDbManager() == null) {
            throw new DAOException("数据库未连接");
        }
        Set<String> rtn = new TreeSet<String>();

        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet rs = dbmd.getTypeInfo();
            while (rs.next()) {
                rtn.add(rs.getString("TYPE_NAME"));
            }
            dbManager.releaseConnection(conn);
        } catch (SQLException e) {
            throw new DAOException(e);
        }


        return rtn;
    }

    DbManager getDbManager() {
        if (dbManager == null) {
            this.reConnect(null);
        }
        return dbManager;
    }

    /**
     * 取得指定模式下的所有匹配的表名
     *
     * @param str 表名前缀,可以为null
     * @return
     * @throws SQLException
     */
    public List<String> getTableNamesFromDb(String str) throws DAOException {
        if (getDbManager() == null) {
            throw new DAOException("数据库未连接");
        }
        Connection conn = null;


        List<String> list = new ArrayList<String>();
        try {
            String names = null;
            if (str != null && !str.equals("") && !str.equals("all")) {
                if (str.endsWith("_") && !str.endsWith("/_")) {
                    str = str.replaceFirst("_$", "/_");
                }
                names = str + "%";
            }
            String[] type = new String[]{"TABLE"};

            conn = dbManager.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            String schem = dbmd.getUserName();
            // ResultSet rs = dbmd.getTables(null, schem.toUpperCase(), names, type);
            ResultSet rs = dbmd.getTables(null, schem, names, type);
            while (rs.next()) {
                list.add(rs.getString("TABLE_NAME"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbManager.releaseConnection(conn);
        }

        return list;
    }

    public List<TableInfo> getTableInfos(String str) throws DAOException {
        List<TableInfo> tableInfos = new ArrayList<>();
        List<TableInfo> simpleTables = getSimpleTableInfo(str);
        for (TableInfo tableInfo : simpleTables) {
            tableInfos.add(this.getTableInfo(tableInfo.getName()));
        }
        return tableInfos;
    }


    List<TableInfo> getSimpleTableInfo(String str) throws DAOException {
        if (getDbManager() == null) {
            throw new DAOException("数据库未连接");
        }
        String names = null;
        List<TableInfo> tableInfos = new ArrayList<TableInfo>();
        Connection conn = null;
        try {
            if (str != null && !str.equals("") && !str.equals("all")) {
                if (str.endsWith("_") && !str.endsWith("/_")) {
                    str = str.replaceFirst("_$", "/_");
                }
                names = str + "%";
            } else {
                names = "%";
            }
            String[] type = new String[]{"TABLE"};
            conn = dbManager.getConnection();
            if (conn != null) {
                DatabaseMetaData dbmd = conn.getMetaData();
                String schem = dbmd.getUserName();
                if (isMySql()) {

                    String table_schema = schem;
                    if (table_schema.indexOf("@") > -1) {
                        table_schema = schem.split("@")[0];
                    }
                    String sql = " SELECT * FROM information_schema.TABLES where table_schema = (select database()) and TABLE_NAME like '" + names + "'";
                    // String sql = " SELECT * FROM information_schema.TABLES where table_schema = '" + table_schema + "' and TABLE_NAME like '" + names + "'";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet msqlrs = stmt.executeQuery();

                    while (msqlrs.next()) {
                        TableInfo info = new TableInfo();
                        info.setName(msqlrs.getString("TABLE_NAME"));
                        info.setCreateTime(msqlrs.getTimestamp("CREATE_TIME"));
                        String cn = msqlrs.getString("TABLE_COMMENT");
                        if (cn == null) {
                            cn = info.getName();
                        }
                        info.setConfigKey(this.configKey);
                        info.setUrl(this.url);
                        info.setCnname(cn);
                        tableInfos.add(info);
                    }

                } else {

                    ResultSet rs = dbmd.getTables(null, schem.toUpperCase(), names, type);

                    while (rs.next()) {
                        TableInfo info = new TableInfo();
                        info.setName(rs.getString("TABLE_NAME"));
                        //    TableInfo info = this.getTableInfo(rs.getString("TABLE_NAME"));
                        String cn = getTableCnname(conn, info.getName());

                        info.setCnname(cn);
                        tableInfos.add(info);
                    }

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                dbManager.releaseConnection(conn);
            }

        }
        List<TableInfo> reallist = new ArrayList<TableInfo>();

        for (TableInfo tableInfo : tableInfos) {
            TableInfo info = tableCache.get(tableInfo.getName());
            if (info != null) {
                reallist.add(info);
            } else {
                reallist.add(tableInfo);
            }
        }
        return reallist;
    }


    public void clear(String tableName) throws DAOException {
        TableInfo info = this.getTableInfo(tableName);
        if (info != null) {
            tableCache.remove(tableName);
            tableCache.remove(getFieldName(tableName));
            tableCache.remove(getClassName(tableName));
            tableCache.remove(url + "[" + tableName + "]");
        }
    }

    void reSetConfig() {
        ProviderConfig config = ConnectionManagerFactory.getInstance().getProviderConfig(configKey);
        if (config != null) {
            init(config);
        }
    }


    public void clearAll() {
        tableCache.clear();
        dbManager = DbManager.getInstance(configKey).clear();
        reSetConfig();
    }


    public String getFieldName(String tableName) {
        return StringUtility.formatJavaName(tableName.toLowerCase(), false);
    }

    public String getClassName(String tableName) {
        return StringUtility.formatJavaName(tableName.toLowerCase(), true);
    }


    public TableInfo getTableInfo(String tableName) throws DAOException {
        if (tableName.indexOf(".") > -1) {
            String configKey = tableName.split("\\.")[0];
            tableName = tableName.split("\\.")[1];
        }


        // tableName = tableName.toLowerCase();
        // TableInfo info = tableCache.get(tableName.toLowerCase());
        TableInfo info = tableCache.get(tableName);
        if (info == null) {
            info = tableCache.get(url + "[" + tableName + "]");
        }
        if (info == null) {
            info = tableCache.get(getFieldName(tableName));
        }
        if (info == null) {
            info = tableCache.get(getClassName(tableName));
        }

        if (info == null) {
            info = loadTableInfoFromDb(tableName);
            if (info != null) {
                tableCache.put(tableName, info);
                tableCache.put(url + "[" + tableName + "]" + tableName, info);
                tableCache.put(getFieldName(tableName), info);
                tableCache.put(getClassName(tableName), info);
            }
        }

        return info;
    }


    public void sortColIndex(String tableName, ColInfo colInfo, String nextColName) throws DAOException {
        if (dbManager == null) {
            throw new DAOException("数据库未连接");
        }

        Connection conn = null;

        StringBuffer sqlBuffer = new StringBuffer(" alter table `" + tableName + "`");
        if (isMySql()) {
            sqlBuffer.append(" modify `").append(colInfo.getName()).append("`  ").append(getDbTypeStr(colInfo));
        } else {
            sqlBuffer.append(" modify ").append(colInfo.getName()).append(" ").append(getDbTypeStr(colInfo));
        }
        if (colInfo.isCanNull()) {
            if (colInfo.isCanNull()) {
                sqlBuffer.append(" null ");
            } else
                sqlBuffer.append(" not null ");
        }
        if (colInfo.getCnname() != null) {
            if (isMySql()) {
                sqlBuffer.append("  comment  '").append(colInfo.getCnname()).append("'");
            } else {
                sqlBuffer.append(colInfo.getName()).append(" is '").append(colInfo.getCnname()).append("'");
            }
        }
        sqlBuffer.append(" AFTER `" + nextColName.toUpperCase() + "`;");
        Statement stmt = null;
        try {
            conn = dbManager.getConnection();
            stmt = conn.createStatement();
            stmt.execute(sqlBuffer.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * 根据数据库表生成对应的表信息
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    TableInfo loadTableInfoFromDb(String tableName) throws DAOException {
        // tableName = tableName.toLowerCase();
        if (getDbManager() == null) {
            throw new DAOException("数据库未连接");
        }
        TableInfo info = null;
        Connection conn = null;

        try {
            conn = dbManager.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            String schema = dbmd.getUserName();

            ResultSet rs = dbmd.getTables(null, schema.toUpperCase(), tableName, new String[]{"TABLE"});

            if (rs.next()) {
                info = new TableInfo();
                String cnname = getTableCnname(conn, tableName);
                if (cnname == null || cnname.equals("")) {
                    info.setCnname(tableName);
                } else {
                    info.setCnname(cnname);
                }
                rs.close();
                info.setUrl(dbmd.getURL());
                info.setName(tableName);
                info.setConfigKey(this.configKey);
                ResultSet pk = dbmd.getPrimaryKeys(null, schema.toUpperCase(), tableName);
                for (; pk.next(); ) {
                    info.getPkNames().add(pk.getString("COLUMN_NAME"));
                }
                pk.close();

                ResultSet cols = dbmd.getColumns(null, schema.toUpperCase(), tableName, null);
                CaselessStringKeyHashMap<String, DBColMeta> colComments = getColsCnname(conn, tableName);
                while (cols.next()) {
                    ColInfo col = new ColInfo();

                    String colName = cols.getString("COLUMN_NAME");
                    int type = cols.getInt("DATA_TYPE");
                    String typeName = cols.getString("TYPE_NAME");
                    int tmp = cols.getInt("NULLABLE");

                    col.setTablename(tableName);
                    col.setConfigKey(this.configKey);
                    col.setName(colName);

                    DBColMeta dbColMeta = colComments.get(colName);
                    String colcnname = dbColMeta.getComment();
                    if (dbColMeta.getType() != null) {
                        col.setEnums(parseEunms(dbColMeta.getType()));
                    }
                    if (colcnname == null || colcnname.equals("")) {
                        col.setCnname(col.getName());
                    } else {
                        col.setCnname(colcnname);
                    }

                    if (info.getPkNames().contains(col.getName())
                            || info.getPkNames().contains(col.getFieldname())
                            ) {
                        col.setPk(true);
                    }


                    if (TypeMapping.isNumeric(type) || TypeMapping.isString(type)) {
                        col.setLength(cols.getInt("COLUMN_SIZE"));
                    } else {
                        col.setLength(0);
                    }
                    col.setDataType(type);// "DATA_TYPE"


                    if (typeName != null) {
                        col.setColType(ColType.fromType(cols.getString("TYPE_NAME")));
                    }


                    col.setFractions(cols.getInt("DECIMAL_DIGITS"));
                    col.setUrl(info.getUrl());

                    if (tmp == DatabaseMetaData.columnNoNulls) {
                        col.setCanNull(false);
                    } else {
                        col.setCanNull(true);
                    }
                    info.setCol(col.getName(), col);
                }
                cols.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbManager.releaseConnection(conn);
        }

        return info;
    }

    /**
     * 取表的注释
     *
     * @param conn
     * @param tableName
     * @return
     * @throws SQLException
     */
    private String getTableCnname(Connection conn, String tableName) throws DAOException {
        if (getDbManager() == null) {
            throw new DAOException("数据库未连接");
        }
        String rtn = null;
        try {
            if (isMySql()) {
                PreparedStatement stmt = null;
                stmt = conn.prepareStatement("SHOW CREATE TABLE " + tableName);
                ResultSet rs = stmt.executeQuery();
                if (rs != null && rs.next()) {
                    String createDDL = rs.getString(2);
                    rtn = parse(createDDL);
                }
                rs.close();
                stmt.close();
            } else {
                String sql = "select comments from user_tab_comments where comments is not null and table_name=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, tableName.toLowerCase());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    rtn = rs.getString("comments");
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        return rtn;
    }


    private String parse(String all) {
        String comment = null;
        int index = all.indexOf("COMMENT='");
        if (index < 0) {
            return "";
        }
        comment = all.substring(index + 9);
        comment = comment.substring(0, comment.length() - 1);
        return comment;
    }

    private String[] parseEunms(String all) {
        String enums = null;
        List<String> enumList = new ArrayList<>();
        String enumKey = "enum(";
        int index = all.indexOf(enumKey);
        if (index < 0) {
            return new String[]{};
        }
        enums = all.substring("enum(".length());
        enums = enums.substring(0, enums.length() - 1);
        String[] enumArr = StringUtility.split(enums, ",");
        for (String enumk : enumArr) {
            enumList.add(enumk.substring(1, enumk.length() - 1));
        }
        return enumList.toArray(new String[]{});
    }

    private String toEunmString(String[] enumArr) {
        StringBuffer enums = null;
        List<String> enumList = new ArrayList<>();
        enums.append("enum(");
        if (enumArr.length > 0) {
            for (String enumk : enumArr) {
                enums.append("'" + enumk + "',");
            }
            enums = enums.deleteCharAt(enums.length() - 1);
        }
        enums.append(")");
        return enums.toString();
    }


    /**
     * 取指定表的列注释
     *
     * @param conn
     * @param tableName
     * @return
     * @throws SQLException
     */
    private CaselessStringKeyHashMap<String, DBColMeta> getColsCnname(Connection conn, String tableName) throws DAOException {
        if (getDbManager() == null) {
            throw new DAOException("数据库未连接");
        }
        CaselessStringKeyHashMap<String, DBColMeta> rtn = new CaselessStringKeyHashMap<String, DBColMeta>();

        try {
            if (isMySql()) {

                PreparedStatement stmt = conn.prepareStatement("show full columns from " + tableName);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    DBColMeta dbColMeta = new DBColMeta();
                    dbColMeta.setField(rs.getString("Field"));
                    dbColMeta.setComment(rs.getString("Comment"));
                    dbColMeta.setType(rs.getString("type"));
                    rtn.put(dbColMeta.getField(), dbColMeta);

                }
                rs.close();
                stmt.close();
            } else {
                String sql = "select column_name, comments from user_col_comments where comments is not null and table_name=?";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, tableName.toLowerCase());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    DBColMeta dbColMeta = new DBColMeta();
                    dbColMeta.setField(rs.getString("Field"));
                    dbColMeta.setComment(rs.getString("Comment"));
                    rtn.put(dbColMeta.getField(), dbColMeta);
                }
                rs.close();
                stmt.close();
            }

        } catch (SQLException e) {
            throw new DAOException(e);
        }


        return rtn;
    }

    public void dropTable(List<String> tableNames) throws DAOException {

        List<String> sqls = new ArrayList<String>();
        for (String tableName : tableNames) {
            String sql = "drop  table " + tableName;
            sqls.add(sql);
            reLoadTableInfo(tableName);
        }
        this.executeBatch(sqls);

    }

    public void addPk(String tableName, String pkName) throws DAOException {
        List<String> sqls = new ArrayList<String>();
        TableInfo tableInfo = this.getTableInfo(tableName);
        if (!tableInfo.getPkNames().contains(pkName)) {
            if (isMySql()) {
                String sql = "alter table `" + tableName + "` add  primary key(`" + pkName + "`)";
                sqls.add(sql);
            } else {
                String sql = "alter table " + tableName + " add constraint " + tableName + "_PK_" + pkName + "  primary key(" + pkName + ")";
                sqls.add(sql);
            }
            this.executeBatch(sqls);
            reLoadTableInfo(tableName);
        }


    }


    public void delCols(String tableName, List<String> colNames) throws DAOException {

        TableInfo tableInfo = this.getTableInfo(tableName);
        if (tableInfo != null) {
            List<String> sqls = new ArrayList<String>();
            for (String colName : colNames) {
                String sql = "alter table `" + tableName + "` drop column `" + colName + "`";
                sqls.add(sql);
            }
            executeBatch(sqls);
            reLoadTableInfo(tableName);
        }


    }

    public void delPk(String tableName, String pkName) throws DAOException {
        List<String> sqls = new ArrayList<String>();
        TableInfo tableInfo = this.getTableInfo(tableName);
        if (tableInfo.getPkNames().contains(pkName)) {
            tableInfo.getPkNames().remove(pkName);

            if (isMySql()) {
                String sql = "alter table `" + tableName + "` drop primary key";
                sqls.add(sql);
                for (String pk : tableInfo.getPkNames()) {
                    sql = "alter table " + tableName + " add  primary key(`" + pk + "`)";
                    sqls.add(sql);
                }

            } else {
                String sql = "alter table `" + tableName + "` drop constraint " + tableName + "_PK_" + pkName + "  primary key(" + pkName + ")";
                sqls.add(sql);
            }
            executeBatch(sqls);
            reLoadTableInfo(tableName);

        }


    }

    /**
     * 返回sql中列的数据类型对应的字符串
     *
     * @param c
     * @return
     */
    private String getDbTypeStr(ColInfo c) {
        String type = c.getColType().name();
        String rtn = type;

        if (type == null) {
            throw new RuntimeException("列[" + c.getName() + "]的数据类型不能为null");
        }

        if (c.getColType().equals(ColType.ENUM)) {
            rtn = toEunmString(c.getEnums());
        } else {
            int len = c.getLength();
            if (len > 0 && !"int".equalsIgnoreCase(type) && !"date".equalsIgnoreCase(type)) {
                rtn += "(" + len + ")";
            } else if (type.toLowerCase().startsWith("varchar")) {
                throw new RuntimeException(type + "类型的长度必须大于0");
            }
        }

        return rtn;
    }

    /**
     * 取得增加的列的sql
     *
     * @param tableName
     * @param cols
     * @return
     */
    private List<String> getAddColSql(String tableName, List<ColInfo> cols) {
        List<String> rtn = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0, n = cols.size(); i < n; i++) {
            ColInfo c = cols.get(i);
            sb.append(" alter table ").append(tableName).append(" add ").append(c.getName()).append(" ").append(getDbTypeStr(c));
            if (!c.isCanNull()) {
                sb.append(" not null ");
            }
            rtn.add(sb.toString());
            sb = new StringBuilder();
            if (c.getCnname() != null) {
                sb.append("comment on column ").append(tableName).append(".").append(c.getName()).append(" is '").append(c.getCnname()).append("'");
                rtn.add(sb.toString());
                sb = new StringBuilder();
            }

        }

        return rtn;
    }

    public TableInfo getInfoByClass(Class clazz) throws DAOException {
        return this.getTableInfoByDAO(clazz);
    }


    public void addCols(String tableName, List<ColInfo> cols) throws DAOException {
        TableInfo tab = this.getTableInfo(tableName);
        List<String> nameList = new ArrayList<>();
        List<ColInfo> newCols = new ArrayList<>();
        for (ColInfo info : tab.getColList()) {
            nameList.add(info.getName());
        }

        for (ColInfo colInfo : cols) {
            if (!nameList.contains(colInfo.getName())) {
                newCols.add(colInfo);
            }
        }

        List<String> sqls = getModifyTableColsSql(tab, newCols);
        executeBatch(sqls);
        reLoadTableInfo(tableName);
    }

    public void modifyTableCols(String tableName, List<ColInfo> cols) throws DAOException {
        TableInfo tableInfo = this.getTableInfo(tableName);
        Set<String> pkList = tableInfo.getPkNames();

        for (ColInfo colInfo : cols) {
            if (colInfo.getPk()) {
                this.addPk(tableName, colInfo.getName());
            } else {
                this.delPk(tableName, colInfo.getName());
            }
        }


        List<String> sqls = getModifyTableColsSql(tableInfo, cols);
        executeBatch(sqls);
        reLoadTableInfo(tableName);
    }

    private List<String> getModifyTableColsSql(TableInfo tab, Collection<ColInfo> newCols) {
        List<String> rtn = new ArrayList<String>(newCols.size());
        String tableName = tab.getName();
        List<ColInfo> oldCols = tab.getColList();

        for (ColInfo nc : newCols) {
            StringBuilder sb = new StringBuilder();
            String name = nc.getName();
            ColInfo oc = tab.getCoInfoByName(name);
            if (oc == null) {
                if (isMySql()) {
                    sb.append(" alter table `").append(tableName).append("` add `").append(nc.getName()).append("` ").append(getDbTypeStr(nc));

                } else {
                    sb.append(" alter table ").append(tableName).append(" add ").append(nc.getName()).append(" ").append(getDbTypeStr(nc));
                }
                if (!nc.isCanNull()) {
                    sb.append(" not null ");
                }
            } else {
                // alter table A_MY_TEST modify NAME VARCHAR2(22)

                if (isMySql()) {
                    sb.append(" alter table `").append(tableName).append("` modify `").append(nc.getName()).append("`  ").append(getDbTypeStr(nc));
                } else {
                    sb.append(" alter table ").append(tableName).append(" modify ").append(nc.getName()).append(" ").append(getDbTypeStr(nc));
                }
                if (oc.isCanNull() != nc.isCanNull()) {
                    if (nc.isCanNull()) {
                        sb.append(" null ");
                    } else
                        sb.append(" not null ");
                }
            }
            rtn.add(sb.toString());
            sb = new StringBuilder();
            if (nc.getCnname() != null) {

                if (isMySql()) {
                    sb.append(" alter table ").append(tableName)
                            .append(" modify column ").append(nc.getName()).append(" " + getDbTypeStr(nc)).append("  comment  '").append(nc.getCnname()).append("'");
                } else {
                    sb.append("comment on column ").append(tableName).append(".").append(nc.getName()).append(" is '").append(nc.getCnname()).append("'");
                }


                rtn.add(sb.toString());
            }
        }

        return rtn;
    }

    /**
     * 创建缓存
     *
     * @param tableInfo
     * @throws SQLException
     */
    public void initTable(TableInfo tableInfo) throws DAOException {
        if (tableInfo != null) {
            TableInfo oldTable = this.loadTableInfoFromDb(tableInfo.getName());
            if (oldTable == null) {
                this.createTableByInfo(tableInfo);
            }
        }

    }

    /**
     * 修改表的注释 即中文名
     *
     * @param tabs
     * @throws SQLException
     */
    public void modifyTableCnname(List<TableInfo> tabs) throws DAOException {
        List<String> sqls = getModifyTableCnnameSql(tabs);

        executeBatch(sqls);
        for (TableInfo info : tabs) {
            reLoadTableInfo(info.getName());
        }

    }

    /**
     * 取修改表的注释的sql
     *
     * @param tabs
     * @return
     */
    private List<String> getModifyTableCnnameSql(List<TableInfo> tabs) throws DAOException {
        List<String> rtn = new ArrayList(tabs.size());
        for (int i = 0, c = tabs.size(); i < c; i++) {
            StringBuilder sb = new StringBuilder();
            TableInfo info = tabs.get(i);

            if (isMySql()) {
                sb.append(" alter table ").append(info.getName()).append(" comment '").append(info.getCnname()).append("'");
            } else {
                sb.append("comment on table ").append(info.getName()).append(" is '").append(info.getCnname()).append("'");

            }

            rtn.add(sb.toString());
            reLoadTableInfo(info.getName());
        }
        return rtn;
    }

    private void executeBatch(List<String> sqls) throws DAOException {
        if (getDbManager() == null) {
            throw new DAOException("数据库未连接");
        }
        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            Statement stmt = conn.createStatement();
            boolean flag = conn.getAutoCommit();
            conn.setAutoCommit(false);
            for (int i = 0, c = sqls.size(); i < c; i++) {
                stmt.addBatch(sqls.get(i));
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (dbManager != null) {
                dbManager.releaseConnection(conn);
            }

        }
        // System.out.println(sqls);


    }

    private boolean isMySql() {
        return (url.toLowerCase().indexOf("mysql") > -1);
    }

}


