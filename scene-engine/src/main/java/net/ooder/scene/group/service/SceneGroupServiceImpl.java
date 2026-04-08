package net.ooder.scene.group.service;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.skill.knowledge.KnowledgeBindingManager;
import net.ooder.scene.llm.config.SceneLlmConfigInfo;
import net.ooder.scene.llm.config.SceneLlmConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * SceneGroup 服务实现类
 *
 * <p>封装场景组的核心业务流程，作为 OS 工程与底层 Manager 之间的桥梁。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Service
public class SceneGroupServiceImpl implements SceneGroupService {

    private static final Logger log = LoggerFactory.getLogger(SceneGroupServiceImpl.class);

    private final SceneGroupManager sceneGroupManager;
    private final KnowledgeBindingManager knowledgeBindingManager;
    private final SceneLlmConfigManager sceneLlmConfigManager;

    public SceneGroupServiceImpl(SceneGroupManager sceneGroupManager,
                                 KnowledgeBindingManager knowledgeBindingManager,
                                 SceneLlmConfigManager sceneLlmConfigManager) {
        this.sceneGroupManager = sceneGroupManager;
        this.knowledgeBindingManager = knowledgeBindingManager;
        this.sceneLlmConfigManager = sceneLlmConfigManager;
    }

    // ========== 场景组生命周期管理 ==========

    @Override
    public SceneGroup createSceneGroup(String sceneGroupId, String templateId, String creatorId,
                                       String name, String description) {
        if (sceneGroupId == null || sceneGroupId.isEmpty()) {
            throw new IllegalArgumentException("sceneGroupId is required");
        }

        // 检查是否已存在
        SceneGroup existing = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (existing != null) {
            log.warn("SceneGroup already exists: {}", sceneGroupId);
            return existing;
        }

        // 创建场景组
        SceneGroup group = sceneGroupManager.createSceneGroup(
            sceneGroupId,
            templateId != null ? templateId : "default",
            creatorId != null ? creatorId : "system",
            SceneGroup.CreatorType.USER
        );

        // 设置名称和描述
        if (name != null) {
            group.setName(name);
        }
        if (description != null) {
            group.setDescription(description);
        }

        // 自动激活
        group.activate();

        log.info("SceneGroup created: {}, name={}", sceneGroupId, name);
        return group;
    }

    @Override
    public SceneGroup getSceneGroup(String sceneGroupId) {
        return sceneGroupManager.getSceneGroup(sceneGroupId);
    }

    @Override
    public List<SceneGroup> getAllSceneGroups() {
        return sceneGroupManager.getAllSceneGroups();
    }

    @Override
    public List<SceneGroup> getSceneGroupsByTemplate(String templateId) {
        return sceneGroupManager.getSceneGroupsByTemplate(templateId);
    }

    @Override
    public boolean activateSceneGroup(String sceneGroupId) {
        boolean success = sceneGroupManager.activateSceneGroup(sceneGroupId);
        if (success) {
            log.info("SceneGroup activated: {}", sceneGroupId);
        }
        return success;
    }

    @Override
    public boolean suspendSceneGroup(String sceneGroupId) {
        boolean success = sceneGroupManager.suspendSceneGroup(sceneGroupId);
        if (success) {
            log.info("SceneGroup suspended: {}", sceneGroupId);
        }
        return success;
    }

