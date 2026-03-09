package net.ooder.sdk.llm.scene;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NLP上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NlpContext {

    /**
     * NLP上下文ID
     */
    private String nlpContextId;

    /**
     * 组件类型
     */
    private String componentType;

    /**
     * ModuleView类型
     */
    private ModuleViewType moduleViewType;

    /**
     * 组件上下文列表
     */
    @Builder.Default
    private List<NlpComponentContext> componentContexts = new ArrayList<>();

    /**
     * 活跃组件ID
     */
    private String activeComponentId;

    /**
     * 表达式变量
     */
    @Builder.Default
    private Map<String, Object> expressionVariables = new HashMap<>();

    /**
     * NLP配置
     */
    private Map<String, Object> config;

    /**
     * ModuleView类型枚举
     */
    public enum ModuleViewType {
        LAYOUTCONFIG,
        FORMCONFIG,
        GRIDCONFIG,
        TREECONFIG,
        GALLERYCONFIG,
        BLOCKCONFIG,
        DIVCONFIG,
        GROUPCONFIG,
        PANELCONFIG
    }

    /**
     * 获取活跃组件
     */
    public NlpComponentContext getActiveComponent() {
        if (activeComponentId == null) {
            return null;
        }
        return componentContexts.stream()
                .filter(c -> c.getComponentId().equals(activeComponentId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 设置活跃组件
     */
    public void setActiveComponent(String componentId) {
        this.activeComponentId = componentId;
    }

    /**
     * 添加组件上下文
     */
    public void addComponentContext(NlpComponentContext componentContext) {
        componentContexts.add(componentContext);
    }

    /**
     * 获取组件上下文
     */
    public NlpComponentContext getComponentContext(String componentId) {
        return componentContexts.stream()
                .filter(c -> c.getComponentId().equals(componentId))
                .findFirst()
                .orElse(null);
    }
}
