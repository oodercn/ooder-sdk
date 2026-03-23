package net.ooder.scene.skill.install;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技能运行时配置
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class SkillRuntimeConfig {

    private String skillId;
    private String version;
    private String configId;
    private long createTime;
    private long updateTime;
    private Map<String, Object> llmConfig;
    private Map<String, Object> knowledgeConfig;
    private List<CapabilityDef> capabilities;
    private List<ToolDef> tools;
    private Map<String, Object> metadata;

    public SkillRuntimeConfig() {
        this.llmConfig = new HashMap<>();
        this.knowledgeConfig = new HashMap<>();
        this.capabilities = new ArrayList<>();
        this.tools = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    public static SkillRuntimeConfig create(String skillId, String version) {
        SkillRuntimeConfig config = new SkillRuntimeConfig();
        config.setSkillId(skillId);
        config.setVersion(version);
        config.setConfigId(generateConfigId(skillId));
        return config;
    }

    private static String generateConfigId(String skillId) {
        return skillId + "-config-" + System.currentTimeMillis();
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public Map<String, Object> getLlmConfig() {
        return llmConfig;
    }

    public void setLlmConfig(Map<String, Object> llmConfig) {
        this.llmConfig = llmConfig != null ? llmConfig : new HashMap<>();
    }

    public Map<String, Object> getKnowledgeConfig() {
        return knowledgeConfig;
    }

    public void setKnowledgeConfig(Map<String, Object> knowledgeConfig) {
        this.knowledgeConfig = knowledgeConfig != null ? knowledgeConfig : new HashMap<>();
    }

    public List<CapabilityDef> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<CapabilityDef> capabilities) {
        this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
    }

    public List<ToolDef> getTools() {
        return tools;
    }

    public void setTools(List<ToolDef> tools) {
        this.tools = tools != null ? tools : new ArrayList<>();
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
}
