/**
 * $RCSfile: APIConfigFactory.java,v $
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

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.StringUtility;
import net.ooder.common.util.java.TmpJavaFileObject;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.web.util.JSONGenUtil;
import javassist.*;

import javax.tools.JavaFileObject;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIConfigFactory {


    Map<CtClass, APIConfig> classMap = new ConcurrentHashMap<CtClass, APIConfig>();

    Map<String, APIConfig> classApiMap = new ConcurrentHashMap<String, APIConfig>();

    private Map<String, RequestMethodBean> apiConfigMap = new ConcurrentHashMap<String, RequestMethodBean>();

    private Map<String, List<APIConfig>> apiConfigPaths = new ConcurrentHashMap<String, List<APIConfig>>();

    private Map<String, Class<?>> allClassMap = new HashMap<>();

    static APIConfigFactory instance;

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, APIConfigFactory.class);

    public static final String THREAD_LOCK = "Thread Lock";

    public static APIConfigFactory getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new APIConfigFactory();
                }
            }
        }
        return instance;
    }

    APIConfigFactory() {
        this.reload();
    }

    public synchronized void reload() {
        classMap.clear();
        classApiMap.clear();
        apiConfigMap.clear();
        allClassMap.putAll(EsbBeanFactory.getInstance().getAllClass());
        Set<Map.Entry<String, Class<?>>> allClass = allClassMap.entrySet();
        for (Map.Entry<String, Class<?>> clazzEntry : allClass) {
            Class clazz = clazzEntry.getValue();
            try {
                getAPIConfig(clazz.getName());
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    public void reload(String clazzName) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        classApiMap.remove(clazzName);
        CtClass ct = pool.getCtClass(clazzName);
        classMap.remove(ct);

//        Map<String, JavaFileObject> fileObjectMap = ClassUtility.getFileObjectMap();
//
//        if (fileObjectMap.containsKey(clazzName)) {
//            Set<String> classNameSet = ClassUtility.getFileObjectMap().keySet();
//            HashSet<String> nameSet = new LinkedHashSet();
//            nameSet.addAll(classNameSet);
//            for (String className : nameSet) {
//                if (className.equals(clazzName)) {
//                    // if (className.equals(clazzName) || className.startsWith(clazzName + "$")) {
//                    TmpJavaFileObject fileObject = (TmpJavaFileObject) fileObjectMap.get(className);
//                    ByteArrayClassPath path = new ByteArrayClassPath(clazzName, fileObject.getCompiledBytes());
//                    pool.appendClassPath(path);
//                }
//            }
//            CtClass ct = pool.getCtClass(clazzName);
//            classMap.remove(ct);
//
//        } else {
//            this.getAPIConfig(clazzName);
//        }

    }

    public Set<Class<?>> dyReload(Set<Class<?>> allClass) {
        if (allClass != null) {
            for (Class clazz : allClass) {
                try {
                    //if (!clazz.isInterface()) {
                    reload(clazz.getName());
                    //  }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return allClass;


    }


    public APIConfig getAPIConfigByEsbId(String esbbeanId) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        ServiceBean bean = EsbBeanFactory.getInstance().getIdMap().get(esbbeanId);
        return getAPIConfig(bean.getClazz());
    }

    public void clear(String url) {
        String localPath = formatUrl(url);
        RequestMethodBean bean = this.findMethodBean(localPath);
        if (bean != null) {
            String className = null;
            try {
                className = bean.getSourceMethod().getDeclaringClass().getName();
                Class clazz = JSONGenUtil.getInnerReturnType(bean.getSourceMethod());
                getAPIConfig(clazz.getName());
                getAPIConfig(className);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public RequestMethodBean getRequestMappingBean(String url) {
        RequestMethodBean requestMethodBean = apiConfigMap.get(url);
        if (requestMethodBean == null && url.startsWith("/")) {
            requestMethodBean = apiConfigMap.get(url.substring(1));
        }
        return requestMethodBean;
    }

    private String formatUrl(String url) {
        url = StringUtility.replace(url, "//", "/");
        if (url.indexOf("/") == -1) {
            url = StringUtility.replace(url, ".", "/");
        }
        String localPath = url;
        if (!localPath.startsWith("/")) {
            localPath = "/" + localPath;
        }
        return localPath;
    }

    public RequestMethodBean findMethodBean(String url) {
        String localPath = formatUrl(url);
        RequestMethodBean methodBean = apiConfigMap.get(localPath);
        synchronized (localPath) {
            String methodUrl = "";
            if (methodBean == null) {
                HashSet<String> nameSet = new LinkedHashSet();
                Set<String> keySet = apiConfigMap.keySet();
                nameSet.addAll(keySet);
                for (String key : nameSet) {
                    String configUrl = key;
                    while (configUrl.indexOf("{") > -1 && configUrl.indexOf("}") > -1) {
                        int start = configUrl.indexOf("{");
                        int end = configUrl.indexOf("}");
                        configUrl = StringUtility.replace(configUrl, configUrl.substring(start, end + 1), ".*?");
                    }

                    if (!configUrl.endsWith("?")) {
                        configUrl = configUrl + "$";
                    }
                    Pattern p = Pattern.compile(configUrl, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = p.matcher(localPath);
                    if (matcher.find()) {
                        if (key.length() > methodUrl.length()) {
                            methodBean = apiConfigMap.get(key);
                        }
                    }
                }

            }
        }

        return methodBean;
    }


    public APIConfig getAPIConfig(String clazzName) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        APIConfig config = classApiMap.get(clazzName);
        if (config == null) {
            CtClass ct = pool.getCtClass(clazzName);
            config = getAPIConfig(ct);
            if (config!=null){
                classApiMap.put(clazzName, config);
                List<APIConfig> configs = getAPIConfigs(config.getUrl());
                if (!configs.contains(config)) {
                    configs.add(config);
                }
            }
        }


//        Map<String, JavaFileObject> fileObjectMap = ClassUtility.getFileObjectMap();
//        if (fileObjectMap.containsKey(clazzName)) {
//            Set<String> classNameSet = ClassUtility.getFileObjectMap().keySet();
//            HashSet<String> nameSet = new LinkedHashSet();
//            nameSet.addAll(classNameSet);
//            for (String className : nameSet) {
//                if (className.equals(clazzName) || className.startsWith(clazzName + "$")) {
//                    TmpJavaFileObject fileObject = (TmpJavaFileObject) fileObjectMap.get(className);
//                    ByteArrayClassPath path = new ByteArrayClassPath(clazzName, fileObject.getCompiledBytes());
//                    pool.appendClassPath(path);
//                }
//            }
//
//            CtClass ct = pool.getCtClass(clazzName);
//            // if (!ct.isInterface()) {
//            config = getAPIConfig(ct);
//            //}
//        } else {
//
//            if (config == null) {
//                CtClass ct = pool.getCtClass(clazzName);
//                //if (!ct.isInterface()) {
//                config = getAPIConfig(ct);
//                if (config!=null){
//                    classApiMap.put(clazzName, config);
//                    List<APIConfig> configs = getAPIConfigs(config.getUrl());
//                    if (!configs.contains(config)) {
//                        configs.add(config);
//                    }
//                }
//                //   }
//            }
//        }
        return config;
    }

    public List<APIConfig> getAPIConfigs(String url) {
        List<APIConfig> configs = apiConfigPaths.get(url);
        if (configs == null) {
            configs = new ArrayList<>();
            apiConfigPaths.put(url, configs);
        }
        return configs;

    }

    public APIConfig getAPIConfig(CtClass clazz) {
        APIConfig config = classMap.get(clazz);
        if (config == null) {
            try {
                config = new APIConfig(clazz);
                for (RequestMethodBean bean : config.getMethods()) {
                    if (bean.getUrl() != null) {
                        if (apiConfigMap.get(bean.getUrl()) != null && !apiConfigMap.get(bean.getUrl()).getClassName().equals(bean.getClassName())) {
                            Class sclazz = bean.getSourceMethod().getDeclaringClass();
                            Class oclazz = apiConfigMap.get(bean.getUrl()).getSourceMethod().getDeclaringClass();
                            if (oclazz.isAssignableFrom(sclazz)) {
                                apiConfigMap.put(bean.getUrl(), bean);
                            }
                            if (!sclazz.isAssignableFrom(oclazz) && !oclazz.isAssignableFrom(sclazz)) {
                                logger.error("url[" + bean.getUrl() + "] error");
                                logger.error("error info fristUlr:[" + apiConfigMap.get(bean.getUrl()).getClassName() + ":" + apiConfigMap.get(bean.getUrl()).getName() + "]===>  lastUrl[" + bean.getClassName() + ":" + bean.getName() + "]");
                            }
                        } else {
                            apiConfigMap.put(bean.getUrl(), bean);
                        }
                    }
                }
            } catch (Throwable e) {
                logger.error("class APIConfig [" + clazz.getName() + "] error");
                e.printStackTrace();
            }
            // if (!clazz.isInterface()) {
            try {
                for (CtClass iclazz : clazz.getInterfaces()) {
                    if (allClassMap.containsKey(iclazz.getName())) {
                        classMap.put(iclazz, config);
                    }
                }
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            //    }
            if (config!=null){
                classMap.put(clazz, config);
            }

        }
        return config;
    }


    public synchronized RequestMappingBean getMapping(Method method, CtClass parentClass) throws SecurityException, NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        APIConfig config = getAPIConfig(parentClass);
        Class[] clazzs = method.getParameterTypes();
        CtClass[] ctClazz = new CtClass[clazzs.length];
        for (int k = 0; k < clazzs.length; k++) {
            ctClazz[k] = pool.get(clazzs[k].getName());
        }
        CtMethod pmethod = parentClass.getDeclaredMethod(method.getName(), ctClazz);
        RequestMappingBean mapping = config.getMethodMap().get(pmethod).getMappingBean();
        return mapping;

    }

}
