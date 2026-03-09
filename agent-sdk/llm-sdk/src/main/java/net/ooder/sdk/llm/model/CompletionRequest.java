package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 补全请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionRequest {
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 提示文本
     */
    private String prompt;
    
    /**
     * 温度
     */
    private double temperature = 0.7;
    
    /**
     * 最大Token数
     */
    private int maxTokens = 256;
}
