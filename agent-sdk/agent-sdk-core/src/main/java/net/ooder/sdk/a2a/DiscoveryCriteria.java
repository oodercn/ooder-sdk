package net.ooder.sdk.a2a;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 发现条件
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
public class DiscoveryCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private String agentType;
    private List<String> capabilities;
    private AgentInfo.AgentStatus status;
    private Map<String, Object> filters;
}
