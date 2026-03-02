/**
 * $RCSfile: SqlUtil.java,v $
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

import com.alibaba.fastjson.util.TypeUtils;
import net.ooder.common.database.metadata.ColInfo;
import net.ooder.common.database.metadata.TableInfo;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.CaselessStringKeyHashMap;
import net.ooder.common.util.IOUtility;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class SqlUtil {


    protected static Map resultSetGetMethods = new HashMap();

    protected static Map preparedStatementSetMethods = new HashMap();

    static String _nullAttributesForSearch = null;

    protected static Map<String, Integer> sqlTypes = new HashMap<String, Integer>();
    protected static Map<Integer, String> beanTypes = new HashMap<Integer, String>();
    public static final String GREAT_THAN = ">";
    public static final String GREAT_EQUALS_THAN = ">=";
    public static final String LESS_THAN = "<";
    public static final String LESS_EQUALS_THAN = "<=";
    public static final String EQUALS = "=";
    public static final String LIKE = " LIKE ";
    public static final String NOTEQALS = "<>";
    public static final String ISNULL = "IS NULL";
    public static final String ISNOTNULL = "IS NOT NULL";

    protected static final String STATUS_NEW = "NEW";
    protected static final String STATUS_DELETED = "DELETED";
    protected static final String STATUS_DIRTY = "DIRTY";
    protected static final String STATUS_ACCORDWITHDB = "ACCORDWITHDB";

    protected transient static final Log logger = LogFactory.getLog(
            "DAO", DBDAOUtil.class);

    static {
        try {

            sqlTypes.put("double", new Integer(Types.DOUBLE));
            sqlTypes.put("float", new Integer(Types.FLOAT));
            sqlTypes.put("int", new Integer(Types.INTEGER));
            sqlTypes.put("java.io.InputStream", new Integer(Types.BLOB));
            sqlTypes.put("java.io.Reader", new Integer(Types.CLOB));
            sqlTypes.put("java.lang.Double", new Integer(Types.DOUBLE));
            sqlTypes.put("java.lang.Float", new Integer(Types.FLOAT));
            sqlTypes.put("java.lang.Integer", new Integer(Types.INTEGER));
            sqlTypes.put("java.lang.Long", new Integer(Types.BIGINT));
            sqlTypes.put("java.lang.String", new Integer(Types.VARCHAR));
            sqlTypes.put("java.math.BigDecimal", new Integer(Types.DECIMAL));
            sqlTypes.put("java.math.BigInteger", new Integer(Types.BIGINT));
            sqlTypes.put("java.sql.Array", new Integer(Types.ARRAY));
            sqlTypes.put("java.sql.Blob", new Integer(Types.BLOB));
            sqlTypes.put("java.sql.Clob", new Integer(Types.CLOB));
            sqlTypes.put("java.sql.Date", new Integer(Types.DATE));
            sqlTypes.put("java.sql.Time", new Integer(Types.TIME));
            sqlTypes.put("java.sql.Timestamp", new Integer(Types.TIMESTAMP));
            sqlTypes.put("java.util.Date", new Integer(Types.TIMESTAMP));


            beanTypes.put(new Integer(Types.DOUBLE), "double");
            beanTypes.put(new Integer(Types.FLOAT), "float");
            beanTypes.put(new Integer(Types.CHAR), "java.lang.String");
            beanTypes.put(new Integer(Types.INTEGER), "int");
            beanTypes.put(new Integer(Types.BLOB), "java.io.InputStream");
            beanTypes.put(new Integer(Types.CLOB), "java.io.Reader");
            beanTypes.put(new Integer(Types.LONGVARCHAR), "java.io.Reader");
            beanTypes.put(new Integer(Types.LONGNVARCHAR), "java.io.Reader");
            beanTypes.put(new Integer(Types.LONGNVARCHAR), "java.io.Reader");
            beanTypes.put(new Integer(Types.DOUBLE), "java.lang.Double");
            beanTypes.put(new Integer(Types.FLOAT), "java.lang.Float");
            beanTypes.put(new Integer(Types.INTEGER), "java.lang.Integer");
            beanTypes.put(new Integer(Types.BIGINT), "java.lang.Long");
            beanTypes.put(new Integer(Types.VARCHAR), "java.lang.String");
            beanTypes.put(new Integer(Types.DECIMAL), "java.math.BigDecimal");
            beanTypes.put(new Integer(Types.BIGINT), "java.math.BigInteger");
            beanTypes.put(new Integer(Types.ARRAY), "java.sql.Array");
            beanTypes.put(new Integer(Types.BLOB), "java.sql.Blob");
            beanTypes.put(new Integer(Types.CLOB), "java.sql.Clob");
            beanTypes.put(new Integer(Types.DATE), "java.sql.Date");
            beanTypes.put(new Integer(Types.TIME), "java.sql.Time");
            //     beanTypes.put(new Integer(Types.TIMESTAMP), "java.sql.Timestamp");
            beanTypes.put(new Integer(Types.TIMESTAMP), "java.util.Date");


            // Get method
            Class[] clazzz = new Class[1];
            clazzz[0] = int.class;

            resultSetGetMethods.put("boolean", ResultSet.class.getMethod("getBoolean", clazzz));
            resultSetGetMethods.put("byte", ResultSet.class.getMethod("getByte", clazzz));
            resultSetGetMethods.put("double", ResultSet.class.getMethod("getDouble", clazzz));
            resultSetGetMethods.put("float", ResultSet.class.getMethod("getFloat", clazzz));
            resultSetGetMethods.put("long", ResultSet.class.getMethod("getLong", clazzz));
            resultSetGetMethods.put("short", ResultSet.class.getMethod("getShort", clazzz));
            resultSetGetMethods.put("int", ResultSet.class.getMethod("getInt", clazzz));
            resultSetGetMethods.put("java.io.InputStream",
                    ResultSet.class.getMethod("getBinaryStream", clazzz));
            resultSetGetMethods.put("java.io.Reader",
                    ResultSet.class.getMethod("getCharacterStream", clazzz));
            resultSetGetMethods.put("java.lang.Byte", ResultSet.class.getMethod("getByte", clazzz));
            resultSetGetMethods.put("java.lang.Byte[]",
                    ResultSet.class.getMethod("getBytes", clazzz));
            resultSetGetMethods.put("java.lang.Double",
                    ResultSet.class.getMethod("getDouble", clazzz));
            resultSetGetMethods.put("java.lang.Float", ResultSet.class.getMethod("getFloat", clazzz));
            resultSetGetMethods.put("java.lang.Integer", ResultSet.class.getMethod("getInt", clazzz));
            resultSetGetMethods.put("java.lang.Long", ResultSet.class.getMethod("getLong", clazzz));
            resultSetGetMethods.put("java.lang.Object",
                    ResultSet.class.getMethod("getObject", clazzz));
            resultSetGetMethods.put("java.lang.Short", ResultSet.class.getMethod("getShort", clazzz));
            resultSetGetMethods.put("java.lang.String",
                    ResultSet.class.getMethod("getString", clazzz));
            resultSetGetMethods.put("java.math.BigDecimal",
                    ResultSet.class.getMethod("getBigDecimal", clazzz));
            resultSetGetMethods.put("java.sql.Array", ResultSet.class.getMethod("getArray", clazzz));
            resultSetGetMethods.put("java.sql.Blob", ResultSet.class.getMethod("getBlob", clazzz));
            resultSetGetMethods.put("java.sql.Clob", ResultSet.class.getMethod("getClob", clazzz));
            resultSetGetMethods.put("java.sql.Date", ResultSet.class.getMethod("getDate", clazzz));
            resultSetGetMethods.put("java.sql.Time", ResultSet.class.getMethod("getTime", clazzz));
            resultSetGetMethods.put("java.sql.Timestamp",
                    ResultSet.class.getMethod("getTimestamp", clazzz));
            resultSetGetMethods.put("java.util.Date",
                    ResultSet.class.getMethod("getTimestamp", clazzz));

            // PreparedStatement 			resultSetGetMethods.put("java.sql.Timestamp", ResultSet.class.getMethod("getTimestamp", clazzz));Set method
            clazzz = new Class[2];
            clazzz[0] = int.class;
            clazzz[1] = Array.class;

            preparedStatementSetMethods.put("java.sql.Array",
                    PreparedStatement.class.
                            getMethod("setArray", clazzz));

            // clazzz[1] = java.io.InputStream.class;
            // preparedStatementSetMethods.put("java.io.InputStream", PreparedStatement.class.getMethod("setBinaryStream", clazzz));
            clazzz[1] = java.math.BigDecimal.class;

            preparedStatementSetMethods.put("java.math.BigDecimal",
                    PreparedStatement.class.
                            getMethod("setBigDecimal", clazzz));

            clazzz[1] = Blob.class;

            preparedStatementSetMethods.put("java.sql.Blob",
                    PreparedStatement.class.getMethod("setBlob", clazzz));

            clazzz[1] = boolean.class;

            preparedStatementSetMethods.put("boolean",
                    PreparedStatement.class.getMethod("setBoolean", clazzz));
            preparedStatementSetMethods.put("java.lang.Boolean",
                    PreparedStatement.class.getMethod("setBoolean", clazzz));

            clazzz[1] = byte.class;

            preparedStatementSetMethods.put("byte",
                    PreparedStatement.class.getMethod("setByte", clazzz));
            preparedStatementSetMethods.put("java.lang.Byte",
                    PreparedStatement.class.getMethod("setByte", clazzz));

            clazzz[1] = byte[].class;

            preparedStatementSetMethods.put("byte[]",
                    PreparedStatement.class.getMethod("setBytes", clazzz));
            preparedStatementSetMethods.put("java.lang.Byte[]",
                    PreparedStatement.class.getMethod("setBytes", clazzz));

            // clazzz[1] = java.io.Reader.class;
            // preparedStatementSetMethods.put("java.io.Reader", PreparedStatement.class.getMethod("setCharacterStream", clazzz));
            clazzz[1] = Clob.class;

            preparedStatementSetMethods.put("java.sql.Clob",
                    PreparedStatement.class.getMethod("setClob", clazzz));

            clazzz[1] = java.sql.Date.class;

            preparedStatementSetMethods.put("java.sql.Date",
                    PreparedStatement.class.getMethod("setDate", clazzz));

            clazzz[1] = double.class;

            preparedStatementSetMethods.put("double",
                    PreparedStatement.class.getMethod("setDouble", clazzz));
            preparedStatementSetMethods.put("java.lang.Double",
                    PreparedStatement.class.getMethod("setDouble", clazzz));

            clazzz[1] = float.class;

            preparedStatementSetMethods.put("float",
                    PreparedStatement.class.getMethod("setFloat", clazzz));
            preparedStatementSetMethods.put("java.lang.Float",
                    PreparedStatement.class.getMethod("setFloat", clazzz));

            clazzz[1] = int.class;

            preparedStatementSetMethods.put("int",
                    PreparedStatement.class.getMethod("setInt", clazzz));
            preparedStatementSetMethods.put("java.lang.Integer",
                    PreparedStatement.class.getMethod("setInt", clazzz));

            clazzz[1] = long.class;

            preparedStatementSetMethods.put("long",
                    PreparedStatement.class.getMethod("setLong", clazzz));
            preparedStatementSetMethods.put("java.lang.Long",
                    PreparedStatement.class.getMethod("setLong", clazzz));

            clazzz[1] = Object.class;

            preparedStatementSetMethods.put("java.lang.Object",
                    PreparedStatement.class.getMethod("setObject", clazzz));

            clazzz[1] = short.class;

            preparedStatementSetMethods.put("short",
                    PreparedStatement.class.getMethod("setShort", clazzz));
            preparedStatementSetMethods.put("java.lang.Short",
                    PreparedStatement.class.getMethod("setShort", clazzz));

            clazzz[1] = String.class;

            preparedStatementSetMethods.put("java.lang.String",
                    PreparedStatement.class.getMethod("setString", clazzz));

            clazzz[1] = Time.class;

            preparedStatementSetMethods.put("java.sql.Time",
                    PreparedStatement.class.getMethod("setTime", clazzz));

            clazzz[1] = Timestamp.class;

            preparedStatementSetMethods.put("java.sql.Timestamp",
                    PreparedStatement.class.getMethod("setTimestamp",
                            clazzz));
            clazzz[1] = Timestamp.class;
            preparedStatementSetMethods.put("java.util.Date",
                    PreparedStatement.class.getMethod("setTimestamp",
                            clazzz));
            clazzz = new Class[3];

            clazzz[0] = int.class;
            clazzz[2] = int.class;

            clazzz[1] = InputStream.class;
            preparedStatementSetMethods.put("java.io.InputStream",
                    PreparedStatement.class.
                            getMethod("setBinaryStream", clazzz));

            clazzz[1] = Reader.class;
            preparedStatementSetMethods.put("java.io.Reader",
                    PreparedStatement.class.getMethod("setCharacterStream",
                            clazzz));

        } catch (NoSuchMethodException e) {
            // Do nothing
        }
    }

    public static String getWhereSqlbyIds(String[] uuids, String pkName) {
        StringBuffer where = new StringBuffer("where '" + pkName + "' in(");
        for (String uuid : uuids) {
            where.append("'" + uuid + "',");
        }
        if (where.toString().endsWith(",")) {
            where.deleteCharAt(where.length() - 1);
        }
        where.append(")");
        return where.toString();

    }

    public static String getFieldArrStr(TableInfo tableInfo) {
        StringBuffer sql = new StringBuffer();
        List<ColInfo> colInfos = tableInfo.getColList();
        for (ColInfo colInfo : colInfos) {
            sql.append("`")
                    .append(colInfo.getName())
                    .append("`,");
        }
        if (sql.toString().endsWith(",")) {
            sql.deleteCharAt(sql.length() - 1);
        }
        return sql.toString();
    }


    public static String getInsertSQLNoParas(TableInfo tableInfo) {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(tableInfo.getName());
        sql.append(" (");
        List<ColInfo> colInfos = tableInfo.getColList();

        Set<String> _allKeys = tableInfo.getPkNames();

        if (_allKeys.size() == 0) {
            sql.append("`");
            sql.append(tableInfo.getPkName());
            sql.append("`")
                    .append(",");
        } else {
            for (String key : _allKeys) {
                sql.append("`" + key + "`").append(",");
            }

        }


        //sql.append((String) tableInfo.getPkName()).append(",");

        for (ColInfo colInfo : colInfos) {
            if (!_allKeys.contains(colInfo.getName())) {
                sql.append("`")
                        .append(colInfo.getName())
                        .append("`,");
            }

        }
        if (sql.toString().endsWith(",")) {
            sql.deleteCharAt(sql.length() - 1);
        }
        sql.append(") values (");

        for (int i = 0; i < colInfos.size(); i++) {
            sql.append("?,");
        }
        String retSQL = sql.toString();
        if (retSQL.endsWith(",")) {
            retSQL = retSQL.substring(0, retSQL.length() - 1);
        }
        retSQL = retSQL + ")";

        return retSQL;
    }


    /**
     * Method declaration
     *
     * @return
     */
    public static String getDeleteSQLByWhereClause(TableInfo tableInfo, String custWhereClause) throws DAOException {

        Set<String> _allKeys = tableInfo.getPkNames();
        if (_allKeys == null && _allKeys.size() == 0) {
            throw new DAOException("Cannot delete, the dao does not have any primary keys!");
        }
        StringBuffer sql = new StringBuffer();
        sql.append("delete from ").append(tableInfo.getName());
        sql.append(" where ");
        sql.append(custWhereClause);
        return sql.toString();
    }

    /**
     * Method declaration
     *
     * @return
     */
    public static String getDeleteSQLByKey(TableInfo tableInfo) throws DAOException {

        String key = tableInfo.getPkName();
        StringBuffer sql = new StringBuffer();
        sql.append("delete from ").append(tableInfo.getName());
        sql.append(" where " + key + "=?");
        return sql.toString();
    }


    public static String getUpdateSQLNoParasBykey(TableInfo tableInfo) {
        StringBuffer sql = new StringBuffer();
        sql.append("update  ").append(tableInfo.getName()).append(" set ");
        List<ColInfo> colInfos = tableInfo.getColList();
        Set<String> _allKeys = tableInfo.getPkNames();

        for (ColInfo colInfo : colInfos) {
            sql.append(" ").append("`" + colInfo.getFieldname() + "`").append("=?,");
        }

        sql.setCharAt(sql.length() - 1, ' ');
        sql.append(" where (");


        for (String key : _allKeys) {
            sql.append("`" + key + "`").append("=? and ");
        }


        String retSQL = sql.toString();
        if (retSQL.endsWith(" and ")) {
            retSQL = retSQL.substring(0, retSQL.length() - 5);
        }

        retSQL = retSQL + ")";

        return retSQL;
    }


    public static String getUpdateSQLNoParas(TableInfo tableInfo, Map<String, Object> updateMap, String custWhereClause) {
        StringBuffer sql = new StringBuffer();
        sql.append("update  ").append(tableInfo.getName()).append(" set ");
        StringBuffer sets = new StringBuffer();

        Set<String> props = updateMap.keySet();
        for (String fileName : props) {
            ColInfo colInfo = tableInfo.getCoInfoByName(fileName);
            if (colInfo != null) {
                sets.append(" ").append("`" + colInfo.getName() + "`").append("=?,");
            }

        }

        if (sets.length() <= 0) {
            return "";
        }
        if (sets.charAt(sets.length() - 1) == ',') {
            sets.setLength(sets.length() - 1);
        }
        sql.append(sets.toString()).append(" ");
        StringBuffer whereClause = new StringBuffer();

        if (custWhereClause == null || custWhereClause.equals("")) {
            Set<String> _allKeys = tableInfo.getPkNames();
            for (String key : _allKeys) {
                whereClause.append("`" + key + "`").append("=? and ");
            }
            if (whereClause.length() > 0) {
                whereClause.setLength(whereClause.length() - 5);
            }
        } else {
            if (custWhereClause != null && custWhereClause.trim().length() > 0) {
                if (whereClause.length() > 0) {
                    if (whereClause.charAt(whereClause.length() - 1) != ' ') {
                        whereClause.append(' ');
                    }
                    whereClause.append("and ");
                }
                whereClause.append('(').append(custWhereClause).append(')').append(' ');
            }
        }
        if (whereClause.length() > 0) {
            whereClause.insert(0, " where ");
        }
        sql.append(whereClause.toString());

        String retSQL = sql.toString();

        return retSQL;
    }


    public static String getBatchUpdateSQLNoParas(TableInfo tableInfo, String custWhereClause) {
        StringBuffer sql = new StringBuffer();
        sql.append("update  ").append(tableInfo.getName()).append(" set ");


        List<ColInfo> colInfos = tableInfo.getColList();
        Set<String> _allKeys = tableInfo.getPkNames();

        for (ColInfo colInfo : colInfos) {
            sql.append("  `").append(colInfo.getFieldname()).append("`=?,");
        }


        sql.setCharAt(sql.length() - 1, ' ');
        // sql.append(" where (");

        String where = getFullWhereClauseNoPara(tableInfo, custWhereClause);
        if (where != null) {
            sql.append(" where (").append(where).append(")");

        }
        return sql.toString();
    }

    public static String getFullSearchSql(TableInfo tableInfo, String custWhereClause) {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT ").append(SqlUtil.getFieldArrStr(tableInfo));
        sql.append(" FROM ").append(tableInfo.getName());
        String whereClause = SqlUtil.getFullWhereClauseNoPara(tableInfo, custWhereClause);
        if (whereClause != null) {
            sql.append(" WHERE ").append(whereClause);
        }

        String sSql = sql.toString();
        return sSql;
    }

    public static void preparePrimaryKeys(TableInfo tableInfo, PreparedStatement ps, int paraBeginPosition, Object value) throws
            DAOException {
        try {
            //  int counter = paraBeginPosition;
            String key = tableInfo.getPkName();
            ColInfo colInfo = tableInfo.getCoInfoByName(key);
            Integer sqlType = colInfo.getDataType();

            String beanType = beanTypes.get(sqlType);
            Method m = (Method) preparedStatementSetMethods.get(beanType);

            if (m == null) { // no such method
                ps.setObject(paraBeginPosition, value);
            } else {
                Object[] objs = new Object[2];
                objs[0] = new Integer(paraBeginPosition);
                objs[1] = value;
                try {
                    m.invoke(ps, objs);
                } catch (Exception e) {
                    throw new DAOException("Unable set value,field is '" + colInfo.getName() +
                            "',prop is '" + colInfo.getFieldname() + "'.msg:" + e.getMessage());
                }
            }
            /* for each for */
        } catch (Exception e) {
            throw new DAOException("Create failed!msg: " + e.getMessage(), e);
        }
    }


    public static String getFullWhereClauseNoPara(TableInfo tableInfo, String custWhereClause) {
        StringBuffer clause = new StringBuffer();
//        if (_relations != null) {
//            clause.append(getRelationClause());
//        }


        if (custWhereClause != null && custWhereClause.length() > 0) {

            if (custWhereClause.toUpperCase().indexOf("ORDER BY") > -1) {
                clause.append(custWhereClause);
            } else {
                if (clause.length() != 0) {
                    clause.append(" and ");
                }
                clause.append('(').append(custWhereClause).append(") ");
            }

        } else {
            StringBuffer nullAttributes = null;
            if (_nullAttributesForSearch != null) {
                nullAttributes = new StringBuffer(_nullAttributesForSearch.replace(',', ' '));

            }
        }

        if (clause.length() > 0) {
            return clause.toString();
        } else {
            return null;
        }
    }

    public static int prepareFields(TableInfo tableInfo, PreparedStatement ps, Map<String, Object> updateMap) throws
            DAOException {
        try {
            CaselessStringKeyHashMap caselessStringKeyHashMap = new CaselessStringKeyHashMap();
            caselessStringKeyHashMap.putAll(updateMap);
            int counter = 1;
            List<ColInfo> colInfos = tableInfo.getColList();
            Set<String> _uuidFields = tableInfo.getPkNames();
            Set<String> props = updateMap.keySet();
            for (String name : props) {
                ColInfo colInfo = tableInfo.getCoInfoByName(name);
                if (colInfo == null) {
                    continue;
                }
                String fieldName = colInfo.getFieldname();
                Object value = caselessStringKeyHashMap.get(fieldName);
                Integer sqlType = colInfo.getDataType();
                String beanType = beanTypes.get(sqlType);

                if (value == null || beanType == null || (value.equals("") && !beanType.equals("java.lang.String"))) {
                    if (sqlType != null && colInfo.isCanNull()) {
                        ps.setNull(counter++, sqlType.intValue());
                        continue;
                    }
                }
                Method m = (Method) preparedStatementSetMethods.get(beanType);
                if (m == null) { // no such method
                    ps.setObject(counter, value);
                } else if (beanType.equals("java.sql.Blob")) {
                    Object[] objs = new Object[2];
                    objs[0] = new Integer(counter);
                    BlobImpl ins = (BlobImpl) value;
                    objs[1] = ins;
                    try {
                        m.invoke(ps, objs);
                    } catch (Throwable th) {
                        throw new DAOException("Unable set value,field is '" + fieldName +
                                "',prop is '" + fieldName + "'.msg:" + th.getMessage(), th);
                    }


                } else if (beanType.equals("java.io.InputStream")) {
                    Object[] objs = new Object[3];
                    objs[0] = new Integer(counter);
                    InputStream ins = null;
                    if (value instanceof InputStream) {
                        ins = (InputStream) value;
                    } else {
                        ins = new ByteArrayInputStream(IOUtility.toByteArray(value.toString()));
                    }
                    objs[1] = ins;
                    objs[2] = new Integer(ins.available());
                    try {
                        m.invoke(ps, objs);
                    } catch (Throwable th) {
                        throw new DAOException("Unable set value,field is '" + fieldName +
                                "',prop is '" + fieldName + "'.msg:" + th.getMessage(), th);
                    }
                } else if (beanType.equals("java.util.Date")) {

                    Object[] objs = new Object[2];
                    objs[0] = new Integer(counter);
                    if (value != null) {
                        value = new Timestamp(((Date) value).getTime());
                    } else if (!colInfo.isCanNull()) {
                        value = new Timestamp(System.currentTimeMillis());
                    }
                    objs[1] = value;
                    try {
                        m.invoke(ps, objs);
                    } catch (Throwable th) {
                        throw new DAOException("Unable set value,field is '" + fieldName +
                                "',prop is '" + fieldName + "'.msg:" + th.getMessage(), th);
                    }
                } else if (beanType.equals("java.io.Reader")) {
                    Object[] objs = new Object[3];
                    objs[0] = new Integer(counter);

                    if (value instanceof Reader) {
                        Reader reader = (Reader) value;
                        int length = -1;
                        if (reader.markSupported()) {
                            reader.mark(Integer.MAX_VALUE);
                            length = (int) reader.skip(Long.MAX_VALUE);
                            reader.reset();
                        } else {
                            StringWriter out = new StringWriter();
                            int len;
                            for (char[] buf = new char[1024]; (len = reader.read(buf)) > 0;
                                 out.write(buf, 0, len)) {
                                ;
                            }
                            out.close();
                            reader.close();
                            String data = out.toString();
                            length = data.length();
                            reader = new StringReader(data);
                        }
                        objs[1] = reader;
                        objs[2] = new Integer(length);
                    } else {
                        objs[1] = new StringReader(value.toString());
                        objs[2] = new Integer(value.toString().length());
                    }


                    try {
                        m.invoke(ps, objs);
                    } catch (Throwable th) {
                        throw new DAOException("Unable set value,field is '" + fieldName +
                                "',prop is '" + fieldName + "'.msg:" + th.getMessage(), th);
                    }
                } else {
                    Object[] objs = new Object[2];
                    objs[0] = new Integer(counter);
                    objs[1] = TypeUtils.castToJavaBean(value, m.getParameterTypes()[1]);
                    try {
                        m.invoke(ps, objs);
                    } catch (Exception e) {
                        e.fillInStackTrace();

                        throw new DAOException("Unable set value,field is '" + fieldName +
                                "',prop is '" + fieldName + "'.msg:" + e.getMessage(), e);
                    }
                }

                counter++;
                //  }
                /* for each field */
            }

            return counter;
        } catch (Exception e) {
            e.printStackTrace();
            e.fillInStackTrace();


            throw new DAOException(e);
        }
    }


    public static int prepareAllFields(TableInfo tableInfo, PreparedStatement ps, String uuid, Map updateMap) throws
            DAOException {
        try {
            int counter = 1;
            CaselessStringKeyHashMap caselessStringKeyHashMap = new CaselessStringKeyHashMap();
            caselessStringKeyHashMap.putAll(updateMap);
            List<ColInfo> colInfos = tableInfo.getColList();
            Set<String> _uuidFields = tableInfo.getPkNames();
            ps.setString(counter, uuid);
            counter = counter + 1;
            for (ColInfo colInfo : colInfos) {
                String fieldName = colInfo.getName();

                if (!_uuidFields.contains(fieldName)) {
                    Object value = caselessStringKeyHashMap.get(fieldName);
                    Integer sqlType = colInfo.getDataType();
                    String beanType = beanTypes.get(sqlType);

                    if (value == null && !colInfo.isCanNull()) {
                        if (beanType.equals("java.util.Date")) {
                            value = new Date();
                        } else if (beanType.equals("java.lang.String")) {
                            value = "";
                        } else if (beanType.equals("java.lang.Double")) {
                            value = Double.valueOf(0);
                        } else if (beanType.equals("java.lang.Float")) {
                            value = Float.valueOf(0);
                        } else if (beanType.equals("java.lang.Integer")) {
                            value = Integer.valueOf(0);
                        }

                    }


                    if (value == null || (value.equals("") && !beanType.equals("java.lang.String"))) {
                        if (sqlType != null) {
                            ps.setNull(counter++, sqlType.intValue());
                            continue;
                        }
                    }


                    Method m = (Method) preparedStatementSetMethods.get(beanType);
                    if (m == null) { // no such method
                        ps.setObject(counter, value);
                    } else if (beanType.equals("java.sql.Blob")) {
                        Object[] objs = new Object[2];
                        objs[0] = new Integer(counter);
                        BlobImpl ins = (BlobImpl) value;
                        objs[1] = ins;
                        try {
                            m.invoke(ps, objs);
                        } catch (Throwable th) {
                            throw new DAOException("Unable set value,field is '" + fieldName +
                                    "',prop is '" + fieldName + "'.msg:" + th.getMessage(), th);
                        }


                    } else if (beanType.equals("java.io.InputStream")) {
                        Object[] objs = new Object[3];
                        objs[0] = new Integer(counter);
                        InputStream ins = null;
                        if (value instanceof InputStream) {
                            ins = (InputStream) value;
                        } else {
                            ins = new ByteArrayInputStream(IOUtility.toByteArray(value.toString()));
                        }
                        objs[1] = ins;
                        objs[2] = new Integer(ins.available());
                        try {
                            m.invoke(ps, objs);
                        } catch (Throwable th) {
                            throw new DAOException("Unable set value,field is '" + fieldName +
                                    "',prop is '" + fieldName + "'.msg:" + th.getMessage(), th);
                        }
                    } else if (beanType.equals("java.util.Date")) {

                        Object[] objs = new Object[2];
                        objs[0] = new Integer(counter);
                        if (value != null) {
                            value = new Timestamp(((Date) value).getTime());
                        }
                        objs[1] = value;
                        try {
                            m.invoke(ps, objs);
                        } catch (Throwable th) {
                            throw new DAOException("Unable set value,field is '" + fieldName +
                                    "',prop is '" + fieldName + "'.msg:" + th.getMessage(), th);
                        }
                    } else if (beanType.equals("java.io.Reader")) {
                        Object[] objs = new Object[3];
                        objs[0] = new Integer(counter);

                        if (value instanceof Reader) {
                            Reader reader = (Reader) value;
                            int length = -1;
                            if (reader.markSupported()) {
                                reader.mark(Integer.MAX_VALUE);
                                length = (int) reader.skip(Long.MAX_VALUE);
                                reader.reset();
                            } else {
                                StringWriter out = new StringWriter();
                                int len;
                                for (char[] buf = new char[1024]; (len = reader.read(buf)) > 0;
                                     out.write(buf, 0, len)) {
                                    ;
                                }
                                out.close();
                                reader.close();
                                String data = out.toString();
                                length = data.length();
                                reader = new StringReader(data);
                            }
                            objs[1] = reader;
                            objs[2] = new Integer(length);
                        } else {
                            objs[1] = new StringReader(value.toString());
                            objs[2] = new Integer(value.toString().length());
                        }


                        try {
                            m.invoke(ps, objs);
                        } catch (Throwable th) {
                            throw new DAOException("Unable set value,field is '" + fieldName +
                                    "',prop is '" + fieldName + "'.msg:" + th.getMessage(), th);
                        }
                    } else {
                        Object[] objs = new Object[2];
                        objs[0] = new Integer(counter);
                        objs[1] = TypeUtils.castToJavaBean(value, m.getParameterTypes()[1]);
                        try {
                            m.invoke(ps, objs);
                        } catch (Exception e) {
                            e.fillInStackTrace();

                            throw new DAOException("Unable set value,field is '" + fieldName +
                                    "',prop is '" + fieldName + "'.msg:" + e.getMessage(), e);
                        }
                    }
                    counter++;

                }

                /* for each field */
            }

            return counter;
        } catch (Exception e) {
            e.printStackTrace();
            e.fillInStackTrace();


            throw new DAOException(e);
        }
    }

    public static String getFindByPrimaryKeySQLNoParas(TableInfo tableInfo) {
        StringBuffer sql = new StringBuffer("select ");
        List<ColInfo> colInfos = tableInfo.getColList();
        Set<String> _uuidFields = tableInfo.getPkNames();
        for (ColInfo colInfo : colInfos) {
            sql.append("`" + colInfo.getName() + "`").append(",");
        }

        if (sql.toString().endsWith(",")) {
            sql.deleteCharAt(sql.length() - 1);
        }


        sql.append(" FROM ");
        sql.append(tableInfo.getName());
        sql.append(" where (");

        Set<String> _allKeys = tableInfo.getPkNames();
        for (String key : _allKeys) {
            sql.append("`" + key + "`").append("=? and ");
        }

        String retSQL = sql.toString();
        if (retSQL.endsWith(" and ")) {
            retSQL = retSQL.substring(0, retSQL.length() - 5);
        }

        retSQL = retSQL + ")";

        return retSQL;
    }

    public static String getSerachSqlNoPara(TableInfo tableInfo, Set<String> keyList, String typeName, String custWhereClause, Map<String, Object> searchMap) {
        StringBuffer clause = new StringBuffer();
//        if (_relations != null) {
//            clause.append(getRelationClause());
//        }

        CaselessStringKeyHashMap caselessStringKeyHashMap = new CaselessStringKeyHashMap();
        caselessStringKeyHashMap.putAll(searchMap);
        if (custWhereClause != null && custWhereClause.length() > 0) {
            if (clause.length() != 0) {
                clause.append(" and ");
            }

            clause.append('(').append(custWhereClause).append(") ");
        } else {
            StringBuffer nullAttributes = null;
            if (_nullAttributesForSearch != null) {
                nullAttributes = new StringBuffer(_nullAttributesForSearch.replace(',', ' '));

            }
            List<ColInfo> colInfos = tableInfo.getColList();
            Set<String> _uuidFields = tableInfo.getPkNames();
            for (ColInfo colInfo : colInfos) {
                String fieldName = (String) colInfo.getName();
                String propName = (String) colInfo.getFieldname();

                if (!_uuidFields.contains(fieldName)) {

                    Method m = null;
                    Object value = caselessStringKeyHashMap.get(propName);
                    if (value != null && !value.equals("*")) {
                        // clause.append(fieldName).append("=? ");
                        if (m.getReturnType().getName().equals("java.lang.String")) {
                            if (!((String) value).trim().equals("")) {
                                if (clause.length() > 0) {
                                    clause.append(" and ");
                                }
                                clause.append("`" + fieldName + "`").append(" like '%").append(((String) value).trim()).append("%' ");
                            }


                        } else if (m.getReturnType().getName().equals("java.lang.Integer")) {
                            String intValue = ((Integer) value).toString();
                            if (!intValue.trim().equals("")) {
                                if (clause.length() > 0) {
                                    clause.append(" and ");
                                }
                                clause.append("`" + fieldName + "`").append(" like '%").append(intValue.trim()).append("%' ");
                            }
                        } else if (!m.getReturnType().getName().equals("java.util.Date")) {
                            if (clause.length() > 0) {
                                clause.append(" and ");
                            }
                            clause.append("`" + fieldName + "`").append("=? ");
                        }
                    } else if (nullAttributes != null &&
                            nullAttributes.append(" ").toString().indexOf(propName + " ") >= 0) {
                        if (clause.length() > 0) {
                            clause.append(" and ").append("`" + fieldName + "`").append(" is null ");
                        }
                    }
                }

            }
        }


        if (keyList != null) {

            List<String> strList = new ArrayList<String>();
            strList.addAll(keyList);
            StringBuffer subWhere = new StringBuffer();
            if (keyList.size() > 0) {

                if (keyList.size() > 500) {
                    for (int f = 0; f < keyList.size() / 500; f++) {
                        List subList = strList.subList(f * 500, (f + 1) * 500 > keyList.size() ? keyList.size() : (f + 1) * 500);
                        if (f + 1 < keyList.size() / 500) {
                            for (int k = 0; k < subList.size(); k++) {
                                if (k < subList.size() - 1) {
                                    subWhere.append("'" + (String) subList.get(k).toString().trim() + "'" + ",");
                                } else {
                                    subWhere.append("'" + (String) subList.get(k).toString().trim() + "') or " + typeName + " in (");
                                }
                            }
                        } else {
                            for (int k = 0; k < subList.size(); k++) {
                                if (k < subList.size() - 1) {
                                    subWhere.append("'" + (String) subList.get(k).toString().trim() + "'" + ",");
                                } else {
                                    subWhere.append("'" + (String) subList.get(k).toString().trim() + "')");
                                }
                            }
                        }
                    }
                } else {
                    for (int k = 0; k < strList.size(); k++) {
                        if (k < strList.size() - 1) {
                            subWhere.append("'" + (String) strList.get(k).toString().trim() + "'" + ",");
                        } else {
                            subWhere.append("'" + (String) strList.get(k).toString().trim() + "' ) ");
                        }
                    }
                }
            } else {

                subWhere.append("'' ) ");
            }
            if (clause != null && clause.length() > 0) {
                clause.append(" and " + typeName + " in (" + subWhere.toString() + "");
            } else {
                clause.append(" " + typeName + " in (" + subWhere.toString() + "");
            }


        }

        if (clause.length() > 0) {
            return clause.toString();
        } else {
            return null;
        }
    }

    public static CaselessStringKeyHashMap<String, Object> fetchResultSet(TableInfo tableInfo, ResultSet result) {
        CaselessStringKeyHashMap valuemap = new CaselessStringKeyHashMap();
        int i = 1;
        Object oneFieldValue = null;
        String oneFieldName = null;
        List<ColInfo> colInfos = tableInfo.getColList();
        for (ColInfo colInfo : colInfos) {
            oneFieldName = colInfo.getName();
            String propName = colInfo.getFieldname();
            String type = colInfo.getColType().name();
            if (type.equalsIgnoreCase("[B")) {
                type = "byte[]";
            }
            Method m = (Method) SqlUtil.resultSetGetMethods.get(type);
            if (m == null) {
                try {
                    oneFieldValue = result.getObject(i);
                } catch (SQLException ignore) {
                }
            } else {
                try {
                    Object[] objs = new Object[1];
                    objs[0] = new Integer(i);
                    oneFieldValue = m.invoke(result, objs);
                    if (result.wasNull()) {
                        oneFieldValue = null;
                    }
                    if (type.equals("java.sql.Blob") && oneFieldValue != null) {
                        oneFieldValue = new BlobImpl(((Blob) oneFieldValue).
                                getBinaryStream());
                    }
                    if (type.equals("java.util.Date") && oneFieldValue != null) {
                        oneFieldValue = new Date(((Timestamp) oneFieldValue).getTime());
                    }


                    if (type.equals("java.sql.Clob") && oneFieldValue != null) {
                        oneFieldValue = new ClobImpl(((Clob) oneFieldValue).
                                getCharacterStream());
                    }
                    if (type.equals("java.io.InputStream") && oneFieldValue != null) {

                        if (!(oneFieldValue instanceof InputStream)) {
                            oneFieldValue = new ByteArrayInputStream(IOUtility.toByteArray(oneFieldValue.toString()));
                        } else {
                            InputStream ins = (InputStream) oneFieldValue;
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            int len;
                            for (byte[] buf = new byte[1024]; (len = ins.read(buf)) > 0;
                                 bos.write(buf, 0, len)) {
                                ;
                            }
                            bos.close();
                            oneFieldValue = new ByteArrayInputStream(bos.toByteArray());
                        }
                    }
                    if (type.equals("java.io.Reader") && oneFieldValue != null) {

                        if (!(oneFieldValue instanceof Reader)) {
                            oneFieldValue = new StringReader(oneFieldValue.toString());
                        } else {
                            Reader reader = (Reader) oneFieldValue;
                            StringWriter writer = new StringWriter();
                            int len;
                            for (char[] buf = new char[1024]; (len = reader.read(buf)) > 0;
                                 writer.write(buf, 0, len)) {
                            }
                            writer.close();
                            oneFieldValue = new StringReader(writer.toString());
                        }


                    }
                } catch (Exception e) {
                    logger.error("Unable get value,field:" + oneFieldName + ".msg:" + e.getMessage());
                    oneFieldValue = null;
                }
            }
            valuemap.put(propName, oneFieldValue);
            i++;
        }


        return valuemap;
    }


    public static class BlobImpl implements Blob {
        private byte[] bytes;


        public BlobImpl(byte[] bytes) {
            this.bytes = bytes;
        }

        public BlobImpl(InputStream ins) throws IOException {
            byte[] buf = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024 * 10);
            for (int readLen = -1; (readLen = ins.read(buf)) > 0; ) {
                bos.write(buf, 0, readLen);
            }
            bos.flush();
            bos.close();
            ins.close();
            bytes = bos.toByteArray();
        }

        public long length() throws SQLException {
            return bytes.length;
        }

        public byte[] getBytes(long pos, int length) throws SQLException {
            byte[] ret = new byte[length];
            System.arraycopy(bytes, (int) pos, ret, 0, length);
            return ret;
        }

        public InputStream getBinaryStream() throws SQLException {
            return new ByteArrayInputStream(bytes);
        }

        public long position(byte[] pattern, long parm2) throws SQLException {
            /**@todo Implement this java.sql.Blob method*/
            throw new UnsupportedOperationException("Method position() not yet implemented.");
        }

        public long position(Blob parm1, long parm2) throws SQLException {
            /**@todo Implement this java.sql.Blob method*/
            throw new UnsupportedOperationException("Method position() not yet implemented.");
        }

        public int setBytes(long parm1, byte[] parm2, int parm3, int parm4) throws SQLException {
            /**@todo Implement this java.sql.Blob abstract method*/
            throw new UnsupportedOperationException("Method setBytes() not yet implemented.");
        }

        public int setBytes(long parm1, byte[] parm2) throws SQLException {
            /**@todo Implement this java.sql.Blob abstract method*/
            throw new UnsupportedOperationException("Method setBytes() not yet implemented.");
        }

        public OutputStream setBinaryStream(long pos) throws SQLException {
            /**@todo Implement this java.sql.Blob abstract method*/
            throw new UnsupportedOperationException("Method setBinaryStream() not yet implemented.");
        }

        public void truncate(long len) throws SQLException {
            /**@todo Implement this java.sql.Blob abstract method*/
        }

        public void free() throws SQLException {
            // TODO Auto-generated method stub

        }

        public InputStream getBinaryStream(long pos, long length) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }
    }


    public static class ClobImpl implements Clob {
        private char[] chars;

        public ClobImpl(char[] chars) {
            this.chars = chars;
        }

        public ClobImpl(String str) {
            this.chars = str.toCharArray();
        }

        public ClobImpl(Reader reader) throws IOException {
            char[] buf = new char[1024];
            CharArrayWriter writer = new CharArrayWriter(1024 * 10);
            for (int readLen = -1; (readLen = reader.read(buf)) > 0; ) {
                writer.write(buf, 0, readLen);
            }
            writer.flush();
            writer.close();
            reader.close();
            chars = writer.toCharArray();
        }

        public long length() throws SQLException {
            return chars.length;
        }

        public String getSubString(long pos, int length) throws SQLException {
            return new String(chars, (int) pos, length);
        }

        public Reader getCharacterStream() throws SQLException {
            return new CharArrayReader(chars);
        }

        public InputStream getAsciiStream() throws SQLException {
            /**@todo Implement this java.sql.Clob method($C=1$) */
            return new ByteArrayInputStream(new String(chars).getBytes());
        }

        public long position(String parm1, long parm2) throws SQLException {
            /**@todo Implement this java.sql.Clob method($C=1$) */
            throw new UnsupportedOperationException("Method position() not yet implemented.");
        }

        public long position(Clob parm1, long parm2) throws SQLException {
            /**@todo Implement this java.sql.Clob method*/
            throw new UnsupportedOperationException("Method position() not yet implemented.");
        }

        public Writer setCharacterStream(long pos) throws SQLException {
            /**@todo Implement this java.sql.Clob abstract method*/
            throw new UnsupportedOperationException("Method setCharacterStream() not yet implemented.");
        }

        public int setString(long pos, String str, int offset, int len) throws SQLException {
            /**@todo Implement this java.sql.Clob abstract method*/
            throw new UnsupportedOperationException("Method setString() not yet implemented.");
        }

        public int setString(long pos, String str) throws SQLException {
            /**@todo Implement this java.sql.Clob abstract method*/
            throw new UnsupportedOperationException("Method setString() not yet implemented.");
        }

        public void truncate(long len) throws SQLException {
            /**@todo Implement this java.sql.Clob abstract method*/
        }

        public OutputStream setAsciiStream(long pos) throws SQLException {
            /**@todo Implement this java.sql.Clob abstract method*/
            throw new UnsupportedOperationException("Method setAsciiStream() not yet implemented.");
        }

        public void free() throws SQLException {
            // TODO Auto-generated method stub

        }

        public Reader getCharacterStream(long pos, long length) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }
    }
}


