package net.ooder.sdk.llm.degradation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 降级条件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DegradationCondition {

    /**
     * 条件类型
     */
    private ConditionType type;

    /**
     * 条件参数
     */
    private String parameter;

    /**
     * 操作符
     */
    private Operator operator;

    /**
     * 阈值
     */
    private Object threshold;

    /**
     * 条件描述
     */
    private String description;

    /**
     * 条件类型枚举
     */
    public enum ConditionType {
        ERROR_RATE,         // 错误率
        RESPONSE_TIME,      // 响应时间
        ERROR_COUNT,        // 错误次数
        CONSECUTIVE_ERRORS, // 连续错误次数
        LLM_UNAVAILABLE,    // LLM不可用
        TIMEOUT,            // 超时
        CUSTOM              // 自定义
    }

    /**
     * 操作符枚举
     */
    public enum Operator {
        GREATER_THAN,       // >
        LESS_THAN,          // <
        EQUALS,             // ==
        GREATER_OR_EQUAL,   // >=
        LESS_OR_EQUAL,      // <=
        NOT_EQUALS          // !=
    }
}
