/**
 * $RCSfile: EsbBeanFactory.java,v $
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


import net.ooder.annotation.Enums;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.annotation.EsbConstructor;
import net.ooder.annotation.ServiceStatus;
import net.ooder.cluster.ServerNode;
import net.ooder.common.*;
import net.ooder.common.expression.ExpressionParser;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.JarLoader;
import net.ooder.common.util.StringUtility;
import net.ooder.common.util.java.DynamicClassLoader;
import net.ooder.config.JDSConfig;
import net.ooder.esb.config.annotation.AbstractAnnotationtExpressionTempManager;
import net.ooder.esb.config.xml.ServiceConfig;
import net.ooder.server.JDSServer;
import net.ooder.server.SubSystem;
import net.ooder.web.util.AnnotationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EsbBeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(EsbBeanFactory.class);
    public Map<String, ServiceBean> nameMap = new LinkedHashMap<String, ServiceBean>();
    public Map<String, ServiceBean> idMap = new LinkedHashMap<String, ServiceBean>();

    public Map<String, ServiceConfigManager> managerMap = new LinkedHashMap<String, ServiceConfigManager>();
    public Map<String, List<? extends ServiceBean>> configListMap = new LinkedHashMap<String, List<? extends ServiceBean>>();
    private Map<String, List<? extends ServiceBean>> esbBeanKeyMap = new LinkedHashMap<String, List<? extends ServiceBean>>();
    public List<ServiceBean> expressionTempBeanList = new ArrayList<ServiceBean>();
    public Map<Class, List<ServiceBean>> classBeansMap = new LinkedHashMap<Class, List<ServiceBean>>();
    public List<EsbBean> esbBeanList = new ArrayList<EsbBean>();
    public EsbBeanConfig esbBeanConfig;
    private static Map<String, Class<?>> allClass = new ConcurrentHashMap<>();
    private static EsbBeanFactory esbBeanFantory;
    private static Map<String, JarLoader> classLoaderManager = new HashMap<String, JarLoader>();
    public static final String THREAD_LOCK = "Thread Lock";
    private Map<Class, ServiceBean> classMap = new HashMap<Class, ServiceBean>();
    private static Map<String, JarLoader> jarLoadManager = new HashMap<String, JarLoader>();
    private static Map<String, JarLoader> sourceManager = new HashMap<String, JarLoader>();


    /****
     * Important Entrance
     *
     * @return EsbBean 工厂,单例模式,只初始化一次
     */
    public static EsbBeanFactory getInstance() {
        if (esbBeanFantory == null) {
            synchronized (THREAD_LOCK) {
                if (esbBeanFantory == null) {
                    esbBeanFantory = new EsbBeanFactory();
                }
            }
        }
        return esbBeanFantory;
    }

    EsbBeanFactory() {
        esbBeanConfig = EsbBeanManager.getEsbBeanList();
        this.init();
    }


    public synchronized Set<Class<?>> dyReLoad(String esbkey) {
        if (esbkey == null) {
            esbkey = "local";
        }
        Set<Class<?>> classes = new LinkedHashSet<>();
        Map<String, EsbBean> esbBeanMap = esbBeanConfig.getEsbBeanMap();
        EsbBean esbBean = esbBeanMap.get(esbkey);
        if (esbBean != null) {
            classes = initEsbBean(esbkey, esbBean);
        }

        Set<String> keySet = ClassUtility.getDynClassMap().keySet();

        for (String key : keySet) {
            classes.add(ClassUtility.getDynClassMap().get(key));
        }
        return classes;
    }

    public void initSourceJar() {
        File file = JDSConfig.Config.libPath();
        for (File jarfile : file.listFiles()) {
            if (jarfile.getName().endsWith("jar")) {
                JarLoader jarLoader = sourceManager.get(jarfile.getAbsolutePath());
                if (jarLoader == null) {
                    try {
                        jarLoader = new JarLoader(jarfile);
                        sourceManager.put(jarfile.getAbsolutePath(), jarLoader);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public byte[] getJavaSource(String className) {
        byte[] javaSource = null;
        JarLoader jarLoader = classLoaderManager.get(className);
        if (jarLoader == null) {
            initSourceJar();
            jarLoader = classLoaderManager.get(className);
        }

        try {
            if (jarLoader == null) {
                Set<Map.Entry<String, JarLoader>> jarSet = sourceManager.entrySet();
                for (Map.Entry<String, JarLoader> entry : jarSet) {
                    JarLoader loader = entry.getValue();
                    javaSource = loader.loadJava(className);
                    if (javaSource != null) {
                        classLoaderManager.put(className, loader);
                    }
                }
            } else {
                javaSource = jarLoader.loadJava(className);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return javaSource;
    }

    public static Map<String, JarLoader> getJarLoadManager() {
        return jarLoadManager;
    }

    public static Map<String, JarLoader> getClassLoadManager() {
        return classLoaderManager;
    }

    public synchronized void reLoad() {
        nameMap.clear();
        idMap.clear();
        classBeansMap.clear();
        configListMap.clear();
        esbBeanKeyMap.clear();
        expressionTempBeanList.clear();
        classMap.clear();
        allClass.clear();
        CommonConfig.reLoad();
        ServiceConfig.clear();
        esbBeanConfig = EsbBeanManager.getEsbBeanList();
        this.init();
    }

    private EsbBean getEsbBeanByUrl(String url) {
        for (EsbBean bean : esbBeanList) {
            if (bean != null && bean.getServerUrl() != null && url.startsWith(bean.getServerUrl())) {
                return bean;
            }
        }
        return null;
    }


    public EsbBean getTopEsbBeanById(String id) {
        for (EsbBean bean : esbBeanList) {
            if (bean.getId() != null && bean.getId().equals(id)) {
                return bean;
            }
        }
        return null;
    }


    public void registerService(String systemCode, ServiceBean bean) {
        ServerNode serverNode = JDSServer.getClusterClient().getServerNodeById(systemCode);
        if (serverNode != null) {
            SubSystem system = JDSServer.getClusterClient().getSystem(serverNode.getId());
            if (system != null) {
                EsbBean esbBean = this.getEsbBeanByUrl(serverNode.getUrl());
                if (esbBean == null) {
                    esbBean = new EsbBean(system);
                }
                this.addBean(esbBean, bean);
            }
        }
    }


    public ExpressionTempBean registerService(Class clazz) {

        if (clazz.getClassLoader() instanceof DynamicClassLoader) {
            try {
                clazz = ClassUtility.loadClass(clazz.getName());
                classBeansMap.remove(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        ExpressionTempBean expressionTempBean = getDefaultServiceBean(clazz);
        if (expressionTempBean == null) {
            AbstractAnnotationtExpressionTempManager localTempManager = (AbstractAnnotationtExpressionTempManager) this.getManagerMap().get("local");
            if (localTempManager == null) {
                localTempManager = (AbstractAnnotationtExpressionTempManager) this.getManagerMap().get("esb");
            }
            expressionTempBean = (ExpressionTempBean) localTempManager.fillBean(clazz, new HashMap());
            if (expressionTempBean != null) {
                this.addBean(localTempManager.getEsbBean(), expressionTempBean);
                expressionTempBean = getDefaultServiceBean(clazz);
            }

        }

        return expressionTempBean;
    }


    public ExpressionTempBean getDefaultServiceBean(Class clazz) {
        ExpressionTempBean bean = (ExpressionTempBean) classMap.get(clazz);
        List<? extends ServiceBean> beans = getServiceBeanList(clazz);
        if (beans.size() > 0) {
            for (ServiceBean tempBean : beans) {
                if (tempBean.getDataType().equals(ContextType.Action)) {
                    bean = (ExpressionTempBean) tempBean;
                }
            }
            if (bean == null) {
                bean = (ExpressionTempBean) beans.get(0);
            }
            classMap.put(clazz, bean);
        }
        return bean;
    }


    public List<? extends ServiceBean> getServiceBeanList(Class clazz) {
        List<ServiceBean> beans = classBeansMap.get(clazz);
        if (beans == null || beans.isEmpty()) {
            beans = new ArrayList<ServiceBean>();

            List<ServiceBean> copyTempBeanList = new ArrayList<ServiceBean>();
            copyTempBeanList.addAll(expressionTempBeanList);
            for (ServiceBean bean : copyTempBeanList) {
                ExpressionTempBean expressionTempBean = (ExpressionTempBean) bean;
                if (expressionTempBean.getStatus().equals(ServiceStatus.normal)) {
                    Class innerClazz = null;
                    try {
                        innerClazz = ClassUtility.loadClass(expressionTempBean.getClazz());
                        if (clazz.getName().equals(innerClazz.getName()) || clazz.isAssignableFrom(innerClazz) || ClassUtility.isAssignableFrom(clazz, innerClazz)) {
                            beans.add(expressionTempBean);
                        } else if (AbstractFunction.class.isAssignableFrom(innerClazz)) {

                        }

                    } catch (ClassNotFoundException e) {
                        ((ExpressionTempBean) bean).setStatus(ServiceStatus.unavailable);
                        logger.error("bean " + bean.getId() + "(" + bean.getClazz() + "): class not found ");
                        // logger.error("ExpressionTempManager addBean Error:", e.getMessage());
                    }
                }

                if (!beans.isEmpty()) {
                    classBeansMap.put(clazz, beans);
                }
            }
        }


        return beans;
    }

    void addBean(EsbBean esbbean, ServiceBean bean) {
        if (bean.getClazz() != null) {
            if (esbbean == null) {
                esbbean = this.getTopEsbBeanById("local");
            }
            if (idMap.containsKey(bean.getId())) {
                ServiceBean oldBean = idMap.get(bean.getId());

                if (oldBean != null && (bean.getVersion() > oldBean.getVersion() || esbbean.getEsbtype().equals(EsbBeanType.Local))) {
                    // update the bean to bigger version
                    expressionTempBeanList.remove(this.idMap.get(bean.getId()));
                    expressionTempBeanList.add(bean);
                    nameMap.put(bean.getName(), bean);
                    idMap.put(bean.getId(), bean);
                    Class clazz = null;
                    try {
                        clazz = ClassUtility.loadClass(bean.getClazz());
                        classBeansMap.remove(clazz);
                        if (bean instanceof ExpressionTempBean) {
                            classMap.put(clazz, (ExpressionTempBean) bean);
                        }
                    } catch (ClassNotFoundException e) {
                        logger.error("ExpressionTempManager addBean Error:", e);
                    }
                }
            } else {
                expressionTempBeanList.add(bean);
                nameMap.put(bean.getName(), bean);
                idMap.put(bean.getId(), bean);
            }
            JDSExpressionParserManager.fullBean((ExpressionTempBean) bean);
        }

    }


    public Map<EsbFlowType, List<? extends ServiceBean>> getAllServiceBeanByType() {
        Map<EsbFlowType, List<? extends ServiceBean>> typeListMap = new HashMap<EsbFlowType, List<? extends ServiceBean>>();
        EsbFlowType[] types = EsbFlowType.values();
        for (EsbFlowType type : types) {
            typeListMap.put(type, this.getServiceBeanByFlowType(type));
        }
        return typeListMap;
    }

    public List<? extends ServiceBean> getServiceBeanByFlowType(EsbFlowType... flowType) {
        List<? extends ServiceBean> beans = loadAllServiceBean();
        List<EsbFlowType> flowTypes = Arrays.asList(flowType);
        List<ExpressionTempBean> fitleBeans = new ArrayList<ExpressionTempBean>();
        for (ServiceBean bean : beans) {
            if (bean.getFlowType() != null && flowTypes.contains(bean.getFlowType())) {
                if (bean instanceof ExpressionTempBean) {
                    fitleBeans.add((ExpressionTempBean) bean);
                }

            }
        }
        return fitleBeans;
    }

    public List<? extends ServiceBean> getFormula(FormulaType... flowType) {
        List<? extends ServiceBean> beans = loadAllServiceBean();
        List<FormulaType> flowTypes = Arrays.asList(flowType);
        List<ExpressionTempBean> fitleBeans = new ArrayList<ExpressionTempBean>();
        for (ServiceBean bean : beans) {
            if (bean.getType() != null && flowTypes.contains(bean.getType())) {
                if (bean instanceof ExpressionTempBean) {
                    fitleBeans.add((ExpressionTempBean) bean);
                }
            }
        }
        return fitleBeans;
    }

    Set<Class<?>> initEsbBean(String key, EsbBean esbBean) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        ServiceConfigManager expressionTemManager = esbBean.getManager();
        try {
            classes = expressionTemManager.init();
            if (expressionTemManager instanceof AbstractAnnotationtExpressionTempManager) {
                if (classes != null) {
                    for (Class clazz : classes) {
                        if (clazz != null) {
                            allClass.put(clazz.getName(), clazz);
                        }
                    }
                } else {
                    logger.warn("ExpressionTempManager [" + esbBean.getId() + "]Init Error: calsses is null, path[" + esbBean.getPath() + "]");
                }
            }


        } catch (JDSBusException e) {
            logger.error("ExpressionTempManager Init Error:", e);
            e.printStackTrace();
        }
        managerMap.put(key, expressionTemManager);
        configListMap.put(esbBean.getPath(), expressionTemManager.loadAllService());
        esbBeanKeyMap.put(key, expressionTemManager.loadAllService());
        List<ServiceBean> esbList = expressionTemManager.loadAllService();
        for (ServiceBean bean : esbList) {
            addBean(esbBean, bean);
        }

        for (ServiceBean bean : esbList) {
            if (bean instanceof ExpressionTempBean) {
                try {
                    if (bean.getClazz() != null) {
                        Class clazz = ClassUtility.loadClass(bean.getClazz());
                        Constructor[] constructors = clazz.getDeclaredConstructors();
                        for (Constructor constructor : constructors) {
                            Parameter[] parameters = constructor.getParameters();
                            if (bean.getDataType() != null && bean.getDataType().equals(ContextType.Server)) {
                                ((ExpressionTempBean) bean).setFlowType(EsbFlowType.remoteAction);
                            }

                            if (parameters.length > 0 && constructor.getAnnotation(EsbConstructor.class) != null) {
                                StringBuffer strMethod = new StringBuffer();
                                strMethod.append(clazz.getSimpleName() + "(");
                                for (Parameter param : parameters) {
                                    ExpressionTempBean paramBean = this.getDefaultServiceBean(param.getType());
                                    if (paramBean != null) {
                                        strMethod.append("$" + paramBean.getExpressionArr() + ",");
                                    } else {
                                        strMethod.append("null,");
                                    }
                                }
                                strMethod.deleteCharAt(strMethod.length() - 1);
                                strMethod.append(")");
                                ((ExpressionTempBean) bean).setExpressionArr(strMethod.toString());
                                // System.out.println("--------------" + ((ExpressionTempBean) bean).getExpressionArr());
                            }
                        }
                        JDSExpressionParserManager.fullBean((ExpressionTempBean) bean);
                    } else {
                        logger.warn("bean " + bean.getId() + ": class in null");
                    }


                } catch (ClassNotFoundException e) {
                    logger.warn("bean " + bean.getId() + "(" + bean.getClazz() + "): class not found ");
                    //  logger.error("ExpressionTempManager Init Error:", e);
                }
            }

        }


        return classes;

    }

    public static Class<?> addClassCache(String className, Class<?> clazz) {
        allClass.put(className, clazz);
        return clazz;
    }

    public static void clear(String className) {
        allClass.remove(className);
    }

    public static Class<?> findClass(String className) {
        Class<?> clazz = allClass.get(className);
        return clazz;
    }


    public Map<String, Class<?>> getAllClass() {
        allClass.putAll(ClassUtility.getDynClassMap());
        return allClass;
    }

    public List<Class> findDicClass(Class<Enums> dicClass) {
        List<Class> classList = new ArrayList<>();
        Set<String> classNameSet = allClass.keySet();
        for (String className : classNameSet) {
            Class clazz = findClass(className);
            if (dicClass.isAssignableFrom(clazz)) {
                classList.add(clazz);
            }
        }
        return classList;
    }

    public Set<Class<?>> getAllClassBykey(String esbkey) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        if (esbkey == null) {
            esbkey = "local";
        }
        Map<String, EsbBean> esbBeanMap = esbBeanConfig.getEsbBeanMap();
        EsbBean esbBean = esbBeanMap.get(esbkey);
        if (esbBean != null) {
            ServiceConfigManager expressionTemManager = esbBean.getManager();
            try {
                classes = expressionTemManager.init();
            } catch (JDSBusException e) {
                logger.error("ExpressionTempManager Init Error:", e);
                e.printStackTrace();
            }
        }
        return classes;
    }


    Map<String, ServiceConfigManager> getManagerMap() {
        return managerMap;
    }

    Map<String, ServiceBean> getNameMap() {
        return nameMap;
    }

    public List<? extends ServiceBean> getAllServiceBeanByEsbKey(String key) {
        return esbBeanKeyMap.get(key);
    }


    public List<? extends ServiceBean> getServiceBeanByName(String serviceName) {
        Set<String> keySet = configListMap.keySet();
        List<ServiceBean> beans = new ArrayList<ServiceBean>();
        for (String key : keySet) {
            ServiceBean bean = getServiceBeanInEsb(key, serviceName);
            if (bean != null) {
                beans.add(bean);
            }
        }
        return beans;
    }


    private ServiceBean getServiceBeanInEsb(String esbkey, String serviceName) {
        List<? extends ServiceBean> beans = configListMap.get(esbkey);
        if (beans != null) {
            for (ServiceBean serviceBean : beans) {
                if (serviceBean.getId().equals(serviceName)) {
                    return serviceBean;
                }
            }
        }
        return null;
    }


    public EsbBeanConfig getEsbBeanConfig() {
        return esbBeanConfig;
    }

    public List<? extends ServiceBean> loadAllServiceBean() {
        return expressionTempBeanList;
    }


    public Map<String, List<? extends ServiceBean>> getConfigListMap() {
        return configListMap;
    }

    /***
     * Esb 初始化
     */
    public void init() {

        Map<String, EsbBean> esbBeanMap = esbBeanConfig.getEsbBeanMap();
        Iterator<String> it = esbBeanMap.keySet().iterator();
        for (; it.hasNext(); ) {
            String key = it.next();
            EsbBean esbBean = esbBeanMap.get(key);
            initEsbBean(key, esbBean);
            esbBeanList.add(esbBean);
        }

    }


    public ServiceBean getEsbBeanById(String objId) {
        EsbBeanFactory factory = EsbBeanFactory.getInstance();
        if (objId.startsWith("$")) {
            objId = objId.substring(1);
        }
        ExpressionTempBean bean = (ExpressionTempBean) factory.getIdMap().get(objId);

        if (factory.getEsbBeanConfig().getReload() != null && factory.getEsbBeanConfig().getReload().equals("true")) {
            ExpressionParser parser = JDSExpressionParserManager.getExpressionParser(null);
            try {
                JDSExpressionParserManager.loadStaticAllData(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bean.getPath() != null) {
                File file = new File(bean.getPath());
                if (bean.getCreatTime() == null || file.lastModified() > bean.getCreatTime()) {
                    String className = bean.getClazz();
                    try {
                        Class clazz = ClassUtility.loadClass(className);
                        EsbBeanAnnotation annotation = AnnotationUtil.getClassAnnotation(clazz, EsbBeanAnnotation.class);
                        bean.setExpressionArr(annotation.expressionArr());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return bean;

    }

    public static String formatFilePath(String path) {
        if (!"/".equals(File.separator)) {
            path = StringUtility.replace(path, "/", File.separator);
        } else {
            path = StringUtility.replace(path, "\\", File.separator);
        }
        return path;
    }

    public List<EsbBean> getLocalBeanList() {
        List<EsbBean> localBeans = new ArrayList<>();
        for (EsbBean esbBean : esbBeanList) {
            if (esbBean.getServerUrl() == null || esbBean.getServerUrl().equals("")) {
                esbBean.setEsbtype(EsbBeanType.Local);
                localBeans.add(esbBean);
            }
        }

        return localBeans;
    }

    public List<EsbBean> getRemoveBeanList() {
        List<EsbBean> localBeans = new ArrayList<>();
        for (EsbBean esbBean : esbBeanList) {
            if (esbBean.getServerUrl() != null && !esbBean.getServerUrl().equals("")) {
                if (esbBean.getEsbtype().equals(EsbBeanType.Local)) {
                    esbBean.setEsbtype(EsbBeanType.Remote);
                }
                localBeans.add(esbBean);
            }
        }

        return localBeans;

    }

    public List<EsbBean> getSystemBeanList() {
        List<EsbBean> localBeans = new ArrayList<>();
        for (EsbBean esbBean : esbBeanList) {
            if (esbBean.getEsbtype().equals(EsbBeanType.System)) {
                localBeans.add(esbBean);
            }
        }
        return localBeans;
    }

    public List<EsbBean> getEsbBeanList() {
        return esbBeanList;
    }

    public void setEsbBeanList(List<EsbBean> esbBeanList) {
        this.esbBeanList = esbBeanList;
    }


    public Map<String, ServiceBean> getIdMap() {
        return idMap;
    }


}