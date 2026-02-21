/**
 * $RCSfile: CtRMsg.java,v $
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

import net.ooder.common.CommandEventEnums;
import net.ooder.common.MsgStatus;
import net.ooder.msg.RMsg;
import net.ooder.msg.TopicMsg;
import net.ooder.annotation.MethodChinaName;

public class CtRMsg extends CtMsg implements RMsg, TopicMsg {


    String lasterSystemCode;

    String modeId;

    String passId;

    String event;

    CommandEventEnums resultCode;


    String sensorId;

    String gatewayId;


    private String topic;
    private Boolean retained = true;
    private Integer qos = 0;


    @MethodChinaName("获取主题")
    @Override
    public String getTopic() {
        return topic;
    }

    @MethodChinaName("设置主题")
    @Override
    public void setTopic(String topic) {
        this.topic = topic;
    }

    @MethodChinaName("获取保留标志")
    @Override
    public Boolean getRetained() {
        return retained;
    }

    @MethodChinaName("设置保留标志")
    @Override
    public void setRetained(Boolean retained) {
        this.retained = retained;
    }

    @MethodChinaName("获取服务质量")
    @Override
    public Integer getQos() {
        return qos;
    }

    @MethodChinaName("设置服务质量")
    @Override
    public void setQos(Integer qos) {
        this.qos = qos;
    }

    @MethodChinaName("获取系统代码")
    @Override
    public String getSystemCode() {
        return systemCode;
    }

    @MethodChinaName("设置系统代码")
    @Override
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }


    @MethodChinaName("获取消息ID")
    @Override
    public String getId() {
        return id;
    }

    @MethodChinaName("设置消息ID")
    public void setId(String id) {
        this.id = id;
    }

    @MethodChinaName("获取消息类型")
    @Override
    public String getType() {
        return type;
    }

    @MethodChinaName("设置消息类型")
    public void setType(String type) {
        this.type = type;
    }


    @MethodChinaName("获取接收人")
    @Override
    public String getReceiver() {
        return receiver;
    }

    @MethodChinaName("设置接收人")
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


    @MethodChinaName("设置接收时间")
    public void setReceiveTime(Long receiveTime) {
        this.receiveTime = receiveTime;
    }

    @MethodChinaName("获取接收时间")
    @Override
    public Long getReceiveTime() {
        return receiveTime;
    }

    @MethodChinaName("获取消息标题")
    @Override
    public String getTitle() {
        return title;
    }


    @MethodChinaName("设置消息标题")
    public void setTitle(String title) {
        this.title = title;
    }

    @MethodChinaName("获取消息次数")
    @Override
    public Integer getTimes() {
        return times;
    }


    @MethodChinaName("设置消息次数")
    public void setTimes(Integer times) {
        this.times = times;
    }


    @MethodChinaName("获取发送人")
    @Override
    public String getFrom() {
        return from;
    }


    @MethodChinaName("设置发送人")
    public void setFrom(String from) {
        this.from = from;

    }


    @MethodChinaName("获取到达时间")
    @Override
    public Long getArrivedTime() {
        return arrivedTime;
    }

    @MethodChinaName("设置到达时间")
    public void setArrivedTime(Long arrivedTime) {
        this.arrivedTime = arrivedTime;
    }


    @MethodChinaName("获取事件时间")
    @Override
    public Long getEventTime() {
        return eventTime;
    }


    @MethodChinaName("设置事件时间")
    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }


    @MethodChinaName("获取消息体")
    @Override
    public String getBody() {
        return body;
    }


    @MethodChinaName("设置消息体")
    public void setBody(String body) {
        this.body = body;
    }

    @MethodChinaName("获取消息状态")
    @Override
    public MsgStatus getStatus() {
        return status;
    }


    @MethodChinaName("设置消息状态")
    public void setStatus(MsgStatus status) {
        this.status = status;
    }


    @MethodChinaName("获取最后系统代码")
    @Override
    public String getLasterSystemCode() {
        return lasterSystemCode;
    }


    @MethodChinaName("设置最后系统代码")
    public void setLasterSystemCode(String lasterSystemCode) {
        this.lasterSystemCode = lasterSystemCode;
    }

    @MethodChinaName("获取模式ID")
    @Override
    public String getModeId() {
        return modeId;
    }

    @MethodChinaName("设置模式ID")
    @Override
    public void setModeId(String modeId) {
        this.modeId = modeId;
    }

    @MethodChinaName("获取通行ID")
    @Override
    public String getPassId() {
        return passId;
    }

    @MethodChinaName("设置通行ID")
    @Override
    public void setPassId(String passId) {
        this.passId = passId;
    }

    @MethodChinaName("获取事件")
    @Override
    public String getEvent() {
        return event;
    }

    @MethodChinaName("设置事件")
    @Override
    public void setEvent(String event) {
        this.event = event;
    }

    @MethodChinaName("获取结果代码")
    @Override
    public CommandEventEnums getResultCode() {
        return resultCode;
    }

    @MethodChinaName("设置结果代码")
    @Override
    public void setResultCode(CommandEventEnums resultCode) {
        this.resultCode = resultCode;
    }

    @MethodChinaName("获取传感器ID")
    @Override
    public String getSensorId() {
        return sensorId;
    }

    @MethodChinaName("设置传感器ID")
    @Override
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @MethodChinaName("获取网关ID")
    @Override
    public String getGatewayId() {
        return gatewayId;
    }

    @MethodChinaName("设置网关ID")
    @Override
    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }
}


