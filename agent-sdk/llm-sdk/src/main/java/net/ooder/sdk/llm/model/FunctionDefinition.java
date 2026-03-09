package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 函数定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDefinition {
    
    /**
     * 函数名
     */
    private String name;
    
    /**
     * 函数描述
     */
    private String description;
    
    /**
     * 参数Schema
     */
    private JsonSchema parameters;
}
