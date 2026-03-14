/**
 * $RCSfile: ConditionKey.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:08 $
 * <p>
 * Copyright (C) 2003 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
package net.ooder.agent.client.home.query;


import  net.ooder.common.ConditionKey;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 可以进行查询或排序的数据库字段。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 * @see HomeConditionKey
 */
public enum HomeConditionKey implements ConditionKey {
    //

    PLACE_NAME("HA_PLACE.NAME"),
    PLACE_PLACEID("HA_PLACE.PLACEID"),
    PLACE_USERID("HA_PLACE.USERID"),
    PLACE_MEMO("HA_PLACE.MEMO"),
    PLACE_ORGID("HA_PLACE.ORGID"),
    PLACE_PARENTID("HA_PLACE.PARENTID"),



    ALARM_TYPEID("HA_ALARM.ALARMID"),
    ALARM_CYCLE("HA_ALARM.CYCLE"),
    ALARM_SENSORID("HA_ALARM.SENSORID"),
    ALARM_ALERTCONTENT("HA_ALARM.ALERTCONTENT"),
    ALARM_COMFORT("HA_ALARM.COMFORT"),
    ALARM_SCENEID("HA_ALARM.SCENEID"),

    SENSORTYPE_TYPEID("HA_SENSORTYPE.TYPEID"),
    SENSORTYPE_TYPE("HA_SENSORTYPE.TYPE"),
    SENSORTYPE_NAME("HA_SENSORTYPE.NAME"),
    SENSORTYPE_DEVICEID("HA_SENSORTYPE.DEVICEID"),
    SENSORTYPE_ICON("HA_SENSORTYPE.ICON"),
    SENSORTYPE_COLOR("HA_SENSORTYPE.COLOR"),
    SENSORTYPE_HISDATAURL("HA_SENSORTYPE.HISDATAURL"),
    SENSORTYPE_ALARMURL("HA_SENSORTYPE.ALARMURL"),
    SENSORTYPE_DATALISTURL("HA_SENSORTYPE.DATALISTURL"),
    SENSORTYPE_HTMLTEMP("HA_SENSORTYPE.HTMLTEMP"),


    AREA_NAME("HA_AREA.NAME"),
    AREA_MEMO("HA_AREA.MEMO"),
    AREA_AREAID("HA_AREA.AREAID"),
    AREA_PLACEID("HA_AREA.PLACEID");


    private HomeConditionKey(String conditionKey) {
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
