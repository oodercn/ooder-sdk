/**
 * $RCSfile: DAOCondition.java,v $ * $Revision: 1.0 $ * $Date: 2025/08/25 $ * <p> * Copyright (c) 2025 ooder.net * </p> * <p> * Company: ooder.net * </p> * <p> * License: MIT License * </p> */
package net.ooder.common.database.dao;

import net.ooder.common.Filter;
import net.ooder.annotation.JoinOperator;
import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.Operator;
import net.ooder.annotation.Order;
import net.ooder.common.Page;
import net.ooder.common.database.metadata.ColInfo;
import net.ooder.common.database.metadata.TableInfo;
import net.ooder.common.util.StringUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * DAOCondition类用于封装对数据库进行查询的条件以及对结果集的排序，
 * 并在内部将其作为SQL查询WHERE子句的一部分，最终转换为SQL查询条件。
 */
public class DAOCondition implements Serializable, Filter {

    /**
     * 条件类型包括：活动状态、活动开始时间、结束时间
     */
    private String conditionKey;

    /**
     * 条件所需的取值，如果Comparator取值为BETWEEN和INCLUDE，该值为java.util.List对象
     */
    private Object value;

    private Operator operator;

    private TableInfo tableInfo;

    private boolean isMysql = true;

    private Page page;

    private List<DAOCondition> childConditionList;

    private List<JoinOperator> childJoinTypeList;

    private List<Order> orderByList;

    /**
     * 带表信息的构造函数
     * @param tableInfo 表信息对象
     */
    DAOCondition(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
        if (tableInfo.getUrl() != null) {
            isMysql = tableInfo.getUrl().indexOf("mysql") > -1;
        }
        childConditionList = new ArrayList<DAOCondition>();
        childJoinTypeList = new ArrayList<JoinOperator>();
        orderByList = new ArrayList<Order>();
        page = new Page();
    }


    /**
     * 获取分页对象
     * @return 分页对象
     */
    @MethodChinaName("获取分页对象")
    public Page getPage() {
        return page;
    }

    /**
     * 设置分页对象
     * @param page 分页对象
     */
    @MethodChinaName("设置分页对象")
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * 获取条件键
     * @return 条件键
     */
    @MethodChinaName("获取条件键")
    public String getConditionKey() {
        return conditionKey;
    }

    /**
     * 设置条件键
     * @param conditionKey 条件键
     */
    @MethodChinaName("设置条件键")
    public void setConditionKey(String conditionKey) {
        this.conditionKey = conditionKey;
    }

    /**
     * 获取表信息
     * @return 表信息对象
     */
    @MethodChinaName("获取表信息")
    public TableInfo getTableInfo() {
        return tableInfo;
    }

    /**
     * 设置表信息
     * @param tableInfo 表信息对象
     */
    @MethodChinaName("设置表信息")
    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    /**
     * 条件构造函数，该方法主要用于对条件主键和某个值进行指定操作符比较的操作。支持此种操作的操作符有：
     *
     * <code>
     * <li>EQUALS
     * <li>NOT_EQUAL
     * <li>LESS_THAN
     * <li>GREATER_THAN
     * <li>LESS_THAN_EQUAL_TO
     * <li>GREATER_THAN_EQUAL_TO
     * <li>LIKE
     * <li>IN
     * <li>NOT_IN
     * <li>BETWEEN
     * </code>
     *
     * @param conditionKey 条件主键
     * @param value        取值
     * @param operator     条件主键与取值的比较操作符类型
     */
//    DAOCondition(String conditionKey, Operator operator, Object value) {
//        // this(tableInfo);
//        this.conditionKey = conditionKey;
//        this.value = value;
//        this.operator = operator;
//
//
//    }

//    /**
//     * 条件构造函数，该方法主要用于对条件主键进行是否可空的条件操作支持此种操作的操作符有：
//     *
//     * <code>
//     * <li>NULL
//     * <li>NOT_NULL
//     * </code>
//     *
//     * @param conditionKey 条件主键
//     * @param operator     条件主键是否可空的操作符
//     */
//    DAOCondition(String conditionKey, Operator operator) {
//        this(conditionKey, operator, null);
//    }


    /**
     * 创建子条件
     * @param conditionKey 条件键
     * @param operator 操作符
     * @param value 值
     * @return DAOCondition对象
     */
    @MethodChinaName("创建子条件")
    DAOCondition createChildCondition(String conditionKey, Operator operator, Object value) {
        DAOCondition condition = new DAOCondition(this.tableInfo);
        condition.setConditionKey(conditionKey);
        condition.setValue(value);
        condition.setOperator(operator);
        return condition;
    }

    /**
     * 获取值
     * @return 值
     */
    @MethodChinaName("获取值")
    public Object getValue() {
        return value;
    }

    /**
     * 设置值
     * @param value 值
     */
    @MethodChinaName("设置值")
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 获取操作符
     * @return 操作符
     */
    @MethodChinaName("获取操作符")
    public Operator getOperator() {
        return operator;
    }

