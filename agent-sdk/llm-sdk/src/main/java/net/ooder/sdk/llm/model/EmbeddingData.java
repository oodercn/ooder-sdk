package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 嵌入数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingData {
    
    /**
     * 索引
     */
    private int index;
    
    /**
     * 嵌入向量
     */
    private float[] embedding;
}
