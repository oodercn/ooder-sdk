/**
 * $RCSfile: UnSubscriptTopicCommand.java,v $
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
package net.ooder.msg.mqtt.command.cmd;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.command.TopicCommand;
import net.ooder.msg.mqtt.enums.MQTTCommandEnums;


@EsbBeanAnnotation(id = "UnSubscriptTopic", name = "取消订阅主题", expressionArr = "UnSubscriptTopicCommand()", desc = "UnSubscriptTopic")
public class UnSubscriptTopicCommand extends MQTTCommand implements TopicCommand {

    String topic;
    boolean retained;
    int Qos = 0;
    String commandServerUrl;
    String userName;

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public void setQos(int qos) {
        Qos = qos;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String password;

    public UnSubscriptTopicCommand() {
        super(MQTTCommandEnums.SubscriptTopic);

    }

    public String getCommandServerUrl() {
        return commandServerUrl;
    }

    public void setCommandServerUrl(String commandServerUrl) {
        this.commandServerUrl = commandServerUrl;
    }


    @Override
    public String getTopic() {
        return topic;
    }


}


