package net.ooder.sdk.a2a;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Agent 信息
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
public class AgentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String agentId;
    private String agentName;
    private String agentType;
    private String version;
    private AgentStatus status;
    private List<String> capabilities;
    private Map<String, Object> metadata;
    private String endpoint;
    private long registeredAt;
    private long lastHeartbeat;

    /**
     * Agent 状态
     */
    public enum AgentStatus {
        ONLINE,     // 在线
        OFFLINE,    // 离线
        BUSY,       // 忙碌
        MAINTENANCE // 维护中
    }
}
