package net.ooder.sdk.llm.degradation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 降级动作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DegradationAction {

    /**
     * 动作类型
     */
    private ActionType type;

    /**
     * 动作参数
     */
    private Map<String, Object> parameters;

    /**
     * 备用响应内容
     */
    private String fallbackResponse;

    /**
     * 备用处理器类名
     */
    private String fallbackHandlerClass;

    /**
     * 动作描述
     */
    private String description;

    /**
     * 动作类型枚举
     */
    public enum ActionType {
        RETURN_DEFAULT,     // 返回默认值
        RETURN_CACHE,       // 返回缓存
        RETURN_ERROR,       // 返回错误
        CALL_FALLBACK,      // 调用降级服务
        MANUAL_INPUT,       // 手动输入
        SKIP                // 跳过
    }
}
