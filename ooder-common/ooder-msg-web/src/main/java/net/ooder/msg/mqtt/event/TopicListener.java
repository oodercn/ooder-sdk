/**
 * $RCSfile: TopicListener.java,v $
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

import net.ooder.annotation.MethodChinaName;
import net.ooder.msg.TopicMsg;
import net.ooder.msg.mqtt.JMQException;

/**
 * <p>
 * Title: Mqtt管理系统
 * </p>
 * <p>
 * Description: 核心事件监听器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2020
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 2.0
 */


public interface TopicListener extends java.util.EventListener {

    /**
     * @param event
     * @throws JMQException
     */
    public void subscriptTopic(TopicEvent<TopicMsg> event) throws JMQException;

    /**
     * @throws JMQException
     */
    public void unSubscriptTopic(TopicEvent<TopicMsg> event) throws JMQException;


    /**
     * @param event
     * @throws JMQException
     */
    public void createTopic(TopicEvent<TopicMsg> event) throws JMQException;

    /**
     * @param event
     * @throws JMQException
     */
    public void clearTopic(TopicEvent<TopicMsg> event) throws JMQException;

    /**
     * @param event
     * @throws JMQException
     */
    public void deleteTopic(TopicEvent<TopicMsg> event) throws JMQException;


    @MethodChinaName("发布订阅消息")
    public void publicTopicMsg(TopicEvent<TopicMsg> event) throws JMQException;




    /**
     * 得到系统Code
     *
     * @return
     */
    public String getSystemCode();

}


