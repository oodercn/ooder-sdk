package net.ooder.sdk.llm.pool;

import net.ooder.sdk.llm.model.ModelInfo;
import net.ooder.sdk.llm.adapter.model.ModelSelectionCriteria;

/**
 * LLM 路由器
 * 根据策略选择合适的模型
 */
public interface LlmRouter {

    /**
     * 选择模型
     *
     * @param criteria 选择条件
     * @return 选中的模型
     */
    ModelInfo selectModel(ModelSelectionCriteria criteria);

    /**
     * 注册路由策略
     *
     * @param strategy 路由策略
     */
    void registerStrategy(RoutingStrategy strategy);

    /**
     * 设置默认策略
     *
     * @param strategyType 策略类型
     */
    void setDefaultStrategy(StrategyType strategyType);

    /**
     * 路由策略类型枚举
     */
    enum StrategyType {
        ROUND_ROBIN,      // 轮询
        LEAST_LATENCY,    // 最低延迟
        WEIGHTED,         // 加权
        CAPABILITY_BASED, // 基于能力
        COST_BASED        // 基于成本
    }

    /**
     * 路由策略接口
     */
    interface RoutingStrategy {
        /**
         * 选择模型
         *
         * @param candidates 候选模型
         * @param criteria   选择条件
         * @return 选中的模型
         */
        ModelInfo select(java.util.List<ModelInfo> candidates, ModelSelectionCriteria criteria);

        /**
         * 获取策略类型
         *
         * @return 策略类型
         */
        StrategyType getType();
    }
}
