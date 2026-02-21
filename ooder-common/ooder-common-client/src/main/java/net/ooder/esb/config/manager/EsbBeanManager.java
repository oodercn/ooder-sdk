/**
 * $RCSfile: EsbBeanManager.java,v $
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
 * JDS 5.0
 */

import net.ooder.common.ContextType;
import net.ooder.common.EsbBeanType;
import net.ooder.common.JDSException;
import net.ooder.common.TokenType;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.esb.config.xml.ServiceConfig;
import net.ooder.esb.util.ESBConstants;
import net.sf.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EsbBeanManager {
    private static final Log logger = LogFactory.getLog(
            ESBConstants.CONFIG_KEY, EsbBeanManager.class);

    private static final String configName = "esbbean_config.xml";
    private static final String userconfigName = "useresbbean_config.xml";
    private static final String configId = "configid";
    private static final String id = "id";
    private static final String esbtype = "esbtype";
    private static final String type = "type";
    private static final String tokenType = "tokenType";

    public static EsbBeanConfig esbBeanListBean;

    static EsbBeanConfig getEsbBeanList() {
        if (esbBeanListBean == null || (esbBeanListBean.getReload() != null && esbBeanListBean.getReload().equals("true"))) {
            esbBeanListBean = newEsbBeanList();
        }
        return esbBeanListBean;
    }


    static EsbBeanConfig initSystemConfig() {
        EsbBeanConfig esbBeanListBean = new EsbBeanConfig();
        ServiceConfig config = null;
        try {
            config = ServiceConfig.getServiceConfig(configName);
            BeanMap esbBeanListBeanMap = BeanMap.create(esbBeanListBean);
            Iterator it = esbBeanListBeanMap.keySet().iterator();
            for (; it.hasNext(); ) {
                String key = (String) it.next();
                Class clazz = esbBeanListBeanMap.getPropertyType(key);
                if (clazz.isAssignableFrom(String.class)) {
                    esbBeanListBeanMap.put(key, config.getValue(key));
                } else {
                    Map<String, EsbBean> esbBeanMap = new HashMap<String, EsbBean>();
                    String[] esbbeanIds = config.getValues(configId);
                    for (String esbBeanId : esbbeanIds) {
                        EsbBean eabBean = new EsbBean();
                        BeanMap beanmap = BeanMap.create(eabBean);
                        Iterator esbit = beanmap.keySet().iterator();
                        for (; esbit.hasNext(); ) {
                            String esbkey = (String) esbit.next();
                            String value = config.getValue(esbBeanId + "." + esbkey);
                            if (esbkey.equals(type)) {
                                if (value != null && !value.equals("")) {
                                    eabBean.setType(ContextType.fromType(value));
                                }
                            } else if (esbkey.equals(tokenType)) {
                                if (value != null && !value.equals("")) {
                                    eabBean.setTokenType(TokenType.fromType(value));
                                }
                            } else {
                                beanmap.put(esbkey, value);
                            }
                        }
                        beanmap.put(id, esbBeanId);
                        beanmap.put(esbtype, EsbBeanType.System);
                        esbBeanMap.put(esbBeanId, eabBean);
                    }
                    esbBeanListBeanMap.put(key, esbBeanMap);
                }
            }
        } catch (JDSException e) {
            logger.warn(e.getMessage());
        }

        return esbBeanListBean;
    }

    /**
     * 初始化用户配置
     *
     * @param esbBeanListBean
     * @return
     * @throws JDSException
     */
    static EsbBeanConfig fullUserConfig(EsbBeanConfig esbBeanListBean) throws JDSException {

        ServiceConfig userconfig = ServiceConfig.getServiceConfig(userconfigName);
        if (esbBeanListBean == null) {
            esbBeanListBean = new EsbBeanConfig();
        }
        BeanMap esbBeanListBeanMap = BeanMap.create(esbBeanListBean);
        Iterator it = esbBeanListBeanMap.keySet().iterator();
        for (; it.hasNext(); ) {
            String key = (String) it.next();
            Class clazz = esbBeanListBeanMap.getPropertyType(key);
            if (clazz.isAssignableFrom(String.class)) {
                esbBeanListBeanMap.put(key, userconfig.getValue(key));
            } else {
                Map<String, EsbBean> sysBeanMap = (Map<String, EsbBean>) esbBeanListBeanMap.get(key);
                if (userconfig != null) {
                    String[] userEsbBeanIds = userconfig.getValues(configId);
                    for (String userEsbBeanId : userEsbBeanIds) {
                        EsbBean eabBean = sysBeanMap.get(userEsbBeanId);
                        if (eabBean == null) {
                            eabBean = new EsbBean();
                        }
                        if (eabBean.getEsbtype() == null || !eabBean.getEsbtype().equals(EsbBeanType.System)) {
                            BeanMap beanmap = BeanMap.create(eabBean);
                            Iterator esbit = beanmap.keySet().iterator();
                            for (; esbit.hasNext(); ) {
                                String esbkey = (String) esbit.next();
                                String value = userconfig.getValue(userEsbBeanId + "." + esbkey);

                                if (esbkey.equals(esbtype)) {
                                    if (value != null && !value.equals("")) {
                                        eabBean.setEsbtype(EsbBeanType.fromType(value));
                                    }
                                } else if (esbkey.equals(type)) {
                                    if (value != null && !value.equals("")) {
                                        eabBean.setType(ContextType.fromType(value));
                                    }
                                } else if (esbkey.equals(tokenType)) {
                                    if (value != null && !value.equals("")) {
                                        eabBean.setTokenType(TokenType.fromType(value));
                                    }
                                } else {
                                    beanmap.put(esbkey, value);
                                }
                            }
                            beanmap.put(id, userEsbBeanId);
                            sysBeanMap.put(userEsbBeanId, eabBean);
                        }
                    }
                }
            }
        }
        return esbBeanListBean;
    }

    /***
     * 获取所有 bean 配置 List 从 xml(system 和 user 两种)
     * 默认从:
     *      esbbean_config.xml[configName] 和
     *      useresbbean_config.xml[userconfigName]
     * 中读取配置
     * @return 配置 listBean
     */
    private static EsbBeanConfig newEsbBeanList() {
        EsbBeanConfig esbBeanListBean = null;
        try {
            esbBeanListBean = initSystemConfig();
            esbBeanListBean = fullUserConfig(esbBeanListBean);
        } catch (JDSException e) {
            logger.error(e);
        }

        return esbBeanListBean;

    }

}
