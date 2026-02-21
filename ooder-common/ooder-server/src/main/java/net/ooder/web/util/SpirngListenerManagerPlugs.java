package net.ooder.web.util;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.JDSListener;
import net.ooder.common.util.ClassUtility;
import net.ooder.context.JDSActionContext;
import net.ooder.esb.config.annotation.AbstractAnnotationtExpressionTempManager;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.esb.event.ListenerLoad;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;


@Component
@EsbBeanAnnotation
public class SpirngListenerManagerPlugs extends AbstractAnnotationtExpressionTempManager implements ResourceLoaderAware, ApplicationContextAware, ListenerLoad {

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private MetadataReaderFactory metadataReaderFactory;
    public static Map<Class<? extends EventListener>, List<ExpressionTempBean>> listenerMap = new HashMap<Class<? extends EventListener>, List<ExpressionTempBean>>();
    public Map<String, ExpressionTempBean> listenerBeanMap = new HashMap<String, ExpressionTempBean>();

    public List<ExpressionTempBean> getListenerTempBeanByType(Class<? extends EventListener> listenerClass) {

        List<JDSListener> listeners = new ArrayList<JDSListener>();

        Set<Map.Entry<String, ExpressionTempBean>> tempEntry = listenerBeanMap.entrySet();

        List<ExpressionTempBean> tempLst = listenerMap.get(listenerClass);

        if (tempLst == null || tempLst.isEmpty()) {
            tempLst = new ArrayList<ExpressionTempBean>();
            for (Map.Entry<String, ExpressionTempBean> entry : tempEntry) {
                ExpressionTempBean bean = entry.getValue();
                String classType = bean.getClazz();
                Class clazz = null;
                try {
                    clazz = ClassUtility.loadClass(classType);
                } catch (ClassNotFoundException e) {
                    continue;
                }

                if (listenerClass.isAssignableFrom(clazz)) {
                    tempLst.add(bean);
                }
                ;
            }
            listenerMap.put(listenerClass, tempLst);

        }
        return tempLst;
        //}
    }

    @Override
    protected void fillBean(Set<Class<?>> classList2) {

    }

    @Override
    public List<ServiceBean> getAllListener() {
        return null;
    }

    public <T extends EventListener> List<T> getListenerByType(Class<T> listenerClass) {
        List<ExpressionTempBean> tempLst = getListenerTempBeanByType(listenerClass);
        List<T> listeners = new ArrayList<T>();
        for (ExpressionTempBean tempBean : tempLst) {
            T listener = JDSActionContext.getActionContext().Par("$" + tempBean.getId(), listenerClass);
            if (listener != null) {
                listeners.add(listener);
            }
        }
        return listeners;


    }


    /**
     * 根据包路径获取包及子包下的所有类
     *
     * @param basePackage basePackage
     * @return Set<Class       <       ?>>
     */
    private Set<Class<?>> scannerPackages(String basePackage) {
        Set<Class<?>> set = new LinkedHashSet<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(className);
                        if (AnnotationUtil.getClassAnnotation(clazz, RequestMapping.class) != null) {
                            set.add(clazz);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }


    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    private ResourcePatternResolver resourcePatternResolver;

    private ApplicationContext applicationContext;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }


}
