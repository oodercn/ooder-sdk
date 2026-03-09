package net.ooder.sdk.drivers.llm;

import net.ooder.sdk.llm.model.ModelInfo;

import java.util.List;

/**
 * LLM 驱动工厂接口
 * 由 LLM-SDK 实现，Engine 调用
 */
public interface LlmDriverFactory {

    /**
     * 创建驱动
     *
     * @param modelInfo 模型信息
     * @return LLM 驱动
     */
    LlmDriver createDriver(ModelInfo modelInfo);

    /**
     * 注册驱动提供者
     *
     * @param provider 驱动提供者
     */
    void registerProvider(DriverProvider provider);

    /**
     * 获取支持的提供者列表
     *
     * @return 提供者列表
     */
    List<String> getSupportedProviders();

    /**
     * 检查是否支持指定模型
     *
     * @param modelId 模型ID
     * @return 是否支持
     */
    boolean supports(String modelId);

    /**
     * 驱动提供者接口
     */
    interface DriverProvider {
        /**
         * 获取提供者名称
         */
        String getName();

        /**
         * 支持的模型列表
         */
        List<String> getSupportedModels();

        /**
         * 创建驱动
         */
        LlmDriver createDriver(ModelInfo modelInfo);

        /**
         * 检查是否支持指定模型
         */
        boolean supports(String modelId);
    }
}
