/**
 * $RCSfile: EsbWebService.java,v $
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
package net.ooder.esb.service;

import net.ooder.common.JDSListener;
import net.ooder.esb.config.manager.EsbBean;
import net.ooder.esb.config.manager.ServiceBean;

import java.util.List;

public class EsbWebService {

    List<ServiceBean> ServiceBeans;
    List<ServiceBean> localServiceBeanList;
    List<JDSListener> listeners;
    List<EsbBean> esbBeans;

    public List<ServiceBean> getServiceBeans() {
        return ServiceBeans;
    }

    public void setServiceBeans(List<ServiceBean> ServiceBeans) {
        this.ServiceBeans = ServiceBeans;
    }


    public List<ServiceBean> getLocalServiceBeanList() {
        return localServiceBeanList;
    }

    public void setLocalServiceBeanList(List<ServiceBean> localServiceBeanList) {
        this.localServiceBeanList = localServiceBeanList;
    }


    public List<JDSListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<JDSListener> listeners) {
        this.listeners = listeners;
    }

    public List<EsbBean> getEsbBeans() {
        return esbBeans;
    }

    public void setEsbBeans(List<EsbBean> esbBeans) {
        this.esbBeans = esbBeans;
    }
}
