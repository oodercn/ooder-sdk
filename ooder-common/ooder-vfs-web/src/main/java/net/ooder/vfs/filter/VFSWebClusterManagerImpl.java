package net.ooder.vfs.filter;

import  net.ooder.cluster.ClusterMananer;
import  net.ooder.cluster.ServerNode;
import  net.ooder.common.ConfigCode;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.SystemStatus;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.context.JDSActionContext;
import  net.ooder.jds.core.esb.util.ActionContext;
import  net.ooder.server.JDSServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VFSWebClusterManagerImpl implements ClusterMananer {
    private static Map<String, List<String>> allIpMap = new LinkedHashMap<String, List<String>>();

    protected static Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, VFSWebClusterManagerImpl.class);

    public ServerNode getSubServer(String sessionId, ConfigCode configCode) {

        List<ServerNode> serverBeanList = getServerNodeListOrderByPersonCount(configCode);
        ServerNode serverBean = serverBeanList.get(0);
        if (sessionId != null) {
            String path = (String) JDSActionContext.getActionContext().getParams("path");
            if (path != null) {

                for (int k = 0; k < serverBeanList.size(); k++) {
                    serverBean = serverBeanList.get(k);
                    String pattern = serverBean.getUserexpression();
                    List<String> iplist;
                    if (allIpMap.containsKey(serverBean.getId())) {
                        iplist = allIpMap.get(serverBean.getId());
                    } else {
                        iplist = new ArrayList<String>();
                        allIpMap.put(serverBean.getId(), iplist);
                    }
                    Integer size = allIpMap.get(serverBean.getId()).size();

                    if (pattern != null && !pattern.equals("")) {
                        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = p.matcher(path);
                        if (matcher.find() && Integer.valueOf(serverBean.getMinconnection()) > size) {
                            if (!iplist.contains(path)) {
                                iplist.add(path);
                                allIpMap.put(serverBean.getId(), iplist);
                            }
                            return serverBean;
                        }

                    }

                }
            }
        }

        return serverBean;
    }

    public Map<String, List<String>> getAllIpMap() {
        return allIpMap;
    }

    public Map<String, ServerNode> getServerMap(ConfigCode configCode) {
        return JDSServer.getClusterClient().getServerNodeListByConfigCode(configCode).getEsbBeanMap();

    }

    public List<ServerNode> getServerNodeListOrderByPersonCount(ConfigCode configCode) {
        List<ServerNode> serverBeanList = new ArrayList<ServerNode>();
        LinkedHashMap map = new LinkedHashMap();
        map.putAll(getServerMap(configCode));

        Iterator<String> it = map.keySet().iterator();

        for (; it.hasNext(); ) {
            String key = it.next();
            ServerNode serverBean = getServerMap(configCode).get(key);
            if (serverBean.getStatus().equals(SystemStatus.ONLINE)) {
                serverBeanList.add(serverBean);
            }

        }

        Collections.sort(serverBeanList, new Comparator() {
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
