package net.ooder.sdk.llm.pool;

import net.ooder.sdk.llm.adapter.model.ModelInfo;

import java.util.List;

/**
 * LLM Pool 管理器
 * 管理多个 LLM 模型实例
 */
public interface LlmPoolManager {

    /**
     * 注册模型到 Pool
     *
     * @param modelInfo 模型信息
     */
    void registerModel(ModelInfo modelInfo);

    /**
     * 注销模型
     *
     * @param modelId 模型ID
     */
    void unregisterModel(String modelId);

    /**
     * 获取模型
     *
     * @param modelId 模型ID
     * @return 模型信息
     */
    ModelInfo getModel(String modelId);

    /**
     * 获取所有可用模型
     *
     * @return 模型列表
     */
    List<ModelInfo> getAvailableModels();

    /**
     * 根据能力获取模型
     *
     * @param capability 能力要求
     * @return 模型列表
     */
    List<ModelInfo> getModelsByCapability(String capability);

    /**
     * 获取健康模型
     *
     * @return 健康模型列表
     */
    List<ModelInfo> getHealthyModels();

    /**
     * 更新模型状态
     *
     * @param modelId 模型ID
     * @param status  状态
     */
    void updateModelStatus(String modelId, ModelStatus status);

    /**
     * 记录模型使用
     *
     * @param modelId 模型ID
     * @param latency 延迟（毫秒）
     * @param success 是否成功
     */
    void recordUsage(String modelId, long latency, boolean success);

    /**
     * 模型状态枚举
     */
    enum ModelStatus {
        HEALTHY,    // 健康
        DEGRADED,   // 降级
        UNAVAILABLE // 不可用
    }
}
