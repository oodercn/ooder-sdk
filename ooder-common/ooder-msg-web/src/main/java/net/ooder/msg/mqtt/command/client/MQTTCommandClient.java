/**
 * $RCSfile: MQTTCommandClient.java,v $
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
package net.ooder.msg.mqtt.command.client;

import net.ooder.msg.mqtt.JMQException;
import net.ooder.msg.mqtt.client.JMQClient;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.command.cmd.*;
import net.ooder.server.JDSClientService;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.Future;

public interface MQTTCommandClient {

    public static final String CTX_USER_ID = "MqttEngine.USERID";

    public boolean isOnLine() throws MqttException;


    public Future<ExecScriptCommand> sendExecScriptCommand(String sensorieee) throws JMQException;

    public Future<ExecScriptCommand> sendExecScriptCommand(String sensorieee, Integer delayTime) throws JMQException;


    public Future<SubscriptTopicCommand> sendSubscriptTopicCommand(String sensorieee) throws JMQException;

    public Future<SubscriptTopicCommand> sendSubscriptTopicCommand(String sensorieee, Integer delayTime) throws JMQException;


    public Future<SycnCommand> sendSycnCommand(String sensorieee) throws JMQException;

    public Future<SycnCommand> sendSycnCommand(String sensorieee, Integer delayTime) throws JMQException;


    public Future<UnSubscriptTopicCommand> sendUnSubscriptTopicCommand() throws JMQException;

    public Future<UnSubscriptTopicCommand> sendUnSubscriptTopicCommand( Integer delayTime) throws JMQException;


    /**
     * 固件升级命令
     *
     * @throws MqttException
     */
    public Future<ClearTopicCommand> sendClearTopicCommand(String vfsUrl, Integer delayTime) throws MqttException;

    public Future<ClearTopicCommand> sendClearTopicCommand(String vfsUrl) throws MqttException;


    public Future<MQTTCommand> sendCheckUpgradeCommand(String sensorieee, Integer delayTime) throws MqttException;

    public Future<MQTTCommand> sendCheckUpgradeCommand(String sensorieee) throws MqttException;

    public <T extends MQTTCommand>Future<T > sendCommand(T command, Integer delayTime) throws MqttException;

    public  <T extends MQTTCommand>  T getCommandById(String commandId);

    public JMQClient getGWClient()throws MqttException;

    public void setClientService(JDSClientService client);

}


