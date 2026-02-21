package net.ooder.org.query;

import net.ooder.annotation.Order;
import net.ooder.common.Filter;
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
 * Description: 用于封装对引擎内部数据进行查询的条件以及对结果集的排序，并 在引擎内部将其作为SQL查询WHERE子句的一部分。也就是说它最终
 * 将被转换成SQL查询条件，例如：
 * <p>
 * <code>Condition c1 = new OrgCondition(ConditionKey.ACTIVITYINST_STATE, ActivityInst.STATE_RUNNING, Condition.EQUALS);</code>
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
public class LuceneCondition implements Serializable, Filter {

	/** 比较符comparator可用的类型 */
	public static final int EQUALS = 1;

	public static final int NOT_EQUAL = 2;

	public static final int LESS_THAN = 3;

	public static final int GREATER_THAN = 4;

	public static final int LESS_THAN_EQUAL_TO = 5;

	public static final int GREATER_THAN_EQUAL_TO = 6;

	public static final int LIKE = 7;

	public static final int IN = 10;

	public static final int NOT_IN = 11;

	public static final int BETWEEN = 12;

	public static final int NULL = 20;

	public static final int NOT_NULL = 21;

	public static final int JOIN_AND = 100;

	public static final int JOIN_OR = 101;

	/** 条件类型包括：活动状态、活动开始时间、结束时间 */
	protected OrgConditionKey conditionKey;

	/** 条件所需的取值，如果Comparator取值为BETWEEN和INCLUDE，该值为java.util.List对象。 */
	protected Object value;

	protected int operator;

	protected List childConditionList;

	protected List childJoinTypeList;

	protected List orderByList;

	public LuceneCondition() {
		childConditionList = new ArrayList();
		childJoinTypeList = new ArrayList();
		orderByList = new ArrayList();
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
	public LuceneCondition( OrgConditionKey conditionKey, int operator) {
		this(conditionKey, operator, null);
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
	public LuceneCondition( OrgConditionKey conditionKey, int operator, Object value) {
		this();
		this.conditionKey = conditionKey;
		this.value = value;
		this.operator = operator;
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
				whereStr = conditionKey + " = " + extractValue(value);
				break;
			}
			case NOT_EQUAL: {
				whereStr = conditionKey + " != " + extractValue(value);
				break;
			}
			case LESS_THAN: {
				whereStr = conditionKey + " < " + extractValue(value);
				break;
			}
			case GREATER_THAN: {
				whereStr = conditionKey + " > " + extractValue(value);
				break;
			}
			case LESS_THAN_EQUAL_TO: {
				whereStr = conditionKey + " <= " + extractValue(value);
				break;
			}
			case GREATER_THAN_EQUAL_TO: {
				whereStr = conditionKey + " >= " + extractValue(value);
				break;
			}
			case LIKE: {
				whereStr = conditionKey + " LIKE " + extractValue(value);
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
					whereStr = conditionKey + " IN (" + sb.toString() + ")";
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
						whereStr = conditionKey + " BETWEEN ";
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
				whereStr = conditionKey + " IS NULL";
				break;
			}
			case NOT_NULL: {
				whereStr = conditionKey + " IS NOT NULL";
				break;
			}
			}
		String childCondition = makeChildrenCondition();
		if (!"".equals(childCondition)) {
			if (!"".equals(whereStr)) {
				int joinType = ((Integer) childJoinTypeList.get(0)).intValue();

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
	public void addOrderBy(Order order) {
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
	public void addCondition(LuceneCondition condition, int joinType) {
		if (condition != null) {
			if (joinType != JOIN_AND && joinType != JOIN_OR) {
				throw new IllegalArgumentException(
						"Parameter joinType must be JOIN_AND or JOIN_OR.");
			}
			if (condition.orderByList.size() != 0) {
				throw new IllegalArgumentException(
						"Parameter condition contains order by and cannot be child conditon.");
			}
			childConditionList.add(condition);
			childJoinTypeList.add(new Integer(joinType));
		}
	}

	private String makeChildrenCondition() {
		String result = "";
		if (childConditionList.size() > 0) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < childConditionList.size(); i++) {
				LuceneCondition condition = (LuceneCondition) childConditionList.get(i);
				if ("".equals(condition.makeConditionString()))
					continue;
				int joinType = ((Integer) childJoinTypeList.get(i)).intValue();

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

	private String extractValue(Object value) {
		if (value instanceof Integer) {
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
