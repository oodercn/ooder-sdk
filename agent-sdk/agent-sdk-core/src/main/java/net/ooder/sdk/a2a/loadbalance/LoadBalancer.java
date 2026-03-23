package net.ooder.sdk.a2a.loadbalance;

import net.ooder.sdk.a2a.AgentInfo;

import java.util.List;

/**
 * 负载均衡服务
 *
 * @version 2.3.1
 * @since 2.3.1
 */
public interface LoadBalancer {

    /**
     * 选择最优 Agent
     *
     * @param agents Agent 列表
     * @param context 选择上下文
     * @return 选中的 Agent
     */
    AgentInfo selectAgent(List<AgentInfo> agents, SelectionContext context);

    /**
     * 更新 Agent 负载
     *
     * @param agentId Agent ID
     * @param loadDelta 负载变化
     */
    void updateLoad(String agentId, int loadDelta);

    /**
     * 获取 Agent 负载信息
     *
     * @param agentId Agent ID
     * @return 负载信息
     */
    LoadInfo getLoadInfo(String agentId);

    /**
     * 重置 Agent 负载
     *
     * @param agentId Agent ID
     */
    void resetLoad(String agentId);

    /**
     * 获取负载均衡策略
     *
     * @return 策略名称
     */
    String getStrategy();
}
