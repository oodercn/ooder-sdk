/**
 * $RCSfile: P2PService.java,v $
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
import net.ooder.msg.Msg;
import net.ooder.msg.TopicMsg;
import org.springframework.web.bind.annotation.ResponseBody;

public interface P2PService {

    @MethodChinaName("私信")
    @ResponseBody
    public ResultModel<Msg> sendMsg2Person(String account, String body);

    @MethodChinaName("广播消息")
    @ResponseBody
    public ResultModel<TopicMsg> sendTopic(String sessionId, String topic, String msg);

    @MethodChinaName("指定客户消息")
    @ResponseBody
    public ResultModel<Msg> sendMsg2Client(String sessionId, String body);

}


