/**
 * $RCSfile: WebClusterManagerImpl.java,v $
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
package net.ooder.web.client;

import net.ooder.client.JDSSessionFactory;
import net.ooder.cluster.ClusterMananer;
import net.ooder.cluster.ServerNode;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.SystemStatus;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.context.JDSActionContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.org.OrgManager;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class WebClusterManagerImpl implements ClusterMananer {
    private static Map<String, List<String>> allIpMap = new LinkedHashMap<String, List<String>>();

    protected static Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, WebClusterManagerImpl.class);

    public ServerNode getSubServer(String sessionId, ConfigCode configCode) {
        List<ServerNode> serverBeanList = getServerNodeListOrderByPersonCount(configCode);
        ServerNode serverBean = serverBeanList.get(0);
        JDSSessionFactory factory = new JDSSessionFactory(JDSActionContext.getActionContext());

        try {
            if (sessionId != null) {
                JDSSessionHandle handle = factory.getSessionHandleBySessionId(sessionId);
                ConnectInfo connectInfo = JDSServer.getInstance().getConnectInfo(handle);
                if (connectInfo != null) {
                    OrgManager orgManager = OrgManagerFactory.getInstance().getOrgManagerByName(configCode.getType());
                    for (int k = 0; k < serverBeanList.size(); k++) {
                        serverBean = serverBeanList.get(k);
                        String userexpression = serverBean.getUserexpression();
                        List<String> iplist;
                        if (allIpMap.containsKey(serverBean.getId())) {
                            iplist = allIpMap.get(serverBean.getId());
                        } else {
                            iplist = new ArrayList<String>();
                            allIpMap.put(serverBean.getId(), iplist);
                        }
                        Integer size = allIpMap.get(serverBean.getId()).size();

                        if (serverBean.par().contains(connectInfo.getUserID()) && Integer.valueOf(serverBean.getMinconnection()) > size) {
                            if (!iplist.contains(connectInfo.getUserID())) {
                                iplist.add(connectInfo.getUserID());
                                allIpMap.put(serverBean.getId(), iplist);
                            }
                            return serverBean;
                        }
                    }
                }
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }

        return serverBean;
    }

    public Map<String, List<String>> getAllIpMap() {
        return allIpMap;
    }

    public Map<String, ServerNode> getServerMap(ConfigCode configCode) {
            return  JDSServer.getClusterClient().getServerNodeListByConfigCode(configCode).getEsbBeanMap();

    }

    public List<ServerNode> getServerNodeListOrderByPersonCount(ConfigCode systemCode) {
        List<ServerNode> serverBeanList = new ArrayList<ServerNode>();
        LinkedHashMap map = new LinkedHashMap();
        map.putAll(getServerMap(systemCode));

        Iterator<String> it = map.keySet().iterator();

        for (; it.hasNext(); ) {
            String key = it.next();
            ServerNode serverBean = getServerMap(systemCode).get(key);
            if (serverBean.getStatus().equals(SystemStatus.ONLINE)) {
                serverBeanList.add(serverBean);
            }

        }

        java.util.Collections.sort(serverBeanList, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final ServerNode server1 = (ServerNode) o1;
                final ServerNode server2 = (ServerNode) o2;
                if (!allIpMap.containsKey(server1.getId())) {

                    return 1;
                }
                if (!allIpMap.containsKey(server2.getId())) {
                    return -1;
                }

                if (allIpMap.get(server1.getId()).size() > allIpMap.get(server2.getId()).size()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return serverBeanList;
    }

    public boolean experssPar(String expressStr) {
        ActionContext context = ActionContext.getContext();
        if (expressStr == null || expressStr.equals("")) {
            return true;
        }
        Object result = true;
        try {
            result = context.getValueStack().findValue(expressStr, boolean.class);

        } catch (Exception e) {

            return false;
        }

        if (result instanceof Boolean) {
            return ((Boolean) result).booleanValue();
        } else {
            return true;
        }
    }

    public String getLocalIp() {
        String localIp = null;
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return localIp;
    }

    public void clearUser(String personId) {
        Map<String, List<String>> allIpMap = getAllIpMap();
        Iterator<String> it = allIpMap.keySet().iterator();
        for (; it.hasNext(); ) {
            String key = it.next();
            List<String> personList = allIpMap.get(key);
            if (personList.contains(personId)) {
                personList.remove(personId);
            }
        }
    }

    public String getServerIdBySessionId(String personId) {
        Map<String, List<String>> allIpMap = getAllIpMap();
        Iterator<String> it = allIpMap.keySet().iterator();
        for (; it.hasNext(); ) {
            String key = it.next();
            List<String> personList = allIpMap.get(key);
            if (personList.contains(personId)) {
                return key;
            }
        }
        return null;
    }

}
