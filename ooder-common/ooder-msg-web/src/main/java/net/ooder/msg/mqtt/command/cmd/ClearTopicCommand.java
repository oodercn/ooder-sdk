/**
 * $RCSfile: ClearTopicCommand.java,v $
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


@EsbBeanAnnotation(id = "ClearTopic", name = "清空订阅主题", expressionArr = "ClearTopicCommand()", desc = "ClearTopicCommand")
public class ClearTopicCommand extends MQTTCommand implements TopicCommand {

    String topic;


    public void setTopic(String topic) {
        this.topic = topic;
    }


    public ClearTopicCommand() {
        super(MQTTCommandEnums.ClearTopic);

    }

    @Override
    public String getTopic() {
        return topic;
    }


}


