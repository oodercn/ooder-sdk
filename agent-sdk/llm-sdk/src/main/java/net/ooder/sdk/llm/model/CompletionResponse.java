package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 补全响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionResponse {
    
    /**
     * 响应ID
     */
    private String id;
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 选项列表
     */
    private List<CompletionChoice> choices;
    
    /**
     * Token使用量
     */
    private TokenUsage usage;
}
