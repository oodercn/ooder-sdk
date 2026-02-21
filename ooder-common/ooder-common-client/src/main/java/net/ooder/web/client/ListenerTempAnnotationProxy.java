/**
 * $RCSfile: ListenerTempAnnotationProxy.java,v $
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


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSListener;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.esb.config.annotation.AbstractAnnotationtExpressionTempManager;
import net.ooder.esb.config.manager.EsbBean;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.web.util.AnnotationUtil;

public class ListenerTempAnnotationProxy extends AbstractAnnotationtExpressionTempManager {
    private static final Log logger = LogFactory.getLog(
            JDSConstants.CONFIG_KEY, ListenerTempAnnotationProxy.class);

    public static Map<String, ExpressionTempBean> listenerBeanMap = new HashMap<String, ExpressionTempBean>();

    public ListenerTempAnnotationProxy(EsbBean esbBean) {
        this.esbBean = esbBean;
    }

    public static Map<String, ExpressionTempBean> getListenerBeanMap() {
        return listenerBeanMap;
    }

    public static void setListenerBeanMap(
            Map<String, ExpressionTempBean> listenerBeanMap) {
        ListenerTempAnnotationProxy.listenerBeanMap = listenerBeanMap;
    }

    public void fillBean(Set<Class<?>> classList) {


        for (Class clazz : classList) {

            if (clazz.isAnnotationPresent(EsbBeanAnnotation.class) && JDSListener.class.isAssignableFrom(clazz)) {
                EsbBeanAnnotation esbBeanAnnotation = AnnotationUtil.getClassAnnotation( clazz,EsbBeanAnnotation.class);

                ExpressionTempBean expressionTempBean = new ExpressionTempBean();

                String name = esbBeanAnnotation.name();
                String id = esbBeanAnnotation.id();

                expressionTempBean.setDataType(esbBeanAnnotation.dataType());
                expressionTempBean.setExpressionArr(esbBeanAnnotation.expressionArr());
                expressionTempBean.setClazz(esbBeanAnnotation.clazz());
                expressionTempBean.setId(id);

                expressionTempBean.setJspUrl(esbBeanAnnotation.jspUrl());
                expressionTempBean.setDesc(esbBeanAnnotation.desc());
                expressionTempBean.setName(name);
                expressionTempBean.setMainClass(clazz.getName());
                expressionTempBean.setReturntype(esbBeanAnnotation.expressionArr().substring(0, esbBeanAnnotation.expressionArr().indexOf("(")));
                expressionTempBean.setClazz(clazz.getName());
                this.nameMap.put(name, expressionTempBean);
                logger.info("JDSListener load: " + name + ":[" + expressionTempBean.getClazz() + "]");
                listenerBeanMap.put(id, expressionTempBean);
                this.idMap.put(id, expressionTempBean);
                serviceBeanList.add(expressionTempBean);
            }
        }
    }

}