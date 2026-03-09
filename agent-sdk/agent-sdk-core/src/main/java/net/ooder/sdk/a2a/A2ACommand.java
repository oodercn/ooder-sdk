package net.ooder.sdk.a2a;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A2A 命令 (扩展支持上下文传递)
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
public class A2ACommand implements Serializable {

    private static final long serialVersionUID = 1L;

    // 基础信息
    private String commandId;
    private String commandType;
    private String version;

    // 路由信息
    private String sourceAgentId;
    private String targetAgentId;
    private String sourceSceneId;
    private String targetSceneId;

    // ========== 新增：上下文传递 ==========
    private ContextTransfer contextTransfer;    // 上下文传递数据
    private String contextReference;            // 上下文引用（引用传递模式）
    private TransferMode transferMode;          // 传递模式

    // 负载
    private Map<String, Object> payload;
    private Map<String, Object> headers;

    // 元数据
    private long timestamp;
    private long ttl;
    private Priority priority;

    // 追踪
    private String traceId;
    private List<String> spanIds;

    // 超时时间（毫秒）
    private int timeout;

    /**
     * 传递模式
     */
    public enum TransferMode {
        FULL,       // 完整传递
        REFERENCE,  // 引用传递
        DELTA,      // 增量传递
        SELECTIVE   // 选择性传递
    }

    /**
     * 优先级
     */
    public enum Priority {
        LOW, NORMAL, HIGH, CRITICAL
    }
}
