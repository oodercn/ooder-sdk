/**
 * $RCSfile: TopicAPI.java,v $
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
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.msg.mqtt.command.cmd.CreateTopicCommand;
import net.ooder.msg.mqtt.command.cmd.SubscriptTopicCommand;
import net.ooder.msg.mqtt.command.cmd.UnSubscriptTopicCommand;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/api/mqtt/topic/")
@MethodChinaName("消息服务")
public class TopicAPI implements TopicService {


    @Override
    @RequestMapping(method = {RequestMethod.POST}, value = {"subscriptTopic"})
    public ResultModel<SubscriptTopicCommand> subscriptTopic(String topic) {
        return getService().subscriptTopic(topic);
    }


    @RequestMapping(method = {RequestMethod.POST}, value = {"unSubscriptTopic"})
    public ResultModel<UnSubscriptTopicCommand> unSubscriptTopic(String topic) {
        return getService().unSubscriptTopic(topic);
    }


    @RequestMapping(method = {RequestMethod.POST}, value = {"createTopic"})
    public ResultModel<CreateTopicCommand> createTopic(String topic, Boolean retained, Integer qos) {
        return getService().createTopic(topic, retained, qos);
    }



    <T> TopicService getService() {
        return (TopicService) EsbUtil.parExpression(TopicService.class);
    }

}


