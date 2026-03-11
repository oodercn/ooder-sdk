package net.ooder.scene.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProviderInfo {

    private String skillId;
    private String name;
    private String tier;
    private String description;
    private boolean configManager;
    private Map<String, Object> config;

    public ProviderInfo() {}

    public ProviderInfo(String skillId, String name, String tier) {
        this.skillId = skillId;
        this.name = name;
        this.tier = tier;
    }

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isConfigManager() { return configManager; }
    public void setConfigManager(boolean configManager) { this.configManager = configManager; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
}