    @Override
    public boolean archiveSceneGroup(String sceneGroupId) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            return false;
        }
        boolean success = group.archive();
        if (success) {
            log.info("SceneGroup archived: {}", sceneGroupId);
        }
        return success;
    }

    @Override
    public boolean restoreSceneGroup(String sceneGroupId) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            return false;
        }
        boolean success = group.restoreFromArchive();
        if (success) {
            log.info("SceneGroup restored: {}", sceneGroupId);
        }
        return success;
    }

    @Override
    public boolean destroySceneGroup(String sceneGroupId) {
        // 先清除关联数据
        try {
            knowledgeBindingManager.clearAllBindings(sceneGroupId);
            sceneLlmConfigManager.resetLlmConfig(sceneGroupId);
        } catch (Exception e) {
            log.warn("Failed to clear associated data for sceneGroup: {}", sceneGroupId, e);
        }

        boolean success = sceneGroupManager.destroySceneGroup(sceneGroupId);
        if (success) {
            log.info("SceneGroup destroyed: {}", sceneGroupId);
        }
        return success;
    }

    @Override
    public boolean updateSceneGroupInfo(String sceneGroupId, String name, String description) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            return false;
        }

        if (name != null) {
            group.setName(name);
        }
        if (description != null) {
            group.setDescription(description);
        }

        log.info("SceneGroup info updated: {}", sceneGroupId);
        return true;
    }

    // ========== 知识库绑定管理 ==========

    @Override
    public String bindKnowledgeBase(String sceneGroupId, String knowledgeBaseId, String knowledgeBaseName) {
        return bindKnowledgeBase(sceneGroupId, knowledgeBaseId, knowledgeBaseName, "SCENE_GROUP", 0);
    }

    @Override
    public String bindKnowledgeBase(String sceneGroupId, String knowledgeBaseId, String knowledgeBaseName,
                                    String scope, int priority) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            throw new IllegalArgumentException("SceneGroup not found: " + sceneGroupId);
        }

        KnowledgeBinding binding = new KnowledgeBinding();
        binding.setKnowledgeBaseId(knowledgeBaseId);
        binding.setKnowledgeBaseName(knowledgeBaseName);
        binding.setLayer(scope);
        binding.setPriority(priority);

        String bindingId = knowledgeBindingManager.bind(sceneGroupId, binding);

        group.addKnowledgeBinding(binding);

        log.info("Knowledge base bound: sceneGroupId={}, kbId={}, bindingId={}",
                sceneGroupId, knowledgeBaseId, bindingId);
        return bindingId;
    }

    @Override
    public boolean unbindKnowledgeBase(String sceneGroupId, String knowledgeBaseId) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        knowledgeBindingManager.unbind(sceneGroupId, knowledgeBaseId);

        if (group != null) {
            group.removeKnowledgeBinding(knowledgeBaseId);
        }

        log.info("Knowledge base unbound: sceneGroupId={}, kbId={}", sceneGroupId, knowledgeBaseId);
        return true;
    }

    @Override
    public List<KnowledgeBinding> getKnowledgeBindings(String sceneGroupId) {
        return knowledgeBindingManager.getBindings(sceneGroupId);
    }

    @Override
    public KnowledgeBinding getKnowledgeBinding(String sceneGroupId, String knowledgeBaseId) {
        return knowledgeBindingManager.getBinding(sceneGroupId, knowledgeBaseId);
    }

    @Override
    public boolean hasKnowledgeBinding(String sceneGroupId, String knowledgeBaseId) {
        return knowledgeBindingManager.hasBinding(sceneGroupId, knowledgeBaseId);
    }

    @Override
    public boolean setKnowledgeBindingPriority(String sceneGroupId, String knowledgeBaseId, int priority) {
        knowledgeBindingManager.setPriority(sceneGroupId, knowledgeBaseId, priority);
        return true;
    }

    @Override
    public boolean clearKnowledgeBindings(String sceneGroupId) {
        knowledgeBindingManager.clearAllBindings(sceneGroupId);

        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group != null) {
            for (KnowledgeBinding binding : group.getAllKnowledgeBindings()) {
                group.removeKnowledgeBinding(binding.getKbId());
            }
        }

        log.info("Knowledge bindings cleared: sceneGroupId={}", sceneGroupId);
        return true;
    }

    // ========== LLM 配置管理 ==========

    @Override
    public SceneLlmConfigInfo getLlmConfig(String sceneGroupId) {
        return sceneLlmConfigManager.getLlmConfig(sceneGroupId);
    }

    @Override
    public boolean setLlmConfig(String sceneGroupId, String provider, String model,
                                double temperature, int maxTokens) {
        SceneLlmConfigInfo config = new SceneLlmConfigInfo();
        config.setProvider(provider);
        config.setModel(model);
        config.setTemperature(temperature);
        config.setMaxTokens(maxTokens);

        return setLlmConfig(sceneGroupId, config);
    }

    @Override
    public boolean setLlmConfig(String sceneGroupId, SceneLlmConfigInfo config) {
        // 检查场景组是否存在
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            throw new IllegalArgumentException("SceneGroup not found: " + sceneGroupId);
        }

        // 设置配置
        sceneLlmConfigManager.setLlmConfig(sceneGroupId, config);

        // 同步到 SceneGroup 内存对象
        group.setLlmConfig("provider", config.getProvider());
        group.setLlmConfig("model", config.getModel());
        group.setLlmConfig("temperature", config.getTemperature());
        group.setLlmConfig("maxTokens", config.getMaxTokens());

        log.info("LLM config set: sceneGroupId={}, provider={}, model={}",
                sceneGroupId, config.getProvider(), config.getModel());
        return true;
    }

    @Override
    public boolean updateLlmConfig(String sceneGroupId, SceneLlmConfigInfo config) {
        sceneLlmConfigManager.updateLlmConfig(sceneGroupId, config);
        log.info("LLM config updated: sceneGroupId={}", sceneGroupId);
        return true;
    }

    @Override
    public boolean resetLlmConfig(String sceneGroupId) {
        sceneLlmConfigManager.resetLlmConfig(sceneGroupId);

        // 同步到 SceneGroup 内存对象
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group != null) {
            // 重置为默认值
            SceneLlmConfigInfo defaultConfig = sceneLlmConfigManager.getDefaultConfig();
            group.setLlmConfig("provider", defaultConfig.getProvider());
            group.setLlmConfig("model", defaultConfig.getModel());
            group.setLlmConfig("temperature", defaultConfig.getTemperature());
            group.setLlmConfig("maxTokens", defaultConfig.getMaxTokens());
        }

        log.info("LLM config reset: sceneGroupId={}", sceneGroupId);
        return true;
    }

    @Override
    public boolean hasCustomLlmConfig(String sceneGroupId) {
        return sceneLlmConfigManager.hasCustomConfig(sceneGroupId);
    }

    @Override
    public SceneLlmConfigInfo getDefaultLlmConfig() {
        return sceneLlmConfigManager.getDefaultConfig();
    }

    // ========== 扩展配置管理 ==========

    @Override
    public boolean setExtendedConfig(String sceneGroupId, String key, Object value) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            return false;
        }
        group.setConfig(key, value);
        return true;
    }

    @Override
    public Object getExtendedConfig(String sceneGroupId, String key) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            return null;
        }
        return group.getConfig(key);
    }

    @Override
    public Map<String, Object> getAllExtendedConfig(String sceneGroupId) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            return null;
        }
        return group.getAllConfig();
    }

    @Override
    public boolean removeExtendedConfig(String sceneGroupId, String key) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            return false;
        }
        // SceneGroup 没有直接移除配置的方法，通过设置 null 实现
        group.setConfig(key, null);
        return true;
    }

    // ========== 统计信息 ==========

    @Override
    public int getSceneGroupCount() {
        return sceneGroupManager.getAllSceneGroups().size();
    }

    @Override
    public int getKnowledgeBindingCount(String sceneGroupId) {
        return knowledgeBindingManager.getBindings(sceneGroupId).size();
    }

    @Override
    public int getTotalKnowledgeBindingCount() {
        return (int) knowledgeBindingManager.getTotalBindingCount();
    }

    @Override
    public int getLlmConfigCount() {
        // 需要底层实现支持
        if (sceneLlmConfigManager instanceof net.ooder.scene.llm.config.SqlSceneLlmConfigManager) {
            return ((net.ooder.scene.llm.config.SqlSceneLlmConfigManager) sceneLlmConfigManager).getConfigCount();
        }
        return 0;
    }
}
