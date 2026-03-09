package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 嵌入响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingResponse {
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 嵌入数据列表
     */
    private List<EmbeddingData> data;
    
    /**
     * Token使用量
     */
    private TokenUsage usage;
}