    /**
     * 设置操作符
     * @param operator 操作符
     */
    @MethodChinaName("设置操作符")
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    /**
     * 获取子条件列表
     * @return 子条件列表
     */
    @MethodChinaName("获取子条件列表")
    public List<DAOCondition> getChildConditionList() {
        return childConditionList;
    }

    /**
     * 设置子条件列表
     * @param childConditionList 子条件列表
     */
    @MethodChinaName("设置子条件列表")
    public void setChildConditionList(List<DAOCondition> childConditionList) {
        this.childConditionList = childConditionList;
    }

    /**
     * 获取子连接类型列表
     * @return 子连接类型列表
     */
    @MethodChinaName("获取子连接类型列表")
    public List<JoinOperator> getChildJoinTypeList() {
        return childJoinTypeList;
    }

    /**
     * 设置子连接类型列表
     * @param childJoinTypeList 子连接类型列表
     */
    @MethodChinaName("设置子连接类型列表")
    public void setChildJoinTypeList(List<JoinOperator> childJoinTypeList) {
        this.childJoinTypeList = childJoinTypeList;
    }

    /**
     * 获取排序列表
     * @return 排序列表
     */
    @MethodChinaName("获取排序列表")
    public List<Order> getOrderByList() {
        return orderByList;
    }

    /**
     * 设置排序列表
     * @param orderByList 排序列表
     */
    @MethodChinaName("设置排序列表")
    public void setOrderByList(List<Order> orderByList) {
        this.orderByList = orderByList;
    }

    /**
     * 产生查询SQL,引擎内部使用
     * @return 返回引擎查询用的SQL
     */
    @MethodChinaName("产生查询SQL")
    public String makeConditionString() {

        String whereStr = "";
        String dbkey = null;

        if (conditionKey != null && conditionKey.indexOf(".") == -1 && this.tableInfo != null) {
            ColInfo colInfo = tableInfo.getCoInfoByName(conditionKey);
            if (colInfo != null && isMysql) {
                dbkey = "`" + colInfo.getName() + "`";
            } else {
                dbkey = colInfo.getName();
            }
        }

        if (dbkey != null && value != null)
            switch (operator) {
                case EQUALS: {
                    whereStr = dbkey + " = " + extractValue(value);
                    break;
                }
                case NOT_EQUAL: {
                    whereStr = dbkey + " != " + extractValue(value);
                    break;
                }
                case LESS_THAN: {
                    whereStr = dbkey + " < " + extractValue(value);
                    break;
                }
                case GREATER_THAN: {
                    whereStr = dbkey + " > " + extractValue(value);
                    break;
                }
                case LESS_THAN_EQUAL_TO: {
                    whereStr = dbkey + " <= " + extractValue(value);
                    break;
                }
                case GREATER_THAN_EQUAL_TO: {
                    whereStr = dbkey + " >= " + extractValue(value);
                    break;
                }
                case LIKE: {
                    whereStr = dbkey + " LIKE " + extractValue(value);
                    break;
                }
                case IN: {
                    StringBuffer sb = new StringBuffer();
                    if (value instanceof Collection) {
                        Iterator ite = ((Collection) value).iterator();
                        boolean first = true;
                        while (ite.hasNext()) {
                            if (first) {
                                first = false;
                            } else {
                                sb.append(",");
                            }

                            sb.append(extractValue(ite.next()));
                        }
                    } else if (value instanceof String) {
                        sb.append((String) value);
                    }
                    if (sb.length() > 0) {
                        whereStr = dbkey + " IN (" + sb.toString() + ")";
                    }
                    break;
                }
                case NOT_IN: {
                    StringBuffer sb = new StringBuffer();
                    if (value instanceof Collection) {
                        Iterator ite = ((Collection) value).iterator();
                        boolean first = true;
                        while (ite.hasNext()) {
                            if (first) {
                                first = false;
                            } else {
                                sb.append(",");
                            }

                            sb.append(extractValue(ite.next()));
                        }
                    } else if (value instanceof String) {
                        sb.append((String) value);
                    }
                    if (sb.length() > 0) {
                        whereStr = conditionKey + " NOT IN (" + sb.toString() + ")";
                    }
                    break;
                }
                case BETWEEN: {
                    if (value instanceof Collection) {
                        Collection valueCol = (Collection) value;
                        if (valueCol.size() >= 2) {
                            whereStr = dbkey + " BETWEEN ";
                            Iterator ite = valueCol.iterator();
                            if (ite.hasNext()) {
                                whereStr += extractValue(ite.next());
                            }
                            whereStr += " AND ";
                            if (ite.hasNext()) {
                                whereStr += extractValue(ite.next());
                            }
                        }
                    }
                    break;
                }
                case NULL: {
                    whereStr = dbkey + " IS NULL";
                    break;
                }
                case NOT_NULL: {
                    whereStr = dbkey + " IS NOT NULL";
                    break;
                }
            }
        String childCondition = makeChildrenCondition();
        if (!"".equals(childCondition)) {
            if (!"".equals(whereStr)) {
                JoinOperator joinType = childJoinTypeList.get(0);

                switch (joinType) {
                    case JOIN_AND: {
                        whereStr += " AND ";
                        break;
                    }
                    case JOIN_OR: {
                        whereStr += " OR ";
                        break;
                    }
                }

            }

            whereStr += childCondition;
        }
        if (!"".equals(whereStr)) {
            whereStr += makeOrderBy();
        }

        return whereStr;
    }

