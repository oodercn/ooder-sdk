package net.ooder.skill.hotplug.registry;

import net.ooder.skill.hotplug.classloader.PluginClassLoader;
import net.ooder.skill.hotplug.config.RouteDefinition;
import net.ooder.skill.hotplug.exception.RouteConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态路由注册器
 * 负责将Skill的Controller动态注册到Spring MVC
 */
@Component
public class RouteRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RouteRegistry.class);

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ServiceRegistry serviceRegistry;

    private String currentSkillId;

    // Skill ID -> 注册的路由信息映射
    private final Map<String, Set<RegisteredRoute>> registeredRoutes = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("RouteRegistry initialized");
    }

    /**
     * 注册路由
     */
    public void registerRoutes(String skillId, List<RouteDefinition> routes, PluginClassLoader classLoader) {
        logger.info("Registering {} routes for skill: {}", routes.size(), skillId);
        
        this.currentSkillId = skillId;

        Set<RegisteredRoute> skillRoutes = ConcurrentHashMap.newKeySet();
        int skippedCount = 0;

        for (RouteDefinition route : routes) {
            try {
                RegisteredRoute registeredRoute = registerRoute(skillId, route, classLoader);
                if (registeredRoute != null) {
                    skillRoutes.add(registeredRoute);
                    logger.debug("Registered route: {} {} -> {}.{}",
                            route.getMethod(), route.getPath(),
                            route.getControllerClass(), route.getMethodName());
                } else {
                    skippedCount++;
                    logger.debug("Skipped route: {} {} -> {}.{}",
                            route.getMethod(), route.getPath(),
                            route.getControllerClass(), route.getMethodName());
                }
            } catch (Exception e) {
                logger.error("Failed to register route: {}", route, e);
            }
        }

        registeredRoutes.put(skillId, skillRoutes);
        logger.info("Successfully registered {} routes for skill: {} (skipped: {})", 
                skillRoutes.size(), skillId, skippedCount);
    }

    /**
     * 注销指定Skill的所有路由
     */
    public void unregisterRoutes(String skillId) {
        logger.info("Unregistering routes for skill: {}", skillId);

        Set<RegisteredRoute> routes = registeredRoutes.remove(skillId);
        if (routes == null || routes.isEmpty()) {
            logger.warn("No routes found for skill: {}", skillId);
            return;
        }

        for (RegisteredRoute route : routes) {
            try {
                unregisterRoute(route);
                logger.debug("Unregistered route: {}", route.getMappingInfo());
            } catch (Exception e) {
                logger.error("Failed to unregister route: {}", route, e);
            }
        }

        logger.info("Successfully unregistered {} routes for skill: {}", routes.size(), skillId);
    }

    /**
     * 获取已注册的路由
     */
    public Set<RegisteredRoute> getRegisteredRoutes(String skillId) {
        return registeredRoutes.get(skillId);
    }

    /**
     * 获取所有已注册的路由
     */
    public Map<String, Set<RegisteredRoute>> getAllRegisteredRoutes() {
        return new ConcurrentHashMap<>(registeredRoutes);
    }

    // ==================== 私有方法 ====================

    private RegisteredRoute registerRoute(String skillId, RouteDefinition routeDef, 
                                           PluginClassLoader classLoader) throws Exception {
        logger.info("Registering route: {} {} -> {}.{}", 
                routeDef.getMethod(), routeDef.getPath(),
                routeDef.getControllerClass(), routeDef.getMethodName());
        
        Class<?> controllerClass = classLoader.loadClass(routeDef.getControllerClass());
        logger.debug("Loaded controller class: {}", controllerClass.getName());

        Optional<RouteRegistrationInfo> existingRegistration = findExistingRouteRegistration(
                routeDef.getPath(), routeDef.getMethod());
        
        if (existingRegistration.isPresent()) {
            RouteRegistrationInfo regInfo = existingRegistration.get();
            String existingSkillId = regInfo.getSkillId();
            
            if (existingSkillId != null && existingSkillId.equals(skillId)) {
                logger.info("Route {} {} already registered by same skill {}, updating", 
                        routeDef.getMethod(), routeDef.getPath(), skillId);
                unregisterRouteByInfo(regInfo);
            } else if (existingSkillId != null) {
                throw new RouteConflictException(
                        routeDef.getPath(), 
                        routeDef.getMethod(), 
                        existingSkillId, 
                        skillId);
            } else {
                logger.warn("Route {} {} is already registered by Spring (not by any skill), skipping. " +
                        "To enable dynamic loading, remove the skill from compile-time dependencies.", 
                        routeDef.getMethod(), routeDef.getPath());
                return null;
            }
        }

        Object controller = createControllerInstance(controllerClass);
        logger.debug("Created controller instance: {}", controller.getClass().getName());

        Method method = findMethod(controllerClass, routeDef.getMethodName(), routeDef.getParameterTypes(), classLoader);
        logger.debug("Found method: {} with {} parameters", method.getName(), method.getParameterCount());

        RequestMappingInfo mappingInfo = createMappingInfo(routeDef);
        logger.debug("Created RequestMappingInfo: {}", mappingInfo);

        org.springframework.web.method.HandlerMethod handlerMethod = 
                new org.springframework.web.method.HandlerMethod(controller, method);

        handlerMapping.registerMapping(mappingInfo, controller, method);
        logger.info("Successfully registered route: {} {} to Spring MVC", routeDef.getMethod(), routeDef.getPath());

        RegisteredRoute registeredRoute = new RegisteredRoute();
        registeredRoute.setSkillId(skillId);
        registeredRoute.setMappingInfo(mappingInfo);
        registeredRoute.setController(controller);
        registeredRoute.setMethod(method);
        registeredRoute.setHandlerMethod(handlerMethod);
        registeredRoute.setRouteDefinition(routeDef);

        return registeredRoute;
    }

    private void unregisterRoute(RegisteredRoute route) {
        handlerMapping.unregisterMapping(route.getMappingInfo());
    }

    /**
     * 查找是否已由 Spring 管理的 Controller Bean
     * @param controllerClass Controller 类
     * @return 如果存在返回 Bean 实例，否则返回 null
     */
    private Object findExistingSpringController(Class<?> controllerClass) {
        try {
            // 尝试按类型获取
            Object bean = applicationContext.getBean(controllerClass);
            if (bean != null) {
                logger.debug("Found existing Spring controller by type: {}", controllerClass.getName());
                return bean;
            }
        } catch (Exception e) {
            logger.debug("No existing Spring controller found by type: {}", controllerClass.getName());
        }
        
        // 尝试按简单类名获取
        String beanName = controllerClass.getSimpleName();
        // 首字母小写（Spring 默认命名规则）
        beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
        try {
            Object bean = applicationContext.getBean(beanName);
            if (bean != null) {
                logger.debug("Found existing Spring controller by name: {}", beanName);
                return bean;
            }
        } catch (Exception e) {
            logger.debug("No existing Spring controller found by name: {}", beanName);
        }
        
        return null;
    }

    /**
     * 查找已存在的路由注册信息
     * @param path 路由路径
     * @param method HTTP 方法
     * @return 如果已存在返回 RouteRegistrationInfo，否则返回 Optional.empty()
     */
    private Optional<RouteRegistrationInfo> findExistingRouteRegistration(String path, String method) {
        try {
            Map<?, ?> handlerMethods = handlerMapping.getHandlerMethods();
            
            for (Map.Entry<?, ?> entry : handlerMethods.entrySet()) {
                if (entry.getKey() instanceof RequestMappingInfo) {
                    RequestMappingInfo existingMapping = (RequestMappingInfo) entry.getKey();
                    
                    Set<String> existingPatterns = existingMapping.getPatternValues();
                    if (existingPatterns != null && existingPatterns.contains(path)) {
                        Set<RequestMethod> existingMethods = existingMapping.getMethodsCondition().getMethods();
                        if (existingMethods == null || existingMethods.isEmpty() || 
                            existingMethods.contains(RequestMethod.valueOf(method.toUpperCase()))) {
                            
                            String skillId = findSkillIdByMapping(existingMapping);
                            return Optional.of(new RouteRegistrationInfo(skillId, existingMapping));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error checking existing route registration: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * 根据 MappingInfo 查找对应的 Skill ID
     */
    private String findSkillIdByMapping(RequestMappingInfo mappingInfo) {
        for (Map.Entry<String, Set<RegisteredRoute>> entry : registeredRoutes.entrySet()) {
            for (RegisteredRoute route : entry.getValue()) {
                if (route.getMappingInfo().equals(mappingInfo)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * 根据路由注册信息注销路由
     */
    private void unregisterRouteByInfo(RouteRegistrationInfo regInfo) {
        if (regInfo.getMappingInfo() != null) {
            handlerMapping.unregisterMapping(regInfo.getMappingInfo());
            logger.debug("Unregistered route by info: {}", regInfo);
        }
    }

    /**
     * 检查路由是否已注册（向后兼容方法）
     * @param path 路由路径
     * @param method HTTP 方法
     * @return 如果已存在返回 true
     */
    private boolean isRouteAlreadyRegistered(String path, String method) {
        return findExistingRouteRegistration(path, method).isPresent();
    }

    /**
     * 创建 Controller 实例
     * 优先级：Spring容器已存在Bean > AutowireCapableBeanFactory创建 > 默认构造器
     */
    private Object createControllerInstance(Class<?> controllerClass) throws Exception {
        String beanName = Character.toLowerCase(controllerClass.getSimpleName().charAt(0)) + 
                          controllerClass.getSimpleName().substring(1);
        String className = controllerClass.getName();
        
        logger.debug("Creating controller instance for: {}", className);
        
        Object existingBean = tryGetExistingBean(controllerClass, beanName, className);
        if (existingBean != null) {
            return existingBean;
        }
        
        return createNewBean(controllerClass, className);
    }

    /**
     * 尝试从 Spring 容器获取已存在的 Bean
     */
    private Object tryGetExistingBean(Class<?> controllerClass, String beanName, String className) {
        try {
            Object bean = applicationContext.getBean(controllerClass);
            logger.info("Using existing Spring bean (by type) for controller: {}", className);
            return bean;
        } catch (Exception e) {
            logger.debug("No existing bean found by type for controller: {}", className);
        }
        
        try {
            Object bean = applicationContext.getBean(beanName);
            if (bean != null && controllerClass.isInstance(bean)) {
                logger.info("Using existing Spring bean (by name) for controller: {}", className);
                return bean;
            }
        } catch (Exception e) {
            logger.debug("No existing bean found by name for controller: {}", beanName);
        }
        
        return null;
    }

    /**
     * 创建新的 Bean 实例
     */
    private Object createNewBean(Class<?> controllerClass, String className) throws Exception {
        try {
            Object bean = applicationContext.getAutowireCapableBeanFactory()
                    .createBean(controllerClass);
            logger.info("Created and autowired controller instance: {}", className);
            return bean;
        } catch (Exception e) {
            logger.warn("Failed to create controller with Spring autowiring ({}), trying default constructor with manual injection. Error: {}", 
                    className, e.getMessage());
            logger.debug("Autowiring failure details", e);
            
            try {
                Object bean = controllerClass.getDeclaredConstructor().newInstance();
                logger.info("Created controller instance with default constructor: {}", className);
                
                manualInjectDependencies(bean, controllerClass);
                
                logger.info("Controller {} dependencies manually injected successfully", className);
                return bean;
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create controller instance: " + className + 
                        ". Ensure the controller has a no-arg constructor or can be autowired by Spring.", ex);
            }
        }
    }

    /**
     * 手动注入依赖
     * 扫描 @Autowired 和 @Resource 字段并从 Spring 容器注入
     */
    private void manualInjectDependencies(Object bean, Class<?> clazz) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (java.lang.reflect.Field field : currentClass.getDeclaredFields()) {
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
     * 优先级：
     * 1. 从 Spring 容器按名称查找（@Resource 指定名称）
     * 2. 从 Spring 容器按类型查找
     * 3. 从 ServiceRegistry 按接口类型查找（当前 Skill）
     * 4. 从 ServiceRegistry 按服务名称查找（当前 Skill）
     * 5. 从 Spring 容器按字段名查找
     * 6. 从 Spring 容器按类名查找
     */
    private Object resolveDependency(java.lang.reflect.Field field) {
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
        
        if (currentSkillId != null && serviceRegistry != null) {
            try {
                List<?> services = serviceRegistry.getServicesByInterface(fieldType);
                if (services != null && !services.isEmpty()) {
                    logger.info("Found service from ServiceRegistry by interface: {} for skill {}", 
                            fieldType.getName(), currentSkillId);
                    return services.get(0);
                }
            } catch (Exception e) {
                logger.debug("Service not found in ServiceRegistry by interface: {}", fieldType.getName());
            }
            
            try {
                Map<String, ServiceRegistry.ServiceProxy> skillServices = serviceRegistry.getServices(currentSkillId);
                if (skillServices != null) {
                    String serviceName = field.getName();
                    ServiceRegistry.ServiceProxy proxy = skillServices.get(serviceName);
                    if (proxy != null && fieldType.isInstance(proxy.getProxy())) {
                        logger.info("Found service from ServiceRegistry by name: {} for skill {}", 
                                serviceName, currentSkillId);
                        return proxy.getProxy();
                    }
                    
                    for (ServiceRegistry.ServiceProxy serviceProxy : skillServices.values()) {
                        if (fieldType.isInstance(serviceProxy.getProxy())) {
                            logger.info("Found service from ServiceRegistry by type match: {} for skill {}", 
                                    fieldType.getName(), currentSkillId);
                            return serviceProxy.getProxy();
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("Service not found in ServiceRegistry by name: {}", field.getName());
            }
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

    private Method findMethod(Class<?> clazz, String methodName, String[] parameterTypes, 
                              PluginClassLoader classLoader) throws NoSuchMethodException {
        if (parameterTypes == null || parameterTypes.length == 0) {
            // 当没有指定参数类型时，查找所有同名方法并选择参数最少的一个
            // 这样可以避免重载方法匹配错误
            Method bestMatch = null;
            int minParams = Integer.MAX_VALUE;
            
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName)) {
                    int paramCount = method.getParameterCount();
                    if (paramCount < minParams) {
                        minParams = paramCount;
                        bestMatch = method;
                    }
                }
            }
            
            if (bestMatch != null) {
                return bestMatch;
            }
        } else {
            Class<?>[] paramClasses = new Class<?>[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                try {
                    paramClasses[i] = loadClass(parameterTypes[i], classLoader);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class not found: " + parameterTypes[i], e);
                }
            }
            return clazz.getMethod(methodName, paramClasses);
        }
        throw new NoSuchMethodException("Method not found: " + methodName);
    }

    private RequestMappingInfo createMappingInfo(RouteDefinition routeDef) {
        // 创建 BuilderConfiguration 并设置 PathPatternParser
        RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();
        config.setPatternParser(new PathPatternParser());

        // 使用 builder() 方法创建，这是 Spring Boot 2.7 兼容的方式
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(routeDef.getPath())
                .methods(RequestMethod.valueOf(routeDef.getMethod().toUpperCase()));

        if (routeDef.getConsumes() != null) {
            builder.consumes(routeDef.getConsumes());
        }

        if (routeDef.getProduces() != null) {
            builder.produces(routeDef.getProduces());
        }

        if (routeDef.getParams() != null) {
            builder.params(routeDef.getParams());
        }

        if (routeDef.getHeaders() != null) {
            builder.headers(routeDef.getHeaders());
        }

        // 设置 options 以确保与 Spring Boot 2.7 兼容
        builder.options(config);

        return builder.build();
    }

    private Class<?> loadClass(String className, PluginClassLoader classLoader) throws ClassNotFoundException {
        try {
            // 首先尝试使用 PluginClassLoader 加载类（优先）
            if (classLoader != null) {
                try {
                    return classLoader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    logger.debug("PluginClassLoader failed to load class: {}, falling back to system classloader", className);
                }
            }
            // 回退到系统类加载器
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + className, e);
        }
    }

    /**
     * 已注册的路由信息
     */
    public static class RegisteredRoute {
        private String skillId;
        private RequestMappingInfo mappingInfo;
        private Object controller;
        private Method method;
        private org.springframework.web.method.HandlerMethod handlerMethod;
        private RouteDefinition routeDefinition;

        // Getters and Setters
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public RequestMappingInfo getMappingInfo() { return mappingInfo; }
        public void setMappingInfo(RequestMappingInfo mappingInfo) { this.mappingInfo = mappingInfo; }
        public Object getController() { return controller; }
        public void setController(Object controller) { this.controller = controller; }
        public Method getMethod() { return method; }
        public void setMethod(Method method) { this.method = method; }
        public org.springframework.web.method.HandlerMethod getHandlerMethod() { return handlerMethod; }
        public void setHandlerMethod(org.springframework.web.method.HandlerMethod handlerMethod) { this.handlerMethod = handlerMethod; }
        public RouteDefinition getRouteDefinition() { return routeDefinition; }
        public void setRouteDefinition(RouteDefinition routeDefinition) { this.routeDefinition = routeDefinition; }

        @Override
        public String toString() {
            return "RegisteredRoute{" +
                    "skillId='" + skillId + '\'' +
                    ", path='" + (routeDefinition != null ? routeDefinition.getPath() : "null") + '\'' +
                    ", method='" + (routeDefinition != null ? routeDefinition.getMethod() : "null") + '\'' +
                    '}';
        }
    }

    /**
     * 路由注册信息（用于冲突检测）
     */
    private static class RouteRegistrationInfo {
        private final String skillId;
        private final RequestMappingInfo mappingInfo;

        public RouteRegistrationInfo(String skillId, RequestMappingInfo mappingInfo) {
            this.skillId = skillId;
            this.mappingInfo = mappingInfo;
        }

        public String getSkillId() {
            return skillId;
        }

        public RequestMappingInfo getMappingInfo() {
            return mappingInfo;
        }

        @Override
        public String toString() {
            return "RouteRegistrationInfo{" +
                    "skillId='" + skillId + '\'' +
                    ", mappingInfo=" + mappingInfo +
                    '}';
        }
    }
}
