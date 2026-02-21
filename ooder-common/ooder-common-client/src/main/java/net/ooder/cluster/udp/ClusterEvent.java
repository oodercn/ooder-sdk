/**
 * $RCSfile: ClusterEvent.java,v $
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
package net.ooder.cluster.udp;

import net.ooder.engine.JDSSessionHandle;
import net.sf.cglib.beans.BeanMap;

import java.util.UUID;

/**
 * 集群监听器参数传递
 *
 * @author wenzhang
 */
public class ClusterEvent {

    String eventId;

    String msgId;

    String token;

    Long sendTime;

    Integer timeout = 500;

    Integer maxtimes = 3;

    //默认串行
    PerformSequence sequence = PerformSequence.MEANWHILE;

    DeadLine deadLine = DeadLine.GOON;


    String sourceJson;

    String eventName;


    String systemCode;

    String expression;

    String sessionId;

    JDSSessionHandle sessionHandle;


    public synchronized ClusterEvent clone() {
        ClusterEvent clusterEvent = new ClusterEvent();
        BeanMap.create(clusterEvent).putAll(BeanMap.create(this));
        clusterEvent.setToken(UUID.randomUUID().toString());
        return clusterEvent;
    }


    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }


    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getMaxtimes() {
        return maxtimes;
    }

    public void setMaxtimes(Integer maxtimes) {
        this.maxtimes = maxtimes;
    }

    public PerformSequence getSequence() {
        return sequence;
    }

    public void setSequence(PerformSequence sequence) {
        this.sequence = sequence;
    }

    public DeadLine getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(DeadLine deadLine) {
        this.deadLine = deadLine;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public ClusterEvent() {

    }

    public JDSSessionHandle getSessionHandle() {
        return sessionHandle;
    }

    public void setSessionHandle(JDSSessionHandle sessionHandle) {
        this.sessionHandle = sessionHandle;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSourceJson() {
        return sourceJson;
    }

    public void setSourceJson(String sourceJson) {
       this.sourceJson = sourceJson;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }


    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
