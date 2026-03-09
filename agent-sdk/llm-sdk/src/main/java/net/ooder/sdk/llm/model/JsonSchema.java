package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * JSON Schema定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonSchema {
    
    /**
     * 类型
     */
    private String type;
    
    /**
     * 属性定义
     */
    private Map<String, Object> properties;
    
    /**
     * 必填字段
     */
    private List<String> required;
}
