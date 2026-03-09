package net.ooder.scene.config;

import net.ooder.cluster.udp.ClusterClient;
import net.ooder.common.ConfigCode;
import net.ooder.config.CAJDSService;
import net.ooder.config.CApplication;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.impl.SceneEngineImpl;
import net.ooder.scene.core.secure.SceneEngineHolder;
import net.ooder.scene.core.secure.SecureSceneEngineProxy;
import net.ooder.scene.discovery.cache.CacheManager;
import net.ooder.scene.discovery.cache.SimpleCacheManager;
import net.ooder.scene.discovery.coordinator.DiscoveryCoordinator;
import net.ooder.scene.service.UnifiedSceneService;
import net.ooder.scene.service.impl.UnifiedSceneServiceImpl;
import net.ooder.scene.session.SessionManager;
import net.ooder.scene.session.impl.SessionManagerImpl;
import net.ooder.scene.skill.adapter.SkillSDKAdapter;
import net.ooder.scene.skill.coordinator.InstallCoordinator;
import net.ooder.scene.skill.instance.SkillInstanceFactory;
import net.ooder.scene.skill.instance.SkillInstancePool;
import net.ooder.scene.skill.runtime.SkillRuntime;
import net.ooder.scene.ui.NexusUiController;
import net.ooder.scene.ui.NexusUiLoader;
import net.ooder.scene.ui.NexusUiRegistry;
import net.ooder.scene.ui.NexusUiRegistryImpl;
import net.ooder.server.JDSServer;
import net.ooder.skills.api.SkillInstaller;
import net.ooder.skills.api.SkillPackageManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * SceneEngine自动配置类
 *
 * <p>为Spring Boot应用提供自动配置，开箱即用。</p>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li><b>开箱即用</b>：自动配置JDSConfig，无需手动配置</li>
 *   <li><b>无侵入</b>：使用纯Spring Bean配置</li>
 *   <li><b>自动装配</b>：依赖通过方法参数注入</li>
 *   <li><b>可覆盖</b>：手动配置的Bean优先</li>
 * </ul>
 *
 * <p>使用方式：</p>
 * <pre>
 * // 引入依赖即可，无需额外配置
 * // scene-engine会自动配置所有必需的组件
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
@Configuration
@ConditionalOnClass({DiscoveryCoordinator.class, InstallCoordinator.class})
public class SceneEngineAutoConfiguration {

    /**
     * 初始化JDSConfig配置
     *
     * <p>设置默认值并创建必要的目录结构，避免NullPointerException</p>
     */
    @PostConstruct
    public void initJDSConfig() {
        // 设置默认的 JDSHome
        if (System.getProperty("JDSHome") == null) {
            System.setProperty("JDSHome", "./JDSHome");
        }

        // 设置默认的 ConfigName
        if (System.getProperty("ConfigName") == null) {
            System.setProperty("ConfigName", "scene");
        }

        // 创建必要的目录结构
        createJDSDirectoryStructure();

        // 创建默认的 engine_config.xml
        createDefaultEngineConfig();

        System.out.println("[SceneEngineAutoConfiguration] JDSConfig initialized successfully");
        System.out.println("[SceneEngineAutoConfiguration] JDSHome: " + System.getProperty("JDSHome"));
        System.out.println("[SceneEngineAutoConfiguration] ConfigName: " + System.getProperty("ConfigName"));

        // 注册Scene引擎到JDSServer（在JDSConfig初始化之后）
        registerSceneEngineToJDSServer();
    }