    /**
     * 添加一个排序条件，将向SQL语句中添加一个Order By子句<br>
     * 注意：只有最上级的Condition主查询可以添加Order，子查询上是不能添加的
     * @param order 一个Order对象
     */
    @MethodChinaName("添加排序条件")
    public void addOrderBy(Order order) {
        orderByList.add(order);
    }

    /**
     * 在当前条件中添加一个子条件，将使用joinType中定义的方法连接到主查询<br>
     *
     * @param condition 子查询条件
     * @param joinType  连接方法
     *                  <li>JOIN_AND - 将使用AND连接子查询
     *                  <li>JOIN_OR - 将使用OR连接子查询
     */


    /**
     * 在当前条件中添加一个子条件，将使用joinType中定义的方法连接到主查询
     * @param condition 子查询条件
     * @param joinType 连接方法
     * <li>JOIN_AND - 将使用AND连接子查询
     * <li>JOIN_OR - 将使用OR连接子查询
     */
    @MethodChinaName("添加子条件")
    public void addCondition(DAOCondition condition, JoinOperator joinType) {
        if (condition != null) {
            if (!(joinType.equals(JoinOperator.JOIN_AND) || joinType.equals(JoinOperator.JOIN_OR))) {
                throw new IllegalArgumentException(
                        "Parameter joinType must be JOIN_AND or JOIN_OR.");
            }
            if (condition.orderByList != null && condition.orderByList.size() != 0) {
                throw new IllegalArgumentException(
                        "Parameter condition contains order by and cannot be child conditon.");
            }
            childConditionList.add(condition);
            childJoinTypeList.add(joinType);
        }
    }

    private String makeChildrenCondition() {
        String result = "";
        if (childConditionList != null && childConditionList.size() > 0) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < childConditionList.size(); i++) {
                DAOCondition condition = (DAOCondition) childConditionList.get(i);
                if ("".equals(condition.makeConditionString()))
                    continue;
                JoinOperator joinType = childJoinTypeList.get(i);

                if (i != 0) {
                    switch (joinType) {
                        case JOIN_AND: {
                            buf.append(" AND ");
                            break;
                        }
                        case JOIN_OR: {
                            buf.append(" OR ");
                            break;
                        }
                    }
                }

                buf.append(condition.makeConditionString());
            }
            String tmp = buf.toString();
            if (!"".equals(tmp)) {
                buf.insert(0, "(");
                buf.append(")");
            }
            result = buf.toString();
        }
        return result;
    }

    private String makeOrderBy() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < orderByList.size(); i++) {
            Order order = (Order) orderByList.get(i);
            if (i == 0) {
                buf.append(" ORDER BY ");
            } else {
                buf.append(",");
            }
            buf.append(order.toString());
        }
        return buf.toString();
    }

    /**
     * 提取值，将对象转换为SQL字符串表示
     * @param value 值对象
     * @return SQL字符串表示
     */
    @MethodChinaName("提取值")
    public String extractValue(Object value) {

        if (value instanceof Enum) {
            return "'" + StringUtility.replace(((Enum) value).toString(), "'", "''") + "'";
        } else if (value instanceof Integer) {
            return ((Integer) value).toString();
        } else if (value instanceof Long) {
            return ((Long) value).toString();
        } else if (value instanceof Double) {
            return ((Double) value).toString();
        } else if (value instanceof Float) {
            return ((Float) value).toString();
        } else if (value instanceof String) {
            return "'" + StringUtility.replace((String) value, "'", "''") + "'";
        } else if (value instanceof java.sql.Date) {
            return String.valueOf(((java.sql.Date) value).getTime());
        } else if (value instanceof java.util.Date) {
            return String.valueOf(((java.util.Date) value).getTime());
        } else {
            return value.toString();
        }
    }


    /**
     * 过滤对象
     * @param obj 对象
     * @param systemCode 系统代码
     * @return 是否通过过滤
     */
    @MethodChinaName("过滤对象")
    public boolean filterObject(Object obj, String systemCode) {
        return true;
    }

    /**
     * 是否为MySQL数据库
     * @return 是否为MySQL数据库
     */
    @MethodChinaName("是否为MySQL数据库")
    public boolean isMysql() {
        return isMysql;
    }

    /**
     * 设置是否为MySQL数据库
     * @param mysql 是否为MySQL数据库
     */
    @MethodChinaName("设置是否为MySQL数据库")
    public void setMysql(boolean mysql) {
        isMysql = mysql;
    }

    public static void main(String[] args) {


    }

}


