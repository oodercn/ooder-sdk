package net.ooder.skills.config;

import java.util.List;
import java.util.Map;

/**
 * 协作统一配置类
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class CollaborativeConfiguration {

    private boolean enabled;
    private String protocol;
    private List<String> participants;
    private List<String> collaborativeCapabilityIds;
    private Map<String, Object> initParams;
    private boolean autoSyncState;
    private Map<String, Object> params;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }

    public List<String> getCollaborativeCapabilityIds() { return collaborativeCapabilityIds; }
    public void setCollaborativeCapabilityIds(List<String> collaborativeCapabilityIds) { this.collaborativeCapabilityIds = collaborativeCapabilityIds; }

    public Map<String, Object> getInitParams() { return initParams; }
    public void setInitParams(Map<String, Object> initParams) { this.initParams = initParams; }

    public boolean isAutoSyncState() { return autoSyncState; }
    public void setAutoSyncState(boolean autoSyncState) { this.autoSyncState = autoSyncState; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