    /**
     * 创建JDS目录结构
     */
    private void createJDSDirectoryStructure() {
        String jdsHome = System.getProperty("JDSHome", "./JDSHome");
        String configName = System.getProperty("ConfigName", "scene");

        String[] dirs = {
            jdsHome + "/application/" + configName + "/config",
            jdsHome + "/application/" + configName + "/lib",
            jdsHome + "/application/" + configName + "/classes",
            jdsHome + "/application/" + configName + "/data",
            jdsHome + "/application/" + configName + "/temp",
            jdsHome + "/config"
        };

        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.exists()) {
                boolean created = file.mkdirs();
                if (created) {
                    System.out.println("[SceneEngineAutoConfiguration] Created directory: " + dir);
                }
            }
        }
    }

    /**
     * 创建默认的 engine_config.xml
     */
    private void createDefaultEngineConfig() {
        String jdsHome = System.getProperty("JDSHome", "./JDSHome");
        String configName = System.getProperty("ConfigName", "scene");

        File configFile = new File(jdsHome, "application/" + configName + "/config/engine_config.xml");

        if (!configFile.exists()) {
            try {
                // 确保父目录存在
                configFile.getParentFile().mkdirs();

                // 创建默认配置文件
                String defaultConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<engine-config>\n" +
                    "    <!-- 服务器配置 -->\n" +
                    "    <property name=\"server.host\">localhost</property>\n" +
                    "    <property name=\"server.port\">10523</property>\n" +
                    "    \n" +
                    "    <!-- 管理员配置 -->\n" +
                    "    <property name=\"admin.host\">localhost</property>\n" +
                    "    <property name=\"admin.port\">10523</property>\n" +
                    "    <property name=\"admin.key\">NA</property>\n" +
                    "    <property name=\"admin.StartAdminThread\">false</property>\n" +
                    "    \n" +
                    "    <!-- 登录配置 -->\n" +
                    "    <property name=\"singleLogin\">false</property>\n" +
                    "    \n" +
                    "    <!-- 缓存配置 -->\n" +
                    "    <property name=\"server.dumpCache\">true</property>\n" +
                    "    <property name=\"server.cacheDbUser\">sa</property>\n" +
                    "    <property name=\"server.cacheDbPassword\"></property>\n" +
                    "    <property name=\"server.cacheDbURL\">jdbc:hsqldb:hsql://localhost</property>\n" +
                    "</engine-config>";

                Files.write(configFile.toPath(), defaultConfig.getBytes(StandardCharsets.UTF_8));

                System.out.println("[SceneEngineAutoConfiguration] Created default engine_config.xml: " + configFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("[SceneEngineAutoConfiguration] Failed to create engine_config.xml: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 注册Scene引擎到JDSServer
     *
     * <p>在JDSConfig初始化之后执行，确保JDSServer能正确加载配置</p>
     */
    private void registerSceneEngineToJDSServer() {
        try {
            JDSServer jdsServer = JDSServer.getInstance();
            if (jdsServer == null) {
                // JDSServer未初始化，延迟注册
                System.err.println("[SceneEngineAutoConfiguration] JDSServer not initialized yet, scene engine will be registered when JDSServer starts");
                return;
            }

            ClusterClient clusterClient = jdsServer.getClusterClient();
            if (clusterClient == null) {
                System.err.println("[SceneEngineAutoConfiguration] ClusterClient not available, scene engine registration skipped");
                return;
            }

            // 创建Scene引擎的CApplication配置
            CApplication sceneApp = new CApplication();
            sceneApp.setConfigCode("scene");  // CApplication.setConfigCode接收String
            sceneApp.setName("场景引擎");
            // 注意：CApplication没有setSystemCode方法，使用configCode作为systemCode

            // 配置JdsService实现类
            CAJDSService jdsService = new CAJDSService();
            jdsService.setImplementation(SecureSceneEngineProxy.class.getName());
            sceneApp.setJdsService(jdsService);

            // 注册到JDSServer
            boolean registered = ((net.ooder.cluster.udp.ClusterClientImpl) clusterClient).registerApplication(sceneApp);
            if (registered) {
                System.out.println("[SceneEngineAutoConfiguration] Scene engine successfully registered to JDSServer");
            } else {
                System.out.println("[SceneEngineAutoConfiguration] Scene engine registration skipped (may already exist)");
            }

        } catch (Exception e) {
            // 注册失败不影响应用启动，记录日志即可
            System.err.println("[SceneEngineAutoConfiguration] Failed to register scene engine to JDSServer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 配置SceneEngine
     *
     * <p>SceneEngine作为核心组件，必须在安全登录前提下访问。</p>
     * <p>自动初始化SceneEngineHolder，供SecureSceneEngineProxy使用。</p>
     *
     * @param sessionManager SessionManager实例
     * @param skillRuntime SkillRuntime实例（新的Skill运行时）
     * @return SceneEngine实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SceneEngine sceneEngine(SessionManager sessionManager, SkillRuntime skillRuntime) {
        SceneEngineImpl sceneEngine = new SceneEngineImpl();
        sceneEngine.setSessionManager(sessionManager);
        sceneEngine.setSkillRuntime(skillRuntime);  // 注入新的Skill运行时

        // 初始化SceneEngineHolder，供SecureSceneEngineProxy使用
        // 这是关键：SecureSceneEngineProxy通过Holder获取SceneEngine，避免Spring注入
        SceneEngineHolder.getInstance().setSceneEngine(sceneEngine);

        return sceneEngine;
    }

    /**
     * 配置SessionManager
     *
     * <p>必须先配置SessionManager，因为SceneEngine依赖它进行安全验证</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionManager sessionManager() {
        SessionManagerImpl sessionManager = new SessionManagerImpl();
        // 配置会话超时时间为30分钟
        sessionManager.setSessionTimeout(30 * 60 * 1000);
        // 配置每个用户最大会话数为5
        sessionManager.setMaxSessionsPerUser(5);
        return sessionManager;
    }

    /**
     * 配置CacheManager
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager() {
        return new SimpleCacheManager();
    }

    /**
     * 配置DiscoveryCoordinator
     */
    @Bean
    @ConditionalOnMissingBean
    public DiscoveryCoordinator discoveryCoordinator(CacheManager cacheManager) {
        DiscoveryCoordinator coordinator = new DiscoveryCoordinator(cacheManager);

        // 注册默认发现器
        // 注意：实际发现器需要SDK支持，这里只注册框架
        // coordinator.registerDiscoverer("local", new LocalDiscoverer());
        // coordinator.registerDiscoverer("github", new GitHubDiscoverer());
        // coordinator.registerDiscoverer("gitee", new GiteeDiscoverer());

        return coordinator;
    }

    /**
     * 配置InstallCoordinator
     */
    @Bean
    @ConditionalOnMissingBean
    public InstallCoordinator installCoordinator(SkillInstaller skillInstaller) {
        return new InstallCoordinator(skillInstaller);
    }

    /**
     * 配置UnifiedSceneService
     *
     * <p>应用层统一入口，封装所有scene-engine功能</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public UnifiedSceneService unifiedSceneService(DiscoveryCoordinator discoveryCoordinator,
                                                    InstallCoordinator installCoordinator) {
        UnifiedSceneServiceImpl service = new UnifiedSceneServiceImpl();
        // 通过setter注入依赖（整改后不再使用@Autowired）
        service.setDiscoveryCoordinator(discoveryCoordinator);
        service.setInstallCoordinator(installCoordinator);
        return service;
    }

    /**
     * 配置NexusUiRegistry
     *
     * <p>UI注册表，管理UI Skill的注册和查询</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public NexusUiRegistry nexusUiRegistry() {
        return new NexusUiRegistryImpl();
    }

    /**
     * 配置NexusUiLoader
     *
     * <p>UI加载器，负责扫描和加载已安装的UI Skills</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public NexusUiLoader nexusUiLoader(SkillPackageManager skillPackageManager, NexusUiRegistry nexusUiRegistry) {
        NexusUiLoader loader = new NexusUiLoader();
        // 通过setter注入依赖（整改后不再使用@Autowired）
        loader.setSkillPackageManager(skillPackageManager);
        loader.setUiRegistry(nexusUiRegistry);
        return loader;
    }

    /**
     * 配置NexusUiController
     *
     * <p>UI管理Controller，提供UI Skills的管理接口</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public NexusUiController nexusUiController(NexusUiRegistry nexusUiRegistry, NexusUiLoader nexusUiLoader) {
        // 通过构造函数注入依赖（整改后不再使用@Autowired）
        return new NexusUiController(nexusUiRegistry, nexusUiLoader);
    }

    // ==================== 新的Skill管理组件（v2.3）====================

    /**
     * 配置SkillInstancePool
     *
     * <p>管理Skill实例池，按用户隔离</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public SkillInstancePool skillInstancePool() {
        SkillInstancePool pool = new SkillInstancePool();
        pool.setIdleTimeout(30 * 60 * 1000);  // 30分钟空闲超时
        pool.setMaxInstancesPerUser(50);       // 每用户最大50个实例
        return pool;
    }

    /**
     * 配置SkillInstanceFactory
     *
     * <p>创建Skill实例</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public SkillInstanceFactory skillInstanceFactory() {
        return new SkillInstanceFactory();
    }

    /**
     * 配置SkillSDKAdapter
     *
     * <p>适配SDK调用</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public SkillSDKAdapter skillSDKAdapter() {
        return new SkillSDKAdapter();
    }

    /**
     * 配置SkillRuntime
     *
     * <p>核心运行时，管理所有Skill实例</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public SkillRuntime skillRuntime(SkillInstancePool instancePool, SkillSDKAdapter sdkAdapter) {
        SkillRuntime runtime = new SkillRuntime();
        runtime.setInstancePool(instancePool);
        runtime.setSdkAdapter(sdkAdapter);
        return runtime;
    }
}
