/**
 * $RCSfile: AbstractAnnotationtExpressionTempManager.java,v $
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
package net.ooder.esb.config.annotation;

import javassist.ClassPool;
import javassist.NotFoundException;
import net.ooder.annotation.ClassMappingAnnotation;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.EsbFlowType;
import net.ooder.common.FormulaType;
import net.ooder.common.JDSBusException;
import net.ooder.common.JDSCommand;
import net.ooder.common.expression.function.Function;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.Constants;
import net.ooder.common.util.JarLoader;
import net.ooder.common.util.StringUtility;
import net.ooder.config.JDSConfig;
import net.ooder.esb.config.manager.*;
import net.ooder.web.ConstructorBean;
import net.ooder.web.RequestMethodBean;
import net.ooder.web.RequestParamBean;
import net.sf.cglib.beans.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractAnnotationtExpressionTempManager implements ServiceConfigManager {
    private static final Log logger = LogFactory.getLog(Constants.CONFIG_KEY, AbstractAnnotationtExpressionTempManager.class);

    private static String OS = System.getProperty("os.name").toLowerCase();

    private static final Logger log = LoggerFactory.getLogger(AbstractAnnotationtExpressionTempManager.class);

    private static final String DefaultName = "perform";

    public Set<Class<?>> classList = new HashSet<>();

    private static Map<Class, String> pathMap = new HashMap<Class, String>();

    public Map<String, ServiceBean> nameMap = new HashMap<String, ServiceBean>();

    public Map<String, ServiceBean> idMap = new HashMap<String, ServiceBean>();

    public List<ServiceBean> serviceBeanList = new ArrayList<ServiceBean>();

    public EsbBean esbBean;

    public EsbBean getEsbBean() {
        return esbBean;
    }

    public void setEsbBean(EsbBean esbBean) {
        this.esbBean = esbBean;
    }

    public List<ServiceBean> loadAllService() {
        return serviceBeanList;
    }

    public Map<String, ServiceBean> findServiceConfigMapById() {
        return idMap;
    }

    public Map<String, ServiceBean> findServiceConfigMapByName() {
        return nameMap;
    }

    public ServiceBean getServiceConfigById(String id) {

        return idMap.get(id);
    }

    public ServiceBean getServiceConfigByName(String name) {
        return nameMap.get(name);
    }

    /**
     * 填充BEAN实体
     *
     * @param clazz
     */
    public ServiceBean fillBean(Class clazz, Map valueMap) {
        ServiceBean bean = null;
        if (pathMap.containsKey(clazz)) {
            String path = pathMap.get(clazz);
            valueMap.put("path", path);
            if (path.endsWith("class")) {
                File file = new File(pathMap.get(clazz));
                valueMap.put("creatTime", file.lastModified());
            }
        }
        try {


            Annotation[] annotations = clazz.getAnnotations();
            for (int j = 0; j < annotations.length; j++) {
                Annotation annotation = annotations[j];
                Class enumType = annotation.annotationType();
                try {
                    ClassMappingAnnotation classMappingAnnotation = annotation.annotationType().getAnnotation(ClassMappingAnnotation.class);
                    if (annotation instanceof EsbBeanAnnotation || (classMappingAnnotation != null && ServiceBean.class.isAssignableFrom(classMappingAnnotation.clazz()))) {
                        for (int k = 0; k < enumType.getDeclaredMethods().length; k++) {
                            Method method = enumType.getDeclaredMethods()[k];
                            valueMap.put(method.getName(), method.invoke(annotation, null));
                        }
                        if (classMappingAnnotation != null) {
                            bean = (ServiceBean) classMappingAnnotation.clazz().newInstance();
                        } else {
                            bean = new ExpressionTempBean();
                        }

                        BeanMap beanMap = BeanMap.create(bean);
                        beanMap.putAll(valueMap);

                        List<RequestParamBean> requestParamBeans = new ArrayList<>();

                        if (Function.class.isAssignableFrom(clazz)) {
                            Method method = findMethodByName(clazz, DefaultName);
                            RequestMethodBean requestMethodBean = new RequestMethodBean(method);
                            requestParamBeans.addAll(requestMethodBean.getParamSet());
                            valueMap.put("clazz", requestMethodBean.getReturnClass().getName());
                            beanMap.put("clazz", requestMethodBean.getReturnClass().getName());

                        } else if (bean.getType() != null && !bean.getType().equals(FormulaType.UNKNOW) && clazz.getConstructors().length == 1) {
                            Constructor constructor = clazz.getConstructors()[0];
                            ConstructorBean constructorBean = new ConstructorBean(constructor);
                            requestParamBeans.addAll(constructorBean.getParamList());
                        }

                        if (beanMap.get("clazz") == null || beanMap.get("clazz").equals("")) {
                            beanMap.put("clazz", clazz.getName());
                        }

                        if (beanMap.get("id") == null || beanMap.get("id").equals("")) {
                            beanMap.put("id", clazz.getSimpleName());
                        }

                        if (beanMap.get("serverUrl") == null || beanMap.get("serverUrl").equals("")) {
                            String serverUrl = JDSConfig.getValue(beanMap.get("id") + "." + "serverUrl");
                            if (serverUrl == null) {
                                serverUrl = JDSConfig.getValue("serverUrl");
                            }
                            beanMap.put("serverUrl", serverUrl);
                        }

                        List<ExpressionParameter> parameters = bean.getParams();
                        if (parameters == null) {
                            parameters = new ArrayList<>();
                        }
                        for (RequestParamBean requestParamBean : requestParamBeans) {
                            if (requestParamBean.getParameter() != null) {
                                parameters.add(requestParamBean.getParameter());
                            }
                        }
                        beanMap.put("params", parameters);
                        if (valueMap.get("expressionArr") == null || valueMap.get("expressionArr").equals("")) {
                            String expressionArr = clazz.getSimpleName() + "(";
                            for (RequestParamBean requestParamBean : requestParamBeans) {
                                ExpressionParameter parameter = requestParamBean.getParameter();
                                if (parameter != null && parameter.getParameterType().getExpression() != null && !requestParamBean.getParamClass().equals(String.class)) {
                                    expressionArr = expressionArr + parameter.getParameterType().getExpression() + ",";
                                } else {
                                    expressionArr = expressionArr + requestParamBean.getParamName() + ",";
                                }
                            }
                            if (expressionArr.endsWith(",")) {
                                expressionArr = expressionArr.substring(0, expressionArr.length() - 1);
                            }
                            expressionArr = expressionArr + ")";
                            beanMap.put("expressionArr", expressionArr);

                        }
                        beanMap.put("mainClass", clazz.getName());
                        String returntype = (String) beanMap.get("expressionArr");
                        if (JDSCommand.class.isAssignableFrom(clazz)) {
                            beanMap.put("flowType", EsbFlowType.command);
                        }
                        if (returntype != null && !returntype.equals("")) {
                            beanMap.put("returntype", returntype.substring(0, returntype.indexOf("(")));
                        }

                        if (bean.getName() == null || bean.getName().equals("")) {
                            beanMap.put("name", bean.getId());
                        }

                        if (this.idMap.containsKey(bean.getId())) {
                            ServiceBean oldBean = idMap.get(bean.getId());
                            if (bean.getVersion() > oldBean.getVersion()) {
                                serviceBeanList.remove(this.idMap.get(bean.getId()));
                                serviceBeanList.add(bean);
                                this.nameMap.put(bean.getName(), bean);
                                this.idMap.put(bean.getId(), bean);
                            } else if (bean.getVersion() == oldBean.getVersion()) {
                                if (!oldBean.getClazz().equals(bean.getClazz())) {
                                    logger.error("load bean err beanid=<<" + oldBean.getId() + ">> def in 2 class " + "[" + oldBean.getClazz() + "," + bean.getClazz() + "]");

                                }
                                serviceBeanList.remove(this.idMap.get(bean.getId()));
                                serviceBeanList.add(bean);
                                this.nameMap.put(bean.getName(), bean);
                                this.idMap.put(bean.getId(), bean);
                            }
                        } else {
                            serviceBeanList.add(bean);
                            this.nameMap.put(bean.getName(), bean);
                            this.idMap.put(bean.getId(), bean);
                        }
                        //EsbUtil.getClassMap().put(clazz, (ExpressionTempBean) bean);
                    }

                } catch (Throwable e) {
                    log.info("Error load  class==" + clazz.getName());
                    e.printStackTrace();
                }

            }
        } catch (Throwable e) {
            log.info("Error load  class==" + clazz.getName());
            e.printStackTrace();
        }

        return bean;

    }


    /***
     * 初始化，一般从 Jar 包读取 Bean
     *
     * @throws JDSBusException
     */
    public Set<Class<?>> init() throws JDSBusException {
        String parma = esbBean.getPath();
        log.info("path====================================:" + parma);
        if (parma == null) {
            return new HashSet<>();
        }
        String[] pathArr = parma.split(";");
        for (int k = 0; k < pathArr.length; k++) {

            String path = pathArr[k];
            try {

                //优先支持正则表达式
                if (path.indexOf(":") > -1) {
                    String[] paths = path.split(":");
                    String pattern = paths[1];
                    String folderPath = this.getRootPath(paths[0]);
                    File file = new File(folderPath);
                    if (!file.exists()) {
                        throw new IOException("file [" + path + "] load error! folderPath=" + folderPath);
                    }

                    if (file.isFile()) {
                        throw new IOException("file [" + path + "] not  folder! ");
                    }
                    log.info(pattern + " load success: start load:" + file.getAbsolutePath());
                    if (Paths.get(folderPath).toFile().isDirectory()) {

                        File[] files = file.listFiles();
                        for (File cfile : files) {
                            Pattern p = Pattern.compile(pattern);
                            Matcher matcher = p.matcher(cfile.getName());
                            if (matcher.find() && cfile.getName().endsWith("jar")) {
                                this.classList.addAll(this.initJar(cfile.getAbsolutePath()));
                                log.info(pattern + " pattern success: start load:" + cfile.getName());
                            }
                        }
                    }
                } else if (path.endsWith(".jar")) {
                    String rootPath = getRootPath(path);
                    this.classList.addAll(this.initJar(rootPath));
                } else {
                    File file = new File(path);
                    if (path.startsWith(File.separator)) {
                        String absolutePath = JDSConfig.getAbsolutePath("", this.getClass());
                        log.info("absolutePath:" + absolutePath);
                        String fileName = absolutePath + StringUtility.replace(path.substring(0, path.length() - 4), File.separator, "/");
                        log.info("realPah:{}", path);
                        file = new File(fileName);
                    } else if (path.equals("*")) {
                        file = new File(JDSConfig.getAbsolutePath("", null));
                    } else if (!path.equals("*") && path.startsWith("*")) {
                        String absolutePath = JDSConfig.getAbsolutePath("", null);
                        log.info("*absolutePath:" + path);
                        String fileName = absolutePath + StringUtility.replace(path.substring(1, path.length()), ".", File.separator);
                        file = new File(fileName);
                    }
                    if (file.exists() && file.listFiles() != null) {
                        toFileNames(file);
                    }
                }
            } catch (Exception e1) {
                throw new JDSBusException(pathArr[k] + " load Err  context=" + JDSConfig.getAbsolutePath("", this.getClass()), e1);
            }
        }
        fillBean(classList);
        return classList;
    }

    protected abstract void fillBean(Set<Class<?>> classList2);

    /**
     * 给定一个文件路径解析其中的.class或jar文件
     *
     * @param src
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void toFileNames(File src) throws IOException {
        File[] chs = src.listFiles();
        for (int i = 0; i < chs.length; i++) {
            if (chs[i].isFile()) {
                String path = chs[i].getPath();
                if (path.endsWith(".class")) {
                    String absolutePath = JDSConfig.getAbsolutePath("", this.getClass());
                    absolutePath = StringUtility.replace(absolutePath, "/", File.separator);
                    absolutePath = StringUtility.replace(absolutePath, "\\", File.separator);

                    if (absolutePath.startsWith(File.separator)) {
                        absolutePath = absolutePath.substring(1, absolutePath.length());
                    }
                    String className = StringUtility.replace(path, absolutePath, "");
                    className = StringUtility.replace(className, File.separator, ".");
                    className = className.substring(0, className.length() - 6);
                    Class clazz;
                    try {
                        clazz = ClassUtility.loadClass(className);
                        this.classList.add(clazz);
                        pathMap.put(clazz, chs[i].getAbsolutePath());
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        // e.printStackTrace();
                    }

                } else if (path.endsWith(".jar")) {
                    String rootPath = getRootPath(path);
                    this.initJar(rootPath);
                }
            } else {
                toFileNames(chs[i]);
            }
        }

    }

    private String getRootPath(String path) {
        log.info("originPath=[{}]", path);
        String rootPath = StringUtility.replace(path, "\\", File.separator);
        rootPath = StringUtility.replace(path, "/", File.separator);
        rootPath = StringUtility.replace(rootPath, "\\", File.separator);
        log.info("originPathAfterReplace=[{}]", rootPath);

        if (path.startsWith("JDSHome")) {
            rootPath = StringUtility.replace(path, "JDSHome", JDSConfig.getServerHome());
        } else if (path.startsWith("/") || path.startsWith("\\")) {
            String classPath = "";

            if (JDSConfig.getAbsolutePath("", this.getClass()).indexOf("WEB-INF/") > -1) {
                log.info("[{}]has[WEB-INF]", path);
                classPath = JDSConfig.getAbsolutePath("", this.getClass()).substring(0, JDSConfig.getAbsolutePath("", this.getClass()).indexOf("WEB-INF/"));
            } else if (JDSConfig.getAbsolutePath("", this.getClass()).indexOf("bin/") > -1) {

                classPath = JDSConfig.getAbsolutePath("", this.getClass()).substring(0, JDSConfig.getAbsolutePath("", this.getClass()).indexOf("bin/"));
            } else {
                classPath = JDSConfig.getAbsolutePath("", this.getClass());
            }


            classPath = StringUtility.replace(classPath, "/", File.separator);
            classPath = StringUtility.replace(classPath, "\\", File.separator);


            if (rootPath.startsWith(File.separator)) {
                rootPath = rootPath.substring(1, rootPath.length());
            }
            rootPath = classPath + rootPath;
            log.info("[{}]OS", OS);
            if (OS.indexOf("window") > -1) {
                if (rootPath.startsWith(File.separator)) {
                    rootPath = rootPath.substring(1, rootPath.length());
                }
            }


        }

        return rootPath;
    }

    /**
     * 解析JAR文件
     *
     * @param path 例如: / xxx/ xxx.jar
     * @return
     * @throws IOException
     */
    private Set<Class<?>> initJar(String path) throws IOException {

        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("file [" + path + "] load error! ");
        }

        JarLoader loader = EsbBeanFactory.getJarLoadManager().get(path);
        if (loader == null) {
            loader = new JarLoader(file, this.getClass().getClassLoader());
            EsbBeanFactory.getJarLoadManager().put(file.getAbsolutePath(), loader);
            ClassPool pool = ClassPool.getDefault();
            try {
                pool.appendClassPath(file.getPath());
            } catch (NotFoundException e) {
                e.printStackTrace();

            }
        }
        Set<Class<?>> classSet = loader.getAllClassByPackage();
        for (Class clazz : classSet) {
            EsbBeanFactory.getClassLoadManager().put(clazz.getName(), loader);
        }

        return loader.getAllClassByPackage();
    }

    public static Class getInnerClass(String fieldName, Class listClass) {
        Class innerClazz = null;

        try {
            Class clazz = null;
            String className = listClass.getName();
            if (className.indexOf("$$") > -1) {
                className = className.substring(0, className.indexOf("$$"));
            }
            clazz = ClassUtility.loadClass(className);

            innerClazz = findClassByKey(clazz, fieldName);
            if (innerClazz == null) {
                String getmethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                innerClazz = findClassByKey(clazz, getmethodName);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return innerClazz;
    }

    private static Class findClassByKey(Class clazz, String methodName) {
        Class classzz = null;
        Method method = findMethodByName(clazz, methodName);
        if (method.getName().equals(methodName)) {
            if (!List.class.isAssignableFrom(method.getReturnType())) {
                classzz = method.getReturnType();
            } else {
                try {
                    Type type = method.getGenericReturnType();
                    classzz = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                } catch (SecurityException e) {
                    log.error("SecurityException fieldName=" + methodName + "  in class" + clazz.getName());

                }
            }
        }

        return classzz;
    }

    private static Method findMethodByName(Class clazz, String methodName) {
        Method[] dmethods = clazz.getDeclaredMethods();
        for (int i = 0; i < dmethods.length; i++) {
            Method method = dmethods[i];
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }


}