package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Token使用量
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenUsage {
    
    /**
     * Prompt Token数
     */
    private Integer promptTokens;
    
    /**
     * 完成Token数
     */
    private Integer completionTokens;
    
    /**
     * 总Token数
     */
    private Integer totalTokens;
}
