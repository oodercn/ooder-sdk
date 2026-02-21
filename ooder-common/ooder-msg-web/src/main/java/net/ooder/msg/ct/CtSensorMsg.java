/**
 * $RCSfile: CtSensorMsg.java,v $
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
package net.ooder.msg.ct;

import net.ooder.msg.SensorMsg;
import net.ooder.annotation.MethodChinaName;

public class CtSensorMsg extends CtMsg implements SensorMsg {

    private String sensorId;
    private String gatewayId;
    private String event;
    @MethodChinaName("获取传感器ID")
    @Override
    public String getSensorId() {
        return sensorId;
    }

    @MethodChinaName("获取事件")
    @Override
    public String getEvent() {
        return event;
    }

    @MethodChinaName("设置事件")
    @Override
    public void setEvent(String event) {

        this.event=event;
    }


    @MethodChinaName("设置传感器ID")
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @MethodChinaName("获取网关ID")
    @Override
    public String getGatewayId() {
        return gatewayId;
    }


    @MethodChinaName("设置网关ID")
    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

}


