/**
 * $RCSfile: ConstructorBean.java,v $
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
import com.alibaba.fastjson.util.TypeUtils;
import net.ooder.annotation.MethodChinaName;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.context.JDSActionContext;
import net.ooder.org.conf.OrgConstants;
import net.ooder.web.util.AnnotationUtil;
import net.ooder.web.util.MethodUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

public class ConstructorBean<T> {

    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), ConstructorBean.class);

    @JSONField(serialize = false)
    public Constructor constructor;
    public String name;
    public String className;
    public MethodChinaName methodChinaName;
    public RequestParamBean fristParam;
    public List<RequestParamBean> paramList = new ArrayList<>();
    public Map<String, Object> defaultParamsValueMap = new LinkedHashMap<String, Object>();
    public Set<String> requiredParams = new HashSet<>();
    public Map<String, String> paramsMap = new LinkedHashMap<String, String>();


    public ConstructorBean() {

    }

    public ConstructorBean(String name, RequestParamBean... params) {
        this.name = name;
        for (RequestParamBean paramBean : params) {
            paramList.add(paramBean.clone());
        }
    }


    public ConstructorBean(Constructor<T> constructor) {
        this.constructor = constructor;
        this.className = constructor.getDeclaringClass().getName();
        this.name = constructor.getDeclaringClass().getSimpleName();
        Type[] parameterTypes = constructor.getGenericParameterTypes();
        Class[] parameterClasses = constructor.getParameterTypes();
        String[] paramNames = new String[parameterTypes.length];
        Object[][] annotations = constructor.getParameterAnnotations();
        this.methodChinaName = AnnotationUtil.getConstructorAnnotation(constructor, MethodChinaName.class);
        Parameter[] parameters = constructor.getParameters();

        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            Parameter parameter = parameters[i];
            if (paramName == null || paramName.equals("null") || paramName.equals("")) {
                if (parameters != null && parameters.length > i) {
                    paramName = parameters[i].getName();
                }
            }
            RequestParam paramAnnotation = parameter.getAnnotation(RequestParam.class);
            if (paramAnnotation != null) {
                if (!paramAnnotation.value().equals("")) {
                    paramName = paramAnnotation.value();
                }
                if (!paramAnnotation.defaultValue().equals("")) {
                    defaultParamsValueMap.put(paramName, paramAnnotation.defaultValue());
                }
                if (paramAnnotation.required()) {
                    requiredParams.add(paramName);
                }

            }
            Set<Annotation> annotationSet = new HashSet<>();
            for (Object annotationType : annotations[i]) {
                annotationSet.add((Annotation) annotationType);
            }
            RequestParamBean paramBean = new RequestParamBean(paramName, annotationSet, parameterTypes[i], parameterClasses[i]);

            paramList.add(paramBean);
            paramsMap.put(paramName, parameterClasses[i].getName());
        }
        if (paramList.size() > 0) {
            this.fristParam = paramList.get(0).clone();
        }

    }

    @JSONField(serialize = false)
    List<Class> getParamClassList() {
        List<Class> paramClassList = new ArrayList<>();
        for (RequestParamBean paramBean : paramList) {
            paramClassList.add(paramBean.getParamClass());
        }
        return paramClassList;
    }

    @JSONField(serialize = false)
    public String getConstructorInfo() {
        String constructorInfo = null;
        try {
            constructorInfo = MethodUtil.toConstructorStr(this).toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return constructorInfo;
    }

    @JSONField(serialize = false)
    public String getParamsInfo() {
        String paramsInfo = null;

        try {
            paramsInfo = MethodUtil.toParamsStr(this).toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return paramsInfo;
    }

    @JSONField(serialize = false)
    public Constructor<T> getSourceConstructor() throws ClassNotFoundException {
        if (constructor == null && this.getClassName() != null) {
            Class clazz = ClassUtility.loadClass(this.getClassName());
            if (constructor == null) {
                for (Constructor innerconstructor : clazz.getConstructors()) {
                    if (eqsClassArr(innerconstructor.getParameterTypes(), getParamClassList().toArray(new Class[]{}))) {
                        constructor = innerconstructor;
                    }
                }
            }
        }
        return constructor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof ConstructorBean) {
            ConstructorBean<T> oBean = (ConstructorBean) obj;
            return eqsClassArr(this.getParamClassList().toArray(new Class[]{}), oBean.getParamClassList().toArray(new Class[]{}));
        }
        return super.equals(obj);
    }


    private boolean eqsClassArr(Class[] oclazzArr, Class[] sclassArr) {
        if (oclazzArr.length != sclassArr.length) {
            return false;
        }
        for (int k = 0; k < oclazzArr.length; k++) {
            if (oclazzArr[k] == null
                    || sclassArr[k] == null ||
                    !oclazzArr[k].getName().equals((sclassArr[k]).getName())) {
                return false;
            }
        }
        return true;

    }


    public RequestParamBean getFristParam() {
        return fristParam;
    }

    public void setFristParam(RequestParamBean fristParam) {
        this.fristParam = fristParam;
    }

    public T invok(Object obj) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        T t = null;
        List<Object> objectSet = new ArrayList<>();
        ConstructorBean constructorBean = new ConstructorBean(getSourceConstructor());
        List<RequestParamBean> paramsList = constructorBean.getParamList();
        RequestParamBean[] requestParamBeans = paramsList.toArray(new RequestParamBean[]{});
        if (requestParamBeans.length > 0) {
            RequestParamBean fristParam = requestParamBeans[0];
            if (obj.getClass().equals(fristParam.getParamClass()) || fristParam.getParamClass().isAssignableFrom(obj.getClass())) {
                objectSet.add(obj);
            } else {
                Object value = TypeUtils.castToJavaBean(JDSActionContext.getActionContext().getParams(fristParam.getParamName()), fristParam.getParamClass());
                objectSet.add(value);
            }
            for (RequestParamBean paramBean : requestParamBeans) {
                if (!fristParam.equals(paramBean)) {
                    Object value = TypeUtils.castToJavaBean(JDSActionContext.getActionContext().getParams(paramBean.getParamName()), paramBean.getParamClass());
                    objectSet.add(value);
                }
            }
        }
        t = (T) getSourceConstructor().newInstance(objectSet.toArray());
        return t;
    }

    public T invokArr(Object[] objs) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        T t = null;
        List<Object> objectSet = new ArrayList<>();
        ConstructorBean constructorBean = new ConstructorBean(getSourceConstructor());
        List<RequestParamBean> paramsList = constructorBean.getParamList();
        RequestParamBean[] requestParamBeans = paramsList.toArray(new RequestParamBean[]{});
        if (requestParamBeans.length > 0) {
            int k = 0;
            for (RequestParamBean paramBean : requestParamBeans) {
                Object object = objs[k];
                if (object != null) {
                    if (object.getClass().equals(paramBean.getParamClass()) || paramBean.getParamClass().isAssignableFrom(object.getClass())) {
                        objectSet.add(object);
                    } else {
                        Object value = TypeUtils.castToJavaBean(object, paramBean.getParamClass());
                        objectSet.add(value);
                    }
                } else {
                    Object value = TypeUtils.castToJavaBean(JDSActionContext.getActionContext().getParams(paramBean.getParamName()), paramBean.getParamClass());
                    objectSet.add(value);
                }
                k++;
            }
        }
        t = (T) getSourceConstructor().newInstance(objectSet.toArray());

        return t;
    }


    public Set<String> getRequiredParams() {
        return requiredParams;
    }

    public void setRequiredParams(Set<String> requiredParams) {
        this.requiredParams = requiredParams;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public MethodChinaName getMethodChinaName() {
        return methodChinaName;
    }

    public void setMethodChinaName(MethodChinaName methodChinaName) {
        this.methodChinaName = methodChinaName;
    }

    public List<RequestParamBean> getParamList() {
        return paramList;
    }

    @JSONField(serialize = false)
    public List<RequestParamBean> getAllParamList() {
        List<RequestParamBean> allParams = new ArrayList<>();
        if (fristParam != null && !paramList.contains(fristParam)) {
            allParams.add(0, fristParam);
        }
        allParams.addAll(paramList);

        return paramList;
    }


    public void setParamList(List<RequestParamBean> paramList) {
        this.paramList = paramList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

}
