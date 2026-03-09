package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 函数调用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionCall {
    
    /**
     * 函数名
     */
    private String name;
    
    /**
     * 参数JSON字符串
     */
    private String arguments;
}
