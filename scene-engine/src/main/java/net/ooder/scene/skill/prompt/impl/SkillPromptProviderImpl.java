package net.ooder.scene.skill.prompt.impl;

import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.scene.skill.prompt.FallbackPromptService;
import net.ooder.scene.skill.prompt.PromptLoadOptions;
import net.ooder.scene.skill.prompt.PromptLoadOptions.PromptSource;
import net.ooder.scene.skill.prompt.SkillPromptProvider;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 技能提示词提供者实现
 *
 * <p>支持从多种来源加载提示词，并按优先级顺序尝试</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class SkillPromptProviderImpl implements SkillPromptProvider {

    private static final Logger log = LoggerFactory.getLogger(SkillPromptProviderImpl.class);

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}|\\{(\\w+)\\}");

    private final UnifiedSkillRegistry skillRegistry;
    private final FallbackPromptService fallbackService;
    private final Map<String, String> promptCache;
    private final int cacheMaxSize;

    private String defaultPrompt = "你是一个智能助手，帮助用户完成各种任务。";

    public SkillPromptProviderImpl(UnifiedSkillRegistry skillRegistry) {
        this(skillRegistry, new FallbackPromptService(), 100);
    }

    public SkillPromptProviderImpl(UnifiedSkillRegistry skillRegistry, FallbackPromptService fallbackService) {
        this(skillRegistry, fallbackService, 100);
    }

    public SkillPromptProviderImpl(UnifiedSkillRegistry skillRegistry, 
                                    FallbackPromptService fallbackService,
                                    int cacheMaxSize) {
        this.skillRegistry = skillRegistry;
        this.fallbackService = fallbackService;
        this.cacheMaxSize = cacheMaxSize;
        this.promptCache = new ConcurrentHashMap<>();
    }

    @Override
    public String loadFromFile(String skillId, String promptFile) {
        if (skillId == null || promptFile == null) {
            return null;
        }

        String cacheKey = skillId + ":" + promptFile;
        String cached = promptCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        try {
            SkillPackage skillPackage = getSkillPackage(skillId);
            if (skillPackage == null) {
                log.warn("Skill not found: {}", skillId);
                return null;
            }

            InputStream is = skillPackage.getResource(promptFile);
            if (is == null) {
                log.debug("Prompt file not found: {} for skill: {}", promptFile, skillId);
                return null;
            }

            String content = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            if (promptCache.size() < cacheMaxSize) {
                promptCache.put(cacheKey, content);
            }

            return content;

        } catch (Exception e) {
            log.error("Failed to load prompt file: {} for skill: {}", promptFile, skillId, e);
            return null;
        }
    }

    @Override
    public String loadFromConfig(String skillId) {
        if (skillId == null) {
            return null;
        }

        try {
            SkillPackage skillPackage = getSkillPackage(skillId);
            if (skillPackage == null) {
                return null;
            }

            Map<String, Object> metadata = skillPackage.getMetadata();

            if (metadata == null) {
                return null;
            }

            Object llmConfig = metadata.get("llmConfig");
            if (llmConfig instanceof Map) {
                Map<String, Object> llmConfigMap = (Map<String, Object>) llmConfig;
                Object systemPrompt = llmConfigMap.get("systemPrompt");
                if (systemPrompt instanceof String) {
                    return (String) systemPrompt;
                }
            }

            Object spec = metadata.get("spec");
            if (spec instanceof Map) {
                Map<String, Object> specMap = (Map<String, Object>) spec;
                Object llmConfigFromSpec = specMap.get("llmConfig");
                if (llmConfigFromSpec instanceof Map) {
                    Map<String, Object> llmConfigMap = (Map<String, Object>) llmConfigFromSpec;
                    Object systemPrompt = llmConfigMap.get("systemPrompt");
                    if (systemPrompt instanceof String) {
                        return (String) systemPrompt;
                    }
                }
            }

            return null;

        } catch (Exception e) {
            log.error("Failed to load prompt from config for skill: {}", skillId, e);
            return null;
        }
    }

    @Override
    public String interpolate(String template, Map<String, Object> variables) {
        if (template == null) {
            return null;
        }
        if (variables == null || variables.isEmpty()) {
            return template;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);

        while (matcher.find()) {
            String varName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            Object value = variables.get(varName);
            String replacement = value != null ? Matcher.quoteReplacement(String.valueOf(value)) : "";
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    @Override
    public String getSystemPrompt(String skillId, PromptLoadOptions options) {
        if (skillId == null) {
            return getDefaultPrompt(skillId);
        }

        if (options == null) {
            options = PromptLoadOptions.defaultOptions();
        }

        String cacheKey = "system:" + skillId;
        if (options.isEnableCache()) {
            String cached = promptCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        String prompt = null;
        for (PromptSource source : options.getSources()) {
            prompt = loadFromSource(skillId, source);
            if (prompt != null && !prompt.isEmpty()) {
                log.debug("Loaded system prompt for skill {} from source: {}", skillId, source);
                break;
            }
        }

        if (prompt == null || prompt.isEmpty()) {
            prompt = getDefaultPrompt(skillId);
        }

        if (options.getMaxLength() > 0 && prompt.length() > options.getMaxLength()) {
            prompt = prompt.substring(0, options.getMaxLength()) + "...";
        }

        if (options.isEnableCache() && promptCache.size() < cacheMaxSize) {
            promptCache.put(cacheKey, prompt);
        }

        return prompt;
    }

    @Override
    public String getRolePrompt(String skillId, String roleId) {
        if (roleId == null || roleId.isEmpty()) {
            return null;
        }

        String cacheKey = "role:" + skillId + ":" + roleId;
        String cached = promptCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        String promptFile = "prompts/roles/" + roleId + ".md";
        String prompt = loadFromFile(skillId, promptFile);

        if (prompt == null) {
            promptFile = "prompts/" + roleId + ".md";
            prompt = loadFromFile(skillId, promptFile);
        }

        if (prompt != null && promptCache.size() < cacheMaxSize) {
            promptCache.put(cacheKey, prompt);
        }

        return prompt;
    }

    @Override
    public String getDefaultPrompt(String skillId) {
        if (fallbackService != null) {
            return fallbackService.getFallbackPrompt(skillId);
        }
        return defaultPrompt;
    }

    public void setDefaultPrompt(String defaultPrompt) {
        this.defaultPrompt = defaultPrompt;
    }

    public void clearCache() {
        promptCache.clear();
    }

    public void clearCache(String skillId) {
        promptCache.keySet().removeIf(key -> key.contains(":" + skillId + ":") || key.endsWith(":" + skillId));
    }

    private SkillPackage getSkillPackage(String skillId) {
        if (skillRegistry == null) {
            return null;
        }
        try {
            return skillRegistry.getSkill(skillId).get();
        } catch (Exception e) {
            log.debug("Failed to get skill package: {}", skillId);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private String loadFromSource(String skillId, PromptSource source) {
        switch (source) {
            case CONFIG:
                return loadFromConfig(skillId);

            case FILE_MD:
                return loadFromFile(skillId, "prompts/system.md");

            case FILE_YAML:
                try {
                    SkillPackage skillPackage = getSkillPackage(skillId);
                    if (skillPackage != null) {
                        Map<String, Object> metadata = skillPackage.getMetadata();
                        if (metadata != null) {
                            Object spec = metadata.get("spec");
                            if (spec instanceof Map) {
                                Map<String, Object> specMap = (Map<String, Object>) spec;
                                Object llmConfig = specMap.get("llmConfig");
                                if (llmConfig instanceof Map) {
                                    Map<String, Object> llmConfigMap = (Map<String, Object>) llmConfig;
                                    Object systemPromptFile = llmConfigMap.get("systemPromptFile");
                                    if (systemPromptFile instanceof String) {
                                        return loadFromFile(skillId, (String) systemPromptFile);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("Failed to load prompt from systemPromptFile for skill: {}", skillId);
                }
                return null;

            case RAG:
                return null;

            case DEFAULT:
                return getDefaultPrompt(skillId);

            default:
                return null;
        }
    }
}
