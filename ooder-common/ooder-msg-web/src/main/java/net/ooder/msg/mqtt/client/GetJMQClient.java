/**
 * $RCSfile: GetJMQClient.java,v $
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


import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.JDSException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.msg.mqtt.JMQException;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;

@EsbBeanAnnotation(expressionArr = "JMQC($JDSC())", id = "JMQC")
public class GetJMQClient extends AbstractFunction {

    public JMQClient perform(JDSClientService clientService) throws JMQException {

        if (clientService == null || clientService.getConnectInfo() == null) {
            try {
                clientService = JDSServer.getInstance().getAdminClient();
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }

        if (clientService == null) {
            throw new JMQException("session失效,请重新登录！", JMQException.NOTLOGINEDERROR);
        }


        JMQClient client = null;
        try {
            client = JMQClientFactory.getJMQClient(clientService);
        } catch (JDSException e) {
            throw new JMQException(e.getMessage(), JMQException.NOTLOGINEDERROR);
        }
        if (client == null) {
            throw new JMQException(" SessionHandle:[" + clientService.getSessionHandle() + "]未获取授权！", JMQException.NOTLOGINEDERROR);
        }
        return client;
    }
}

