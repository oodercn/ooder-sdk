/**
 * $RCSfile: RequestMethodBean.java,v $
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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.TypeUtils;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import net.ooder.annotation.*;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.context.JDSActionContext;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.conf.OrgConstants;
import net.ooder.web.util.AnnotationUtil;
import net.ooder.web.util.JSONGenUtil;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

public class RequestMethodBean {
    private static final Log logger1 = LogFactory.getLog(JDSConstants.CONFIG_KEY, RequestMethodBean.class);

    @JSONField(serialize = false)
    public Method method;

    @JSONField(serialize = false)
    public CtMethod ctmethod;

    public Class returnClass;
    public String returnClassName;
    public RequestMappingBean mappingBean;
    public String url;
    public String domainId;
    public String name;
    public String className;
    public String methodName;
    public String pathVariable;

    public HttpMethod queryMethod = HttpMethod.POST;
    public RequestType requestType = RequestType.FORM;
    public ResponseType responseType = ResponseType.JSON;

    public LinkedHashSet<RequestParamBean> paramSet = new LinkedHashSet<>();
    public Map<String, String> paramsMap = new LinkedHashMap<String, String>();

    public Map<String, Object> defaultParamsValueMap = new LinkedHashMap<String, Object>();
    public Set<String> requiredParams = new HashSet<>();

    public ResponseBody responseBody;
    public RequestBody requestBody;
    public MethodChinaName methodChinaName;
    public Map<String, PathVariable> pathVariables = new HashMap<String, PathVariable>();

    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), RequestMethodBean.class);

    public RequestMethodBean(String methodName, String returnClassName, List<RequestParamBean> params) {
        this.methodName = methodName;
        this.returnClassName = returnClassName;
        mappingBean = new RequestMappingBean(methodName, null);
        for (RequestParamBean requestParamBean : params) {
            if (!hasParams(requestParamBean.getParamName())) {
                paramSet.add(requestParamBean);
            }
        }
    }


    public void update(String methodName, String returnClassName) {
        this.methodName = methodName;
        this.returnClassName = returnClassName;
    }


    public RequestMethodBean() {

    }

    public RequestMethodBean(Method method) {
        this.method = method;
        RequestMapping requestMapping = AnnotationUtil.getMethodAnnotation(method, RequestMapping.class);
        if (requestMapping != null) {
            mappingBean = new RequestMappingBean(requestMapping);
        }
        init(method);
    }

    public RequestMethodBean(Method method, RequestMappingBean mappingBean, String domainId) {
        this.method = method;
        this.className = method.getDeclaringClass().getName();
        this.mappingBean = mappingBean;
        this.domainId = domainId;
        init(method);
    }

    boolean hasParams(String paramName) {
        for (RequestParamBean paramBean : paramSet) {
            if (paramBean.getParamName().equals(paramName)) {
                return true;
            }
        }
        return false;
    }

    void init(Method sourceMethod) {
        RequestMapping requestMapping = AnnotationUtil.getMethodAnnotation(sourceMethod, RequestMapping.class);
        if (requestMapping != null && mappingBean != null) {
            mappingBean = new RequestMappingBean(requestMapping, mappingBean.getParentPath());
        }

        Type[] parameterTypes = sourceMethod.getGenericParameterTypes();
        Class[] parameterClasses = sourceMethod.getParameterTypes();
        String[] paramNames = new String[parameterTypes.length];


        //  Object[][] annotations = sourceMethod.getParameterAnnotations();
        this.requestType = RequestType.FORM;
        this.methodName = sourceMethod.getName();
        this.returnClassName = sourceMethod.getReturnType().getName();

        this.responseBody = AnnotationUtil.getMethodAnnotation(sourceMethod, ResponseBody.class);
        this.methodChinaName = AnnotationUtil.getMethodAnnotation(sourceMethod, MethodChinaName.class);
        this.name = methodName;


        if (mappingBean != null) {
            Set<RequestMethod> methods = mappingBean.getMethod();
            this.url = mappingBean.getFristUrl();

            if (mappingBean.getName() != null && !mappingBean.getName().equals("")) {
                this.name = mappingBean.getName();
            }

            if (methods != null && methods.size() > 0) {
                if (methods.size() > 1) {
                    queryMethod = HttpMethod.auto;
                } else {
                    queryMethod = HttpMethod.fromType(methods.iterator().next().name());
                }
            } else {
                if (responseBody != null) {
                    responseType = ResponseType.JSON;
                } else {
                    responseType = ResponseType.TEXT;
                }
            }
        }


        Parameter[] parameters = sourceMethod.getParameters();
        for (int i = 0; i < paramNames.length; i++) {
            Parameter parameter = parameters[i];
            RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
            if (requestBody != null) {
                requestType = RequestType.JSON;
            }
        }


        if (requestType.equals(RequestType.JSON)) {
            Class clazz = parameterClasses[0];
            Method[] ctMethods = clazz.getDeclaredMethods();
            for (Method method : ctMethods) {
                if (method.getName().startsWith("get")) {
                    String fileName = method.getName().substring(3, method.getName().length());
                    if (fileName.length() > 0) {
                        fileName = fileName.substring(0, 1).toLowerCase() + fileName.substring(1, fileName.length());
                    } else {
                        System.out.println(method.getName() + method);
                    }
                    paramsMap.put(fileName, method.getReturnType().getName());
                }
            }
        } else {
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
                Annotation[] annotations = AnnotationUtil.getParameterAnnotations(sourceMethod, i);
                for (Object annotationType : annotations) {
                    annotationSet.add((Annotation) annotationType);
                }
                RequestParamBean paramBean = new RequestParamBean(paramName, annotationSet, parameterTypes[i], parameterClasses[i]);
                paramSet.add(paramBean);
                paramsMap.put(paramName, parameterClasses[i].getName());
            }
        }
    }


    public RequestMethodBean(CtMethod ctmethod, RequestMappingBean mapping, String domainId) {

        this.ctmethod = ctmethod;
        this.mappingBean = mapping;//new RequestMappingBean(mapping, config.getUrl());
        this.methodName = ctmethod.getName();
        this.className = ctmethod.getDeclaringClass().getName();

        try {
            MethodInfo methodInfo = ctmethod.getMethodInfo();
            Object[][] annotations = ctmethod.getAvailableParameterAnnotations();
            if (this.getSourceMethod() != null) {
                Type[] parameterTypes = this.getSourceMethod().getGenericParameterTypes();
                Class[] parameterClasses = this.getSourceMethod().getParameterTypes();
                String[] paramNames = new String[parameterTypes.length];
                Set<RequestMethod> methods = mapping.getMethod();
                this.requestType = RequestType.FORM;
                this.methodName = methodInfo.getName();
                this.url = mappingBean.getFristUrl();
                this.responseBody = AnnotationUtil.getMethodAnnotation(this.getSourceMethod(), ResponseBody.class);
                this.methodChinaName = AnnotationUtil.getMethodAnnotation(this.getSourceMethod(), MethodChinaName.class);
                this.name = methodName;
                if (mappingBean.getName() != null && !mappingBean.getName().equals("")) {
                    this.name = mappingBean.getName();
                } else if (mappingBean.getValue() != null && mappingBean.getValue().size() > 0) {
                    this.name = mappingBean.getValue().iterator().next();
                }

                if (methods != null && methods.size() > 0) {
                    if (methods.size() > 1) {
                        queryMethod = HttpMethod.auto;
                    } else {
                        queryMethod = HttpMethod.fromType(methods.iterator().next().name());
                    }
                } else {
                    if (responseBody != null) {
                        responseType = ResponseType.JSON;
                    } else {
                        responseType = ResponseType.TEXT;
                    }
                }

                if (annotations.length == 1 && requestType.equals(RequestType.JSON)) {
                    Class clazz = parameterClasses[0];
                    Method[] ctMethods = clazz.getDeclaredMethods();
                    for (Method method : ctMethods) {
                        if (method.getName().startsWith("get")) {
                            String fileName = method.getName().substring(3, method.getName().length());
                            if (fileName.length() > 0) {
                                fileName = fileName.substring(0, 1).toLowerCase() + fileName.substring(1, fileName.length());
                            } else {
                                System.out.println(method.getName() + method);
                            }
                            paramsMap.put(fileName, method.getReturnType().getName());
                        }
                    }
                } else {

                    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

                    if (codeAttribute != null) {
                        //JAVA8获取
                        TreeMap<Integer, String> sortMap = new TreeMap<Integer, String>();
                        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                        int pos = Modifier.isStatic(ctmethod.getModifiers()) ? 0 : 1;
                        if (attr != null) {
                            for (int i = 0; i < attr.tableLength(); i++) {
                                sortMap.put(attr.index(i), attr.variableName(i));
                                if (attr.index(i) >= pos && attr.index(i) < paramNames.length + pos) {
                                    paramNames[attr.index(i) - pos] = attr.variableName(i);
                                }
                            }
                        }
                    }
                    Method sourceMethod = this.getSourceMethod();
                    if (annotations != null && annotations.length > 0) {
                        int k = 0;
                        for (Object[] annotationTypes : annotations) {
                            for (Object annotation : annotationTypes) {
                                if (RequestBody.class.isAssignableFrom(annotation.getClass())) {
                                    requestType = RequestType.JSON;
                                }
                                if (PathVariable.class.isAssignableFrom(annotation.getClass()) && paramNames.length > k) {
                                    pathVariables.put(paramNames[k], (PathVariable) annotation);
                                }
                            }

                            k = k + 1;
                        }
                    }

                    Parameter[] parameters = sourceMethod.getParameters();
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
                        paramSet.add(paramBean);
                        paramsMap.put(paramName, parameterClasses[i].getName());
                    }
                }
            }
            this.method = this.getSourceMethod();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @JSONField(serialize = false)
    public RequestParamBean getRealParams(String name) {
        for (RequestParamBean param : this.getParamSet()) {
            if (param.getParamName().equals(name)) {
                return param;
            }
        }
        return null;
    }


    @JSONField(serialize = false)
    public Method getSourceMethod() throws ClassNotFoundException {
        if (method == null && this.getClassName() != null) {
            Class clazz = ClassUtility.loadClass(this.getClassName());
            for (Method cmethod : clazz.getDeclaredMethods()) {
                RequestMapping cmapping = AnnotationUtil.getMethodAnnotation(cmethod, RequestMapping.class);
                if (cmapping != null) {
                    RequestMappingBean cmappingBean = new RequestMappingBean(cmapping);
                    if (cmappingBean.getValue().equals(mappingBean.getValue())) {
                        if (ctmethod != null) {
                            try {
                                if (cmethod.getParameters().length == ctmethod.getParameterTypes().length) {
                                    method = cmethod;
                                }
                            } catch (NotFoundException e) {
                                e.printStackTrace();
                            }
                        } else if (cmethod.getParameters().length == paramSet.size()) {
                            method = cmethod;
                        }

                    }
                }

            }

            if (method == null) {
                for (Method cmethod : clazz.getMethods()) {
                    if (cmethod.getName().equals(this.getMethodName())) {
                        if (ctmethod != null) {
                            try {
                                if (cmethod.getParameters().length == ctmethod.getParameterTypes().length) {
                                    method = cmethod;
                                }
                            } catch (NotFoundException e) {
                                e.printStackTrace();
                            }
                        } else if (cmethod.getParameters().length == paramSet.size()) {
                            method = cmethod;
                        }
                    }

                }
            }
        }

        return method;

    }


    @JSONField(serialize = false)
    public Class getInnerClass() throws ClassNotFoundException {
        Method method = this.getSourceMethod();
        Class clazz = JSONGenUtil.getInnerReturnType(method.getReturnType());
        return clazz;

    }


    private Object getService(OgnlContext onglContext, Map<String, Object> allParamsMap) throws ClassNotFoundException, OgnlException {
        Class clazz = ClassUtility.loadClass(this.getClassName());
        Object service = getRealService(clazz, onglContext);
        for (Field field : clazz.getDeclaredFields()) {
            if (allParamsMap.get(field.getName()) != null) {
                try {
                    OgnlRuntime.setProperty(onglContext, service, field.getName(), TypeUtils.castToJavaBean(allParamsMap.get(field.getName()), field.getType()));
                } catch (OgnlException e) {
                }
            }
        }
        return service;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public CtMethod getCtmethod() {
        return ctmethod;
    }

    public void setCtmethod(CtMethod ctmethod) {
        this.ctmethod = ctmethod;
    }

    public void setReturnClass(Class returnClass) {
        this.returnClass = returnClass;
    }

    Object getRealService(Class clazz, OgnlContext onglContext) throws OgnlException {
        Object service = null;
        if (clazz.getInterfaces().length > 0) {
            service = EsbUtil.parExpression(clazz.getInterfaces()[0]);
        } else {
            service = EsbUtil.parExpression(clazz);
        }

        if (service == null) {
            if (clazz.isInterface()) {
                Aggregation aggregation = (Aggregation) clazz.getAnnotation(Aggregation.class);
                if (aggregation != null && !aggregation.rootClass().equals(Void.class) && !aggregation.rootClass().equals(clazz)) {
                    clazz = aggregation.rootClass();
                    service = getRealService(clazz, onglContext);
                }
            } else {
                service = OgnlRuntime.callConstructor(onglContext, clazz.getName(), new Object[]{});
            }

        }

        return service;
    }


    public Object invok(OgnlContext onglContext, Map<String, Object> allParamsMap) throws ClassNotFoundException, OgnlException {
        Object object = null;
        long startTime = System.currentTimeMillis();
        if (onglContext == null) {
            onglContext = JDSActionContext.getActionContext().getOgnlContext();
        }
        Object service = getService(onglContext, allParamsMap);

        Map<String, String> paramsMap = this.getParamsMap();
        Set<RequestParamBean> keySet = this.getParamSet();
        Object[] objects = new Object[paramsMap.size()];
        Class[] objectTyps = new Class[paramsMap.size()];
        if (!this.getRequestType().equals(RequestType.JSON)) {
            int k = 0;
            for (RequestParamBean paramBean : keySet) {
                String key = paramBean.getParamName();
                Class ctClass = ClassUtility.loadClass(paramsMap.get(paramBean.getParamName()));
                String iClassName = ctClass.getName();


                Class iClass = ClassUtility.loadClass(iClassName);
                Object value = null;
                Map<String, Object> contextMap = JDSActionContext.getActionContext().getContext();
                if (allParamsMap.get(key) != null) {
                    if (paramBean.getJsonData()) {
                        value = JSONObject.parseObject(JSONObject.toJSONString(allParamsMap.get(key)), paramBean.getParamClass());
                    } else {
                        value = TypeUtils.castToJavaBean(allParamsMap.get(key), iClass);
                    }
                    contextMap.put(key, value);
                }
                objectTyps[k] = iClass;
                objects[k] = value;
                k = k + 1;
            }
        } else {
            RequestParamBean requestParamBean = keySet.iterator().next();
            JSONObject jsonObject = new JSONObject(allParamsMap);
            JSONObject.toJavaObject(jsonObject, requestParamBean.getParamClass());
            objects[0] = TypeUtils.castToJavaBean(allParamsMap, requestParamBean.getParamClass());
        }


        if (service != null) {
            object = OgnlRuntime.callMethod(onglContext, service, this.getMethodName(), objects);
        }

        return object;
    }

    public String getReturnClassName() {
        return returnClassName;
    }

    public void setReturnClassName(String returnClassName) {
        this.returnClassName = returnClassName;
    }

    public Map<String, Object> getDefaultParamsValueMap() {
        return defaultParamsValueMap;
    }

    public void setDefaultParamsValueMap(Map<String, Object> defaultParamsValueMap) {
        this.defaultParamsValueMap = defaultParamsValueMap;
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

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    public HttpMethod getQueryMethod() {
        return queryMethod;
    }

    public void setQueryMethod(HttpMethod queryMethod) {
        this.queryMethod = queryMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public MethodChinaName getMethodChinaName() {
        return methodChinaName;
    }

    public void setMethodChinaName(MethodChinaName methodChinaName) {
        this.methodChinaName = methodChinaName;
    }

    public RequestMappingBean getMappingBean() {
        return mappingBean;
    }

    public void setMappingBean(RequestMappingBean mappingBean) {
        this.mappingBean = mappingBean;
    }

    public String getPathVariable() {
        return pathVariable;
    }

    public void setPathVariable(String pathVariable) {
        this.pathVariable = pathVariable;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }


    public Map<String, PathVariable> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(Map<String, PathVariable> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public LinkedHashSet<RequestParamBean> getParamSet() {
        return paramSet;
    }

    public void setParamSet(LinkedHashSet<RequestParamBean> paramSet) {
        this.paramSet = paramSet;
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

    @JSONField(serialize = false)
    public Class getReturnClass() {
        if (returnClass == null && returnClassName != null) {
            try {
                returnClass = ClassUtility.loadClass(returnClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return returnClass;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    @Override
    public String toString() {
        return methodName;
    }
}
