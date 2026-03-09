package net.ooder.sdk.llm.degradation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 降级策略
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DegradationStrategy {

    /**
     * 策略ID
     */
    private String strategyId;

    /**
     * 策略名称
     */
    private String name;

    /**
     * 策略描述
     */
    private String description;

    /**
     * 降级条件列表
     */
    private List<DegradationCondition> conditions;

    /**
     * 降级动作
     */
    private DegradationAction action;

    /**
     * 优先级（数值越小优先级越高）
     */
    @Builder.Default
    private int priority = 100;

    /**
     * 是否启用
     */
    @Builder.Default
    private boolean enabled = true;

    /**
     * 创建时间
     */
    @Builder.Default
    private long createdAt = System.currentTimeMillis();
}
