
/**
 * $RCSfile: TopicListenerAdapter.java,v $
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
import net.ooder.msg.mqtt.JMQException;

public abstract class TopicListenerAdapter implements TopicListener {

    @Override
    public void subscriptTopic(TopicEvent<TopicMsg> event) throws JMQException {

    }

    @Override
    public void unSubscriptTopic(TopicEvent<TopicMsg> event) throws JMQException {

    }

    @Override
    public void createTopic(TopicEvent<TopicMsg> event) throws JMQException {

    }

    @Override
    public void clearTopic(TopicEvent<TopicMsg> event) throws JMQException {

    }

    @Override
    public void deleteTopic(TopicEvent<TopicMsg> event) throws JMQException {

    }

    @Override
    public void publicTopicMsg(TopicEvent<TopicMsg> event) throws JMQException {

    }

    @Override
    public String getSystemCode() {
        return null;
    }
}


