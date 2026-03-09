package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 嵌入请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingRequest {
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 输入文本列表
     */
    private List<String> input;
    
    /**
     * 编码格式
     */
    private String encodingFormat;
}
