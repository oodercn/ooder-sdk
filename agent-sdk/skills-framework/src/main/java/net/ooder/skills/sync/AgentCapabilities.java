package net.ooder.skills.sync;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent 能力定义
 */
public class AgentCapabilities implements Serializable {

    private static final long serialVersionUID = 1L;

    private String agentId;
    private List<String> skills;
    private List<String> sceneTypes;
    private Map<String, Object> customCapabilities;
    private int maxConcurrentTasks;
    private boolean supportsStreaming;
    private boolean supportsAsync;

    public AgentCapabilities() {
        this.skills = new ArrayList<>();
        this.sceneTypes = new ArrayList<>();
        this.customCapabilities = new HashMap<>();
        this.maxConcurrentTasks = 10;
        this.supportsStreaming = true;
        this.supportsAsync = true;
    }

    public AgentCapabilities(String agentId) {
        this();
        this.agentId = agentId;
    }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills != null ? skills : new ArrayList<>(); }

    public List<String> getSceneTypes() { return sceneTypes; }
    public void setSceneTypes(List<String> sceneTypes) { this.sceneTypes = sceneTypes != null ? sceneTypes : new ArrayList<>(); }

    public Map<String, Object> getCustomCapabilities() { return customCapabilities; }
    public void setCustomCapabilities(Map<String, Object> customCapabilities) { 
        this.customCapabilities = customCapabilities != null ? customCapabilities : new HashMap<>(); 
    }

    public int getMaxConcurrentTasks() { return maxConcurrentTasks; }
    public void setMaxConcurrentTasks(int maxConcurrentTasks) { this.maxConcurrentTasks = maxConcurrentTasks; }

    public boolean isSupportsStreaming() { return supportsStreaming; }
    public void setSupportsStreaming(boolean supportsStreaming) { this.supportsStreaming = supportsStreaming; }

    public boolean isSupportsAsync() { return supportsAsync; }
    public void setSupportsAsync(boolean supportsAsync) { this.supportsAsync = supportsAsync; }

    public void addSkill(String skillId) {
        if (skillId != null && !skills.contains(skillId)) {
            skills.add(skillId);
        }
    }

    public void removeSkill(String skillId) {
        skills.remove(skillId);
    }

    public boolean hasSkill(String skillId) {
        return skills.contains(skillId);
    }

    public void addSceneType(String sceneType) {
        if (sceneType != null && !sceneTypes.contains(sceneType)) {
            sceneTypes.add(sceneType);
        }
    }

    public boolean supportsSceneType(String sceneType) {
        return sceneTypes.contains(sceneType);
    }
}
