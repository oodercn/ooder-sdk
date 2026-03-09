package net.ooder.sdk.llm.context;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 上下文模板
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextTemplate {

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 必需变量列表
     */
    private List<String> requiredVariables;

    /**
     * 默认值
     */
    private Map<String, Object> defaultValues;

    /**
     * 模板版本
     */
    @Builder.Default
    private String version = "1.0.0";

    /**
     * 创建时间
     */
    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    /**
     * 更新时间
     */
    @Builder.Default
    private long updatedAt = System.currentTimeMillis();

    /**
     * 是否启用
     */
    @Builder.Default
    private boolean enabled = true;

    /**
     * 父模板ID（用于模板继承）
     */
    private String parentTemplateId;

    /**
     * 模板标签
     */
    private List<String> tags;
}
