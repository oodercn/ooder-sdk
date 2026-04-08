package net.ooder.scene.skill.model;

import net.ooder.scene.skill.capability.Capability;

import java.util.*;

/**
 * 场景结构实现
 *
 * <p>从 SkillPackage 解析场景结构信息</p>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0
 */
public class SceneStructureImpl implements SceneStructure {

    private final List<InternalCapability> internalCapabilities;
    private final List<Skill> childSkills;
    private final Orchestration orchestration;
    private final CollaborationConfig collaborationConfig;
    private final String entryCapability;
    private final SceneState state;
    private final Map<String, Object> metadata;

    public SceneStructureImpl(
            List<InternalCapability> internalCapabilities,
            List<Skill> childSkills,
            Orchestration orchestration,
            CollaborationConfig collaborationConfig,
            String entryCapability,
            SceneState state,
            Map<String, Object> metadata) {
        this.internalCapabilities = internalCapabilities != null ? internalCapabilities : Collections.emptyList();
        this.childSkills = childSkills != null ? childSkills : Collections.emptyList();
        this.orchestration = orchestration;
        this.collaborationConfig = collaborationConfig;
        this.entryCapability = entryCapability;
        this.state = state != null ? state : SceneState.CREATED;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    @Override
    public List<InternalCapability> getInternalCapabilities() {
        return internalCapabilities;
    }

    @Override
    public List<Skill> getChildSkills() {
        return childSkills;
    }

    @Override
    public Orchestration getOrchestration() {
        return orchestration;
    }

    @Override
    public CollaborationConfig getCollaborationConfig() {
        return collaborationConfig;
    }

    @Override
    public String getEntryCapability() {
        return entryCapability;
    }

    @Override
    public SceneState getState() {
        return state;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * 内部能力实现
     */
    public static class InternalCapabilityImpl implements InternalCapability {
        private final String id;
        private final String name;
        private final String description;
        private final CapabilityType type;
        private final Map<String, Object> config;
        private final boolean isPrivate;

        public InternalCapabilityImpl(String id, String name, String description, 
                                       String type, Map<String, Object> config, boolean isPrivate) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = parseType(type);
            this.config = config != null ? config : new HashMap<>();
            this.isPrivate = isPrivate;
        }
        
        private CapabilityType parseType(String type) {
            if (type == null) return CapabilityType.INTERNAL;
            try {
                return CapabilityType.valueOf(type.toUpperCase());
            } catch (Exception e) {
                return CapabilityType.INTERNAL;
            }
        }

        @Override
        public String getId() { return id; }

        @Override
        public String getName() { return name; }

        @Override
        public String getDescription() { return description; }

        @Override
        public CapabilityType getType() { return type; }

        @Override
        public boolean isPrivate() { return isPrivate; }

        public Map<String, Object> getConfig() { return config; }
    }

    /**
     * 编排逻辑实现
     */
    public static class OrchestrationImpl implements Orchestration {
        private final OrchestrationType type;
        private final List<Orchestration.Step> steps;

        public OrchestrationImpl(OrchestrationType type, List<Orchestration.Step> steps) {
            this.type = type != null ? type : OrchestrationType.SEQUENTIAL;
            this.steps = steps != null ? steps : Collections.emptyList();
        }

        @Override
        public OrchestrationType getType() { return type; }

        @Override
        public List<Orchestration.Step> getSteps() { return steps; }
    }

    /**
     * 执行步骤实现
     */
    public static class StepImpl implements Orchestration.Step {
        private final String id;
        private final String capabilityId;
        private final Map<String, String> inputMapping;
        private final Map<String, String> outputMapping;
        private final String condition;

        public StepImpl(String id, String capabilityId, 
                        Map<String, String> inputMapping, 
                        Map<String, String> outputMapping, 
                        String condition) {
            this.id = id;
            this.capabilityId = capabilityId;
            this.inputMapping = inputMapping != null ? inputMapping : new HashMap<>();
            this.outputMapping = outputMapping != null ? outputMapping : new HashMap<>();
            this.condition = condition;
        }

        @Override
        public String getId() { return id; }

        @Override
        public String getCapabilityId() { return capabilityId; }

        @Override
        public Map<String, String> inputMapping() { return inputMapping; }

        @Override
        public Map<String, String> outputMapping() { return outputMapping; }

        @Override
        public String getCondition() { return condition; }
    }

    /**
     * 协作配置实现
     */
    public static class CollaborationConfigImpl implements CollaborationConfig {
        private final boolean externallyAccessible;
        private final List<String> exposedCapabilities;
        private final List<ExternalDependency> externalDependencies;
        private final A2AConfig a2aConfig;

        public CollaborationConfigImpl(boolean externallyAccessible,
                                        List<String> exposedCapabilities,
                                        List<ExternalDependency> externalDependencies,
                                        A2AConfig a2aConfig) {
            this.externallyAccessible = externallyAccessible;
            this.exposedCapabilities = exposedCapabilities != null ? exposedCapabilities : Collections.emptyList();
            this.externalDependencies = externalDependencies != null ? externalDependencies : Collections.emptyList();
            this.a2aConfig = a2aConfig;
        }

        @Override
        public boolean isExternallyAccessible() { return externallyAccessible; }

        @Override
        public List<String> getExposedCapabilities() { return exposedCapabilities; }

        @Override
        public List<ExternalDependency> getExternalDependencies() { return externalDependencies; }

        @Override
        public A2AConfig getA2AConfig() { return a2aConfig; }
    }

    /**
     * 外部依赖实现
     */
    public static class ExternalDependencyImpl implements ExternalDependency {
        private final String skillId;
        private final String capabilityId;
        private final boolean required;
        private final String fallbackStrategy;

        public ExternalDependencyImpl(String skillId, String capabilityId, 
                                       boolean required, String fallbackStrategy) {
            this.skillId = skillId;
            this.capabilityId = capabilityId;
            this.required = required;
            this.fallbackStrategy = fallbackStrategy;
        }

        @Override
        public String getSkillId() { return skillId; }

        @Override
        public String getCapabilityId() { return capabilityId; }

        @Override
        public boolean isRequired() { return required; }

        @Override
        public String getFallbackStrategy() { return fallbackStrategy; }
    }

    /**
     * A2A配置实现
     */
    public static class A2AConfigImpl implements A2AConfig {
        private final boolean enabled;
        private final String endpoint;
        private final Map<String, String> headers;

        public A2AConfigImpl(boolean enabled, String endpoint, Map<String, String> headers) {
            this.enabled = enabled;
            this.endpoint = endpoint;
            this.headers = headers != null ? headers : new HashMap<>();
        }

        @Override
        public boolean enabled() { return enabled; }

        @Override
        public String getEndpoint() { return endpoint; }

        @Override
        public Map<String, String> getHeaders() { return headers; }
    }
}
