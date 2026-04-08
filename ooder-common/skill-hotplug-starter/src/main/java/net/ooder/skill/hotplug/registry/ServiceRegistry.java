package net.ooder.skill.hotplug.registry;

import net.ooder.skill.hotplug.classloader.PluginClassLoader;
import net.ooder.skill.hotplug.config.ServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务注册器
 * 负责管理Skill服务的动态注册和代理
 */
@Component
public class ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    @Autowired
    private ApplicationContext applicationContext;

    // Skill ID -> 服务代理映射
    private final Map<String, Map<String, ServiceProxy>> serviceProxies = new ConcurrentHashMap<>();

    // 接口 -> 实现映射（用于服务发现）
    private final Map<String, List<ServiceReference>> interfaceRegistry = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("ServiceRegistry initialized");
    }

    /**
     * 注册服务
     */
    public void registerService(String skillId, ServiceDefinition serviceDef, PluginClassLoader classLoader) {
        logger.info("Registering service: {} for skill: {}", serviceDef.getName(), skillId);

        try {
            // 加载接口和实现类
            Class<?> interfaceClass = classLoader.loadClass(serviceDef.getInterfaceClass());
            Class<?> implClass = classLoader.loadClass(serviceDef.getImplementationClass());

            // 创建服务实例
            Object serviceInstance = createServiceInstance(implClass);
            
            if (serviceInstance == null) {
                logger.info("Skipping service registration for: {} (requires Spring injection)", serviceDef.getName());
                return;
            }

            // 创建动态代理
            Object proxy = createProxy(interfaceClass, serviceInstance, classLoader, skillId);

            // 保存服务代理
            Map<String, ServiceProxy> skillServices = serviceProxies.computeIfAbsent(skillId, k -> new ConcurrentHashMap<>());
            ServiceProxy serviceProxy = new ServiceProxy(skillId, serviceDef.getName(), interfaceClass, serviceInstance, proxy);
            skillServices.put(serviceDef.getName(), serviceProxy);

            // 注册到接口索引
            String interfaceName = interfaceClass.getName();
            List<ServiceReference> references = interfaceRegistry.computeIfAbsent(interfaceName, k -> new ArrayList<>());
            references.add(new ServiceReference(skillId, serviceDef.getName(), serviceProxy));

            logger.info("Successfully registered service: {}.{} (interface: {})",
                    skillId, serviceDef.getName(), interfaceName);

        } catch (Exception e) {
            logger.error("Failed to register service: {} for skill: {}", serviceDef.getName(), skillId, e);
            throw new RuntimeException("Service registration failed", e);
        }
    }

    /**
     * 注册已创建的服务实例（用于 @Bean 方法创建的实例）
     */
    public void registerServiceInstance(String skillId, String serviceName, Class<?> interfaceClass, Object instance) {
        logger.info("Registering service instance: {} (type: {}) for skill: {}", serviceName, interfaceClass.getName(), skillId);

        try {
            Object proxy = instance;
            
            if (interfaceClass.isInterface()) {
                proxy = Proxy.newProxyInstance(
                        instance.getClass().getClassLoader(),
                        new Class<?>[]{interfaceClass},
                        new ServiceInvocationHandler(instance, skillId)
                );
            }

            Map<String, ServiceProxy> skillServices = serviceProxies.computeIfAbsent(skillId, k -> new ConcurrentHashMap<>());
            ServiceProxy serviceProxy = new ServiceProxy(skillId, serviceName, interfaceClass, instance, proxy);
            skillServices.put(serviceName, serviceProxy);

            String interfaceName = interfaceClass.getName();
            List<ServiceReference> references = interfaceRegistry.computeIfAbsent(interfaceName, k -> new ArrayList<>());
            references.add(new ServiceReference(skillId, serviceName, serviceProxy));

            logger.info("Successfully registered service instance: {}.{} (interface: {})",
                    skillId, serviceName, interfaceName);

        } catch (Exception e) {
            logger.error("Failed to register service instance: {} for skill: {}", serviceName, skillId, e);
            throw new RuntimeException("Service instance registration failed", e);
        }
    }

    /**
     * 注销指定Skill的所有服务
     */
    public void unregisterServices(String skillId) {
        logger.info("Unregistering services for skill: {}", skillId);

        Map<String, ServiceProxy> services = serviceProxies.remove(skillId);
        if (services == null || services.isEmpty()) {
            logger.warn("No services found for skill: {}", skillId);
            return;
        }

        // 从接口索引中移除
        for (ServiceProxy proxy : services.values()) {
            String interfaceName = proxy.getInterfaceClass().getName();
            List<ServiceReference> references = interfaceRegistry.get(interfaceName);
            if (references != null) {
                references.removeIf(ref -> ref.getSkillId().equals(skillId));
                if (references.isEmpty()) {
                    interfaceRegistry.remove(interfaceName);
                }
            }
        }

        logger.info("Successfully unregistered {} services for skill: {}", services.size(), skillId);
    }

    /**
     * 获取服务代理
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(String skillId, String serviceName, Class<T> interfaceClass) {
        Map<String, ServiceProxy> skillServices = serviceProxies.get(skillId);
        if (skillServices == null) {
            return null;
        }

        ServiceProxy proxy = skillServices.get(serviceName);
        if (proxy == null) {
            return null;
        }

        if (!interfaceClass.isAssignableFrom(proxy.getInterfaceClass())) {
            throw new IllegalArgumentException("Interface mismatch for service: " + serviceName);
        }

        return (T) proxy.getProxy();
    }

    /**
     * 通过接口获取服务
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getServicesByInterface(Class<T> interfaceClass) {
        List<ServiceReference> references = interfaceRegistry.get(interfaceClass.getName());
        if (references == null || references.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> services = new ArrayList<>();
        for (ServiceReference ref : references) {
            services.add((T) ref.getServiceProxy().getProxy());
        }
        return services;
    }

    /**
     * 获取指定Skill的所有服务
     */
    public Map<String, ServiceProxy> getServices(String skillId) {
        return serviceProxies.get(skillId);
    }

    /**
     * 获取所有已注册的服务
     */
    public Map<String, Map<String, ServiceProxy>> getAllServices() {
        return new ConcurrentHashMap<>(serviceProxies);
    }

    /**
     * 调用服务方法
     */
    public Object invokeService(String skillId, String serviceName, String methodName, Object... args) throws Exception {
        Map<String, ServiceProxy> skillServices = serviceProxies.get(skillId);
        if (skillServices == null) {
            throw new IllegalArgumentException("Skill not found: " + skillId);
        }

        ServiceProxy proxy = skillServices.get(serviceName);
        if (proxy == null) {
            throw new IllegalArgumentException("Service not found: " + serviceName);
        }

        Object serviceInstance = proxy.getTarget();
        Method method = findMethod(serviceInstance.getClass(), methodName, args);
        return method.invoke(serviceInstance, args);
    }

    // ==================== 私有方法 ====================

    private Object createServiceInstance(Class<?> implClass) throws Exception {
        Object instance = null;
        
        try {
            instance = applicationContext.getAutowireCapableBeanFactory().createBean(implClass);
            logger.info("Created service instance with Spring autowiring: {}", implClass.getName());
            return instance;
        } catch (Exception e) {
            logger.debug("Failed to create service with Spring autowiring ({}), trying constructor injection. Error: {}", 
                    implClass.getName(), e.getMessage());
        }
        
        instance = createInstanceWithConstructorInjection(implClass);
        if (instance != null) {
            return instance;
        }
        
        try {
            instance = implClass.getDeclaredConstructor().newInstance();
            logger.info("Created service instance with default constructor: {}", implClass.getName());
            
            manualInjectDependencies(instance, implClass);
            
            logger.info("Service {} dependencies manually injected successfully", implClass.getSimpleName());
            return instance;
        } catch (Exception e) {
            logger.warn("Could not create service instance: {}. Error: {}", implClass.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * 通过构造函数注入创建实例
     * 分析构造函数参数并从 Spring 容器获取依赖
     */
    private Object createInstanceWithConstructorInjection(Class<?> implClass) {
        java.lang.reflect.Constructor<?>[] constructors = implClass.getDeclaredConstructors();
        
        java.lang.reflect.Constructor<?> selectedConstructor = null;
        for (java.lang.reflect.Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                continue;
            }
            
            if (selectedConstructor == null || 
                constructor.getParameterCount() > selectedConstructor.getParameterCount()) {
                selectedConstructor = constructor;
            }
        }
        
        if (selectedConstructor == null) {
            logger.debug("No suitable constructor found for constructor injection in {}", implClass.getName());
            return null;
        }
        
        Class<?>[] paramTypes = selectedConstructor.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        
        for (int i = 0; i < paramTypes.length; i++) {
            Object dependency = resolveDependencyByType(paramTypes[i]);
            if (dependency == null) {
                logger.debug("Could not resolve constructor parameter {} (type: {}) for {}", 
                        i, paramTypes[i].getName(), implClass.getName());
                return null;
            }
            args[i] = dependency;
        }
        
        try {
            selectedConstructor.setAccessible(true);
            Object instance = selectedConstructor.newInstance(args);
            logger.info("Created service instance with constructor injection: {} ({} parameters)", 
                    implClass.getName(), args.length);
            
            manualInjectDependencies(instance, implClass);
            
            return instance;
        } catch (Exception e) {
            logger.warn("Failed to create instance with constructor injection for {}: {}", 
                    implClass.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * 按类型从 Spring 容器获取 Bean
     */
    private Object resolveDependencyByType(Class<?> type) {
        try {
            return applicationContext.getBean(type);
        } catch (Exception e) {
            logger.debug("Bean not found by type: {}", type.getName());
            return null;
        }
    }

    /**
     * 手动注入依赖
     * 扫描 @Autowired 和 @Resource 字段并从 Spring 容器注入
     */
    private void manualInjectDependencies(Object bean, Class<?> clazz) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(org.springframework.beans.factory.annotation.Autowired.class) ||
                    field.isAnnotationPresent(jakarta.annotation.Resource.class)) {
                    
                    try {
                        Object dependency = resolveDependency(field);
                        if (dependency != null) {
                            field.setAccessible(true);
                            field.set(bean, dependency);
                            logger.info("Manually injected dependency: {} -> {} in {}", 
                                    field.getName(), dependency.getClass().getSimpleName(), clazz.getSimpleName());
                        } else {
                            boolean required = true;
                            org.springframework.beans.factory.annotation.Autowired autowired = 
                                field.getAnnotation(org.springframework.beans.factory.annotation.Autowired.class);
                            if (autowired != null) {
                                required = autowired.required();
                            }
                            
                            if (required) {
                                logger.warn("Failed to resolve required dependency: {} in {}", 
                                        field.getName(), clazz.getSimpleName());
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to inject dependency: {} in {}. Error: {}", 
                                field.getName(), clazz.getSimpleName(), e.getMessage());
                    }
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    /**
     * 解析依赖
     * 优先按类型查找，其次按名称查找
     */
    private Object resolveDependency(Field field) {
        Class<?> fieldType = field.getType();
        
        jakarta.annotation.Resource resource = field.getAnnotation(jakarta.annotation.Resource.class);
        if (resource != null && resource.name() != null && !resource.name().isEmpty()) {
            try {
                return applicationContext.getBean(resource.name(), fieldType);
            } catch (Exception e) {
                logger.debug("Bean not found by name: {}", resource.name());
            }
        }
        
        try {
            return applicationContext.getBean(fieldType);
        } catch (Exception e) {
            logger.debug("Bean not found by type: {}", fieldType.getName());
        }
        
        String beanName = field.getName();
        try {
            Object bean = applicationContext.getBean(beanName);
            if (fieldType.isInstance(bean)) {
                return bean;
            }
        } catch (Exception e) {
            logger.debug("Bean not found by field name: {}", beanName);
        }
        
        String classNameBeanName = Character.toLowerCase(fieldType.getSimpleName().charAt(0)) + 
                                   fieldType.getSimpleName().substring(1);
        try {
            Object bean = applicationContext.getBean(classNameBeanName);
            if (fieldType.isInstance(bean)) {
                return bean;
            }
        } catch (Exception e) {
            logger.debug("Bean not found by class name: {}", classNameBeanName);
        }
        
        return null;
    }

    private Object createProxy(Class<?> interfaceClass, Object target, PluginClassLoader classLoader, String skillId) {
        // 使用JDK动态代理
        return Proxy.newProxyInstance(
                classLoader,
                new Class<?>[]{interfaceClass},
                new ServiceInvocationHandler(target, skillId)
        );
    }

    private Method findMethod(Class<?> clazz, String methodName, Object[] args) throws NoSuchMethodException {
        if (args == null || args.length == 0) {
            return clazz.getMethod(methodName);
        }

        Class<?>[] paramTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
        }

        return clazz.getMethod(methodName, paramTypes);
    }

    /**
     * 服务调用处理器
     */
    private static class ServiceInvocationHandler implements InvocationHandler {

        private final Object target;
        private final String skillId;

        public ServiceInvocationHandler(Object target, String skillId) {
            this.target = target;
            this.skillId = skillId;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 可以在这里添加拦截逻辑：日志、监控、权限检查等
            long startTime = System.currentTimeMillis();
            try {
                return method.invoke(target, args);
            } finally {
                long duration = System.currentTimeMillis() - startTime;
                if (logger.isDebugEnabled()) {
                    logger.debug("Service method invoked: {}.{} ({}ms)",
                            skillId, method.getName(), duration);
                }
            }
        }
    }

    /**
     * 服务代理信息
     */
    public static class ServiceProxy {
        private final String skillId;
        private final String serviceName;
        private final Class<?> interfaceClass;
        private final Object target;
        private final Object proxy;

        public ServiceProxy(String skillId, String serviceName, Class<?> interfaceClass, 
                           Object target, Object proxy) {
            this.skillId = skillId;
            this.serviceName = serviceName;
            this.interfaceClass = interfaceClass;
            this.target = target;
            this.proxy = proxy;
        }

        public String getSkillId() {
            return skillId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public Class<?> getInterfaceClass() {
            return interfaceClass;
        }

        public Object getTarget() {
            return target;
        }

        public Object getProxy() {
            return proxy;
        }
    }

    /**
     * 服务引用
     */
    public static class ServiceReference {
        private final String skillId;
        private final String serviceName;
        private final ServiceProxy serviceProxy;

        public ServiceReference(String skillId, String serviceName, ServiceProxy serviceProxy) {
            this.skillId = skillId;
            this.serviceName = serviceName;
            this.serviceProxy = serviceProxy;
        }

        public String getSkillId() {
            return skillId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public ServiceProxy getServiceProxy() {
            return serviceProxy;
        }
    }
}
