
package net.ooder.sdk.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.ooder.sdk.api.agent.AgentFactory;
import net.ooder.sdk.api.agent.EndAgent;
import net.ooder.sdk.api.agent.McpAgent;
import net.ooder.sdk.api.agent.RouteAgent;
import net.ooder.sdk.api.metadata.ChangeLogService;
import net.ooder.sdk.api.metadata.MetadataQueryService;
import net.ooder.sdk.api.scene.CapabilityInvoker;
import net.ooder.sdk.api.scene.SceneGroupManager;
import net.ooder.sdk.api.scene.SceneManager;
import net.ooder.sdk.core.agent.factory.AgentFactoryImpl;
import net.ooder.sdk.core.metadata.impl.ChangeLogServiceImpl;
import net.ooder.sdk.core.metadata.impl.MetadataQueryServiceImpl;
// SceneManager 和 SceneGroupManager 的实现已移至外部 scene-engine 工程
// 如需使用，请单独引入 scene-engine 依赖
import net.ooder.sdk.core.capability.impl.CapabilityInvokerImpl;
import net.ooder.skills.api.InstallRequest;
import net.ooder.skills.api.SkillCenterClient;
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.sdk.common.enums.AgentType;
import net.ooder.sdk.infra.config.SDKConfiguration;
import net.ooder.sdk.infra.lifecycle.LifecycleManager;
import net.ooder.skills.core.impl.SkillPackageManagerImpl;

/**
 * Ooder SDK 入口类
 * <p>
 * 此类为公共 API，用户可直接使用。
 * 这是 SDK 的主要入口点，用于创建和管理各种 Agent。
 * </p>
 *
 * @version 3.0.0
 * @since 3.0.0
 */
@PublicAPI
public class OoderSDK {
    
    private final SDKConfiguration configuration;
    private final AgentFactory agentFactory;
    private final SkillPackageManager skillPackageManager;
    private final SceneManager sceneManager;
    private final SceneGroupManager sceneGroupManager;
    private final CapabilityInvoker capabilityInvoker;
    private final MetadataQueryService metadataQueryService;
    private final ChangeLogService changeLogService;
    private final SkillCenterClient skillCenterClient;
    private final LifecycleManager lifecycleManager;
    
    private volatile boolean initialized = false;
    private volatile boolean started = false;
    
