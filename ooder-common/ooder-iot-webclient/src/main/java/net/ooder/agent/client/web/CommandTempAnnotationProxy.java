package net.ooder.agent.client.web;

import net.ooder.agent.client.command.CommandFactory;
import  net.ooder.common.JDSCommand;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.esb.config.annotation.AbstractAnnotationtExpressionTempManager;
import  net.ooder.esb.config.manager.EsbBean;
import  net.ooder.esb.config.manager.ExpressionTempBean;
import  net.ooder.web.util.AnnotationUtil;

import java.util.Set;

public class CommandTempAnnotationProxy extends AbstractAnnotationtExpressionTempManager {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, CommandTempAnnotationProxy.class);

    public CommandTempAnnotationProxy(EsbBean esbBean) {
        this.esbBean = esbBean;
    }

    public void fillBean(Set<Class<?>> classList) {

        for (Class clazz : classList) {
            if (clazz.isAnnotationPresent(EsbBeanAnnotation.class) && JDSCommand.class.isAssignableFrom(clazz)) {
                EsbBeanAnnotation esbBeanAnnotation = AnnotationUtil.getClassAnnotation(clazz,EsbBeanAnnotation.class);
                ExpressionTempBean expressionTempBean = new ExpressionTempBean();
                String name = esbBeanAnnotation.name();
                String id = esbBeanAnnotation.id();
                expressionTempBean.setFlowType(EsbFlowType.command);
                expressionTempBean.setDataType(esbBeanAnnotation.dataType());
                expressionTempBean.setExpressionArr(esbBeanAnnotation.expressionArr());
                expressionTempBean.setClazz(esbBeanAnnotation.clazz());
                expressionTempBean.setId(id);
                expressionTempBean.setTokenType(esbBeanAnnotation.tokenType());
                expressionTempBean.setJspUrl(esbBeanAnnotation.jspUrl());
                expressionTempBean.setDesc(esbBeanAnnotation.desc());
                expressionTempBean.setName(name);
                expressionTempBean.setMainClass(clazz.getName());
                expressionTempBean.setReturntype(esbBeanAnnotation.expressionArr().substring(0, esbBeanAnnotation.expressionArr().indexOf("(")));
                expressionTempBean.setClazz(clazz.getName());

                logger.info("JDSCommand load: " + name + ":[" + expressionTempBean.getClazz() + "]");
                this.nameMap.put(name, expressionTempBean);
                CommandFactory.getInstance().addClass(id, clazz);
                this.idMap.put(id, expressionTempBean);
                serviceBeanList.add(expressionTempBean);
            }
        }
    }

}