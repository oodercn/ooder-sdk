/**
 * $RCSfile: SpringMVCTempLoadProxy.java,v $
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


import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.JDSBusException;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.StringUtility;
import net.ooder.esb.config.manager.EsbBean;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.esb.config.manager.ServiceConfigManager;
import net.ooder.web.util.AnnotationUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.*;

/**
 * 采用springMvc装载
 */
public class SpringMVCTempLoadProxy implements ServiceConfigManager {
    private static final Log logger = LogFactory.getLog(
            JDSConstants.CONFIG_KEY, SpringMVCTempLoadProxy.class);

    private final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private final String defaulePath = "net.ooder.";

    private final EsbBean esbBean;

    public SpringMVCTempLoadProxy(EsbBean esbBean) {
        this.esbBean = esbBean;
        esbBean.getPath();

    }

    List<ServiceBean> remoteBeans = new ArrayList<ServiceBean>();
    Map<String, ServiceBean> remoteBeanMap = new HashMap<String, ServiceBean>();
    Map<String, ServiceBean> nameBeanMap = new HashMap<String, ServiceBean>();

    @Override
    public ServiceBean getServiceConfigById(String id) {
        return remoteBeanMap.get(id);
    }

    @Override
    public ServiceBean getServiceConfigByName(String name) {
        return nameBeanMap.get(name);
    }

    @Override
    public Map<String, ServiceBean> findServiceConfigMapByName() {
        return remoteBeanMap;
    }

    @Override
    public Map<String, ServiceBean> findServiceConfigMapById() {
        return remoteBeanMap;
    }

    @Override
    public List<ServiceBean> loadAllService() {
        return remoteBeans;
    }

    @Override
    public Set<Class<?>> init() throws JDSBusException {
        String path = esbBean.getPath();
        if (path != null && path.indexOf(";") > -1) {
            String[] paths = StringUtility.split(";", path);
            for (String packetPath : paths) {
                this.scannerPackages(packetPath);
            }
        } else {
            this.scannerPackages(defaulePath);
        }

        return null;
    }


    /**
     * @param basePackage basePackage
     */
    private Set<Class<?>> scannerPackages(String basePackage) {
        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(null);
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        Set<Class<?>> set = new LinkedHashSet<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(className);
                        fullExpressionTempBean(clazz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }


    public void fullExpressionTempBean(Class clazz) {
        ExpressionTempBean expressionTempBean = null;
        if (clazz.isAnnotationPresent(EsbBeanAnnotation.class)) {
            EsbBeanAnnotation esbBeanAnnotation = AnnotationUtil.getClassAnnotation(clazz,EsbBeanAnnotation.class);
            expressionTempBean = new ExpressionTempBean();
            String name = esbBeanAnnotation.name();
            String id = esbBeanAnnotation.id();
            expressionTempBean.setDataType(esbBeanAnnotation.dataType());

            if (esbBeanAnnotation.dataType().equals("server")) {
                expressionTempBean.setExpressionArr("GetClientService(\"" + clazz.getName() + "\",\"" + esbBeanAnnotation.serverUrl() + "\")");
            } else {
                expressionTempBean.setExpressionArr(esbBeanAnnotation.expressionArr());
            }
            expressionTempBean.setServerUrl(esbBeanAnnotation.serverUrl());
            expressionTempBean.setId(id);
            expressionTempBean.setJspUrl(esbBeanAnnotation.jspUrl());
            expressionTempBean.setDesc(esbBeanAnnotation.desc());
            expressionTempBean.setName(name);
            expressionTempBean.setMainClass(clazz.getName());
            expressionTempBean.setReturntype(esbBeanAnnotation.expressionArr().substring(0, esbBeanAnnotation.expressionArr().indexOf("(")));
            expressionTempBean.setClazz(clazz.getName());
            remoteBeans.add(expressionTempBean);
            remoteBeanMap.put(id, expressionTempBean);
            nameBeanMap.put(name, expressionTempBean);


        }
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(basePackage);
    }


}