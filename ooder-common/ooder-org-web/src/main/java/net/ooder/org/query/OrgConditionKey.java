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
 * Company:raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 * @see OrgConditionKey
 */
public enum OrgConditionKey implements ConditionKey {


    PERSON_PERSONID("RO_PERSON.PERSONID"),

    PERSON_USERID("RO_PERSON.USERID"),

    PERSON_STATUS("RO_PERSON.STATUS"),

    PERSON_ACCOUNTYTPE("RO_PERSON.ACCOUNTYTPE"),

    PERSON_LASTLOGINDATE("RO_PERSON.LASTLOGINDATE"),

    PERSON_MOBILE("RO_PERSON.MOBILE"),

    PERSON_NAME("RO_PERSON.NAME"),

    PERSON_CREATETIME("RO_PERSON.CREATETIME"),

    ORG_ORGID("RO_ORG.ORGID"),

    ORG_NAME("RO_ORG.NAME"),

    ORG_PARENTID("RO_ORG.PARENTID"),

    ROLE_ROLEID("RO_ROLE.ROLEID"),

    ROLE_NAME("RO_ROLE.NAME"),

    ROLE_TYPE("RO_ROLE.TYPE");


    private OrgConditionKey(String conditionKey) {
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
