/**
 * $RCSfile: DAO.java,v $
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
import net.ooder.annotation.JoinOperator;
import net.ooder.annotation.Operator;
import net.ooder.annotation.Order;
import net.ooder.common.database.metadata.TableInfo;
import net.ooder.common.util.CaselessStringKeyHashMap;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;


/**
 * 简化版单表操作实现
 *
 * @param <T>
 */
public class DAO<T> implements Serializable {

    protected Log logger = LogFactory.getLog(DAO.class.getName());

    //实例化标记
    private boolean _isInDB = false;

    //表管理，简化版中不做实现
    ArrayList<String> _relations = new ArrayList<String>();


    Set<Order> orders = new LinkedHashSet<>();


    public TableInfo tableInfo;

    DBDAOUtil<T> dbUtil;


    public DAO(TableInfo info, Class<T> clazz) throws SQLException {
        this.tableInfo = info;
        dbUtil = new DBDAOUtil<T>(info, clazz);

    }


    public DAOCondition createCondition(String conditionKey, Operator operator, Object value) {
        DAOCondition condition = new DAOCondition(tableInfo);
        condition.setConditionKey(conditionKey);
        condition.setOperator(operator);
        condition.setValue(value);
        return condition;
    }

    public Set<Order> addOrder(Order order) {
        orders.add(order);
        return orders;

    }


    public DAOCondition getMainCondition() {

        DAOCondition mainCondition = new DAOCondition(tableInfo);
        for (Order order : orders) {
            mainCondition.addOrderBy(order);
        }
        return mainCondition;
    }


    public T createBean() throws DAOException {
        return dbUtil.create();
    }


    public T createBean(String uuid) throws DAOException {
        return dbUtil.create(uuid);
    }


    public boolean isInDB() throws DAOException {
        return _isInDB;
    }

    public void saveAsMap(Map<String, Object> map) throws DAOException {

        CaselessStringKeyHashMap caselessStringKeyHashMap = new CaselessStringKeyHashMap();
        caselessStringKeyHashMap.putAll(map);
        T bean = DBBeanUtil.genBean(this.tableInfo, caselessStringKeyHashMap, null);
        //  BeanMap.create(bean).putAll(map);
        update(bean);
    }

    public Class getBeanClass(boolean mix) {
        Class clazz = DBBeanUtil.getDefaultBeanClass(this.tableInfo, mix);
        return clazz;

    }


    public void update(T bean) throws DAOException {
        dbUtil.update(BeanMap.create(bean));
    }

    public int delete(String uuid) throws DAOException {

        return dbUtil.deleteByKey(uuid);
    }

    public int delete(String[] uuid) throws DAOException {
        return dbUtil.deleteByKeys(uuid);
    }


    public int deleteByWhere(String whereClause) throws DAOException {
        return dbUtil.deleteByWhere(whereClause);
    }


    public synchronized T findByPrimaryKey(String uuid) throws DAOException {
        T bean = null;
        List<T> beans = dbUtil.findPrimaryKey(uuid);
        if (beans != null && beans.size() > 0) {
            bean = beans.get(0);
        }
        return bean;
    }


    public synchronized List<T> findAll() throws DAOException {

        return dbUtil.findAll(this.getMainCondition().makeConditionString());
    }


    public synchronized List<T> find(String whereClause) throws DAOException {
        return dbUtil.findAll(whereClause);
    }

    public synchronized List<T> find(T bean) throws DAOException {
        Map searchMap = BeanMap.create(bean);
        return find(searchMap);
    }

    public synchronized List<T> find(Map<String, Object> searchMap) throws DAOException {
        Set<String> keySet = searchMap.keySet();
        DAOCondition mainCondition = this.getMainCondition();
        for (String key : keySet) {
            Object value = searchMap.get(key);
            if (value != null) {
                String strValue = TypeUtils.castToJavaBean(value, String.class);
                if (!strValue.equals("")) {
                    DAOCondition childCondition = mainCondition.createChildCondition(key, Operator.LIKE, "%" + strValue + "%");
                    mainCondition.addCondition(childCondition, JoinOperator.JOIN_AND);
                }
            }

        }
        return find(mainCondition);
    }


    public synchronized List<T> find(DAOCondition condition) throws DAOException {
        String whereClause = "1=1";
        if (condition != null) {
            condition.setTableInfo(this.tableInfo);
            whereClause = condition.makeConditionString();
        }

        return dbUtil.findAll(whereClause);
    }


    void addRelation(String table1, String key, String operator, String table2, String foreignKey) throws
            DAOException {
        if (_relations == null) {
            _relations = new ArrayList();
        }
        _relations.add(new StringBuffer().append(table1).append(".").append(key).append(operator).
                append(table2).append(".").append(foreignKey).toString());
    }


    void addRelation(String table1, String key, String table2, String foreignKey) throws
            DAOException {
        addRelation(table1, key, "=", table2, foreignKey);
    }


    protected String getRelationClause() {
        if (_relations == null) {
            return null;
        }
        StringBuffer clause = new StringBuffer().append(" (");
        Iterator enu = _relations.iterator();
        while (enu.hasNext()) {
            clause.append(enu.next()).append(" and ");
        }
        return clause.substring(0, clause.length() - 4) + ") ";
    }

    public void close() {
        dbUtil.close();

    }


}


