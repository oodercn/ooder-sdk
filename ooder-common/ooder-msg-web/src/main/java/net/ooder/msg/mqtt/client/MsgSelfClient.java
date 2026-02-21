/**
 * $RCSfile: MsgSelfClient.java,v $
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
package net.ooder.msg.mqtt.client;

import net.ooder.common.JDSException;
import net.ooder.msg.TopicMsg;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class MsgSelfClient {
    public static final String HOST = "ws://127.0.0.1:7019";
    private static final String userName = "admin";
    private static final String passWord = "admin";
    MqttClient client = null;
    private static MsgSelfClient instance = null;
    private static Map<String, TopicMsg> topicMsgMap = new HashMap<>();
    public static final String THREAD_LOCK = "Thread Lock";
    private String sessionId = "";

    /**
     * 取得JDSServer服务器的单例实现
     *
     * @return
     * @throws JDSException
     */
    public static MsgSelfClient getInstance(String sessionId) {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new MsgSelfClient(sessionId);
                }
            }
        }
        return instance;
    }

    public MsgSelfClient(String sessionId) {
        this.sessionId = sessionId;
    }


    public void sendTopicMsg(TopicMsg event) {
        try {
            if (topicMsgMap.get(event.getId()) == null) {
                topicMsgMap.put(event.getId(), event);
                MqttMessage message = new MqttMessage();
                message.setQos(event.getQos());
                message.setRetained(true);
                this.getClient().setCallback(new PushCallback());
                message.setPayload(event.getBody().getBytes(Charset.forName("utf-8")));
                MqttTopic topic = this.getClient().getTopic(event.getTopic());
                this.getClient().setCallback(new PushCallback());
                MqttDeliveryToken token = topic.publish(message);
                token.waitForCompletion();
            }


        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    public MqttClient getClient() {

        if (client == null || !client.isConnected()) {
            try {
                client = new MqttClient(HOST, sessionId, new MemoryPersistence());
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(false);
                options.setUserName(userName);
                options.setPassword(passWord.toCharArray());
                // 设置超时时间
                options.setConnectionTimeout(10);
                // 设置会话心跳时间
                options.setKeepAliveInterval(20);
                client.connect(options);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }


        return client;
    }

}


