/**
 * $RCSfile: ClusterCommand.java,v $
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
public class ClusterCommand {


    String msgId;

    String token;

    Integer timeout = 100;

    Integer maxtimes = 3;

    //默认串行
    PerformSequence sequence = PerformSequence.MEANWHILE;

    DeadLine deadLine = DeadLine.DELAY;

    String eventId;


    String systemCode;

    String expression;

    String command;

    String commandJson;

    JDSSessionHandle sessionHandle;

    String sessionId;


    public synchronized ClusterCommand clone() {
        ClusterCommand clusterCommand = new ClusterCommand();
        BeanMap.create(clusterCommand).putAll(BeanMap.create(this));
        clusterCommand.setToken(UUID.randomUUID().toString());
        return clusterCommand;
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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


    public ClusterCommand() {

    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommandJson() {
        return commandJson;
    }

    public void setCommandJson(String commandJson) {
        this.commandJson = commandJson;
    }

    public JDSSessionHandle getSessionHandle() {
        return sessionHandle;
    }

    public void setSessionHandle(JDSSessionHandle sessionHandle) {
        this.sessionHandle = sessionHandle;
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
