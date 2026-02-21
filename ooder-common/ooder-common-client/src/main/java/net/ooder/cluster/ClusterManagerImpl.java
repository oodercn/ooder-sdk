/**
 * $RCSfile: ClusterManagerImpl.java,v $
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
package net.ooder.cluster;

import net.ooder.common.ConfigCode;
import net.ooder.common.JDSConstants;
import net.ooder.common.SystemStatus;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.server.JDSServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class ClusterManagerImpl implements ClusterMananer {
    private static final Logger logger = LoggerFactory.getLogger(ClusterManagerImpl.class);

    protected static Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY,
            ClusterManagerImpl.class);
    private static Map<String, List<String>> allIpMap = new LinkedHashMap<String, List<String>>();

    public ServerNode getSubServer(String personId, ConfigCode configCode) {

        logger.info("集群服务查询:id={},systemCode={}", personId, configCode);

        Collection<ServerNode> collect = getServerMap(configCode).values();
        List<ServerNode> serverNodeList = new ArrayList<>(collect.size());
        for (ServerNode serverNode : collect) {
            if (SystemStatus.ONLINE.equals(serverNode.getStatus())) {
                serverNodeList.add(serverNode);
            }
        }

        int count = serverNodeList.size();

        int index = Math.abs(personId.hashCode() % count);

        ServerNode ServerNode = serverNodeList.get(index);

        logger.info("集群服务查询:id={},systemCode={},serverCount={}, ServerNode={}",
                personId,
                configCode,
                serverNodeList.size(),
                ServerNode.getUrl());

        return ServerNode;
    }

    public ServerNode getSubServerOLd(String personId, ConfigCode configCode) {

        List<ServerNode> serverNodeList = getServerNodeListOrderByPersonCount(configCode);
//
        logger.info("集群服务查询:personId={},systemCode={},serverCount={}",
                personId,
                configCode,
                serverNodeList.size());

        ServerNode serverNode = serverNodeList.get(0);

        for (int k = 0; k < serverNodeList.size(); k++) {
            serverNode = serverNodeList.get(k);
            List<String> iplist;

            if (allIpMap.containsKey(serverNode.getId())) {
                iplist = allIpMap.get(serverNode.getId());
            } else {
                iplist = new ArrayList<String>();
                allIpMap.put(serverNode.getId(), iplist);
            }
            Integer size = allIpMap.get(serverNode.getId()).size();

            if (Integer.valueOf(serverNode.getMinconnection()) > size) {
                if (!iplist.contains(personId)) {
                    iplist.add(personId);
                    allIpMap.put(serverNode.getId(), iplist);
                }
                return serverNode;
            }
        }
        return serverNode;
    }

    public Map<String, List<String>> getAllIpMap() {
        return allIpMap;
    }

    public Map<String, ServerNode> getServerMap(ConfigCode configCode) {
        return JDSServer.getClusterClient().getServerNodeListByConfigCode(configCode).getEsbBeanMap();
    }


    public List<ServerNode> getServerNodeListOrderByPersonCount(
            ConfigCode configCode) {
        List<ServerNode> serverNodeList = new ArrayList<ServerNode>();
        LinkedHashMap map = new LinkedHashMap();
        map.putAll(getServerMap(configCode));

        Iterator<String> it = map.keySet().iterator();

        for (; it.hasNext(); ) {
            String key = it.next();
            ServerNode serverNode = getServerMap(configCode).get(key);
            if (serverNode.getStatus().equals(SystemStatus.ONLINE)) {
                serverNodeList.add(serverNode);
            }
        }

        java.util.Collections.sort(serverNodeList, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final ServerNode server1 = (ServerNode) o1;
                final ServerNode server2 = (ServerNode) o2;
                if (!allIpMap.containsKey(server1.getId())) {

                    return 1;
                }
                if (!allIpMap.containsKey(server2.getId())) {
                    return -1;
                }

                if (allIpMap.get(server1.getId()).size() > allIpMap.get(
                        server2.getId()).size()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return serverNodeList;
    }

    public boolean experssPar(String expressStr) {
        ActionContext context = ActionContext.getContext();
        if (expressStr == null || expressStr.equals("")) {
            return true;
        }
        Object result = true;
        try {
            result = context.getValueStack().findValue(expressStr,
                    boolean.class);

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
