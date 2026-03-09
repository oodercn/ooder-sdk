package net.ooder.sdk.llm.scene.transfer;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 上下文传递对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextTransfer {

    /**
     * 传递ID
     */
    private String transferId;

    /**
     * 源上下文ID
     */
    private String sourceContextId;

    /**
     * 目标上下文ID（引用模式使用）
     */
    private String targetContextId;

    /**
     * 源场景ID
     */
    private String sourceSceneId;

    /**
     * 目标场景ID
     */
    private String targetSceneId;

    /**
     * 传递模式
     */
    private ContextTransferHandler.TransferMode transferMode;

    /**
     * 包含的部分
     */
    private Set<String> includedParts;

    /**
     * 序列化的上下文（FULL/SELECTIVE模式）
     */
    private String serializedContext;

    /**
     * 上下文引用（REFERENCE模式）
     */
    private ContextReference contextReference;

    /**
     * 上下文差异（DELTA模式）
     */
    private Map<String, Object> contextDelta;

    /**
     * 创建时间戳
     */
    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    /**
     * 过期时间戳
     */
    private long expiresAt;

    /**
     * 传递状态
     */
    @Builder.Default
    private TransferStatus status = TransferStatus.PENDING;

    /**
     * 传递状态枚举
     */
    public enum TransferStatus {
        PENDING,    // 待传递
        IN_TRANSIT, // 传递中
        COMPLETED,  // 已完成
        FAILED,     // 失败
        EXPIRED     // 已过期
    }

    /**
     * 上下文引用
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContextReference {
        private String contextId;
        private String registryEndpoint;
        private String accessToken;
        private long validUntil;
    }
}
