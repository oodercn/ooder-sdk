/**
 * $RCSfile: JMQClient.java,v $
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
package net.ooder.msg.mqtt.client;

import net.ooder.engine.ConnectInfo;
import net.ooder.annotation.MethodChinaName;
import net.ooder.jmq.JMQUser;
import net.ooder.msg.Msg;
import net.ooder.msg.TopicMsg;
import net.ooder.msg.mqtt.JMQException;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.command.cmd.CreateTopicCommand;
import net.ooder.server.JDSClientService;

import java.util.concurrent.Future;

public interface JMQClient extends JDSClientService{

    public static final String CTX_USER_ID = "MqttEngine.USERID";

    public static final String CTX_USERS = "MqttEngine.USERS";


    /**
     * @param topic
     * @return
     * @throws JMQException
     */
    public void subscriptTopic(String topic) throws JMQException;


    /**
     * @param topic
     * @return
     * @throws JMQException
     */
    public void unSubscriptTopic(String topic) throws JMQException;


    /**
     * @param topic
     * @return
     * @throws JMQException
     */
    public CreateTopicCommand createTopic(String topic, boolean retained, int qos) throws JMQException;

    /**
     * @param topic
     * @return
     * @throws JMQException
     */
    public void deleteTopic(String topic) throws JMQException;


    /**
     * 检查是否在"
     *
     * @throws JDSException
     */
    @MethodChinaName("检查是否在线")
    public Boolean isOnLine() throws JMQException;


    /**
     * 客户端下"
     *
     * @param deviceId
     * @throws JMQException
     */
    public void clientOffLine(String deviceId) throws JMQException;


    /**
     * 客户端上"
     *
     * @param deviceId
     * @throws JMQException
     */
    public void clientOnLine(String deviceId) throws JMQException;

    /**
     * @param commandId
     * @return
     */
    public <T extends MQTTCommand> T getCommandById(String commandId) throws JMQException;


    /**
     * 发送命"
     *
     * @param command
     * @param delayTime
     * @return
     * @throws JDSException
     */
    public Future<MQTTCommand> sendCommand(MQTTCommand command, Integer delayTime) throws JMQException;


    /**
     * @return
     */
    public JMQUser getUserInfo()throws JMQException;

    /**
     * @param topicMsg
     */
    public TopicMsg sendMsg(TopicMsg topicMsg) throws JMQException;

    /**
     * @param sessionId
     * @param msg
     */
    public TopicMsg sendMsgToClient(String sessionId, String msg) throws JMQException;

    /**
     * @param topic
     * @param msg
     */
    public TopicMsg broadcast(String topic, String msg) throws JMQException;

    /**
     * @param account
     * @param msg
     */
    public TopicMsg sendMsgToPerson(String account, String msg) throws JMQException;


    /**
     * 发送消"
     *
     * @param msg
     * @throws JMQException
     */
    public boolean sendSystemMsg(Msg msg) throws JMQException;

    /**
     * 开始事务操"
     *
     * @throws JMQException
     */
    @MethodChinaName("开始事务操作")
    public void beginTransaction() throws JMQException;

    /**
     * 提交事务操作
     *
     * @throws JMQException
     */
    @MethodChinaName("提交事务操作")
    public void commitTransaction() throws JMQException;

    /**
     * 回滚事务操作
     *
     * @throws JMQException
     */
    @MethodChinaName("回滚事务操作")
    public void rollbackTransaction() throws JMQException;

    public void setSystemCode(String systemCode);

    public void setClientService(JDSClientService client);

    public ConnectInfo getConnectInfo();
}


