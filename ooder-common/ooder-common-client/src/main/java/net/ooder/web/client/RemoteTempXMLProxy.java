/**
 * $RCSfile: RemoteTempXMLProxy.java,v $
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


import net.ooder.common.*;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.esb.config.manager.EsbBean;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.esb.config.manager.ServiceConfigManager;
import net.ooder.server.JDSServer;

import java.util.*;

public class RemoteTempXMLProxy implements ServiceConfigManager {
    private static final Log logger = LogFactory.getLog(
            JDSConstants.CONFIG_KEY, RemoteTempXMLProxy.class);

    private final EsbBean esbBean;

    public RemoteTempXMLProxy(EsbBean esbBean) {
        this.esbBean = esbBean;
        this.esbBean.setEsbtype(EsbBeanType.Cluster);
    }

    List<ServiceBean> remoteBeans = new ArrayList<ServiceBean>();

    Map<String, ServiceBean> remoteBeanMap = new HashMap<String, ServiceBean>();

    Map<String, ServiceBean> nameBeanMap = new HashMap<String, ServiceBean>();

    @Override
    public ServiceBean getServiceConfigById(String id) {
        return remoteBeanMap.get(id);
    }

    @Override
    public ServiceBean getServiceConfigByName(String name) {
        return nameBeanMap.get(name);
    }

    @Override
    public Map<String, ServiceBean> findServiceConfigMapByName() {
        return remoteBeanMap;
    }

    @Override
    public Map<String, ServiceBean> findServiceConfigMapById() {
        return remoteBeanMap;
    }

    @Override
    public List<ServiceBean> loadAllService() {
        return remoteBeans;
    }

    @Override
    public Set<Class<?>> init() throws JDSBusException {
        try {
            List<ExpressionTempBean> remoteTempBeans = JDSServer.getInstance().getClusterSevice();
            for (ExpressionTempBean bean : remoteTempBeans) {
                bean.setFlowType(EsbFlowType.clusterAction);
                remoteBeans.add(bean);
                remoteBeanMap.put(bean.getId(), bean);
                nameBeanMap.put(bean.getName(), bean);
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }
}