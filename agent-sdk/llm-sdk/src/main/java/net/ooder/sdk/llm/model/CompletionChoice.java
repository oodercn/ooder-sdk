package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 补全选项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionChoice {
    
    /**
     * 文本内容
     */
    private String text;
    
    /**
     * 索引
     */
    private int index;
    
    /**
     * 结束原因
     */
    private String finishReason;
}
