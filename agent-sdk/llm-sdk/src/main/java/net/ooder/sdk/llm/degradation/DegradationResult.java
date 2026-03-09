package net.ooder.sdk.llm.degradation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 降级结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DegradationResult {

    /**
     * 是否已降级
     */
    private boolean degraded;

    /**
     * 降级方法
     */
    private String fallbackMethod;

    /**
     * 降级数据
     */
    private Object fallbackData;

    /**
     * 降级原因
     */
    private String reason;

    /**
     * 应用的策略ID
     */
    private String appliedStrategyId;

    /**
     * 降级时间戳
     */
    @Builder.Default
    private long degradedAt = System.currentTimeMillis();

    /**
     * 额外属性
     */
    @Builder.Default
    private Map<String, Object> properties = new HashMap<>();

    /**
     * 创建成功降级结果
     */
    public static DegradationResult degraded(String strategyId, String fallbackMethod, Object fallbackData) {
        return DegradationResult.builder()
                .degraded(true)
                .appliedStrategyId(strategyId)
                .fallbackMethod(fallbackMethod)
                .fallbackData(fallbackData)
                .build();
    }

    /**
     * 创建未降级结果
     */
    public static DegradationResult notDegraded() {
        return DegradationResult.builder()
                .degraded(false)
                .build();
    }
}
