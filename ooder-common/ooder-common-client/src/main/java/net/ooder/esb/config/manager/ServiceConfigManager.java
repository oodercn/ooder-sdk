/**
 * $RCSfile: ServiceConfigManager.java,v $
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
package net.ooder.esb.config.manager;
/**
 * time 06-01-01
 *
 * @author wenzhang
 */


import net.ooder.common.JDSBusException;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface ServiceConfigManager {
    /**
     * 指定应用指定ID查找
     * @param id
     * @return
     */
    public ServiceBean getServiceConfigById(String id);

    /**
     * 根据模板名称查询模板实例
     * @param name
     * @return
     */
    public ServiceBean getServiceConfigByName(String name);

    /**
     * 取得所有模板列表
     * @return
     */
    public Map<String, ServiceBean> findServiceConfigMapByName();

    public Map<String, ServiceBean> findServiceConfigMapById();

    public List<ServiceBean> loadAllService();

    public Set<Class<?>> init() throws JDSBusException;

}