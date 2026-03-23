package net.ooder.sdk.a2a.loadbalance;

/**
 * 故障转移服务
 *
 * @version 2.3.1
 * @since 2.3.1
 */
public interface FailoverService {

    /**
     * 执行带故障转移的操作
     *
     * @param operation 操作
     * @param failoverOptions 故障转移选项
     * @return 操作结果
     */
    <T> T executeWithFailover(Operation<T> operation, FailoverOptions failoverOptions);

    /**
     * 记录失败
     *
     * @param agentId Agent ID
     * @param error 错误信息
     */
    void recordFailure(String agentId, Exception error);

    /**
     * 检查 Agent 是否可用
     *
     * @param agentId Agent ID
     * @return 是否可用
     */
    boolean isAvailable(String agentId);

    /**
     * 获取熔断器状态
     *
     * @param agentId Agent ID
     * @return 熔断器状态
     */
    CircuitBreakerState getCircuitBreakerState(String agentId);

    /**
     * 重置熔断器
     *
     * @param agentId Agent ID
     */
    void resetCircuitBreaker(String agentId);
}
