
/**
 * $RCSfile: TopicDataFilterImpl.java,v $
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
package net.ooder.msg.mqtt.command.filter.appmsg;

import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.Msg;
import net.ooder.msg.MsgType;
import net.ooder.msg.filter.MsgFilter;
import net.ooder.msg.mqtt.client.JMQClient;
import net.ooder.server.JDSServer;

/**
 * @author wenzhang
 */
public class TopicDataFilterImpl implements MsgFilter {


    private JDSSessionHandle handle;


    public boolean filterObject(Msg msg, JDSSessionHandle handle) {

        MsgType type = MsgType.fromType(msg.getType());
        this.handle = handle;

        if (type.equals(MsgType.TOPIC)) {
            try {
                sendMessage(msg);
            } catch (JDSException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;

    }


    private void sendMessage(Msg msg) throws JDSException {

        String systemCode = JDSServer.getInstance().getSessionhandleSystemCodeCache().get(handle.toString());
        if (systemCode != null) {
            JMQClient client = (JMQClient) JDSServer.getInstance().getJDSClientService(handle, ConfigCode.fromType(systemCode));
            if (client != null && client.getConnectInfo() != null) {
                client.sendSystemMsg(msg);
            }
        }

    }


}


