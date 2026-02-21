/**
 * $RCSfile: RemoteCommandFilterImpl.java,v $
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.ServerNode;
import net.ooder.cluster.udp.ClusterCommand;
import net.ooder.common.JDSCommand;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.annotation.Enumstype;
import net.ooder.msg.mqtt.MqttConstants;
import net.ooder.msg.mqtt.command.filter.CommandFilter;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.server.SubSystem;
import net.ooder.web.RemoteConnectionManager;
import net.sf.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author wenzhang
 */

public class RemoteCommandFilterImpl implements CommandFilter {
    private Log logger = LogFactory.getLog(MqttConstants.CONFIG_ENGINE_KEY, RemoteCommandFilterImpl.class);

    public boolean filterObject(final JDSCommand command, final JDSSessionHandle handle) {
        final String subsystemCode = JDSServer.getSessionhandleSystemCodeCache().get(handle.getSessionID());
        if (subsystemCode != null) {
            ServerNode currServerBean = null;
            try {
                currServerBean = JDSServer.getInstance().getCurrServerBean();
                SubSystem subSystem = JDSServer.getClusterClient().getSystem(subsystemCode);
                ExecutorService executorService = RemoteConnectionManager.getConntctionService("RemoteCommand");
                logger.info("RemoteCommandFilterImpl  sendMessage " + JSON.toJSONString(subSystem));
                if (subSystem != null && !subsystemCode.equals(currServerBean.getId()) || true) {
                    if (currServerBean.getType() != null && currServerBean.getType().equals("main")) {
                        return this.sendMessage(handle, command, subsystemCode);
                    } else {
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                Long sendCommandTime = System.currentTimeMillis();
                                Boolean isSend = null;
                                try {
                                    Map commandBeanmap = BeanMap.create(command);
                                    Enumstype commandCMD = (Enumstype) commandBeanmap.get("command");
                                    Iterator<String> keyit = commandBeanmap.keySet().iterator();
                                    // 过滤空值
                                    Map valueMap = new HashMap();
                                    for (; keyit.hasNext(); ) {
                                        final String key = keyit.next();
                                        final Object value = commandBeanmap.get(key);

                                        if (value != null && !value.equals("")) {
                                            valueMap.put(key, value);
                                        }
                                    }
                                    final ClusterCommand clusterCommand = new ClusterCommand();
                                    clusterCommand.setCommand(commandCMD.getType());
                                    clusterCommand.setCommandJson(JSONObject.toJSONString(command));
                                    clusterCommand.setExpression("$RepeatMqttMsg");
                                    clusterCommand.setSessionId(handle.getSessionID());
                                    clusterCommand.setMsgId(command.getCommandId());
                                    clusterCommand.setSessionHandle(handle);
                                    clusterCommand.setSystemCode(subsystemCode);
                                    String commandStr = JSONObject.toJSON(clusterCommand).toString();
                                    isSend = JDSServer.getClusterClient().getUDPClient().send(commandStr);

                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }

                } else {
                    return false;
                }

            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private Boolean sendMessage(JDSSessionHandle handle, JDSCommand command, String systemCode) throws JDSException {
        logger.info("start  sendMessage " + JSON.toJSONString(command));
        String subsystemCode = JDSServer.getSessionhandleSystemCodeCache().get(handle.toString());
        ServerNode remoteServerBean = JDSServer.getClusterClient().getAllServerMap().get(subsystemCode);
        Person toPerson;
        try {
            toPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(remoteServerBean.getId() + remoteServerBean.getId());
            ConnectInfo connInfo = new ConnectInfo(toPerson.getID(), toPerson.getAccount(), toPerson.getPassword());
            Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connInfo);
            for (JDSSessionHandle serverhandle : sessionHandleList) {
                JDSClientService client = JDSServer.getInstance().getJDSClientService(serverhandle, OrgConstants.CONFIG_KEY);
                if (client.getConnectInfo() == null) {
                    client.connect(connInfo);
                }
                command.setSystemCode(systemCode);
                logger.info("start updCommand = " + JSON.toJSONString(command));
                client.getConnectionHandle().repeatCommand(command, handle);
                logger.info("serverhandle=" + serverhandle);
            }
            return true;
        } catch (PersonNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;

    }

}


