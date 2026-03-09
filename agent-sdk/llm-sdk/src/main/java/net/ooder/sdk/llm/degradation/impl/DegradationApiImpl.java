package net.ooder.sdk.llm.degradation.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.degradation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 降级策略 API 实现
 */
@Slf4j
public class DegradationApiImpl implements DegradationApi {

    private final Map<String, DegradationStrategy> strategyRegistry = new ConcurrentHashMap<>();
    private final Map<String, DegradationStatus> statusRegistry = new ConcurrentHashMap<>();
    private final Map<String, List<ErrorRecord>> errorRecords = new ConcurrentHashMap<>();

    private static final int MAX_ERROR_RECORDS = 100;

    @Override
    public void registerStrategy(String strategyId, DegradationStrategy strategy) {
        if (strategyId == null || strategy == null) {
            throw new IllegalArgumentException("StrategyId and strategy cannot be null");
        }

        strategy.setStrategyId(strategyId);
        strategyRegistry.put(strategyId, strategy);
        log.info("Degradation strategy registered: {}", strategyId);
    }

    @Override
    public void unregisterStrategy(String strategyId) {
        if (strategyId != null) {
            strategyRegistry.remove(strategyId);
            log.info("Degradation strategy unregistered: {}", strategyId);
        }
    }

    @Override
    public DegradationStrategy getStrategy(String strategyId) {
        return strategyRegistry.get(strategyId);
    }

    @Override
    public List<DegradationStrategy> listStrategies() {
        return new ArrayList<>(strategyRegistry.values());
    }

