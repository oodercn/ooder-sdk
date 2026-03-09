package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 工具调用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCall {
    
    /**
     * 调用ID
     */
    private String id;
    
    /**
     * 类型 (function)
     */
    private String type;
    
    /**
     * 函数调用
     */
    private FunctionCall function;
}