    private OoderSDK(Builder builder) {
        this.configuration = builder.configuration;
        this.agentFactory = builder.agentFactory;
        this.skillPackageManager = builder.skillPackageManager;
        this.sceneManager = builder.sceneManager;
        this.sceneGroupManager = builder.sceneGroupManager;
        this.capabilityInvoker = builder.capabilityInvoker;
        this.metadataQueryService = builder.metadataQueryService;
        this.changeLogService = builder.changeLogService;
        this.skillCenterClient = builder.skillCenterClient;
        this.lifecycleManager = LifecycleManager.getInstance();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public synchronized void initialize() throws Exception {
        if (initialized) {
            return;
        }
        
        lifecycleManager.initialize();
        initialized = true;
    }
    
    public synchronized void start() throws Exception {
        if (!initialized) {
            initialize();
        }
        
        if (started) {
            return;
        }
        
        lifecycleManager.start();
        started = true;
    }
    
    public synchronized void stop() {
        if (!started) {
            return;
        }
        
        lifecycleManager.stop();
        started = false;
    }
    
    public synchronized void shutdown() {
        lifecycleManager.shutdown();
        initialized = false;
        started = false;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public boolean isStarted() {
        return started;
    }
    
    public boolean isRunning() {
        return lifecycleManager.isRunning();
    }
    
    public SDKConfiguration getConfiguration() {
        return configuration;
    }
    
    public AgentFactory getAgentFactory() {
        return agentFactory;
    }
    
    public SkillPackageManager getSkillPackageManager() {
        return skillPackageManager;
    }
    
    public SceneManager getSceneManager() {
        return sceneManager;
    }
    
    public SceneGroupManager getSceneGroupManager() {
        return sceneGroupManager;
    }
    
    public CapabilityInvoker getCapabilityInvoker() {
        return capabilityInvoker;
    }
    
    public MetadataQueryService getMetadataQueryService() {
        return metadataQueryService;
    }
    
    public ChangeLogService getChangeLogService() {
        return changeLogService;
    }
    
    public SkillCenterClient getSkillCenterClient() {
        return skillCenterClient;
    }
    
    public McpAgent createMcpAgent() {
        return agentFactory.createMcpAgent(configuration);
    }
    
    public RouteAgent createRouteAgent() {
        return agentFactory.createRouteAgent(configuration);
    }
    
    public EndAgent createEndAgent() {
        return agentFactory.createEndAgent(configuration);
    }
    
    public CompletableFuture<Void> installSkill(String skillId) {
        return skillPackageManager.discover(skillId, null)
            .thenCompose(pkg -> {
                InstallRequest request = new InstallRequest();
                request.setSkillId(skillId);
                return skillPackageManager.install(request);
            })
            .thenApply(result -> null);
    }
    
    public Map<String, ServiceStatus> diagnoseServices() {
        Map<String, ServiceStatus> status = new LinkedHashMap<String, ServiceStatus>();
        
        status.put("AgentFactory", checkService(agentFactory));
        status.put("SkillPackageManager", checkService(skillPackageManager));
        status.put("SceneManager", checkService(sceneManager));
        status.put("SceneGroupManager", checkService(sceneGroupManager));
        status.put("CapabilityInvoker", checkService(capabilityInvoker));
        status.put("MetadataQueryService", checkService(metadataQueryService));
        status.put("ChangeLogService", checkService(changeLogService));
        status.put("SkillCenterClient", checkService(skillCenterClient));
        
        return status;
    }
    
    private ServiceStatus checkService(Object service) {
        if (service == null) {
            return ServiceStatus.NULL;
        }
        return ServiceStatus.AVAILABLE;
    }
    
    public enum ServiceStatus {
        NULL("服务未初始化"),
        AVAILABLE("服务可用"),
        ERROR("服务异常");
        
        private final String description;
        
        ServiceStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public static class Builder {
        private SDKConfiguration configuration;
        private AgentFactory agentFactory;
        private SkillPackageManager skillPackageManager;
        private SceneManager sceneManager;
        private SceneGroupManager sceneGroupManager;
        private CapabilityInvoker capabilityInvoker;
        private MetadataQueryService metadataQueryService;
        private ChangeLogService changeLogService;
        private SkillCenterClient skillCenterClient;
        
        private SDKConfiguration getOrCreateConfiguration() {
            if (configuration == null) {
                configuration = new SDKConfiguration();
            }
            return configuration;
        }
        
        public Builder configuration(SDKConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }
        
        public Builder agentId(String agentId) {
            getOrCreateConfiguration().setAgentId(agentId);
            return this;
        }
        
        public Builder agentName(String agentName) {
            getOrCreateConfiguration().setAgentName(agentName);
            return this;
        }
        
        public Builder agentType(String agentType) {
            getOrCreateConfiguration().setAgentType(agentType);
            return this;
        }
        
        public Builder endpoint(String endpoint) {
            getOrCreateConfiguration().setEndpoint(endpoint);
            return this;
        }
        
        public Builder udpPort(int udpPort) {
            getOrCreateConfiguration().setUdpPort(udpPort);
            return this;
        }
        
        public Builder skillRootPath(String skillRootPath) {
            getOrCreateConfiguration().setSkillRootPath(skillRootPath);
            return this;
        }
        
        public Builder skillCenterUrl(String skillCenterUrl) {
            getOrCreateConfiguration().setSkillCenterUrl(skillCenterUrl);
            return this;
        }
        
        public Builder vfsUrl(String vfsUrl) {
            getOrCreateConfiguration().setVfsUrl(vfsUrl);
            return this;
        }
        
        public Builder strictMode(boolean strictMode) {
            getOrCreateConfiguration().setStrictMode(strictMode);
            return this;
        }
        
        public Builder discoveryEnabled(boolean discoveryEnabled) {
            getOrCreateConfiguration().setDiscoveryEnabled(discoveryEnabled);
            return this;
        }
        
        public Builder agentFactory(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
            return this;
        }
        
        public Builder skillPackageManager(SkillPackageManager skillPackageManager) {
            this.skillPackageManager = skillPackageManager;
            return this;
        }
        
        public Builder sceneManager(SceneManager sceneManager) {
            this.sceneManager = sceneManager;
            return this;
        }
        
        public Builder sceneGroupManager(SceneGroupManager sceneGroupManager) {
            this.sceneGroupManager = sceneGroupManager;
            return this;
        }
        
        public Builder capabilityInvoker(CapabilityInvoker capabilityInvoker) {
            this.capabilityInvoker = capabilityInvoker;
            return this;
        }
        
        public Builder metadataQueryService(MetadataQueryService metadataQueryService) {
            this.metadataQueryService = metadataQueryService;
            return this;
        }
        
        public Builder changeLogService(ChangeLogService changeLogService) {
            this.changeLogService = changeLogService;
            return this;
        }
        
        public Builder skillCenterClient(SkillCenterClient skillCenterClient) {
            this.skillCenterClient = skillCenterClient;
            return this;
        }
        
        /**
         * 创建默认的 SceneManager
         * <p>注意：SceneManager 的完整实现已移至外部 scene-engine 工程</p>
         * <p>此方法返回 null，如需使用 Scene 功能，请：</p>
         * <ul>
         *   <li>1. 引入外部 scene-engine 依赖</li>
         *   <li>2. 通过 Builder.sceneManager() 注入实现</li>
         * </ul>
         */
        private SceneManager createDefaultSceneManager() {
            // SceneManager 的完整实现已移至外部 scene-engine 工程
            // 如需使用，请引入 scene-engine 依赖或通过 Builder 注入
            return null;
        }
        
        /**
         * 创建默认的 SceneGroupManager
         * <p>注意：SceneGroupManager 的完整实现已移至外部 scene-engine 工程</p>
         * <p>此方法返回 null，如需使用 Scene 功能，请：</p>
         * <ul>
         *   <li>1. 引入外部 scene-engine 依赖</li>
         *   <li>2. 通过 Builder.sceneGroupManager() 注入实现</li>
         * </ul>
         */
        private SceneGroupManager createDefaultSceneGroupManager() {
            // SceneGroupManager 的完整实现已移至外部 scene-engine 工程
            // 如需使用，请引入 scene-engine 依赖或通过 Builder 注入
            return null;
        }
        
        public OoderSDK build() {
            if (configuration == null) {
                configuration = new SDKConfiguration();
            }
            if (agentFactory == null) {
                agentFactory = new AgentFactoryImpl();
            }
            if (skillPackageManager == null) {
                skillPackageManager = new SkillPackageManagerImpl();
            }
            // SceneManager 和 SceneGroupManager 的实现已移至外部 scene-engine 工程
            // 如需使用，请通过 Builder 注入或单独引入 scene-engine 依赖
            if (sceneManager == null) {
                sceneManager = createDefaultSceneManager();
            }
            if (sceneGroupManager == null) {
                sceneGroupManager = createDefaultSceneGroupManager();
            }
            if (capabilityInvoker == null) {
                capabilityInvoker = new CapabilityInvokerImpl();
            }
            if (metadataQueryService == null) {
                metadataQueryService = new MetadataQueryServiceImpl();
            }
            if (changeLogService == null) {
                changeLogService = new ChangeLogServiceImpl();
            }
            return new OoderSDK(this);
        }
    }
}
