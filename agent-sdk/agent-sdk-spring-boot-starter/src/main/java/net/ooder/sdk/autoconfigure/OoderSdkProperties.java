package net.ooder.sdk.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ooder SDK 配置属性
 *
 * @version 3.0.0
 * @since 3.0.0
 */
@ConfigurationProperties(prefix = "ooder.sdk")
public class OoderSdkProperties {

    private boolean enabled = true;

    private String agentId;

    private String agentName;

    private String agentType;

    private String endpoint;

    private int udpPort = 8080;

    private String skillRootPath;

    private String skillCenterUrl;

    private String vfsUrl;

    private boolean strictMode = false;

    private boolean discoveryEnabled = true;

    private int heartbeatInterval = 30000;

    private int heartbeatTimeout = 10000;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public String getSkillRootPath() {
        return skillRootPath;
    }

    public void setSkillRootPath(String skillRootPath) {
        this.skillRootPath = skillRootPath;
    }

    public String getSkillCenterUrl() {
        return skillCenterUrl;
    }

    public void setSkillCenterUrl(String skillCenterUrl) {
        this.skillCenterUrl = skillCenterUrl;
    }

    public String getVfsUrl() {
        return vfsUrl;
    }

    public void setVfsUrl(String vfsUrl) {
        this.vfsUrl = vfsUrl;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public boolean isDiscoveryEnabled() {
        return discoveryEnabled;
    }

    public void setDiscoveryEnabled(boolean discoveryEnabled) {
        this.discoveryEnabled = discoveryEnabled;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }
}
