/**
 * $RCSfile: JMQClientFactory.java,v $
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
import net.ooder.server.JDSClientService;

import java.util.HashMap;
import java.util.Map;

public class JMQClientFactory {
    static Map<JDSClientService, JMQClient> clientMap = new HashMap<JDSClientService, JMQClient>();

    public static JMQClient getJMQClient(JDSClientService client) throws JDSException {
        JMQClient jmqClientService = clientMap.get(client);
        if (jmqClientService == null) {
            jmqClientService = new JMQClientImpl(client);
            clientMap.put(client, jmqClientService);
        }
        return jmqClientService;
    }
}


