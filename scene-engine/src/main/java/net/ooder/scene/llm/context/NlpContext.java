package net.ooder.scene.llm.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NLP 上下文
 * 
 * <p>封装 NLP 组件相关信息，支持场景定义中的 NLP 管理上下文功能。</p>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class NlpContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nlpContextId;
    private String componentType;
    private String moduleViewType;
    private String panelType;
    
    private Map<String, Object> moduleConfig;
    private Map<String, NlpComponentContext> componentContexts;
    private List<String> activeComponentIds;
    
    private String currentExpression;
    private Map<String, Object> expressionVariables;

    public NlpContext() {
        this.componentContexts = new HashMap<>();
        this.activeComponentIds = new ArrayList<>();
        this.expressionVariables = new HashMap<>();
        this.moduleConfig = new HashMap<>();
    }
    
    public NlpContext(String componentType, String moduleViewType) {
        this();
        this.componentType = componentType;
        this.moduleViewType = moduleViewType;
        this.nlpContextId = generateNlpContextId();
    }
    
    public void registerComponentContext(NlpComponentContext componentContext) {
        if (componentContext != null && componentContext.getComponentId() != null) {
            componentContexts.put(componentContext.getComponentId(), componentContext);
        }
    }
    
    public NlpComponentContext getComponentContext(String componentId) {
        return componentContexts != null ? componentContexts.get(componentId) : null;
    }
    
    public void setActiveComponent(String componentId) {
        if (activeComponentIds.contains(componentId)) {
            activeComponentIds.remove(componentId);
        }
        activeComponentIds.add(0, componentId);
        
        for (NlpComponentContext ctx : componentContexts.values()) {
            ctx.setActive(ctx.getComponentId().equals(componentId));
        }
    }
    
    public NlpComponentContext getActiveComponent() {
        if (activeComponentIds != null && !activeComponentIds.isEmpty()) {
            String activeId = activeComponentIds.get(0);
            return componentContexts.get(activeId);
        }
        return null;
    }
    
    public void setExpressionVariable(String name, Object value) {
        if (expressionVariables == null) {
            expressionVariables = new HashMap<>();
        }
        expressionVariables.put(name, value);
    }
    
    public Object getExpressionVariable(String name) {
        return expressionVariables != null ? expressionVariables.get(name) : null;
    }
    
    private String generateNlpContextId() {
        return "nlp-" + Long.toHexString(System.currentTimeMillis());
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getNlpContextId() { return nlpContextId; }
    public void setNlpContextId(String nlpContextId) { this.nlpContextId = nlpContextId; }
    
    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }
    
    public String getModuleViewType() { return moduleViewType; }
    public void setModuleViewType(String moduleViewType) { this.moduleViewType = moduleViewType; }
    
    public String getPanelType() { return panelType; }
    public void setPanelType(String panelType) { this.panelType = panelType; }
    
    public Map<String, Object> getModuleConfig() { return moduleConfig; }
    public void setModuleConfig(Map<String, Object> moduleConfig) { this.moduleConfig = moduleConfig; }
    
    public Map<String, NlpComponentContext> getComponentContexts() { return componentContexts; }
    public void setComponentContexts(Map<String, NlpComponentContext> componentContexts) { this.componentContexts = componentContexts; }
    
    public List<String> getActiveComponentIds() { return activeComponentIds; }
    public void setActiveComponentIds(List<String> activeComponentIds) { this.activeComponentIds = activeComponentIds; }
    
    public String getCurrentExpression() { return currentExpression; }
    public void setCurrentExpression(String currentExpression) { this.currentExpression = currentExpression; }
    
    public Map<String, Object> getExpressionVariables() { return expressionVariables; }
    public void setExpressionVariables(Map<String, Object> expressionVariables) { this.expressionVariables = expressionVariables; }
    
    public static class Builder {
        private NlpContext context = new NlpContext();
        
        public Builder nlpContextId(String nlpContextId) {
            context.setNlpContextId(nlpContextId);
            return this;
        }
        
        public Builder componentType(String componentType) {
            context.setComponentType(componentType);
            return this;
        }
        
        public Builder moduleViewType(String moduleViewType) {
            context.setModuleViewType(moduleViewType);
            return this;
        }
        
        public Builder panelType(String panelType) {
            context.setPanelType(panelType);
            return this;
        }
        
        public Builder moduleConfig(Map<String, Object> moduleConfig) {
            context.setModuleConfig(moduleConfig);
            return this;
        }
        
        public Builder componentContext(NlpComponentContext componentContext) {
            context.registerComponentContext(componentContext);
            return this;
        }
        
        public Builder expressionVariable(String name, Object value) {
            context.setExpressionVariable(name, value);
            return this;
        }
        
        public NlpContext build() {
            if (context.getNlpContextId() == null) {
                context.setNlpContextId(context.generateNlpContextId());
            }
            return context;
        }
    }
}
