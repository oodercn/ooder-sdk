package net.ooder.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AIServer 配置类
 * 用于配置 AIServer 相关的 Bean
 */
@Configuration
public class AIServerConfig {
    
    @Autowired
    private AIServerProperties aiServerProperties;
    
    /**
     * 获取 AIServer 配置属性实例
     * @return AIServerProperties AIServer 配置属性
     */
    @Bean
    public AIServerProperties aiServerProperties() {
        return new AIServerProperties();
    }
    
    /**
     * 示例：获取完整的模型路径
     * @return String 完整的模型路径
     */
    public String getFullModelPath() {
        return aiServerProperties.getModel().getPath() + "/" + 
               aiServerProperties.getModel().getType() + "/" + 
               aiServerProperties.getModel().getVersion();
    }
    
    /**
     * 示例：获取 GPU 设备 ID 数组
     * @return int[] GPU 设备 ID 数组
     */
    public int[] getGpuIds() {
        String gpuIdsStr = aiServerProperties.getResource().getGpuIds();
        String[] gpuIdStrings = gpuIdsStr.split(",");
        int[] gpuIds = new int[gpuIdStrings.length];
        for (int i = 0; i < gpuIdStrings.length; i++) {
            gpuIds[i] = Integer.parseInt(gpuIdStrings[i].trim());
        }
        return gpuIds;
    }
    
    /**
     * 示例：检查是否使用 GPU
     * @return boolean 是否使用 GPU
     */
    public boolean isUsingGpu() {
        String gpuIdsStr = aiServerProperties.getResource().getGpuIds();
        return gpuIdsStr != null && !gpuIdsStr.trim().equals("-1");
    }
}