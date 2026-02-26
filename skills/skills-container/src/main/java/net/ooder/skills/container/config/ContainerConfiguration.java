package net.ooder.skills.container.config;

import java.util.List;
import java.util.Map;

/**
 * 容器配置
 */
public class ContainerConfiguration {

    private String workspace;
    private boolean autoStart;
    private long healthCheckInterval;
    private boolean enableBlueGreenDeployment;
    private List<SkillConfig> skills;
    private Map<String, Object> properties;

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public long getHealthCheckInterval() {
        return healthCheckInterval;
    }

    public void setHealthCheckInterval(long healthCheckInterval) {
        this.healthCheckInterval = healthCheckInterval;
    }

    public boolean isEnableBlueGreenDeployment() {
        return enableBlueGreenDeployment;
    }

    public void setEnableBlueGreenDeployment(boolean enableBlueGreenDeployment) {
        this.enableBlueGreenDeployment = enableBlueGreenDeployment;
    }

    public List<SkillConfig> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillConfig> skills) {
        this.skills = skills;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Skill 配置
     */
    public static class SkillConfig {
        private String id;
        private String version;
        private String location;
        private boolean enabled;
        private Map<String, Object> config;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Map<String, Object> getConfig() {
            return config;
        }

        public void setConfig(Map<String, Object> config) {
            this.config = config;
        }
    }
}
