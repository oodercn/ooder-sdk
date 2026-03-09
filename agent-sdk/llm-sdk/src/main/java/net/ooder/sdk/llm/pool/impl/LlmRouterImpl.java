package net.ooder.sdk.llm.pool.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.model.ModelInfo;
import net.ooder.sdk.llm.adapter.model.ModelSelectionCriteria;
import net.ooder.sdk.llm.pool.LlmPoolManager;
import net.ooder.sdk.llm.pool.LlmRouter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LLM 路由器实现
 */
@Slf4j
public class LlmRouterImpl implements LlmRouter {

    private final LlmPoolManager poolManager;
    private final Map<StrategyType, RoutingStrategy> strategies = new ConcurrentHashMap<>();
    private StrategyType defaultStrategy = StrategyType.ROUND_ROBIN;

    // 轮询计数器
    private final AtomicInteger roundRobinCounter = new AtomicInteger(0);

    public LlmRouterImpl(LlmPoolManager poolManager) {
        this.poolManager = poolManager;
        // 注册内置策略
        registerBuiltInStrategies();
    }

    @Override
    public ModelInfo selectModel(ModelSelectionCriteria criteria) {
        // 获取候选模型
        List<ModelInfo> candidates;
        if (criteria.getRequiredCapabilities() != null && !criteria.getRequiredCapabilities().isEmpty()) {
            // 根据能力筛选 - 将 CapabilityType 转换为 String
            candidates = new ArrayList<>();
            for (net.ooder.sdk.llm.common.enums.CapabilityType capability : criteria.getRequiredCapabilities()) {
                candidates.addAll(poolManager.getModelsByCapability(capability.name()));
            }
            // 去重
            candidates = new ArrayList<>(new LinkedHashSet<>(candidates));
        } else {
            candidates = poolManager.getHealthyModels();
        }

        if (candidates.isEmpty()) {
            log.warn("No available models for criteria: {}", criteria);
            return null;
        }

        // 使用默认策略（ModelSelectionCriteria 没有 strategy 字段）
        RoutingStrategy strategy = strategies.get(defaultStrategy);
        if (strategy == null) {
            strategy = strategies.get(StrategyType.ROUND_ROBIN);
        }

        ModelInfo selected = strategy.select(candidates, criteria);
        if (selected != null) {
            log.debug("Model selected: {} using strategy: {}",
                    selected.getModelId(), defaultStrategy);
        }
        return selected;
    }

    @Override
    public void registerStrategy(RoutingStrategy strategy) {
        strategies.put(strategy.getType(), strategy);
        log.info("Routing strategy registered: {}", strategy.getType());
    }

    @Override
    public void setDefaultStrategy(StrategyType strategyType) {
        this.defaultStrategy = strategyType;
        log.info("Default routing strategy set to: {}", strategyType);
    }

    /**
     * 注册内置策略
     */
    private void registerBuiltInStrategies() {
        // 轮询策略
        registerStrategy(new RoutingStrategy() {
            @Override
            public ModelInfo select(List<ModelInfo> candidates, ModelSelectionCriteria criteria) {
                if (candidates.isEmpty()) return null;
                int index = roundRobinCounter.getAndIncrement() % candidates.size();
                return candidates.get(index);
            }

            @Override
            public StrategyType getType() {
                return StrategyType.ROUND_ROBIN;
            }
        });

        // 最低延迟策略
        registerStrategy(new RoutingStrategy() {
            @Override
            public ModelInfo select(List<ModelInfo> candidates, ModelSelectionCriteria criteria) {
                return candidates.stream()
                        .min(Comparator.comparingDouble(m ->
                                getModelMetrics(m.getModelId()).getAverageLatency()))
                        .orElse(null);
            }

            @Override
            public StrategyType getType() {
                return StrategyType.LEAST_LATENCY;
            }
        });

        // 加权策略
        registerStrategy(new RoutingStrategy() {
            @Override
            public ModelInfo select(List<ModelInfo> candidates, ModelSelectionCriteria criteria) {
                // FIXME: 实现加权选择
                return candidates.get(0);
            }

            @Override
            public StrategyType getType() {
                return StrategyType.WEIGHTED;
            }
        });

        // 基于能力策略
        registerStrategy(new RoutingStrategy() {
            @Override
            public ModelInfo select(List<ModelInfo> candidates, ModelSelectionCriteria criteria) {
                // 选择能力匹配度最高的
                return candidates.stream()
                        .max(Comparator.comparingInt(m ->
                                countMatchingCapabilities(m, criteria.getRequiredCapabilities())))
                        .orElse(null);
            }

            @Override
            public StrategyType getType() {
                return StrategyType.CAPABILITY_BASED;
            }
        });

        // 基于成本策略
        registerStrategy(new RoutingStrategy() {
            @Override
            public ModelInfo select(List<ModelInfo> candidates, ModelSelectionCriteria criteria) {
                // FIXME: 实现基于成本的选择
                return candidates.get(0);
            }

            @Override
            public StrategyType getType() {
                return StrategyType.COST_BASED;
            }
        });
    }

    /**
     * 获取模型指标
     */
    private LlmPoolManagerImpl.ModelMetrics getModelMetrics(String modelId) {
        if (poolManager instanceof LlmPoolManagerImpl) {
            return ((LlmPoolManagerImpl) poolManager).getModelMetrics(modelId);
        }
        return new LlmPoolManagerImpl.ModelMetrics();
    }

    /**
     * 计算能力匹配数
     */
    private int countMatchingCapabilities(ModelInfo model, List<net.ooder.sdk.llm.common.enums.CapabilityType> requiredCapabilities) {
        if (model.getCapabilities() == null || requiredCapabilities == null) {
            return 0;
        }
        int count = 0;
        for (net.ooder.sdk.llm.common.enums.CapabilityType capability : requiredCapabilities) {
            if (model.getCapabilities().contains(capability.name())) {
                count++;
            }
        }
        return count;
    }
}
