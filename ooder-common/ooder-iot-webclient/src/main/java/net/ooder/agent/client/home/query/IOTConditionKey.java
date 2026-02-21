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
 * @see IOTConditionKey
 */
public enum IOTConditionKey implements ConditionKey {

    ZNODE_ENDPOINTID("HA_ZNODE.ENDPOINTID"),

    ZNODE_DEVICEID("HA_ENDPOINT.DEVICEID"),

    ZNODE_PARENTID("HA_ENDPOINT.PARENTID"),

    ZNODE_PLACEID("HA_ENDPOINT.PLACEID"),

    ZNODE_AREAID("HA_ENDPOINT.AREAID"),

    ZNODE_ZMODULEID("HA_ENDPOINT.ZMODULEID"),

    ZNODE_CREATEUISERID("HA_ENDPOINT.CREATEUISERID"),





    ENDPOINT_ENDPOINTID("HA_ENDPOINT.ENDPOINTID"),

    ENDPOINT_DEVICEID("HA_ENDPOINT.DEVICEID"),

    ENDPOINT_SENSORTYPE("HA_ENDPOINT.SENSORTYPE"),

    ENDPOINT_NAME("HA_ENDPOINT.NAME"),

    ENDPOINT_EP("HA_ENDPOINT.EP"),

    ENDPOINT_PROFILEID("HA_ENDPOINT.PROFILEID"),

    ENDPOINT_VALUE("HA_ENDPOINT.VALUE"),

    ENDPOINT_HADEVICEID("HA_ENDPOINT.HADEVICEID"),

    ENDPOINT_NWKADDRESS("HA_ENDPOINT.NWKADDRESS"),

    ENDPOINT_IEEEADDRESS("HA_ENDPOINT.IEEEADDRESS"),










    DEVICE_SERIALNO("HA_DEVICE.SERIALNO"),

    DEVICE_MACADDRESS("HA_DEVICE.MACADDRESS"),

    DEVICE_BATCH("HA_DEVICE.BATCH"),

    DEVICE_FACTORY("HA_DEVICE.FACTORY"),

    DEVICE_BINDINGACCOUNT("HA_DEVICE.BINDINGACCOUNT"),

    DEVICE_CHANNEL( "HA_DEVICE.CHANNEL"),

    DEVICE_SUBSYSCODE("HA_DEVICE.SUBSYSCODE"),

    DEVICE_AREAID( "HA_DEVICE.AREAID"),

    DEVICE_PLACEID(  "HA_DEVICE.PLACEID"),

    DEVICE_NAME("HA_DEVICE.NAME"),

    DEVICE_STATES("HA_DEVICE.STATES"),

    DEVICE_DEVICETYPE("HA_DEVICE.DEVICETYPE"),

    DEVICE_ADDTIME( "HA_DEVICE.ADDTIME"),

    DEVICE_LASTLOGINTIME( "HA_DEVICE.LASTLOGINTIME"),



    SCENE_SCENEID( "HA_SCENE.SCENEID"),

    SCENE_SENSORID( "HA_SCENE.SENSORID"),

    SCENE_INTVALUE( "HA_SCENE.INTVALUE"),

    SCENE_OBJVALUE( "HA_SCENE.OBJVALUE"),

    SCENE_NAME( "HA_SCENE.NAME"),

    SCENE_STATUS( "HA_SCENE.STATUS");

    private IOTConditionKey(String conditionKey) {
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
