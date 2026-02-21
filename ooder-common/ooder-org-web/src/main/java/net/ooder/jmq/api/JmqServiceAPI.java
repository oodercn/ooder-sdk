package net.ooder.jmq.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.annotation.MethodChinaName;
import net.ooder.common.ContextType;
import net.ooder.common.TokenType;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.jmq.JMQUser;
import net.ooder.jmq.service.JmqService;
import net.ooder.msg.TopicMsg;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/jmq/")
@MethodChinaName(cname = "消息服务")
@EsbBeanAnnotation(dataType = ContextType.Server,tokenType = TokenType.user)
public class JmqServiceAPI implements JmqService {


    @Override
    @MethodChinaName(cname = "获取连接信息")
    @RequestMapping(method = RequestMethod.POST, value = "getUserInfo")
    public @ResponseBody
    ResultModel<JMQUser> getUserInfo() {
        return getJmqService().getUserInfo();
    }

    @Override
    @MethodChinaName(cname = "发送主题消息")
    @RequestMapping(method = RequestMethod.POST, value = "sendMsg")
    public @ResponseBody
    ResultModel<TopicMsg> sendMsg(@RequestBody TopicMsg topicMsg) {
        return getJmqService().sendMsg(topicMsg);
    }

    @Override
    @MethodChinaName(cname = "执行脚本")
    @RequestMapping(method = RequestMethod.POST, value = "execScript")
    @ResponseBody
    public ResultModel<Boolean> execScript(String script) {
        return getJmqService().execScript(script);
    }

    @Override
    @MethodChinaName(cname = "向指定客户端发送消息")
    @RequestMapping(method = RequestMethod.POST, value = "sendMsgToClient")
    public @ResponseBody
    ResultModel<TopicMsg> sendMsgToClient(String sessionId, String msg) {
        return getJmqService().sendMsgToClient(sessionId, msg);
    }

    @Override
    @MethodChinaName(cname = "广播信息")
    @RequestMapping(method = RequestMethod.POST, value = "broadcast")
    public @ResponseBody
    ResultModel<TopicMsg> broadcast(String topic, String msg) {
        return getJmqService().broadcast(topic, msg);
    }

    @Override
    @MethodChinaName(cname = "向指定人员发送消息")
    @RequestMapping(method = RequestMethod.POST, value = "sendMsgToPerson")
    public @ResponseBody
    ResultModel<TopicMsg> sendMsgToPerson(String account, String msg) {
        return getJmqService().sendMsgToPerson(account, msg);
    }

    JmqService getJmqService() {
        return (JmqService) EsbUtil.parExpression(JmqService.class);
    }
}
