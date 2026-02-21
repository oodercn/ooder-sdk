/**
 * $RCSfile: APIConfig.java,v $
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

import net.ooder.annotation.MethodChinaName;
import net.ooder.common.util.StringUtility;
import net.ooder.web.util.AnnotationUtil;
import javassist.CtClass;
import javassist.CtMethod;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Modifier;
import java.util.*;

public class APIConfig {

    MethodChinaName chinaName;
    String className;
    String packageName;
    String url;
    String desc;
    String imageClass;
    String name;
    List<RequestMethodBean> methods = new ArrayList<RequestMethodBean>();
    Map<CtMethod, RequestMethodBean> methodMap = new HashMap<CtMethod, RequestMethodBean>();
    Set<String> urls = new LinkedHashSet<>();

    APIConfig(CtClass clazz) throws ClassNotFoundException {
        this.className = clazz.getName();
        this.url = "";
        name = clazz.getSimpleName();

        RequestMapping mapping = AnnotationUtil.getClassAnnotation(clazz, RequestMapping.class);
        if (mapping != null) {
            if (mapping.value().length > 0) {
                this.url = mapping.value()[0];
                for (String value : mapping.value()) {
                    this.urls.add(value);
                }
            } else if (mapping.path().length > 0) {
                this.url = mapping.path()[0];
                for (String value : mapping.path()) {
                    this.urls.add(value);
                }
            }
            if (url.indexOf("/") > -1) {
                String[] paths = url.split("/");
                this.packageName = "";
                for (int k = 0; k < paths.length; k++) {
                    if (packageName.equals("")) {
                        packageName = paths[k];
                    } else {
                        packageName = packageName + "." + paths[k];
                    }
                }
            }

        }

        this.chinaName = AnnotationUtil.getClassAnnotation(clazz, MethodChinaName.class);
        if (chinaName != null) {
            if (!chinaName.cname().equals("")) {
                desc = chinaName.cname();
            }
            if (!chinaName.imageClass().equals("")) {
                this.imageClass = chinaName.imageClass();
            }

        } else {
            desc = name;
        }

        List<CtMethod> allMethods = new ArrayList<>();
        for (CtMethod method : clazz.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                allMethods.add(method);
            }
        }

        for (CtMethod method : clazz.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && !method.getDeclaringClass().equals(clazz) && !method.getDeclaringClass().equals(Object.class)) {
                allMethods.add(method);
            }
        }


        for (CtMethod method : allMethods) {
            RequestMapping methodmapping = AnnotationUtil.getMethodAnnotation(method, RequestMapping.class);
            //  if (methodmapping != null && !method.getDeclaringClass().isInterface()) {
            if (methodmapping != null) {
                RequestMappingBean mappingBean = new RequestMappingBean(methodmapping, this.getUrl());
                RequestMethodBean requestMethodBean = new RequestMethodBean(method, mappingBean, null);
                Set<String> keys = requestMethodBean.getParamsMap().keySet();
                boolean isVal = true;
                for (String key : keys) {
                    if (key == null || key.equals("null")) {
                        isVal = false;
                    }
                }
                if (isVal && requestMethodBean.getUrl() != null && !requestMethodBean.getUrl().equals("")) {
                    methods.add(requestMethodBean);
                    methodMap.put(method, requestMethodBean);
                }

            }
        }

    }

    public RequestMethodBean getMethodByName(String methodName) {
        if (methodName.indexOf(".") > -1) {
            methodName = StringUtility.replace(methodName, ".", "/");
            if (!methodName.startsWith("/")) {
                methodName = "/" + methodName;
            }
        }
        for (RequestMethodBean bean : methods) {
            if (bean.getName().equals(methodName) || bean.getMethodName().equals(methodName) || bean.getUrl().endsWith("/" + methodName)) {
                return bean;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof APIConfig) {
            return ((APIConfig) obj).getClassName().equals(this.getClassName());
        }
        return super.equals(obj);
    }

    public void setChinaName(MethodChinaName chinaName) {
        this.chinaName = chinaName;
    }

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }

    public String getImageClass() {
        return imageClass;
    }

    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<CtMethod, RequestMethodBean> getMethodMap() {
        return methodMap;
    }

    public void setMethodMap(Map<CtMethod, RequestMethodBean> methodMap) {
        this.methodMap = methodMap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<RequestMethodBean> getMethods() {
        return methods;
    }


    public void setMethods(List<RequestMethodBean> methods) {
        this.methods = methods;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public MethodChinaName getChinaName() {
        return chinaName;
    }
}
