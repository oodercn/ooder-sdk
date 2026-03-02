/**
 * $RCSfile: DAOFactory.java,v $
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

import net.ooder.common.database.metadata.ColInfo;
import net.ooder.common.database.metadata.MetadataFactory;
import net.ooder.common.database.metadata.TableInfo;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.annotation.DBField;
import net.ooder.annotation.DBTable;
import net.ooder.annotation.MethodChinaName;
import net.ooder.web.util.AnnotationUtil;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;


public class DAOFactory<T> {

    protected transient static final Log logger = LogFactory.getLog("DAO", DAOFactory.class);

    private TableInfo tableInfo;

    private DAO<T> dao = null;

    MetadataFactory metafactory;

    public DAOFactory(TableInfo tableInfo, Class<T> clazz) throws DAOException {
        try {
            this.dao = new DAO<T>(tableInfo, clazz);
            this.tableInfo = tableInfo;
            metafactory = MetadataFactory.getInstance(tableInfo.getUrl());
        } catch (SQLException e) {
            throw new DAOException(e);
        }


    }


    public DAOFactory(TableInfo tableInfo) throws DAOException {
        try {
            this.tableInfo = tableInfo;
            this.dao = new DAO<T>(tableInfo, null);
            metafactory = MetadataFactory.getInstance(tableInfo.getUrl());
        } catch (SQLException e) {
            throw new DAOException(e);
        }


    }

    public DAOFactory(String connfigKey, String tableName) throws DAOException {
        try {
            MetadataFactory factory = MetadataFactory.getInstance(connfigKey);
            this.tableInfo = factory.getTableInfo(tableName);
            this.dao = new DAO<T>(tableInfo, null);
            metafactory = MetadataFactory.getInstance(tableInfo.getUrl());
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public DAOFactory(String connfigKey, String tableName, Class<T> clazz) throws DAOException {
        try {
            metafactory = MetadataFactory.getInstance(connfigKey);
            TableInfo tableInfo = metafactory.getTableInfo(tableName);
            this.dao = new DAO<T>(tableInfo, clazz);
        } catch (SQLException e) {
            throw new DAOException(e);
        }

    }


    public DAOFactory(Class<T> tClass) throws DAOException {
        try {
            TableInfo tableInfo = getTableInfoByDAO(tClass);
            metafactory = MetadataFactory.getInstance(tableInfo.getUrl());
            this.dao = new DAO<T>(tableInfo, tClass);
        } catch (SQLException e) {
            throw new DAOException(e);
        }

    }

    private TableInfo getTableInfoByDAO(Class<T> tClass) throws DAOException {

        TableInfo info = null;
        DBTable cAnn = (DBTable) AnnotationUtils.findAnnotation(tClass, DBTable.class);

        if (cAnn != null && cAnn.tableName() != null && cAnn.configKey() != null) {
            String tableName = cAnn.tableName();
            metafactory = MetadataFactory.getInstance(cAnn.configKey());
            info = metafactory.getTableInfo(tableName);
            if (info == null) {
                info = createTable(tClass);
            }

        }
        return info;
    }


    private TableInfo createTable(Class<T> tClass) throws DAOException {
        DBTable cAnn = (DBTable) AnnotationUtil.getClassAnnotation(tClass, DBTable.class);
        TableInfo info = new TableInfo();
        info.setName(cAnn.tableName());
        info.setPkName(cAnn.primaryKey());
        info.setCnname(cAnn.cname());
        info.setConfigKey(cAnn.configKey());
        info.setUrl(cAnn.url());
        Field ms[] = tClass.getDeclaredFields();
        for (int i = 0; i < ms.length; i++) {
            Field m = ms[i];
            DBField ma = m.getAnnotation(DBField.class);
            if (ma != null) {
                ColInfo col = new ColInfo();
                if (info.getPkNames().contains(col.getName())) {
                    col.setPk(true);
                }
                col.setName(ma.dbFieldName());
                col.setFieldname(m.getName());
                col.setColType(ma.dbType());
                col.setLength(ma.length());
                col.setCanNull(ma.isNull());
                col.setUrl(cAnn.url());
                col.setTablename(info.getName());
                MethodChinaName mcn = m.getAnnotation(MethodChinaName.class);
                if (mcn != null) {
                    col.setCnname(mcn.cname());
                }
                if (col.getCnname() == null) {
                    col.setCnname(col.getName());
                }
                info.addCol(col);
            }
        }

        metafactory.createTableByInfo(info);
        info = metafactory.getTableInfo(cAnn.tableName());
        return info;

    }

    public DAO<T> getDAO() {
        return dao;
    }


    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }


    public void close() {
        dao.close();
    }
}

