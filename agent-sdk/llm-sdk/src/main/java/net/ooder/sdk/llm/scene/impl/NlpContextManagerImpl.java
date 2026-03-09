package net.ooder.sdk.llm.scene.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.scene.NlpComponentContext;
import net.ooder.sdk.llm.scene.NlpContext;
import net.ooder.sdk.llm.scene.NlpContextManager;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NLP 上下文管理器实现
 */
@Slf4j
public class NlpContextManagerImpl implements NlpContextManager {

    private final Map<String, NlpContext> nlpContextStore = new ConcurrentHashMap<>();
    private final SpelExpressionParser expressionParser = new SpelExpressionParser();

    @Override
    public NlpContext initializeNlpContext(String componentType, NlpContext.ModuleViewType moduleViewType, Object config) {
        String nlpContextId = generateNlpContextId();

        NlpContext nlpContext = NlpContext.builder()
                .nlpContextId(nlpContextId)
                .componentType(componentType)
                .moduleViewType(moduleViewType)
                .config(config instanceof Map ? (Map<String, Object>) config : null)
                .build();

        nlpContextStore.put(nlpContextId, nlpContext);
        log.info("NLP context initialized: {} for componentType: {}, moduleViewType: {}",
                nlpContextId, componentType, moduleViewType);
        return nlpContext;
    }

    @Override
    public void registerComponentContext(String nlpContextId, NlpComponentContext componentContext) {
        NlpContext nlpContext = nlpContextStore.get(nlpContextId);
        if (nlpContext == null) {
            throw new IllegalArgumentException("NLP context not found: " + nlpContextId);
        }

        nlpContext.addComponentContext(componentContext);
        log.debug("Component context registered: {} to NLP context: {}",
                componentContext.getComponentId(), nlpContextId);
    }

    @Override
    public NlpComponentContext getComponentContext(String nlpContextId, String componentId) {
        NlpContext nlpContext = nlpContextStore.get(nlpContextId);
        if (nlpContext == null) {
            return null;
        }
        return nlpContext.getComponentContext(componentId);
    }

    @Override
    public void setActiveComponent(String nlpContextId, String componentId) {
        NlpContext nlpContext = nlpContextStore.get(nlpContextId);
        if (nlpContext == null) {
            throw new IllegalArgumentException("NLP context not found: " + nlpContextId);
        }

        // 验证组件是否存在
        NlpComponentContext component = nlpContext.getComponentContext(componentId);
        if (component == null) {
            throw new IllegalArgumentException("Component not found: " + componentId);
        }

        nlpContext.setActiveComponent(componentId);
        component.setStatus(NlpComponentContext.ComponentStatus.ACTIVE);
        log.debug("Active component set: {} for NLP context: {}", componentId, nlpContextId);
    }

    @Override
    public NlpComponentContext getActiveComponent(String nlpContextId) {
        NlpContext nlpContext = nlpContextStore.get(nlpContextId);
        if (nlpContext == null) {
            return null;
        }
        return nlpContext.getActiveComponent();
    }

    @Override
    public void setExpressionVariable(String nlpContextId, String name, Object value) {
        NlpContext nlpContext = nlpContextStore.get(nlpContextId);
        if (nlpContext == null) {
            throw new IllegalArgumentException("NLP context not found: " + nlpContextId);
        }

        nlpContext.getExpressionVariables().put(name, value);
        log.debug("Expression variable set: {} = {} for NLP context: {}", name, value, nlpContextId);
    }

    @Override
    public Object evaluateExpression(String nlpContextId, String expression) {
        NlpContext nlpContext = nlpContextStore.get(nlpContextId);
        if (nlpContext == null) {
            throw new IllegalArgumentException("NLP context not found: " + nlpContextId);
        }

        try {
            StandardEvaluationContext evalContext = new StandardEvaluationContext();

            // 设置变量
            evalContext.setVariables(nlpContext.getExpressionVariables());

            // 设置活跃组件属性
            NlpComponentContext activeComponent = nlpContext.getActiveComponent();
            if (activeComponent != null) {
                evalContext.setVariable("activeComponent", activeComponent);
                if (activeComponent.getProperties() != null) {
                    for (Map.Entry<String, Object> entry : activeComponent.getProperties().entrySet()) {
                        evalContext.setVariable(entry.getKey(), entry.getValue());
                    }
                }
            }

            // 解析并求值表达式
            return expressionParser.parseExpression(expression).getValue(evalContext);
        } catch (Exception e) {
            log.error("Failed to evaluate expression: {} for NLP context: {}", expression, nlpContextId, e);
            return null;
        }
    }

    @Override
    public NlpContext getNlpContext(String nlpContextId) {
        return nlpContextStore.get(nlpContextId);
    }

    @Override
    public void updateNlpContext(NlpContext nlpContext) {
        if (nlpContext == null || nlpContext.getNlpContextId() == null) {
            throw new IllegalArgumentException("NLP context and ID cannot be null");
        }
        nlpContextStore.put(nlpContext.getNlpContextId(), nlpContext);
    }

    @Override
    public void destroyNlpContext(String nlpContextId) {
        NlpContext nlpContext = nlpContextStore.remove(nlpContextId);
        if (nlpContext != null) {
            // 标记所有组件为销毁状态
            for (NlpComponentContext component : nlpContext.getComponentContexts()) {
                component.setStatus(NlpComponentContext.ComponentStatus.DESTROYED);
            }
            log.info("NLP context destroyed: {}", nlpContextId);
        }
    }

    /**
     * 生成 NLP 上下文 ID
     */
    private String generateNlpContextId() {
        return "nlp_" + UUID.randomUUID().toString().replace("-", "");
    }
}
