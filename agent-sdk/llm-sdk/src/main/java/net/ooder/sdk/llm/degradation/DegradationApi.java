package net.ooder.sdk.llm.degradation;

import java.util.List;

/**
 * 降级策略 API
 */
public interface DegradationApi {

    /**
     * 注册降级策略
     * @param strategyId 策略ID
     * @param strategy 降级策略
     */
    void registerStrategy(String strategyId, DegradationStrategy strategy);

    /**
     * 注销降级策略
     * @param strategyId 策略ID
     */
    void unregisterStrategy(String strategyId);

    /**
     * 获取降级策略
     * @param strategyId 策略ID
     * @return 降级策略
     */
    DegradationStrategy getStrategy(String strategyId);

    /**
     * 列出所有策略
     * @return 策略列表
     */
    List<DegradationStrategy> listStrategies();

    /**
     * 检查是否需要降级
     * @param context 上下文
     * @return 是否需要降级
     */
    boolean shouldDegrade(DegradationContext context);

    /**
     * 执行降级
     * @param context 上下文
     * @return 降级结果
     */
    DegradationResult degrade(DegradationContext context);

    /**
     * 恢复服务
     * @param sessionId 会话ID
     */
    void recover(String sessionId);

    /**
     * 恢复服务
     * @param context 上下文
     */
    void recover(DegradationContext context);

    /**
     * 获取降级状态
     * @param sessionId 会话ID
     * @return 降级状态
     */
    DegradationStatus getStatus(String sessionId);

    /**
     * 更新降级状态
     * @param sessionId 会话ID
     * @param status 降级状态
     */
    void updateStatus(String sessionId, DegradationStatus status);

    /**
     * 记录错误
     * @param sessionId 会话ID
     * @param errorMessage 错误信息
     */
    void recordError(String sessionId, String errorMessage);

    /**
     * 记录成功
     * @param sessionId 会话ID
     */
    void recordSuccess(String sessionId);

    /**
     * 重置状态
     * @param sessionId 会话ID
     */
    void resetStatus(String sessionId);
}
