package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 统一的模型信息
 * 包含LLM模型的元数据和能力描述
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelInfo {
    
    /**
     * 模型ID
     */
    private String modelId;
    
    /**
     * 模型名称
     */
    private String modelName;
    
    /**
     * 模型版本
     */
    private String modelVersion;
    
    /**
     * 提供商
     */
    private String provider;
    
    /**
     * 最大上下文长度
     */
    private Integer maxTokens;
    
    /**
     * 上下文长度（兼容旧字段）
     */
    private Integer contextLength;
    
    /**
     * 能力列表
     */
    private List<String> capabilities;
    
    /**
     * 每Token成本
     */
    private Double costPerToken;
    
    /**
     * 模型描述
     */
    private String description;
    
    /**
     * 是否支持流式
     */
    private Boolean supportsStreaming;
    
    /**
     * 是否支持嵌入
     */
    private Boolean supportsEmbeddings;
    
    /**
     * 是否支持函数调用
     */
    private Boolean supportsFunctionCalling;
    
    /**
     * 获取有效的最大Token数
     */
    public Integer getEffectiveMaxTokens() {
        if (maxTokens != null) {
            return maxTokens;
        }
        return contextLength;
    }
}
