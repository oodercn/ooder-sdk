package net.ooder.org.query;

import net.ooder.annotation.JoinOperator;
import net.ooder.annotation.Operator;
import net.ooder.annotation.Order;
import net.ooder.common.Page;
import net.ooder.common.util.StringUtility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class OrgCondition {




    /** 条件类型包括：活动状态、活动开始时间、结束时间 */
    protected OrgConditionKey conditionKey;

    /** 条件所需的取值，如果Comparator取值为BETWEEN和INCLUDE，该值为java.util.List对象。 */
    protected Object value;

    protected Operator operator;


    public Page page;


    protected List<OrgCondition> childConditionList;

    protected List<JoinOperator> childJoinTypeList;

    protected List<Order<OrgConditionKey>> orderByList;

    public OrgCondition() {
        childConditionList = new ArrayList<OrgCondition>();
        childJoinTypeList = new ArrayList<JoinOperator> ();
        orderByList = new ArrayList<Order<OrgConditionKey>> ();
        page=new Page();
    }


    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }


    /**
     * 条件构造函数，该方法主要用于对条件主键进行是否可空的条件操作。 支持此种操作的操作符有：
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
    public OrgCondition(OrgConditionKey conditionKey, Operator operator) {
        this(conditionKey, operator, null);
    }



    public OrgConditionKey getConditionKey() {
        return conditionKey;
    }

    public void setConditionKey(OrgConditionKey conditionKey) {
        this.conditionKey = conditionKey;
    }

    /**
     * 条件构造函数，该方法主要用于对条件主键和某个值进行指定 操作符比较的操作。支持此种操作的操作符有：
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
    public OrgCondition(OrgConditionKey conditionKey, Operator operator, Object value) {
        this();


        this.conditionKey = conditionKey;

        this.value = value;
        this.operator = operator;
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

    public List<OrgCondition> getChildConditionList() {
        return childConditionList;
    }

    public void setChildConditionList(List<OrgCondition> childConditionList) {
        this.childConditionList = childConditionList;
    }

    public List<JoinOperator> getChildJoinTypeList() {
        return childJoinTypeList;
    }

    public void setChildJoinTypeList(List<JoinOperator> childJoinTypeList) {
        this.childJoinTypeList = childJoinTypeList;
    }

    public List<Order<OrgConditionKey>> getOrderByList() {
        return orderByList;
    }

    public void setOrderByList(List<Order<OrgConditionKey>> orderByList) {
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
                    whereStr = conditionKey.getValue()  + " != " + extractValue(value);
                    break;
                }
                case LESS_THAN: {
                    whereStr = conditionKey.getValue()  + " < " + extractValue(value);
                    break;
                }
                case GREATER_THAN: {
                    whereStr = conditionKey.getValue()  + " > " + extractValue(value);
                    break;
                }
                case LESS_THAN_EQUAL_TO: {
                    whereStr = conditionKey.getValue()  + " <= " + extractValue(value);
                    break;
                }
                case GREATER_THAN_EQUAL_TO: {
                    whereStr = conditionKey.getValue()  + " >= " + extractValue(value);
                    break;
                }
                case LIKE: {
                    whereStr = conditionKey.getValue()  + " LIKE " + extractValue(value);
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
                        whereStr = conditionKey.getValue()  + " NOT IN (" + sb.toString() + ")";
                    }
                    break;
                }
                case BETWEEN: {
                    if (value instanceof Collection) {
                        Collection valueCol = (Collection) value;
                        if (valueCol.size() >= 2) {
                            whereStr = conditionKey.getValue()  + " BETWEEN ";
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
                    whereStr = conditionKey.getValue()  + " IS NULL";
                    break;
                }
                case NOT_NULL: {
                    whereStr = conditionKey.getValue()  + " IS NOT NULL";
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
     * 注意：只有最上级的Condition主查询可以添加Order，子查询上是不能添加的
     *
     * @param order
     *            一个Order对象。
     */
    public void addOrderBy(Order<OrgConditionKey> order) {
        orderByList.add(order);
    }

    /**
     * 在当前条件中添加一个子条件，将使用joinType中定义的方法连接到主查询上<br>
     *
     * @param condition
     *            子查询条件
     * @param joinType
     *            连接方法
     *            <li>JOIN_AND - 将使用AND连接子查询
     *            <li>JOIN_OR - 将使用OR连接子查询
     */
    public void addCondition(OrgCondition condition, JoinOperator joinType) {
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
                OrgCondition condition = childConditionList.get(i);
                if ("".equals(condition.makeConditionString()))
                    continue;
                JoinOperator joinType =  childJoinTypeList.get(i);

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

    public  String extractValue(Object value) {

        if (value instanceof Enum) {
            return "'" + StringUtility.replace( ((Enum) value).toString(), "'", "''") + "'";
        }else if (value instanceof Integer) {
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


    public boolean filterObject(Object obj,String systemCode) {
        return true;
    }
}