    @Override
    public boolean shouldDegrade(DegradationContext context) {
        if (context == null || context.getSessionId() == null) {
            return false;
        }

        String sessionId = context.getSessionId();
        DegradationStatus status = getStatus(sessionId);

        // 如果已经在降级状态，继续降级
        if (status.isDegraded()) {
            return true;
        }

        // 检查所有启用的策略
        List<DegradationStrategy> enabledStrategies = strategyRegistry.values().stream()
                .filter(DegradationStrategy::isEnabled)
                .sorted(Comparator.comparingInt(DegradationStrategy::getPriority))
                .collect(Collectors.toList());

        for (DegradationStrategy strategy : enabledStrategies) {
            if (checkConditions(strategy.getConditions(), context, status)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public DegradationResult degrade(DegradationContext context) {
        if (context == null || context.getSessionId() == null) {
            return DegradationResult.notDegraded();
        }

        String sessionId = context.getSessionId();

        // 查找匹配的策略
        List<DegradationStrategy> enabledStrategies = strategyRegistry.values().stream()
                .filter(DegradationStrategy::isEnabled)
                .sorted(Comparator.comparingInt(DegradationStrategy::getPriority))
                .collect(Collectors.toList());

        DegradationStatus status = getStatus(sessionId);

        for (DegradationStrategy strategy : enabledStrategies) {
            if (checkConditions(strategy.getConditions(), context, status)) {
                // 执行降级
                DegradationAction action = strategy.getAction();
                DegradationResult result = executeAction(action, strategy.getStrategyId(), context);

                // 更新状态
                status.setDegraded(true);
                status.setCurrentStrategyId(strategy.getStrategyId());
                status.setDegradedSince(System.currentTimeMillis());
                status.setLastDegradedAt(System.currentTimeMillis());
                status.setDegradationCount(status.getDegradationCount() + 1);
                status.setStatusDescription("Degraded with strategy: " + strategy.getStrategyId());
                updateStatus(sessionId, status);

                log.info("Degradation executed for session: {} with strategy: {}",
                        sessionId, strategy.getStrategyId());

                return result;
            }
        }

        return DegradationResult.notDegraded();
    }

    @Override
    public void recover(String sessionId) {
        recoverInternal(sessionId);
    }

    @Override
    public void recover(DegradationContext context) {
        if (context != null && context.getSessionId() != null) {
            recoverInternal(context.getSessionId());
        }
    }

    private void recoverInternal(String sessionId) {
        DegradationStatus status = statusRegistry.get(sessionId);
        if (status != null && status.isDegraded()) {
            status.setDegraded(false);
            status.setCurrentStrategyId(null);
            status.setRecoveredAt(System.currentTimeMillis());
            status.setConsecutiveErrors(0);
            status.setStatusDescription("Recovered");
            log.info("Degradation recovered for session: {}", sessionId);
        }
    }

    @Override
    public DegradationStatus getStatus(String sessionId) {
        return statusRegistry.computeIfAbsent(sessionId, DegradationStatus::normal);
    }

    @Override
    public void updateStatus(String sessionId, DegradationStatus status) {
        if (sessionId != null && status != null) {
            status.setSessionId(sessionId);
            statusRegistry.put(sessionId, status);
        }
    }

    @Override
    public void recordError(String sessionId, String errorMessage) {
        DegradationStatus status = getStatus(sessionId);
        status.setErrorCount(status.getErrorCount() + 1);
        status.setConsecutiveErrors(status.getConsecutiveErrors() + 1);
        updateStatus(sessionId, status);

        // 记录错误历史
        errorRecords.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(new ErrorRecord(errorMessage, System.currentTimeMillis()));

        // 限制错误记录数量
        List<ErrorRecord> records = errorRecords.get(sessionId);
        if (records.size() > MAX_ERROR_RECORDS) {
            records.remove(0);
        }
    }

    @Override
    public void recordSuccess(String sessionId) {
        DegradationStatus status = getStatus(sessionId);
        status.setConsecutiveErrors(0);
        updateStatus(sessionId, status);
    }

    @Override
    public void resetStatus(String sessionId) {
        statusRegistry.remove(sessionId);
        errorRecords.remove(sessionId);
        log.info("Degradation status reset for session: {}", sessionId);
    }

    /**
     * 检查条件
     */
    private boolean checkConditions(List<DegradationCondition> conditions,
                                    DegradationContext context,
                                    DegradationStatus status) {
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }

        for (DegradationCondition condition : conditions) {
            if (!checkCondition(condition, context, status)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查单个条件
     */
    private boolean checkCondition(DegradationCondition condition,
                                   DegradationContext context,
                                   DegradationStatus status) {
        if (condition == null || condition.getType() == null) {
            return true;
        }

        Object actualValue = getConditionValue(condition.getType(), context, status);
        Object threshold = condition.getThreshold();

        if (actualValue == null || threshold == null) {
            return false;
        }

        return compareValues(actualValue, threshold, condition.getOperator());
    }

    /**
     * 获取条件值
     */
    private Object getConditionValue(DegradationCondition.ConditionType type,
                                     DegradationContext context,
                                     DegradationStatus status) {
        switch (type) {
            case ERROR_RATE:
                // 简化实现：返回错误次数作为错误率指标
                return (double) status.getErrorCount();
            case RESPONSE_TIME:
                return context.getExecutionTime();
            case ERROR_COUNT:
                return status.getErrorCount();
            case CONSECUTIVE_ERRORS:
                return status.getConsecutiveErrors();
            case LLM_UNAVAILABLE:
                return context.getErrorMessage() != null &&
                        context.getErrorMessage().contains("unavailable");
            case TIMEOUT:
                return context.isTimeout();
            case CUSTOM:
                return context.getVariable(type.name());
            default:
                return null;
        }
    }

    /**
     * 比较值
     */
    @SuppressWarnings("unchecked")
    private boolean compareValues(Object actual, Object threshold,
                                  DegradationCondition.Operator operator) {
        if (operator == null) {
            operator = DegradationCondition.Operator.GREATER_THAN;
        }

        // 布尔值特殊处理
        if (actual instanceof Boolean) {
            boolean actualBool = (Boolean) actual;
            boolean thresholdBool = threshold instanceof Boolean ?
                    (Boolean) threshold : Boolean.parseBoolean(threshold.toString());
            return actualBool == thresholdBool;
        }

        // 数值比较
        double actualNum = toDouble(actual);
        double thresholdNum = toDouble(threshold);

        switch (operator) {
            case GREATER_THAN:
                return actualNum > thresholdNum;
            case LESS_THAN:
                return actualNum < thresholdNum;
            case EQUALS:
                return actualNum == thresholdNum;
            case GREATER_OR_EQUAL:
                return actualNum >= thresholdNum;
            case LESS_OR_EQUAL:
                return actualNum <= thresholdNum;
            case NOT_EQUALS:
                return actualNum != thresholdNum;
            default:
                return false;
        }
    }

    /**
     * 转换为double
     */
    private double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 执行降级动作
     */
    private DegradationResult executeAction(DegradationAction action,
                                            String strategyId,
                                            DegradationContext context) {
        if (action == null) {
            return DegradationResult.degraded(strategyId, "NO_ACTION", null);
        }

        DegradationAction.ActionType type = action.getType();
        Object fallbackData = null;

        switch (type) {
            case RETURN_DEFAULT:
                fallbackData = action.getParameters() != null ?
                        action.getParameters().get("defaultValue") : null;
                break;
            case RETURN_CACHE:
                // TODO: 实现缓存返回
                fallbackData = "Cached response placeholder";
                break;
            case RETURN_ERROR:
                fallbackData = action.getFallbackResponse();
                break;
            case CALL_FALLBACK:
                // TODO: 实现降级服务调用
                fallbackData = "Fallback service response placeholder";
                break;
            case MANUAL_INPUT:
                fallbackData = action.getFallbackResponse();
                break;
            case SKIP:
                fallbackData = null;
                break;
        }

        return DegradationResult.degraded(strategyId, type.name(), fallbackData);
    }

    /**
     * 错误记录
     */
    private static class ErrorRecord {
        final String message;
        final long timestamp;

        ErrorRecord(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
