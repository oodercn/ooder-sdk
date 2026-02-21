package net.ooder.jmq.service;

import net.ooder.config.ResultModel;
import net.ooder.jmq.JMQUser;
import net.ooder.msg.TopicMsg;
import org.springframework.web.bind.annotation.RequestBody;

public interface JmqService {

    ResultModel<JMQUser> getUserInfo();

    ResultModel<TopicMsg> sendMsg(@RequestBody TopicMsg topicMsg);

    ResultModel<Boolean> execScript(String script);

    ResultModel<TopicMsg> sendMsgToClient(String sessionId, String msg);

    ResultModel<TopicMsg> broadcast(String topic, String msg);

    ResultModel<TopicMsg> sendMsgToPerson(String account, String msg);

}
