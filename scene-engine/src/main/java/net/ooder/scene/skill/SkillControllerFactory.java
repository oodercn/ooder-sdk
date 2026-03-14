package net.ooder.scene.skill;

import net.ooder.scene.skill.conversation.ConversationService;
import net.ooder.scene.skill.knowledge.TerminologyService;
import net.ooder.scene.spi.SceneServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Skill 控制器工厂
 * <p>解决 Skill 控制器不是 Spring Bean 的问题</p>
 *
 * <p>功能：</p>
 * <ul>
 *   <li>自动识别和注册 Skill 控制器</li>
 *   <li>为 Skill 控制器注入 SE 服务</li>
 *   <li>支持 Skill 控制器的生命周期管理</li>
 * </ul>
 *
 * <p>使用方式：</p>
 * <pre>
 * // 在 SE 启动时注册 Skill 控制器
 * SkillControllerFactory.register(ChatController.class);
 *
 * // 获取 Skill 控制器实例（已注入 SE 服务）
 * ChatController controller = SkillControllerFactory.getController(ChatController.class);
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class SkillControllerFactory {

    private static final Logger log = LoggerFactory.getLogger(SkillControllerFactory.class);

    private static final Map<Class<?>, Object> controllers = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    private SkillControllerFactory() {
        // 禁止实例化
    }

    /**
     * 初始化工厂
     * <p>应在 SE 服务初始化完成后调用</p>
     */
    public static synchronized void initialize() {
        if (initialized) {
            log.warn("SkillControllerFactory already initialized");
            return;
        }

        if (!SceneServices.isInitialized()) {
            throw new IllegalStateException("SceneServices must be initialized before SkillControllerFactory");
        }

        initialized = true;
        log.info("SkillControllerFactory initialized");
    }

    /**
     * 注册 Skill 控制器类
     *
     * @param controllerClass 控制器类
     * @param <T> 控制器类型
     * @return 控制器实例
     */
    public static <T> T register(Class<T> controllerClass) {
        if (!initialized) {
            throw new IllegalStateException("SkillControllerFactory not initialized. Call initialize() first.");
        }

        if (controllers.containsKey(controllerClass)) {
            log.debug("Controller already registered: {}", controllerClass.getName());
            return controllerClass.cast(controllers.get(controllerClass));
        }

        try {
            T instance = createController(controllerClass);
            controllers.put(controllerClass, instance);
            log.info("Registered Skill controller: {}", controllerClass.getName());
            return instance;
        } catch (Exception e) {
            log.error("Failed to register controller: {}", controllerClass.getName(), e);
            throw new RuntimeException("Failed to register controller: " + controllerClass.getName(), e);
        }
    }

    /**
     * 获取 Skill 控制器实例
     *
     * @param controllerClass 控制器类
     * @param <T> 控制器类型
     * @return 控制器实例，如果不存在返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T getController(Class<T> controllerClass) {
        return (T) controllers.get(controllerClass);
    }

    /**
     * 创建控制器实例
     * <p>尝试以下构造函数（按优先级）：</p>
     * <ol>
     *   <li>ConversationService, TerminologyService</li>
     *   <li>ConversationService</li>
     *   <li>无参构造函数</li>
     * </ol>
     */
    private static <T> T createController(Class<T> controllerClass) throws Exception {
        ConversationService conversationService = SceneServices.getConversationService();
        TerminologyService terminologyService = SceneServices.getTerminologyService();

        // 尝试 ConversationService, TerminologyService 构造函数
        try {
            Constructor<T> constructor = controllerClass.getConstructor(
                    ConversationService.class,
                    TerminologyService.class
            );
            return constructor.newInstance(conversationService, terminologyService);
        } catch (NoSuchMethodException e) {
            log.debug("No constructor (ConversationService, TerminologyService) found for {}",
                    controllerClass.getName());
        }

        // 尝试 ConversationService 构造函数
        try {
            Constructor<T> constructor = controllerClass.getConstructor(ConversationService.class);
            return constructor.newInstance(conversationService);
        } catch (NoSuchMethodException e) {
            log.debug("No constructor (ConversationService) found for {}", controllerClass.getName());
        }

        // 尝试无参构造函数
        try {
            Constructor<T> constructor = controllerClass.getConstructor();
            T instance = constructor.newInstance();

            // 尝试通过 setter 注入服务
            injectServices(instance, conversationService, terminologyService);

            return instance;
        } catch (NoSuchMethodException e) {
            log.error("No suitable constructor found for {}", controllerClass.getName());
            throw new RuntimeException("No suitable constructor found for " + controllerClass.getName());
        }
    }

    /**
     * 通过 setter 注入服务
     */
    private static <T> void injectServices(T instance,
                                           ConversationService conversationService,
                                           TerminologyService terminologyService) {
        Class<?> clazz = instance.getClass();

        // 注入 ConversationService
        try {
            clazz.getMethod("setConversationService", ConversationService.class)
                    .invoke(instance, conversationService);
        } catch (Exception e) {
            log.debug("No setConversationService method found in {}", clazz.getName());
        }

        // 注入 TerminologyService
        try {
            clazz.getMethod("setTerminologyService", TerminologyService.class)
                    .invoke(instance, terminologyService);
        } catch (Exception e) {
            log.debug("No setTerminologyService method found in {}", clazz.getName());
        }
    }

    /**
     * 检查是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * 获取所有已注册的控制器
     */
    public static Map<Class<?>, Object> getAllControllers() {
        return new ConcurrentHashMap<>(controllers);
    }

    /**
     * 重置工厂（主要用于测试）
     */
    public static synchronized void reset() {
        controllers.clear();
        initialized = false;
        log.info("SkillControllerFactory reset");
    }
}
