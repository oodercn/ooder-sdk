/**
 * $RCSfile: CtMsg.java,v $
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

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.MsgStatus;
import net.ooder.msg.Msg;
import net.ooder.org.conf.OrgConstants;
import net.ooder.annotation.MethodChinaName;

public class CtMsg implements Msg {

    public String systemCode = OrgConstants.CONFIG_KEY.getType();

    public String id;
    public String type;
    public String receiver;

    public Long receiveTime;


    public String title;
    public Integer times = 1;
    public String from;


    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Long arrivedTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Long eventTime;
    public String body;
    public MsgStatus status = MsgStatus.NORMAL;

    @MethodChinaName("获取系统代码")
    @Override
    public String getSystemCode() {
        return systemCode;
    }

    @MethodChinaName("设置系统代码")
    @Override
    public void setSystemCode(String systemCode) {
        this.systemCode=systemCode;
    }

    public CtMsg(){

    }
    public CtMsg(Msg msg){
        this.setType(msg.getType());
        this.setId(msg.getId());
        this.setEventTime(msg.getEventTime());
        this.setArrivedTime(msg.getArrivedTime());
        this.setBody(msg.getBody());
        this.setFrom(msg.getFrom());
        this.setReceiver(msg.getReceiver());
        this.setStatus(msg.getStatus());
        this.setTimes(msg.getTimes());
        this.setTitle(msg.getTitle());
        this.setSystemCode(msg.getSystemCode());
        this.setReceiveTime(msg.getReceiveTime());

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
        this.from=from;

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
}


