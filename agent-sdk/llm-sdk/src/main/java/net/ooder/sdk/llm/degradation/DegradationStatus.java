package net.ooder.sdk.llm.degradation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 降级状态
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DegradationStatus {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 是否处于降级状态
     */
    private boolean degraded;

    /**
     * 当前应用的策略ID
     */
    private String currentStrategyId;

    /**
     * 降级开始时间
     */
    private long degradedSince;

    /**
     * 降级次数
     */
    @Builder.Default
    private int degradationCount = 0;

    /**
     * 上次降级时间
     */
    private long lastDegradedAt;

    /**
     * 恢复时间
     */
    private long recoveredAt;

    /**
     * 错误计数
     */
    @Builder.Default
    private int errorCount = 0;

    /**
     * 连续错误计数
     */
    @Builder.Default
    private int consecutiveErrors = 0;

    /**
     * 状态描述
     */
    private String statusDescription;

    /**
     * 创建正常状态
     */
    public static DegradationStatus normal(String sessionId) {
        return DegradationStatus.builder()
                .sessionId(sessionId)
                .degraded(false)
                .statusDescription("Normal")
                .build();
    }

    /**
     * 创建降级状态
     */
    public static DegradationStatus degraded(String sessionId, String strategyId) {
        long now = System.currentTimeMillis();
        return DegradationStatus.builder()
                .sessionId(sessionId)
                .degraded(true)
                .currentStrategyId(strategyId)
                .degradedSince(now)
                .lastDegradedAt(now)
                .statusDescription("Degraded with strategy: " + strategyId)
                .build();
    }
}
