/**
 * $RCSfile: Condition.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:08 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: Condition.java,v $
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
package net.ooder.common;

import net.ooder.annotation.*;
import net.ooder.common.util.StringUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 用于封装对引擎内部数据进行查询的条件以及对结果集的排序，在引擎内部将其作为SQL查询WHERE子句的一部分。也就是说它最终
 * 将被转换成SQL查询条件，例如：
 * <p>
 * <code>Condition c1 = new Condition(ConditionKey.ACTIVITYINST_STATE, ActivityInst.STATE_RUNNING, Condition.EQUALS);</code>
 * <p>
 * 调用<code>c1.makeConditionString()</code>将返回查询条件
 * <code>WORKFLOW_ACTIVITYINSTANCE.ACTIVITYINST_STATE = 'running'</code>
 * <p>
 * 如果继续进行如下调用：
 * <p>
 * <code>
 * java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
 * Condition c2 = new Condition(ConditionKey.ACTIVITYINST_ARRIVEDTIME, date , Condition.LESS_THAN);
 * c1.addCondition(c2, Condition.JOIN_AND);
 * Condition c3 = new Condition(ConditionKey.ACTIVITYINST_STARTTIME, date , Condition.GREATER_THAN);
 * c1.addCondition(c3, Condition.JOIN_OR);
 * </code>
 * <p>
 * 此时调用<code>c1.makeConditionString()</code>将返回查询条件
 * <code>WORKFLOW_ACTIVITYINSTANCE.ACTIVITYINST_STATE = 'running' AND (WORKFLOW_ACTIVITYINSTANCE.ARRIVEDTIME < '2003-12-25' OR WORKFLOW_ACTIVITYINSTANCE.STARTTIME > '2003-12-25')</code>
 * <p>
 * 如果需要对结果集进行排序，可以进行如下操作：
 * <p>
 * <code>
 * c1.addOrderBy(new Order(ConditionKey.ACTIVITYINST_STATE, true));
 * c1.addOrderBy(new Order(ConditionKey.ACTIVITYINST_ARRIVEDTIME, false));
 * </code>
 * <p>
 * 此时调用<code>c1.makeConditionString()</code>将返回查询条件
 * <code>WORKFLOW_ACTIVITYINSTANCE.ACTIVITYINST_STATE = 'running' AND (WORKFLOW_ACTIVITYINSTANCE.ARRIVEDTIME < '2003-12-25' OR WORKFLOW_ACTIVITYINSTANCE.STARTTIME > '2003-12-25') ORDER BY WORKFLOW_ACTIVITYINSTANCE.ACTIVITYINST_STATE ASC,WORKFLOW_ACTIVITYINSTANCE.ARRIVEDTIME DESC</code>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public class Condition<T extends ConditionKey, V extends JLuceneIndex> implements Serializable, Filter {


    V index;

    /** 条件类型包括：活动状态、活动开始时间、结束时间*/
    protected T conditionKey;

    /** 条件所需的取值，如果Comparator取值为BETWEEN和INCLUDE，该值为java.util.List对象。*/
    protected Object value;

    protected Operator operator;


    public Page page;


    protected List<Condition<T, V>> childConditionList;

    protected List<JoinOperator> childJoinTypeList;

    protected List<Order<T>> orderByList = new ArrayList<Order<T>>();

    public Condition() {
        childConditionList = new ArrayList<Condition<T, V>>();
        childJoinTypeList = new ArrayList<JoinOperator>();
        orderByList = new ArrayList<Order<T>>();
        page = new Page();
    }

    public V getIndex() {
        return index;
    }

    public void setIndex(V index) {
        this.index = index;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }


    /**
     * 条件构造函数，该方法主要用于对条件主键进行是否可空的条件操作。支持此种操作的操作符有：
     * <p>
     * <code>
     * <li>NULL
     * <li>NOT_NULL
     * </code>
     *
     * @param conditionKey
     *            条件主键
     * @param operator
     *            条件主键是否可空的操作符
     */
    public Condition(T conditionKey, V index, Operator operator) {
        this(conditionKey, index, operator, null);
    }

    public Condition(T conditionKey, Operator operator) {
        this(conditionKey, null, operator, null);
    }

    public T getConditionKey() {
        return conditionKey;
    }

    public void setConditionKey(T conditionKey) {
        this.conditionKey = conditionKey;
    }

    /**
     * 条件构造函数，该方法主要用于对条件主键和某个值进行指定操作符比较的操作。支持此种操作的操作符有：
     * <p>
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
     * @param conditionKey
     *            条件主键
     * @param value
     *            取值
     * @param operator
     *            条件主键与取值的比较操作符类型
     */
    public Condition(T conditionKey, V index, Operator operator, Object value) {
        this();
        this.index = index;

        this.conditionKey = conditionKey;

        this.value = value;
        this.operator = operator;
    }

    public Condition(T conditionKey, Operator operator, Object value) {
        this(conditionKey, null, operator, value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public List<Condition<T, V>> getChildConditionList() {
        return childConditionList;
    }

    public void setChildConditionList(List<Condition<T, V>> childConditionList) {
        this.childConditionList = childConditionList;
    }

    public List<JoinOperator> getChildJoinTypeList() {
        return childJoinTypeList;
    }

    public void setChildJoinTypeList(List<JoinOperator> childJoinTypeList) {
        this.childJoinTypeList = childJoinTypeList;
    }

    public List<Order<T>> getOrderByList() {
        return orderByList;
    }

    public void setOrderByList(List<Order<T>> orderByList) {
        this.orderByList = orderByList;
    }

    /**
     * 产生查询Sql,引擎内部使用
     *
     * @return 返回引擎查询用的sql
     */
    public String makeConditionString() {

        String whereStr = "";
        if (value != null)
            switch (operator) {
                case EQUALS: {
                    whereStr = conditionKey.getValue() + " = " + extractValue(value);
                    break;
                }
                case NOT_EQUAL: {
                    whereStr = conditionKey.getValue() + " != " + extractValue(value);
                    break;
                }
                case LESS_THAN: {
                    whereStr = conditionKey.getValue() + " < " + extractValue(value);
                    break;
                }
                case GREATER_THAN: {
                    whereStr = conditionKey.getValue() + " > " + extractValue(value);
                    break;
                }
                case LESS_THAN_EQUAL_TO: {
                    whereStr = conditionKey.getValue() + " <= " + extractValue(value);
                    break;
                }
                case GREATER_THAN_EQUAL_TO: {
                    whereStr = conditionKey.getValue() + " >= " + extractValue(value);
                    break;
                }
                case LIKE: {
                    whereStr = conditionKey.getValue() + " LIKE " + extractValue(value);
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
                        whereStr = conditionKey.getValue() + " IN (" + sb.toString() + ")";
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
                        whereStr = conditionKey.getValue() + " NOT IN (" + sb.toString() + ")";
                    }
                    break;
                }
                case BETWEEN: {
                    if (value instanceof Collection) {
                        Collection valueCol = (Collection) value;
                        if (valueCol.size() >= 2) {
                            whereStr = conditionKey.getValue() + " BETWEEN ";
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
                    whereStr = conditionKey.getValue() + " IS NULL";
                    break;
                }
                case NOT_NULL: {
                    whereStr = conditionKey.getValue() + " IS NOT NULL";
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
     * 添加一个排序条件，将向Sql语句中添加一个Order By子句<br>
     * 注意：只有最上级的Condition主查询可以添加Order，子查询上是不能添加的。
     *
     * @param order
     *            一个Order对象。
     */
    public void addOrderBy(Order<T> order) {
        orderByList.add(order);
    }

    /**
     * 在当前条件中添加一个子条件，将使用joinType中定义的方法连接到主查询。<br>
     *
     * @param condition
     *            子查询条件
     * @param joinType
     *            连接方法
     *            <li>JOIN_AND - 将使用AND连接子查询
     *            <li>JOIN_OR - 将使用OR连接子查询
     */
    public void addCondition(Condition<T, V> condition, JoinOperator joinType) {
        if (condition != null) {
            if (joinType.equals(JoinOperator.JOIN_AND) && joinType.equals(JoinOperator.JOIN_OR)) {
                throw new IllegalArgumentException(
                        "Parameter joinType must be JOIN_AND or JOIN_OR.");
            }
            if (condition.orderByList.size() != 0) {
                throw new IllegalArgumentException(
                        "Parameter condition contains order by and cannot be child conditon.");
            }
            childConditionList.add(condition);
            childJoinTypeList.add(joinType);
        }
    }

    private String makeChildrenCondition() {
        String result = "";
        if (childConditionList.size() > 0) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < childConditionList.size(); i++) {
                Condition condition = (Condition) childConditionList.get(i);
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


    public boolean filterObject(Object obj, String systemCode) {
        return true;
    }

    public static void main(String[] args) {


    }

}
