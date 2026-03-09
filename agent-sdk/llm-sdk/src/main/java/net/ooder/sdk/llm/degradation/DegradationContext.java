package net.ooder.sdk.llm.degradation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 降级上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DegradationContext {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 上下文变量
     */
    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 异常对象
     */
    private Throwable exception;

    /**
     * 请求时间戳
     */
    @Builder.Default
    private long requestTime = System.currentTimeMillis();

    /**
     * 响应时间
     */
    private long responseTime;

    /**
     * 是否超时
     */
    private boolean timeout;

    /**
     * 获取执行耗时
     */
    public long getExecutionTime() {
        if (responseTime > 0 && requestTime > 0) {
            return responseTime - requestTime;
        }
        return 0;
    }

    /**
     * 添加上下文变量
     */
    public void addVariable(String key, Object value) {
        variables.put(key, value);
    }

    /**
     * 获取上下文变量
     */
    public Object getVariable(String key) {
        return variables.get(key);
    }
}
