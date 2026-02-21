/**
 * $RCSfile: ExpressionTempXmlProxy.java,v $
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
package net.ooder.esb.config.xml;

import net.ooder.annotation.FormulaParams;
import net.ooder.common.ContextType;
import net.ooder.common.EsbFlowType;
import net.ooder.common.JDSException;
import net.ooder.common.TokenType;
import net.ooder.esb.config.manager.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExpressionTempXmlProxy implements ServiceConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ExpressionTempXmlProxy.class);
    private static final String strParams = "params";
    private static final String templetid = "templetid";
    private static final String templetdesc = "templetdesc";
    private static final String templetname = "templetname";
    private static final String expressionArr = "expressionArr";
    private static final String templet = "templet";
    private static final String code = "paramcode";
    private static final String clazz = "class";
    private static final String mainClass = "mainclass";
    private static final String name = "paramname";
    private static final String type = "paramtype";

    private static final String username = "username";
    private static final String password = "password";

    private static final String desc = "paramdesc";
    private static final String paramid = "paramid";
    private static final String returntype = "returntype";
    private static final String flowType = "flowtype";
    private static final String dataType = "datatype";
    private static final String tokenType = "tokenType";
    private static final String serverUrl = "serverUrl";
    private static final String filter = "filter";
    private static final String jspurl = "jspurl";
    private ServiceConfig serviceConfig;
    private EsbBean esbBean;

    public ExpressionTempXmlProxy(EsbBean esbBean) {
        this.esbBean = esbBean;
    }

    public String getPath() {
        return esbBean.getPath();
    }

    /***
     * xml Bean 初始化
     */
    public Set<Class<?>> init() {
        try {
            this.serviceConfig = ServiceConfig.getServiceConfig(esbBean.getPath());


        } catch (JDSException e) {
            e.printStackTrace();
            logger.error("esb load error: name=" + esbBean.getCnname() == null ? esbBean.getDesc() : esbBean.getCnname() + "[" + esbBean.getId() + "]" + " path=" + esbBean.getPath());
        }
        return null;
    }

    public ExpressionTempBean getServiceConfigById(String id) {

        String str = templet + "." + id;
        ExpressionTempBean expressionTempBean = new ExpressionTempBean();

        expressionTempBean.setId(id);
        expressionTempBean.setDesc(serviceConfig.getValue(str + "." + templetdesc));
        expressionTempBean.setMainClass(serviceConfig.getValue(str + "." + mainClass));
        expressionTempBean.setName(serviceConfig.getValue(str + "." + templetname));
        expressionTempBean.setExpressionArr(serviceConfig.getValue(str + "." + expressionArr));
        expressionTempBean.setJspUrl(serviceConfig.getValue(str + "." + jspurl));
        expressionTempBean.setClazz(serviceConfig.getValue(str + "." + clazz));
        expressionTempBean.setFilter(serviceConfig.getValue(str + "." + filter));
        String surl = serviceConfig.getValue(str + "." + serverUrl);


        String strTokenType = serviceConfig.getValue(str + "." + tokenType);
        if (strTokenType == null || strTokenType.equals("")) {
            expressionTempBean.setTokenType(esbBean.getTokenType());
        } else {
            expressionTempBean.setTokenType(TokenType.fromType(strTokenType));
        }

        String strDataType = serviceConfig.getValue(str + "." + dataType);
        if (strDataType == null || strDataType.equals("")) {
            expressionTempBean.setDataType(ContextType.Action);
        } else {
            expressionTempBean.setDataType(ContextType.fromType(strDataType));
        }

        String strReturnType = serviceConfig.getValue(str + "." + returntype);
        if (strReturnType == null || strReturnType.equals("")) {
            expressionTempBean.setReturntype(id);
        } else {
            expressionTempBean.setReturntype(strReturnType);
        }

        if (surl == null || surl.equals("")) {
            expressionTempBean.setServerUrl(esbBean.getServerUrl());
        } else {
            expressionTempBean.setServerUrl(surl);
        }


        if (expressionTempBean.getExpressionArr() == null) {
            if (expressionTempBean.getDataType().equals(ContextType.Server)) {
                expressionTempBean.setExpressionArr("GetClientService(\"" + expressionTempBean.getClazz() + "\",\"" + expressionTempBean.getServerUrl() + "\")");
            } else {
                expressionTempBean.setExpressionArr(expressionTempBean.getId() + "()");
            }
        }

        expressionTempBean.setFlowType(EsbFlowType.fromType(serviceConfig.getValue(str + "." + flowType)));

        expressionTempBean.setParams(getParams(str, serviceConfig));

        return expressionTempBean;
    }

    public ExpressionTempBean getNewExpressionTempBean(String id) {
        ExpressionTempBean expressionTempBean = new ExpressionTempBean();
        expressionTempBean.setId(id);
        return expressionTempBean;
    }

    public ServiceBean getServiceConfigByName(String name) {
        return this.findServiceConfigMapByName().get(name);
    }

    public Map<String, ServiceBean> findServiceConfigMapByName() {

        String str = templet + "." + templetid;
        Map<String, ServiceBean> expressionTempBeanMap = new HashMap<String, ServiceBean>();
        ;
        String[] tempBeanIds = serviceConfig.getValues(str);
        if (tempBeanIds != null) {
            for (String tempBeanId : tempBeanIds) {
                ExpressionTempBean exb = this.getServiceConfigById(tempBeanId);
                expressionTempBeanMap.put(exb.getName(), exb);
            }
        }
        return expressionTempBeanMap;
    }

    /**
     * 获取所有的 Beans 配置
     *
     * @return Bean 配置 Map
     */
    public Map<String, ServiceBean> findServiceConfigMapById() {

        String str = templet + "." + templetid;
        Map<String, ServiceBean> expressionTempBeanMap = new HashMap<String, ServiceBean>();

        String[] tempBeanIds = serviceConfig.getValues(str);
        if (tempBeanIds != null) {
            for (String tempBeanId : tempBeanIds) {
                ExpressionTempBean exb = this.getServiceConfigById(tempBeanId);
                expressionTempBeanMap.put(tempBeanId, exb);
            }
        }

        return expressionTempBeanMap;
    }

    /***
     * 解析 bean 参数列表(String 参数)
     *
     * @param beanNodePath
     *            beanNode 名称
     * @param serviceConfig
     *            配置文件 Holder
     * @return 解析后的参数列表
     */
    private List<ExpressionParameter> getParams(String beanNodePath, ServiceConfig serviceConfig) {
        String strParamStr = beanNodePath + "." + strParams;
        String[] paramIdArray = serviceConfig.getValues(strParamStr + "." + paramid);
        List<ExpressionParameter> paramsList = new ArrayList<ExpressionParameter>();

        if (paramIdArray == null) {
            return paramsList;
        }

        for (String paramId : paramIdArray) {
            String paramIdPath = strParamStr + "." + paramId;
            ExpressionTempParamBean param = new ExpressionTempParamBean();
            param.setParameterCode(serviceConfig.getValue(paramIdPath + "." + code));
            param.setParameterId(paramId);
            param.setParameterenName(serviceConfig.getValue(paramIdPath + "." + name));
            param.setParameterType(FormulaParams.fromType(serviceConfig.getValue(paramIdPath + "." + type)));
            param.setParameterDesc(serviceConfig.getValue(paramIdPath + "." + desc));
            paramsList.add(param);
        }

        return paramsList;
    }

    /***
     * 获取所有表达式模板 Bean 列表
     *
     * @return Bean List that contain the config of the bean.
     */
    public List<ServiceBean> loadAllService() {
        String str = templet + "." + templetid;
        List<ServiceBean> expressionTempBeanList = new ArrayList<ServiceBean>();
        String[] tempBeanIds = serviceConfig.getValues(str);
        if (tempBeanIds != null) {
            for (String tempBeanId : tempBeanIds) {
                ExpressionTempBean exb = this.getServiceConfigById(tempBeanId);
                expressionTempBeanList.add(exb);
            }
        }
        return expressionTempBeanList;
    }

}