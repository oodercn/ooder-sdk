/**
 * $RCSfile: CreateTopicCommand.java,v $
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

@EsbBeanAnnotation(id = "CreateTopic", name = "新建主题", expressionArr = "CreateTopic()", desc = "CreateTopic")
public class CreateTopicCommand extends MQTTCommand implements TopicCommand {

    String topic;
    boolean retained;
    int qos = 0;
    String commandServerUrl;
    String userName;
    String password;

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
        this.qos = qos;
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


    public CreateTopicCommand() {
        super(MQTTCommandEnums.CreateTopic);

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


    public boolean getRetained() {
        return retained;
    }


    public int getQos() {
        return 0;
    }
}


