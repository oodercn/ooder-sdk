package net.ooder.scene.skill.install.impl;

import net.ooder.scene.core.template.SceneTemplate;
import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.scene.skill.exception.SceneValidationException;
import net.ooder.scene.skill.install.*;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.KnowledgeBindingService;
import net.ooder.scene.skill.prompt.model.PromptDocument;
import net.ooder.scene.skill.prompt.rag.SkillPromptRagProvider;
import net.ooder.scene.skill.tool.ToolRegistry;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 技能安装处理器实现
 *
 * <p>负责技能安装时的配置构建和入库流程</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class SkillInstallProcessorImpl implements SkillInstallProcessor {

    private static final Logger log = LoggerFactory.getLogger(SkillInstallProcessorImpl.class);

    private final UnifiedSkillRegistry skillRegistry;
    private final KnowledgeBaseService knowledgeBaseService;
    private final KnowledgeBindingService knowledgeBindingService;
    private final SkillPromptRagProvider promptRagProvider;
    private final ToolRegistry toolRegistry;
    private final SceneConfigLoader sceneConfigLoader;

    private final Map<String, InstallInfo> installedSkills = new ConcurrentHashMap<>();
    private final Map<String, SkillRuntimeConfig> configs = new ConcurrentHashMap<>();
    private final Map<String, ConfigHistory> configHistories = new ConcurrentHashMap<>();
    private final Map<String, SceneTemplate> sceneTemplates = new ConcurrentHashMap<>();

    public SkillInstallProcessorImpl(UnifiedSkillRegistry skillRegistry,
                                      KnowledgeBaseService knowledgeBaseService,
                                      KnowledgeBindingService knowledgeBindingService,
                                      SkillPromptRagProvider promptRagProvider,
                                      ToolRegistry toolRegistry) {
        this.skillRegistry = skillRegistry;
        this.knowledgeBaseService = knowledgeBaseService;
        this.knowledgeBindingService = knowledgeBindingService;
        this.promptRagProvider = promptRagProvider;
        this.toolRegistry = toolRegistry;
        this.sceneConfigLoader = new SceneConfigLoader();
    }

    @Override
    public InstallResult install(InstallRequest request) {
        String skillId = request.getSkillId();
        log.info("Starting installation for skill: {}", skillId);

        try {
            SkillPackage skillPackage = getSkillPackage(skillId);
            if (skillPackage == null) {
                return InstallResult.failure(skillId, "Skill not found: " + skillId);
            }

            String skillForm = determineSkillForm(skillPackage);
            if ("SCENE".equals(skillForm)) {
                validateSceneSkill(skillId, skillPackage);
            }

            SkillRuntimeConfig config = buildConfig(skillId, skillPackage);

            buildAndStoreConfig(skillId, config);

            List<String> promptFiles = extractPromptFiles(skillPackage);
            indexPromptDocuments(skillId, promptFiles);

            List<CapabilityDef> capabilities = extractCapabilities(skillPackage);
            registerCapabilities(skillId, capabilities);

            InstallInfo installInfo = createInstallInfo(request, config);
            installedSkills.put(skillId, installInfo);

            InstallResult result = InstallResult.success(skillId);
            result.setConfigId(config.getConfigId());
            result.setCapabilities(capabilities.stream()
                    .map(CapabilityDef::getCapId)
                    .collect(Collectors.toList()));

            log.info("Installation completed for skill: {}", skillId);
            return result;

        } catch (SceneValidationException e) {
            log.error("Scene validation failed for skill: {}", skillId, e);
            return InstallResult.failure(skillId, "场景配置验证失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("Installation failed for skill: {}", skillId, e);
            return InstallResult.failure(skillId, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private String determineSkillForm(SkillPackage skillPackage) {
        Map<String, Object> metadata = skillPackage.getMetadata();
        if (metadata == null) {
            return "ATOMIC";
        }

        Object type = metadata.get("type");
        if (type instanceof String) {
            String typeStr = (String) type;
            if (typeStr.contains("scene") || typeStr.contains("SCENE")) {
                return "SCENE";
            }
        }

        Object spec = metadata.get("spec");
        if (spec instanceof Map) {
            Map<String, Object> specMap = (Map<String, Object>) spec;
            Object specType = specMap.get("type");
            if (specType instanceof String) {
                String typeStr = (String) specType;
                if (typeStr.contains("scene") || typeStr.contains("SCENE")) {
                    return "SCENE";
                }
            }

            if (specMap.containsKey("roles") || specMap.containsKey("activationSteps") || specMap.containsKey("menus")) {
                return "SCENE";
            }
        }

        return "ATOMIC";
    }

    private void validateSceneSkill(String skillId, SkillPackage skillPackage) {
        SceneTemplate template = sceneConfigLoader.loadSceneConfig(skillId, skillPackage);
        
        if (template == null) {
            throw new SceneValidationException(skillId, "SCENE_CONFIG_MISSING",
                "场景配置缺失: 技能包中未定义场景配置，且未找到场景模板。" +
                "请在 skill.yaml 中添加 spec.roles、spec.activationSteps、spec.menus 配置，" +
                "或在 src/main/resources/templates/ 目录创建场景模板文件。");
        }
        
        sceneConfigLoader.validateSceneConfig(skillId, template);
        
        sceneTemplates.put(skillId, template);
        
        log.info("[validateSceneSkill] Scene skill validated: {}", skillId);
    }

    @Override
    public void buildAndStoreConfig(String skillId, SkillRuntimeConfig config) {
        if (skillId == null || config == null) {
            return;
        }

        config.setUpdateTime(System.currentTimeMillis());
        configs.put(skillId, config);

        addToConfigHistory(skillId, config);

        log.info("Config stored for skill: {} (configId: {})", skillId, config.getConfigId());
    }

    @Override
    public void indexPromptDocuments(String skillId, List<String> promptFiles) {
        if (skillId == null || promptFiles == null || promptFiles.isEmpty()) {
            return;
        }

        List<PromptDocument> documents = new ArrayList<>();

        for (String promptFile : promptFiles) {
            try {
                PromptDocument doc = loadPromptDocument(skillId, promptFile);
                if (doc != null) {
                    documents.add(doc);
                }
            } catch (Exception e) {
                log.warn("Failed to load prompt file: {} for skill: {}", promptFile, skillId, e);
            }
        }

        if (!documents.isEmpty() && promptRagProvider != null) {
            promptRagProvider.indexPromptDocuments(skillId, documents);
            log.info("Indexed {} prompt documents for skill: {}", documents.size(), skillId);
        }
    }

    @Override
    public void registerCapabilities(String skillId, List<CapabilityDef> capabilities) {
        if (skillId == null || capabilities == null || capabilities.isEmpty()) {
            return;
        }

        for (CapabilityDef capability : capabilities) {
            log.info("Registered capability: {} for skill: {}", capability.getCapId(), skillId);
        }
    }

    @Override
    public ConfigHistory getConfigHistory(String skillId) {
        return configHistories.get(skillId);
    }

    @Override
    public boolean rollbackConfig(String skillId, int version) {
        ConfigHistory history = configHistories.get(skillId);
        if (history == null) {
            return false;
        }

        ConfigHistory.ConfigVersion targetVersion = history.getVersion(version);
        if (targetVersion == null) {
            return false;
        }

        log.info("Rollback config for skill: {} to version: {}", skillId, version);
        return true;
    }

    @Override
    public UninstallResult uninstall(String skillId) {
        if (!isInstalled(skillId)) {
            return UninstallResult.failure(skillId, "Skill not installed: " + skillId);
        }

        UninstallResult result = UninstallResult.success(skillId);

        try {
            SkillRuntimeConfig config = configs.remove(skillId);
            if (config != null) {
                result.setConfigRemoved(true);
            }

            if (promptRagProvider != null) {
                promptRagProvider.deletePromptIndex(skillId);
                result.setPromptIndexRemoved(true);
            }

            if (toolRegistry != null) {
                toolRegistry.clearSkillTools(skillId);
            }

            installedSkills.remove(skillId);

            log.info("Uninstall completed for skill: {}", skillId);

        } catch (Exception e) {
            log.error("Uninstall failed for skill: {}", skillId, e);
            return UninstallResult.failure(skillId, e.getMessage());
        }

        return result;
    }

    @Override
    public boolean isInstalled(String skillId) {
        return installedSkills.containsKey(skillId);
    }

    @Override
    public InstallInfo getInstallInfo(String skillId) {
        return installedSkills.get(skillId);
    }

    @SuppressWarnings("unchecked")
    private SkillRuntimeConfig buildConfig(String skillId, SkillPackage skillPackage) {
        SkillRuntimeConfig config = SkillRuntimeConfig.create(skillId, skillPackage.getVersion());

        Map<String, Object> metadata = skillPackage.getMetadata();
        if (metadata != null) {
            Object llmConfig = metadata.get("llmConfig");
            if (llmConfig instanceof Map) {
                config.setLlmConfig((Map<String, Object>) llmConfig);
            }

            Object spec = metadata.get("spec");
            if (spec instanceof Map) {
                Map<String, Object> specMap = (Map<String, Object>) spec;
                Object llmConfigFromSpec = specMap.get("llmConfig");
                if (llmConfigFromSpec instanceof Map) {
                    config.getLlmConfig().putAll((Map<String, Object>) llmConfigFromSpec);
                }

                Object knowledge = specMap.get("knowledge");
                if (knowledge instanceof Map) {
                    config.setKnowledgeConfig((Map<String, Object>) knowledge);
                }
            }
        }

        return config;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractPromptFiles(SkillPackage skillPackage) {
        List<String> promptFiles = new ArrayList<>();

        Map<String, Object> metadata = skillPackage.getMetadata();
        if (metadata == null) {
            return promptFiles;
        }

        Object spec = metadata.get("spec");
        if (spec instanceof Map) {
            Map<String, Object> specMap = (Map<String, Object>) spec;
            Object llmConfig = specMap.get("llmConfig");
            if (llmConfig instanceof Map) {
                Map<String, Object> llmConfigMap = (Map<String, Object>) llmConfig;
                Object systemPromptFile = llmConfigMap.get("systemPromptFile");
                if (systemPromptFile instanceof String) {
                    promptFiles.add((String) systemPromptFile);
                }
            }
        }

        promptFiles.add("prompts/system.md");

        return promptFiles;
    }

    @SuppressWarnings("unchecked")
    private List<CapabilityDef> extractCapabilities(SkillPackage skillPackage) {
        List<CapabilityDef> capabilities = new ArrayList<>();

        Map<String, Object> metadata = skillPackage.getMetadata();
        if (metadata == null) {
            return capabilities;
        }

        Object caps = metadata.get("capabilities");
        if (caps instanceof List) {
            for (Object cap : (List<?>) caps) {
                if (cap instanceof Map) {
                    Map<String, Object> capMap = (Map<String, Object>) cap;
                    CapabilityDef def = new CapabilityDef();
                    def.setCapId((String) capMap.get("capId"));
                    def.setName((String) capMap.get("name"));
                    def.setDescription((String) capMap.get("description"));
                    def.setType((String) capMap.get("type"));
                    capabilities.add(def);
                }
            }
        }

        return capabilities;
    }

    private PromptDocument loadPromptDocument(String skillId, String promptFile) {
        try {
            SkillPackage skillPackage = getSkillPackage(skillId);
            if (skillPackage == null) {
                return null;
            }

            InputStream is = skillPackage.getResource(promptFile);
            if (is == null) {
                return null;
            }

            String content = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            String docId = skillId + ":" + promptFile.replace("/", ":");
            PromptDocument.PromptType type = determinePromptType(promptFile);

            return PromptDocument.builder()
                    .docId(docId)
                    .skillId(skillId)
                    .type(type)
                    .content(content)
                    .build();

        } catch (Exception e) {
            log.error("Failed to load prompt document: {} for skill: {}", promptFile, skillId, e);
            return null;
        }
    }

    private PromptDocument.PromptType determinePromptType(String promptFile) {
        if (promptFile.contains("system")) {
            return PromptDocument.PromptType.SYSTEM;
        } else if (promptFile.contains("role")) {
            return PromptDocument.PromptType.ROLE;
        } else if (promptFile.contains("context")) {
            return PromptDocument.PromptType.CONTEXT;
        } else if (promptFile.contains("instruction")) {
            return PromptDocument.PromptType.INSTRUCTION;
        } else if (promptFile.contains("example")) {
            return PromptDocument.PromptType.EXAMPLE;
        }
        return PromptDocument.PromptType.SYSTEM;
    }

    private InstallInfo createInstallInfo(InstallRequest request, SkillRuntimeConfig config) {
        InstallInfo info = new InstallInfo();
        info.setSkillId(request.getSkillId());
        info.setVersion(request.getVersion() != null ? request.getVersion() : config.getVersion());
        info.setInstallTime(System.currentTimeMillis());
        info.setSource(request.getSource() != null ? request.getSource().getCode() : "local");
        info.setTargetSceneId(request.getTargetSceneId());
        return info;
    }

    private void addToConfigHistory(String skillId, SkillRuntimeConfig config) {
        ConfigHistory history = configHistories.computeIfAbsent(skillId, ConfigHistory::new);
        ConfigHistory.ConfigVersion version = new ConfigHistory.ConfigVersion();
        version.setVersion(history.getVersions().size() + 1);
        version.setConfigId(config.getConfigId());
        version.setTimestamp(System.currentTimeMillis());
        version.setDescription("Config update");
        history.addVersion(version);
        history.setCurrentVersion(version.getVersion());
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

    /**
     * 获取已安装技能的场景模板
     *
     * @param skillId 技能ID
     * @return 场景模板，如果不存在则返回 null
     */
    public SceneTemplate getSceneTemplate(String skillId) {
        return sceneTemplates.get(skillId);
    }
}
