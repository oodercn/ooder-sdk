package net.ooder.sdk.llm.pool.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.model.ModelInfo;
import net.ooder.sdk.llm.pool.LlmPoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LLM Pool 管理器实现
 */
@Slf4j
public class LlmPoolManagerImpl implements LlmPoolManager {

    private final Map<String, ModelInfo> modelRegistry = new ConcurrentHashMap<>();
    private final Map<String, ModelStatus> modelStatusMap = new ConcurrentHashMap<>();
    private final Map<String, ModelMetrics> modelMetricsMap = new ConcurrentHashMap<>();

    @Override
    public void registerModel(ModelInfo modelInfo) {
        if (modelInfo == null || modelInfo.getModelId() == null) {
            throw new IllegalArgumentException("ModelInfo and modelId cannot be null");
        }

        modelRegistry.put(modelInfo.getModelId(), modelInfo);
        modelStatusMap.put(modelInfo.getModelId(), ModelStatus.HEALTHY);
        modelMetricsMap.put(modelInfo.getModelId(), new ModelMetrics());

        log.info("Model registered to pool: {} ({})",
                modelInfo.getModelId(), modelInfo.getModelName());
    }

    @Override
    public void unregisterModel(String modelId) {
        modelRegistry.remove(modelId);
        modelStatusMap.remove(modelId);
        modelMetricsMap.remove(modelId);
        log.info("Model unregistered from pool: {}", modelId);
    }

    @Override
    public ModelInfo getModel(String modelId) {
        return modelRegistry.get(modelId);
    }

    @Override
    public List<ModelInfo> getAvailableModels() {
        List<ModelInfo> availableModels = new ArrayList<>();
        for (Map.Entry<String, ModelInfo> entry : modelRegistry.entrySet()) {
            ModelStatus status = modelStatusMap.get(entry.getKey());
            if (status == ModelStatus.HEALTHY || status == ModelStatus.DEGRADED) {
                availableModels.add(entry.getValue());
            }
        }
        return availableModels;
    }

    @Override
    public List<ModelInfo> getModelsByCapability(String capability) {
        List<ModelInfo> models = new ArrayList<>();
        for (ModelInfo model : getAvailableModels()) {
            if (model.getCapabilities() != null &&
                    model.getCapabilities().contains(capability)) {
                models.add(model);
            }
        }
        return models;
    }

    @Override
    public List<ModelInfo> getHealthyModels() {
        List<ModelInfo> healthyModels = new ArrayList<>();
        for (Map.Entry<String, ModelInfo> entry : modelRegistry.entrySet()) {
            if (modelStatusMap.get(entry.getKey()) == ModelStatus.HEALTHY) {
                healthyModels.add(entry.getValue());
            }
        }
        return healthyModels;
    }

    @Override
    public void updateModelStatus(String modelId, ModelStatus status) {
        ModelStatus oldStatus = modelStatusMap.put(modelId, status);
        if (oldStatus != status) {
            log.info("Model status updated: {} from {} to {}",
                    modelId, oldStatus, status);
        }
    }

    @Override
    public void recordUsage(String modelId, long latency, boolean success) {
        ModelMetrics metrics = modelMetricsMap.get(modelId);
        if (metrics != null) {
            metrics.recordRequest(latency, success);
        }
    }

    /**
     * 获取模型指标
     */
    public ModelMetrics getModelMetrics(String modelId) {
        return modelMetricsMap.get(modelId);
    }

    /**
     * 模型指标
     */
    public static class ModelMetrics {
        private final AtomicLong totalRequests = new AtomicLong(0);
        private final AtomicLong successfulRequests = new AtomicLong(0);
        private final AtomicLong failedRequests = new AtomicLong(0);
        private final AtomicLong totalLatency = new AtomicLong(0);

        public void recordRequest(long latency, boolean success) {
            totalRequests.incrementAndGet();
            totalLatency.addAndGet(latency);
            if (success) {
                successfulRequests.incrementAndGet();
            } else {
                failedRequests.incrementAndGet();
            }
        }

        public long getTotalRequests() {
            return totalRequests.get();
        }

        public long getSuccessfulRequests() {
            return successfulRequests.get();
        }

        public long getFailedRequests() {
            return failedRequests.get();
        }

        public double getAverageLatency() {
            long total = totalRequests.get();
            return total > 0 ? (double) totalLatency.get() / total : 0;
        }

        public double getSuccessRate() {
            long total = totalRequests.get();
            return total > 0 ? (double) successfulRequests.get() / total : 0;
        }
    }
}
