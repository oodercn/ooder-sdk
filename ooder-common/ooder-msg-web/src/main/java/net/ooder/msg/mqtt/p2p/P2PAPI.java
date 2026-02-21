/**
 * $RCSfile: P2PAPI.java,v $
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
package net.ooder.msg.mqtt.p2p;

import net.ooder.annotation.MethodChinaName;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.msg.Msg;
import net.ooder.msg.TopicMsg;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/api/mqtt/p2p/")
@MethodChinaName("消息服务")
public class P2PAPI implements P2PService {

    @RequestMapping(method = {RequestMethod.POST}, value = {"sendMsg2Person"})
    @Override
    public ResultModel<Msg> sendMsg2Person(String account, String body) {
        return getService().sendMsg2Person(account, body);
    }

    @RequestMapping(method = {RequestMethod.POST}, value = {"sendTopic"})
    @Override
    public ResultModel<TopicMsg> sendTopic(String sessionId, String topic, String msg) {
        return getService().sendTopic(sessionId, topic, msg);
    }

    @Override
    @RequestMapping(method = {RequestMethod.POST}, value = {"sendMsg2Client"})
    public ResultModel<Msg> sendMsg2Client(String sessionId, String body) {
        return getService().sendMsg2Client(sessionId, body);
    }

    <T> P2PService getService() {
        return (P2PService) EsbUtil.parExpression(P2PService.class);
    }
}


