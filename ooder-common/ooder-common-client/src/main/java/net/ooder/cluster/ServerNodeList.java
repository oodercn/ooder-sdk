/**
 * $RCSfile: ServerNodeList.java,v $
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

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ServerNodeList {

    private String reload = "false";
    private String clientreloadtime;
    private String userexpression;
    private String clusterManagerClass;
    private Map<String, ServerNode> esbBeanMap;

    @JSONField(serialize = false)
    public List<ServerNode> getServerNodeList() {
        List<ServerNode> serverNodes = new ArrayList<ServerNode>();
        Set<String> keySet = esbBeanMap.keySet();
        for (String key : keySet) {
            if (esbBeanMap.get(key) != null) {
                serverNodes.add(esbBeanMap.get(key));
            }

        }
        return serverNodes;
    }


    public String getClientreloadtime() {
        return clientreloadtime;
    }

    public void setClientreloadtime(String clientreloadtime) {
        this.clientreloadtime = clientreloadtime;
    }

    public String getUserexpression() {
        return userexpression;
    }

    public void setUserexpression(String userexpression) {
        this.userexpression = userexpression;
    }

    public Map<String, ServerNode> getEsbBeanMap() {
        return esbBeanMap;
    }

    public void setEsbBeanMap(Map<String, ServerNode> esbBeanMap) {
        this.esbBeanMap = esbBeanMap;
    }

    public String getReload() {
        return reload == null ? "false" : reload;
    }

    public void setReload(String reload) {
        this.reload = reload;
    }


    public String getClusterManagerClass() {
        return clusterManagerClass;
    }

    public void setClusterManagerClass(String clusterManagerClass) {
        this.clusterManagerClass = clusterManagerClass;
    }


}