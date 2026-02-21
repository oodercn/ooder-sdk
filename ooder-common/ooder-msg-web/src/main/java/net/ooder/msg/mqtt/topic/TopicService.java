/**
 * $RCSfile: TopicService.java,v $
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
package net.ooder.msg.mqtt.topic;

import net.ooder.annotation.MethodChinaName;
import net.ooder.config.ResultModel;
import net.ooder.msg.mqtt.JMQException;
import net.ooder.msg.mqtt.command.cmd.CreateTopicCommand;
import net.ooder.msg.mqtt.command.cmd.SubscriptTopicCommand;
import net.ooder.msg.mqtt.command.cmd.UnSubscriptTopicCommand;
import org.springframework.web.bind.annotation.ResponseBody;

public interface TopicService {
    /**
     * @param topic
     * @return
     * @throws JMQException
     */
    @MethodChinaName("订阅")
    @ResponseBody
    public ResultModel<SubscriptTopicCommand> subscriptTopic(String topic);


    /**
     * @param topic
     * @return
     * @throws JMQException
     */
    @MethodChinaName("取消订阅")
    @ResponseBody
    public ResultModel<UnSubscriptTopicCommand> unSubscriptTopic(String topic);


    /**
     * @param topic
     * @return
     * @throws JMQException
     */
    @MethodChinaName("创建")
    @ResponseBody
    public ResultModel<CreateTopicCommand> createTopic(String topic, Boolean retained, Integer qos);


}


