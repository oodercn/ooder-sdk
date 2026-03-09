package net.ooder.scene.core.init;

import net.ooder.scene.core.*;
import net.ooder.scene.core.impl.SceneAgentBridge;
import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.scene.SceneAgentEvent;
import net.ooder.sdk.api.capability.CapRegistry;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.scene.SceneGroup;
import net.ooder.sdk.api.scene.SceneGroupManager;
import net.ooder.sdk.api.scene.SceneMember;
import net.ooder.sdk.common.enums.MemberRole;
import net.ooder.skills.api.SkillPackage;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景组初始化器
 *
 * <p>执行场景组的完整初始化流程，包括 6 个步骤：</p>
 *
 * <h3>初始化流程：</h3>
 * <ol>
 *   <li><b>场景加载</b> - 加载场景定义和配置</li>
 *   <li><b>Agent 初始化</b> - 创建 SceneAgent，分配角色</li>
 *   <li><b>CAP 解析</b> - 解析所需能力</li>
 *   <li><b>Skill 发现</b> - 查询匹配的 Skill</li>
 *   <li><b>Skill 挂载</b> - 创建连接器并挂载</li>
 *   <li><b>场景激活</b> - 启动场景组</li>
 * </ol>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SceneGroupInitializer {

    private final SceneGroupManager sceneGroupManager;
    private final CapRegistry capRegistry;
    private final SceneEventPublisher eventPublisher;
    private final UnifiedSkillRegistry skillRegistry;
    private final Map<String, InitContext> initContexts = new ConcurrentHashMap<>();

    public SceneGroupInitializer(SceneGroupManager sceneGroupManager,
                                  CapRegistry capRegistry,
                                  SceneEventPublisher eventPublisher) {
        this(sceneGroupManager, capRegistry, eventPublisher, null);
    }

    public SceneGroupInitializer(SceneGroupManager sceneGroupManager,
                                  CapRegistry capRegistry,
                                  SceneEventPublisher eventPublisher,
                                  UnifiedSkillRegistry skillRegistry) {
        this.sceneGroupManager = sceneGroupManager;
        this.capRegistry = capRegistry;
        this.eventPublisher = eventPublisher;
        this.skillRegistry = skillRegistry;
    }

    /**
     * 执行场景组初始化
     *
     * @param request 初始化请求
     * @return 初始化结果
     */
    public CompletableFuture<InitResult> initialize(InitRequest request) {
        InitContext context = new InitContext(request);
        initContexts.put(context.getInitId(), context);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Step 1: 场景加载
                loadScene(context);

                // Step 2: Agent 初始化
                initializeAgents(context);

                // Step 3: CAP 解析
                parseCapabilities(context);

                // Step 4: Skill 发现
                discoverSkills(context);

                // Step 5: Skill 挂载
                mountSkills(context);

                // Step 6: 场景激活
                activate(context);

                context.setStatus(InitStatus.COMPLETED);
                return InitResult.success(context);

            } catch (Exception e) {
                context.setStatus(InitStatus.FAILED);
                context.setErrorMessage(e.getMessage());
                return InitResult.failure(context, e.getMessage());
            }
        });
    }

    // ==================== Step 1: 场景加载 ====================

    private void loadScene(InitContext context) {
        context.setStatus(InitStatus.LOADING_SCENE);

        InitRequest request = context.getRequest();

        // 创建场景组配置
        SceneGroupManager.SceneGroupConfig config = new SceneGroupManager.SceneGroupConfig();
        config.setSceneId(request.getSceneId());
        config.setMinMembers(request.getMinMembers());
        config.setMaxMembers(request.getMaxMembers());
        config.setHeartbeatInterval(request.getHeartbeatInterval());
        config.setHeartbeatTimeout(request.getHeartbeatTimeout());
        config.setKeyThreshold(request.getKeyThreshold());
        config.setProperties(request.getProperties());

        context.setGroupConfig(config);

        // 发布事件
        publishEvent(SceneAgentEvent.created(
            context.getInitId(),
            request.getSceneId(),
            request.getSceneName(),
            request.getUserId()
        ));
    }

    // ==================== Step 2: Agent 初始化 ====================

    private void initializeAgents(InitContext context) {
        context.setStatus(InitStatus.INITIALIZING_AGENTS);

        InitRequest request = context.getRequest();
        List<AgentConfig> agentConfigs = request.getAgentConfigs();

        if (agentConfigs == null || agentConfigs.isEmpty()) {
            // 默认创建一个 PRIMARY Agent
            agentConfigs = Collections.singletonList(
                new AgentConfig(MemberRole.PRIMARY, request.getUserId())
            );
        }

        for (AgentConfig agentConfig : agentConfigs) {
            // 创建 SceneAgent
            SceneAgentCore agent = createAgent(agentConfig);

            // 保存到上下文
            context.addAgent(agent);

            // 创建成员信息
            SceneMemberInfo member = new SceneMemberInfo();
            member.setMemberId(agent.getAgentId());
            member.setRole(agentConfig.getRole());
            member.setUserId(agentConfig.getUserId());
            context.addMember(member);
        }
    }

    private SceneAgentCore createAgent(AgentConfig config) {
        SceneAgentBridge agent = new SceneAgentBridge();
        agent.setMemberRole(config.getRole());
        return agent;
    }

    // ==================== Step 3: CAP 解析 ====================

    private void parseCapabilities(InitContext context) {
        context.setStatus(InitStatus.PARSING_CAPS);

        InitRequest request = context.getRequest();
        List<String> requiredCaps = request.getRequiredCapabilities();
        List<String> optionalCaps = request.getOptionalCapabilities();

        // 解析必需能力
        if (requiredCaps != null) {
            for (String capId : requiredCaps) {
                Capability cap = capRegistry.findById(capId);
                if (cap != null) {
                    context.addRequiredCapability(cap);
                } else {
                    throw new InitException("Required capability not found: " + capId);
                }
            }
        }

        // 解析可选能力
        if (optionalCaps != null) {
            for (String capId : optionalCaps) {
                Capability cap = capRegistry.findById(capId);
                if (cap != null) {
                    context.addOptionalCapability(cap);
                }
            }
        }
    }

    // ==================== Step 4: Skill 发现 ====================

    private void discoverSkills(InitContext context) {
        context.setStatus(InitStatus.DISCOVERING_SKILLS);

        // 遍历所需能力，查找匹配的 Skill
        for (Capability cap : context.getRequiredCapabilities()) {
            List<SkillMatch> matches = findMatchingSkills(cap);
            context.addSkillMatches(cap.getCapId(), matches);
        }

        for (Capability cap : context.getOptionalCapabilities()) {
            List<SkillMatch> matches = findMatchingSkills(cap);
            context.addSkillMatches(cap.getCapId(), matches);
        }
    }

    private List<SkillMatch> findMatchingSkills(Capability capability) {
        List<SkillMatch> matches = new ArrayList<>();
        
        if (skillRegistry == null) {
            return matches;
        }

        try {
            String capId = capability.getCapId();
            List<SkillPackage> allSkills = skillRegistry.getAllSkills().join();
            
            for (SkillPackage pkg : allSkills) {
                if (pkg.getCapabilities() != null && 
                    pkg.getCapabilities().stream().anyMatch(c -> c.getCapId().equals(capId))) {
                    
                    SkillMatch match = new SkillMatch();
                    match.setSkillId(pkg.getSkillId());
                    match.setSkillName(pkg.getName());
                    match.setConnectorType(determineConnectorType(pkg));
                    match.setEndpoint(getMetadataString(pkg, "endpoint"));
                    match.setPriority(getMetadataInt(pkg, "priority", 0));
                    match.setScore(calculateMatchScore(capability, pkg));
                    
                    matches.add(match);
                }
            }
            
            matches.sort((a, b) -> {
                int priorityCompare = Integer.compare(b.getPriority(), a.getPriority());
                if (priorityCompare != 0) return priorityCompare;
                return Double.compare(b.getScore(), a.getScore());
            });
            
        } catch (Exception e) {
            // 记录错误但继续执行
        }
        
        return matches;
    }

    private String getMetadataString(SkillPackage pkg, String key) {
        if (pkg.getMetadata() != null) {
            Object value = pkg.getMetadata().get(key);
            return value != null ? value.toString() : null;
        }
        return null;
    }

    private int getMetadataInt(SkillPackage pkg, String key, int defaultValue) {
        if (pkg.getMetadata() != null) {
            Object value = pkg.getMetadata().get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        }
        return defaultValue;
    }

    private String determineConnectorType(SkillPackage pkg) {
        String runtime = getMetadataString(pkg, "runtime");
        if (runtime == null) {
            return "HTTP";
        }
        
        switch (runtime.toLowerCase()) {
            case "http":
            case "rest":
                return "HTTP";
            case "grpc":
                return "GRPC";
            case "websocket":
            case "ws":
                return "WEBSOCKET";
            case "udp":
                return "UDP";
            case "local":
            case "jar":
                return "LOCAL_JAR";
            default:
                return "HTTP";
        }
    }

    private double calculateMatchScore(Capability capability, SkillPackage pkg) {
        double score = 1.0;
        
        if (pkg.getTags() != null && capability.getTags() != null) {
            long matchingTags = pkg.getTags().stream()
                .filter(tag -> capability.getTags().contains(tag))
                .count();
            score += matchingTags * 0.1;
        }
        
        Double rating = getMetadataDouble(pkg, "rating");
        if (rating != null) {
            score += rating * 0.2;
        }
        
        return score;
    }

    private Double getMetadataDouble(SkillPackage pkg, String key) {
        if (pkg.getMetadata() != null) {
            Object value = pkg.getMetadata().get(key);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }
        return null;
    }

    // ==================== Step 5: Skill 挂载 ====================

    private void mountSkills(InitContext context) {
        context.setStatus(InitStatus.MOUNTING_SKILLS);

        for (SceneAgentCore agent : context.getAgents()) {
            for (Map.Entry<String, List<SkillMatch>> entry : context.getSkillMatches().entrySet()) {
                String capId = entry.getKey();
                List<SkillMatch> matches = entry.getValue();

                if (!matches.isEmpty()) {
                    SkillMatch best = matches.get(0);

                    SceneConfig skillConfig = new SceneConfig(best.getSkillId());
                    skillConfig.setProperty("capId", capId);
                    skillConfig.setProperty("connectorType", best.getConnectorType());
                    skillConfig.setProperty("endpoint", best.getEndpoint());
                    skillConfig.setProperty("priority", best.getPriority());

                    agent.mountSkill(best.getSkillId(), skillConfig);

                    SkillBinding binding = new SkillBinding(best.getSkillId(), capId, best.getPriority());
                    context.addSkillBinding(capId, binding);
                }
            }
        }
    }

    // ==================== Step 6: 场景激活 ====================

    private void activate(InitContext context) {
        context.setStatus(InitStatus.ACTIVATING);

        InitRequest request = context.getRequest();

        // 创建场景组
        try {
            SceneGroup group = sceneGroupManager.create(
                request.getSceneId(),
                context.getGroupConfig()
            ).join();

            context.setSceneGroup(group);

            // 让所有 Agent 加入场景组
            for (SceneAgentCore agent : context.getAgents()) {
                MemberRole role = agent.getMemberRole();
                if (role == null) {
                    role = MemberRole.MEMBER;
                }
                sceneGroupManager.join(group.getSceneGroupId(), agent.getAgentId(), role).join();
                agent.setGroupId(group.getSceneGroupId());
            }

            // 启动所有 Agent
            for (SceneAgentCore agent : context.getAgents()) {
                SceneConfig config = new SceneConfig(agent.getAgentId());
                config.setProperty("sceneId", request.getSceneId());
                config.setProperty("groupId", group.getSceneGroupId());
                agent.initialize(config);
            }

            // 启动心跳
            sceneGroupManager.startHeartbeat(group.getSceneGroupId());

            // 发布激活事件
            publishEvent(SceneAgentEvent.activated(
                context.getInitId(),
                request.getSceneId(),
                request.getSceneName(),
                request.getUserId()
            ));

        } catch (Exception e) {
            throw new InitException("Failed to activate scene group: " + e.getMessage(), e);
        }
    }

    // ==================== 辅助方法 ====================

    private void publishEvent(SceneAgentEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }

    /**
     * 获取初始化上下文
     */
    public InitContext getInitContext(String initId) {
        return initContexts.get(initId);
    }

    /**
     * 取消初始化
     */
    public CompletableFuture<Boolean> cancel(String initId) {
        return CompletableFuture.supplyAsync(() -> {
            InitContext context = initContexts.get(initId);
            if (context == null) {
                return false;
            }

            context.setStatus(InitStatus.CANCELLED);

            // 清理已创建的资源
            for (SceneAgentCore agent : context.getAgents()) {
                try {
                    agent.shutdown();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }

            return true;
        });
    }

    // ==================== 内部类 ====================

    /**
     * 初始化上下文
     */
    public static class InitContext {
        private final String initId;
        private final InitRequest request;
        private volatile InitStatus status = InitStatus.CREATED;
        private String errorMessage;

        private SceneGroupManager.SceneGroupConfig groupConfig;
        private SceneGroup sceneGroup;

        private final List<SceneAgentCore> agents = new ArrayList<>();
        private final List<SceneMemberInfo> members = new ArrayList<>();
        private final List<Capability> requiredCapabilities = new ArrayList<>();
        private final List<Capability> optionalCapabilities = new ArrayList<>();
        private final Map<String, List<SkillMatch>> skillMatches = new ConcurrentHashMap<>();
        private final List<SkillBinding> skillBindings = new ArrayList<>();

        public InitContext(InitRequest request) {
            this.initId = "init-" + UUID.randomUUID().toString();
            this.request = request;
        }

        public String getInitId() { return initId; }
        public InitRequest getRequest() { return request; }
        public InitStatus getStatus() { return status; }
        public void setStatus(InitStatus status) { this.status = status; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public SceneGroupManager.SceneGroupConfig getGroupConfig() { return groupConfig; }
        public void setGroupConfig(SceneGroupManager.SceneGroupConfig groupConfig) { this.groupConfig = groupConfig; }
        public SceneGroup getSceneGroup() { return sceneGroup; }
        public void setSceneGroup(SceneGroup sceneGroup) { this.sceneGroup = sceneGroup; }
        public List<SceneAgentCore> getAgents() { return agents; }
        public void addAgent(SceneAgentCore agent) { agents.add(agent); }
        public List<SceneMemberInfo> getMembers() { return members; }
        public void addMember(SceneMemberInfo member) { members.add(member); }
        public List<Capability> getRequiredCapabilities() { return requiredCapabilities; }
        public void addRequiredCapability(Capability cap) { requiredCapabilities.add(cap); }
        public List<Capability> getOptionalCapabilities() { return optionalCapabilities; }
        public void addOptionalCapability(Capability cap) { optionalCapabilities.add(cap); }
        public Map<String, List<SkillMatch>> getSkillMatches() { return skillMatches; }
        public void addSkillMatches(String capId, List<SkillMatch> matches) { skillMatches.put(capId, matches); }
        public List<SkillBinding> getSkillBindings() { return skillBindings; }
        public void addSkillBinding(String capId, SkillBinding binding) { skillBindings.add(binding); }
    }

    /**
     * 初始化状态
     */
    public enum InitStatus {
        CREATED,
        LOADING_SCENE,
        INITIALIZING_AGENTS,
        PARSING_CAPS,
        DISCOVERING_SKILLS,
        MOUNTING_SKILLS,
        ACTIVATING,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    /**
     * 初始化请求
     */
    public static class InitRequest {
        private String sceneId;
        private String sceneName;
        private String userId;
        private int minMembers = 1;
        private int maxMembers = 10;
        private int heartbeatInterval = 5000;
        private int heartbeatTimeout = 15000;
        private int keyThreshold = 2;
        private Map<String, Object> properties = new HashMap<>();
        private List<AgentConfig> agentConfigs = new ArrayList<>();
        private List<String> requiredCapabilities = new ArrayList<>();
        private List<String> optionalCapabilities = new ArrayList<>();

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getSceneName() { return sceneName; }
        public void setSceneName(String sceneName) { this.sceneName = sceneName; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public int getMinMembers() { return minMembers; }
        public void setMinMembers(int minMembers) { this.minMembers = minMembers; }
        public int getMaxMembers() { return maxMembers; }
        public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
        public int getHeartbeatInterval() { return heartbeatInterval; }
        public void setHeartbeatInterval(int heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }
        public int getHeartbeatTimeout() { return heartbeatTimeout; }
        public void setHeartbeatTimeout(int heartbeatTimeout) { this.heartbeatTimeout = heartbeatTimeout; }
        public int getKeyThreshold() { return keyThreshold; }
        public void setKeyThreshold(int keyThreshold) { this.keyThreshold = keyThreshold; }
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
        public List<AgentConfig> getAgentConfigs() { return agentConfigs; }
        public void setAgentConfigs(List<AgentConfig> agentConfigs) { this.agentConfigs = agentConfigs; }
        public List<String> getRequiredCapabilities() { return requiredCapabilities; }
        public void setRequiredCapabilities(List<String> requiredCapabilities) { this.requiredCapabilities = requiredCapabilities; }
        public List<String> getOptionalCapabilities() { return optionalCapabilities; }
        public void setOptionalCapabilities(List<String> optionalCapabilities) { this.optionalCapabilities = optionalCapabilities; }
    }

    /**
     * Agent 配置
     */
    public static class AgentConfig {
        private MemberRole role;
        private String userId;
        private String domainId;
        private Map<String, Object> config = new HashMap<>();

        public AgentConfig(MemberRole role, String userId) {
            this.role = role;
            this.userId = userId;
        }

        public MemberRole getRole() { return role; }
        public void setRole(MemberRole role) { this.role = role; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getDomainId() { return domainId; }
        public void setDomainId(String domainId) { this.domainId = domainId; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }

    /**
     * Skill 匹配信息
     */
    public static class SkillMatch {
        private String skillId;
        private String skillName;
        private String connectorType;
        private String endpoint;
        private int priority;
        private double score;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getSkillName() { return skillName; }
        public void setSkillName(String skillName) { this.skillName = skillName; }
        public String getConnectorType() { return connectorType; }
        public void setConnectorType(String connectorType) { this.connectorType = connectorType; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
    }

    /**
     * 初始化结果
     */
    public static class InitResult {
        private final boolean success;
        private final InitContext context;
        private final String errorMessage;

        private InitResult(boolean success, InitContext context, String errorMessage) {
            this.success = success;
            this.context = context;
            this.errorMessage = errorMessage;
        }

        public static InitResult success(InitContext context) {
            return new InitResult(true, context, null);
        }

        public static InitResult failure(InitContext context, String errorMessage) {
            return new InitResult(false, context, errorMessage);
        }

        public boolean isSuccess() { return success; }
        public InitContext getContext() { return context; }
        public String getErrorMessage() { return errorMessage; }
    }

    /**
     * 初始化异常
     */
    public static class InitException extends RuntimeException {
        public InitException(String message) {
            super(message);
        }

        public InitException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
