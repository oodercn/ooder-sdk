/**
 * $RCSfile: RequestParamBean.java,v $
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
package net.ooder.web;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.JsonData;
import net.ooder.common.util.ClassUtility;
import net.ooder.annotation.FParams;
import net.ooder.esb.config.manager.ExpressionParameter;
import net.ooder.esb.config.manager.ExpressionTempParamBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

public class RequestParamBean {
    @JSONField(serialize = false)
    Set<Annotation> annotations = new LinkedHashSet<Annotation>();

    @JSONField(serialize = false)
    private String sourceClassName;

    @JSONField(serialize = false)
    private String domainId;

    @JSONField(serialize = false)
    private String methodName;

    @JSONField(serialize = false, deserialize = false)
    Type paramType;

    String paramName;

    Boolean jsonData = false;

    String paramClassName;

    @JSONField(serialize = false)
    Class paramClass;

    ExpressionParameter parameter;


    public RequestParamBean() {

    }

    public RequestParamBean(String paramName, Class paramClass, Boolean jsonData) {
        this.paramName = paramName;
        this.paramClass = paramClass;
        this.paramClassName = paramClass.getName();
        if (jsonData != null) {
            this.jsonData = jsonData;
        }

    }


    public RequestParamBean clone() {
        RequestParamBean paramBean = new RequestParamBean();
        paramBean.setParamName(this.getParamName());
        paramBean.setParamClass(this.getParamClass());
        paramBean.setParamClassName(this.getParamClassName());
        paramBean.setAnnotations(this.getAnnotations());
        paramBean.setDomainId(this.getDomainId());
        paramBean.setParamType(this.getParamType());
        paramBean.setJsonData(this.getJsonData());
        paramBean.setSourceClassName(this.getSourceClassName());
        paramBean.setMethodName(this.getMethodName());
        return paramBean;
    }

    public RequestParamBean copy(RequestParamBean paramBean) {
        paramBean.setParamName(this.getParamName());
        paramBean.setParamClassName(this.getParamClassName());
        paramBean.setParamClass(this.getParamClass());
        paramBean.setAnnotations(this.getAnnotations());
        paramBean.setDomainId(this.getDomainId());
        paramBean.setParamType(this.getParamType());
        paramBean.setJsonData(this.getJsonData());
        paramBean.setSourceClassName(this.getSourceClassName());
        paramBean.setMethodName(this.getMethodName());
        return paramBean;
    }

    public RequestParamBean(String paramName, Set<Annotation> annotations, Type paramType, Class paramClass) {
        this.paramName = paramName;
        this.annotations = annotations;
        this.paramType = paramType;
        this.paramClass = paramClass;
        this.paramClassName = paramClass.getName();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(JsonData.class)) {
                jsonData = true;
            } else if (annotation.annotationType().equals(FParams.class)) {
                FParams fParams = (FParams) annotation;
                parameter = new ExpressionTempParamBean();
                parameter.setParameterCode(fParams.parameterCode().equals("") ? paramName : fParams.parameterCode());
                parameter.setParameterenName(fParams.parameterName().equals("") ? paramName : fParams.parameterName());
                parameter.setParameterId(paramName);
                parameter.setParameterType(fParams.type());
                parameter.setParameterValue(fParams.value());
                parameter.setSingle(fParams.single());
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof RequestParamBean) {
            return ((RequestParamBean) obj).getParamName().equals(paramName) && ((RequestParamBean) obj).getParamClass().equals(getParamClass());
        }
        return super.equals(obj);
    }

    public ExpressionParameter getParameter() {
        return parameter;
    }

    public void setParameter(ExpressionParameter parameter) {
        this.parameter = parameter;
    }

    public Boolean getJsonData() {
        return jsonData;
    }

    public void setJsonData(Boolean jsonData) {
        this.jsonData = jsonData;
    }

    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Set<Annotation> annotations) {
        this.annotations = annotations;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Type getParamType() {
        if (paramType == null) {
            paramType = this.getParamClass();
        }
        return paramType;
    }

    public void setParamType(Type paramType) {
        this.paramType = paramType;
    }

    @JSONField(serialize = false)
    public Class getParamClass() {
        if (paramClass == null && paramClassName != null) {
            try {
                paramClass = ClassUtility.loadClass(paramClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return paramClass;
    }

    public void setParamClass(Class paramClass) {
        this.paramClass = paramClass;
    }

    @JSONField(serialize = false)
    public Class getSourceParamClass() {
        Class paramClass = null;
        if (paramClassName != null) {
            try {
                paramClass = ClassUtility.loadClass(paramClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return paramClass;
    }


    public String getSourceClassName() {
        return sourceClassName;
    }

    public void setSourceClassName(String sourceClassName) {
        this.sourceClassName = sourceClassName;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParamClassName() {
        return paramClassName;
    }

    public void setParamClassName(String paramClassName) {
        this.paramClassName = paramClassName;
    }
}
