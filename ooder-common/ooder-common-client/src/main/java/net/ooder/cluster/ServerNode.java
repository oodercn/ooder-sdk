/**
 * $RCSfile: ServerNode.java,v $
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
import net.ooder.common.ConfigCode;
import net.ooder.common.ContextType;
import net.ooder.common.SystemNodeType;
import net.ooder.common.SystemStatus;
import net.ooder.config.JDSConfig;
import net.ooder.config.UserBean;
import net.ooder.context.JDSActionContext;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.server.SubSystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServerNode {

    String maxconnection = "2000";
    String minconnection = "200";
    String timeout = "60";
    public SystemStatus status = SystemStatus.ONLINE;
    public Integer checkTimes = 0;
    public List<String> onLinePersonIds;
    public List<String> defPersonIds;
    public Set<String> adminPersonIds;
    public String id;
    public String name;
    public String url;
    public String desc;

    SystemNodeType type = SystemNodeType.SUB;


    ConfigCode configCode;


    String userexpression = "true";

    @JSONField(serialize = false)
    public Set<ServiceBean> services;


    public ServerNode() {

    }

    public ServerNode(UserBean userBean) {
        this.id = JDSConfig.getConfigName().getName();
        if (JDSConfig.getValue("server.port") != null) {
            this.url = "http://127.0.0.1" + JDSConfig.getValue("server.port").trim();
        }
        this.name = JDSConfig.getConfigName().getName();
        this.adminPersonIds = new HashSet<>();
        String adminPersonId = userBean.getUsername();
        if (adminPersonId.indexOf(";") > -1) {
            String[] personIds = adminPersonId.split(";");
            for (String personId : personIds) {
                adminPersonIds.add(personId);
            }
        } else {
            adminPersonIds.add(adminPersonId);
        }

        this.configCode = JDSConfig.getConfigName();
    }

    public ServerNode(SubSystem subSystem) {
        this.id = subSystem.getSysId();
        this.url = subSystem.getUrl();
        this.name = subSystem.getName();
        this.adminPersonIds = new HashSet<>();
        String adminPersonId = subSystem.getAdminId();
        if (adminPersonId == null) {
            adminPersonId = subSystem.getEnname() + "admin";
        }
        if (adminPersonId.indexOf(";") > -1) {
            String[] personIds = adminPersonId.split(";");
            for (String personId : personIds) {
                adminPersonIds.add(personId);
            }
        } else {
            adminPersonIds.add(adminPersonId);
        }

        this.configCode = subSystem.getConfigname();

    }


    public ConfigCode getConfigCode() {
        return configCode;
    }

    public void setConfigCode(ConfigCode configCode) {
        this.configCode = configCode;
    }

    public Set<? extends ServiceBean> getServices() {
        if (services == null) {
            services = new HashSet<ServiceBean>();
            List<? extends ServiceBean> allservices = EsbBeanFactory.getInstance().loadAllServiceBean();
            for (ServiceBean serviceBean : allservices) {
                if (serviceBean.getDataType().equals(ContextType.Server)
                        && serviceBean.getServerUrl() != null
                        && serviceBean.getServerUrl().equals(this.getUrl())
                        ) {
                    services.add(serviceBean);
                }
            }
        }
        return services;

    }


    public void setAdminPersonIds(Set<String> adminPersonIds) {
        this.adminPersonIds = adminPersonIds;
    }

    public void setServices(Set<ServiceBean> services) {
        this.services = services;
    }


    public Set<String> getAdminPersonIds() {

        return adminPersonIds;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAdminPersonId() {
        if (getAdminPersonIds().size() > 0) {
            return getAdminPersonIds().iterator().next();
        }
        return null;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getMaxconnection() {
        return maxconnection;
    }

    public void setMaxconnection(String maxconnection) {
        this.maxconnection = maxconnection;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }


    public String getMinconnection() {
        return minconnection;
    }

    public void setMinconnection(String minconnection) {
        this.minconnection = minconnection;
    }

    public String getName() {
        return name;
    }


    public String getUrl() {
        return url;
    }


    public SystemStatus getStatus() {
        return status;
    }


    public SystemNodeType getType() {
        return type;
    }


    public List<String> getOnLinePersonIds() {
        if (onLinePersonIds == null) {
            onLinePersonIds = new ArrayList<String>();
        }
        return onLinePersonIds;
    }

    public void setOnLinePersonIds(List<String> onLinePersonIds) {
        this.onLinePersonIds = onLinePersonIds;
    }

    public List<String> par() {
        if (defPersonIds == null) {
            String expressStr = this.getUserexpression();
            defPersonIds = new ArrayList<String>();
            if (expressStr != null && !expressStr.equals("")) {
                Object obj = JDSActionContext.getActionContext().Par(expressStr);
                if (obj != null && obj.getClass().isArray()) {
                    defPersonIds = (List<String>) obj;
                }

            }
        }
        return defPersonIds;
    }

    public void setStatus(SystemStatus status) {
        this.status = status;
    }

    public void setCheckTimes(Integer checkTimes) {
        this.checkTimes = checkTimes;
    }

    public List<String> getDefPersonIds() {
        return defPersonIds;
    }

    public void setUserexpression(String userexpression) {
        this.userexpression = userexpression;
    }


    public void setType(SystemNodeType type) {
        this.type = type;
    }

    public String getUserexpression() {
        return userexpression;
    }

    public void setDefPersonIds(List<String> defPersonIds) {
        this.defPersonIds = defPersonIds;
    }

    public Integer getCheckTimes() {
        return checkTimes;
    }

    public void addCheckTimes() {
        this.checkTimes++;
    }

    public void resetCheckTimes() {
        this.checkTimes = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}