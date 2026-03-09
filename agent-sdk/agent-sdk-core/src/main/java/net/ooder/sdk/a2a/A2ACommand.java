package net.ooder.sdk.a2a;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * A2A 命令
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
public class A2ACommand {

    /**
     * 命令类型
     */
    private String commandType;

    /**
     * 目标 Agent
     */
    private String targetAgent;

    /**
     * 命令体
     */
    private Map<String, Object> body;

    /**
     * 超时时间（毫秒）
     */
    private int timeout;
}
