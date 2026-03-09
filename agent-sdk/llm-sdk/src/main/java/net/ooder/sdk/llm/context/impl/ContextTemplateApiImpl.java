package net.ooder.sdk.llm.context.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.context.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 上下文模板 API 实现
 */
@Slf4j
public class ContextTemplateApiImpl implements ContextTemplateApi {

    private final Map<String, ContextTemplate> templateRegistry = new ConcurrentHashMap<>();
    private final Map<String, ContextInstance> instanceRegistry = new ConcurrentHashMap<>();
    private final Pattern variablePattern = Pattern.compile("\\$\\{([^}]+)\\}");

    @Override
    public void registerTemplate(String templateId, ContextTemplate template) {
        if (templateId == null || template == null) {
            throw new IllegalArgumentException("TemplateId and template cannot be null");
        }

        template.setTemplateId(templateId);
        template.setUpdatedAt(System.currentTimeMillis());
        templateRegistry.put(templateId, template);
        log.info("Context template registered: {}", templateId);
    }

    @Override
    public void unregisterTemplate(String templateId) {
        if (templateId != null) {
            templateRegistry.remove(templateId);
            log.info("Context template unregistered: {}", templateId);
        }
    }

    @Override
    public ContextTemplate getTemplate(String templateId) {
        return templateRegistry.get(templateId);
    }

    @Override
    public boolean hasTemplate(String templateId) {
        return templateRegistry.containsKey(templateId);
    }

    @Override
    public List<ContextTemplate> listTemplates() {
        return new ArrayList<>(templateRegistry.values());
    }

    @Override
    public String renderContext(String templateId, Map<String, Object> variables) {
        ContextTemplate template = templateRegistry.get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }

        return renderSystemPrompt(templateId, variables);
    }

    @Override
    public String renderSystemPrompt(String templateId, Map<String, Object> variables) {
        ContextTemplate template = templateRegistry.get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }

        String systemPrompt = template.getSystemPrompt();
        if (systemPrompt == null) {
            return "";
        }

        // 合并变量（默认值 + 传入变量）
        Map<String, Object> mergedVariables = new HashMap<>();
        if (template.getDefaultValues() != null) {
            mergedVariables.putAll(template.getDefaultValues());
        }
        if (variables != null) {
            mergedVariables.putAll(variables);
        }

        // 检查必需变量
        if (template.getRequiredVariables() != null) {
            for (String requiredVar : template.getRequiredVariables()) {
                if (!mergedVariables.containsKey(requiredVar)) {
                    throw new IllegalArgumentException("Required variable missing: " + requiredVar);
                }
            }
        }

        // 渲染变量
        return renderTemplate(systemPrompt, mergedVariables);
    }

    @Override
    public ContextInstance createContext(String templateId, String sessionId) {
        return createContext(templateId, sessionId, null);
    }

    @Override
    public ContextInstance createContext(String templateId, String sessionId, Map<String, Object> variables) {
        ContextTemplate template = templateRegistry.get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }

        // 生成实例ID
        String instanceId = generateInstanceId(templateId, sessionId);

        // 合并变量
        Map<String, Object> mergedVariables = new HashMap<>();
        if (template.getDefaultValues() != null) {
            mergedVariables.putAll(template.getDefaultValues());
        }
        if (variables != null) {
            mergedVariables.putAll(variables);
        }

        // 检查必需变量
        if (template.getRequiredVariables() != null) {
            for (String requiredVar : template.getRequiredVariables()) {
                if (!mergedVariables.containsKey(requiredVar)) {
                    throw new IllegalArgumentException("Required variable missing: " + requiredVar);
                }
            }
        }

        // 创建实例
        ContextInstance instance = ContextInstance.builder()
                .instanceId(instanceId)
                .templateId(templateId)
                .sessionId(sessionId)
                .variables(mergedVariables)
                .build();

        // 添加系统消息
        String systemPrompt = renderSystemPrompt(templateId, mergedVariables);
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            instance.addMessage("system", systemPrompt);
        }

        instanceRegistry.put(instanceId, instance);
        log.info("Context instance created: {} for template: {}", instanceId, templateId);

        return instance;
    }

    @Override
    public ContextInstance getContextInstance(String instanceId) {
        return instanceRegistry.get(instanceId);
    }

    @Override
    public void updateContextInstance(String instanceId, ContextInstance instance) {
        if (instanceId == null || instance == null) {
            throw new IllegalArgumentException("InstanceId and instance cannot be null");
        }
        instance.setLastUpdatedAt(System.currentTimeMillis());
        instanceRegistry.put(instanceId, instance);
    }

    @Override
    public void deleteContextInstance(String instanceId) {
        if (instanceId != null) {
            instanceRegistry.remove(instanceId);
            log.info("Context instance deleted: {}", instanceId);
        }
    }

    @Override
    public ContextInstance cloneContextInstance(String sourceInstanceId, String newSessionId) {
        ContextInstance sourceInstance = instanceRegistry.get(sourceInstanceId);
        if (sourceInstance == null) {
            throw new IllegalArgumentException("Source instance not found: " + sourceInstanceId);
        }

        // 生成新实例ID
        String newInstanceId = generateInstanceId(sourceInstance.getTemplateId(), newSessionId);

        // 克隆实例
        ContextInstance clonedInstance = ContextInstance.builder()
                .instanceId(newInstanceId)
                .templateId(sourceInstance.getTemplateId())
                .sessionId(newSessionId)
                .variables(new HashMap<>(sourceInstance.getVariables()))
                .messages(new ArrayList<>(sourceInstance.getMessages()))
                .metadata(new HashMap<>(sourceInstance.getMetadata()))
                .build();

        instanceRegistry.put(newInstanceId, clonedInstance);
        log.info("Context instance cloned: {} from {}", newInstanceId, sourceInstanceId);

        return clonedInstance;
    }

    /**
     * 渲染模板
     */
    private String renderTemplate(String template, Map<String, Object> variables) {
        if (template == null || variables == null) {
            return template;
        }

        Matcher matcher = variablePattern.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);
            Object varValue = variables.get(varName);
            String replacement = varValue != null ? varValue.toString() : "";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 生成实例ID
     */
    private String generateInstanceId(String templateId, String sessionId) {
        return templateId + "_" + sessionId + "_" + System.currentTimeMillis();
    }
}
