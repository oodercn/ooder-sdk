package net.ooder.org.query;


import net.ooder.common.ConditionKey;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 可以进行查询或排序的数据库字段。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @see OrgConditionKey
 * @author wenzhang li
 * @version 2.0
 */
public enum MsgConditionKey implements ConditionKey {


	MSG_MSGID ("MSGID"),

	MSG_SYSID ("SYSID"),

	MSG_TYPE ("TYPE"),

	MSG_TITLE ("TITLE"),

	MSG_STATUS ("STATUS"),

	MSG_RECEIVE ("FORM"),

	MSG_SEND ("SEND"),

	MSG_BODY ("BODY"),

	MSG_SENDTIME ("SENDTIME"),

	MSG_ARRIVEDTIME ("ARRIVEDTIME"),

	MSG_PROCESSINSTID ("PROCESSINSTID"),

	MSG_ACTIVITYINSTID ("ACTIVITYINSTID"),

	MSG_MODEID ("MODEID"),


	MSG_PASSID ("PASSID"),

	MSG_TIMES ("TIMES"),

	MSG_EVENTTIME ("EVENTTIME"),

	MSG_RESULTCODE ("RESULTCODE"),

	MSG_EVENT ("EVENT");



	

	private MsgConditionKey(String conditionKey) {
		this.conditionKey = conditionKey;
	}

	private String conditionKey;

	public String toString() {
		return conditionKey;
	}

	@Override
	public String getValue() {
		return conditionKey;
	}
}
