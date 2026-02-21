/**
 * $RCSfile: LocalCommandFilterImpl.java,v $
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
package net.ooder.msg.mqtt.command.filter.command;

import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.ServerNode;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSCommand;
import net.ooder.common.JDSException;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.mqtt.command.filter.CommandFilter;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.msg.mqtt.MqttConstants;

/**
 * @author wenzhang
 */
public class LocalCommandFilterImpl implements CommandFilter {
    private Log logger = LogFactory.getLog(MqttConstants.CONFIG_ENGINE_KEY, LocalCommandFilterImpl.class);

    public LocalCommandFilterImpl() {

    }

    /**
     * 应用应该实现的过滤方法。
     * <p>
     * 需要过滤的对象
     *
     * @return
     */
    public boolean filterObject(JDSCommand command, JDSSessionHandle handle) {

        boolean isSended = false;
        String subsystemCode = JDSServer.getSessionhandleSystemCodeCache().get(handle.toString());
        ServerNode currServerBean = null;
        try {
            currServerBean = JDSServer.getInstance().getCurrServerBean();
            if (JDSServer.getInstance().isCometServer()) {
                isSended = this.sendMessage(handle, command, subsystemCode);
            }
        } catch (JDSException e) {
            e.printStackTrace();
            isSended = false;
        }

        return isSended;

    }

    private boolean sendMessage(JDSSessionHandle handle, JDSCommand command, String systemCode) {
        try {
            JDSClientService client = JDSServer.getInstance().getJDSClientService(handle, ConfigCode.fromType(systemCode));

            if (client != null && client.getConnectInfo() != null && client.getConnectionHandle().isconnect()) {
                logger.info("comet command [" + JSONObject.toJSONString(command) + "]");
                client.getConnectionHandle().send(command);
                return true;
            }
        } catch (JDSException e) {
            return false;
        }
        return false;

    }

}


