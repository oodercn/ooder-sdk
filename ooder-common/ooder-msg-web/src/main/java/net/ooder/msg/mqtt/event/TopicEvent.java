/**
 * $RCSfile: TopicEvent.java,v $
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
package net.ooder.msg.mqtt.event;

import net.ooder.msg.TopicMsg;
import net.ooder.msg.mqtt.Topic;
import net.ooder.msg.mqtt.enums.TopicEnums;
import net.ooder.server.JDSClientService;

/**
 * <p>
 * Title: IM管理系统
 * </p>
 * <p>
 * Description: 主题订阅
 * </p>
 * <p>
 * Copyright: Copyright (c) 2020
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 1.0
 */
@SuppressWarnings("all")
public class TopicEvent<T extends TopicMsg> extends MQTTEvent<T> {

    public static final String CONTEXT_TOPIC = "TopicEvent.CONTEXT_COMMAND";

    public static final String  RepeatMqttEvent = "$RepeatMqttEvent";

    private Integer errCode;

    /**
     * GetwayEvent
     *
     * @param path
     * @param eventID
     */
    public TopicEvent(T topic, JDSClientService client, TopicEnums eventID, String sysCode) {
        super(topic, null);
        id = eventID;
        this.systemCode = sysCode;
        this.setClientService(client);

    }

    @Override
    public TopicEnums getID() {
        return (TopicEnums) id;

    }


    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    @Override
    String getExpression() {
        return RepeatMqttEvent;
    }
}


