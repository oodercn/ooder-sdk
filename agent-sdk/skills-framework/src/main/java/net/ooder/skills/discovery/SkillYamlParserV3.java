package net.ooder.skills.discovery;

import net.ooder.skills.api.*;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Skill YAML 解析器 v3.0
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class SkillYamlParserV3 {

    private final Yaml yaml = new Yaml();

    /**
     * 解析 v3.0 YAML 格式
     */
    public SkillManifestV3 parse(InputStream yamlStream) {
        Map<String, Object> data = yaml.load(yamlStream);
        
        SkillManifestV3 manifest = new SkillManifestV3();
        
        // 基础信息
        manifest.setId(getString(data, "id"));
        manifest.setName(getString(data, "name"));
        manifest.setVersion(getString(data, "version"));
        manifest.setDescription(getString(data, "description"));
        
        // v3.0 核心字段
        manifest.setForm(parseSkillForm(getString(data, "form")));
        manifest.setCategory(parseSkillCategory(getString(data, "category")));
        manifest.setPurposes(parsePurposes(getStringList(data, "purposes")));
        
        // 场景类型（仅 SCENE 时）
        if (manifest.getForm() == SkillForm.SCENE) {
            manifest.setSceneType(parseSceneType(getString(data, "sceneType")));
            manifest.setSceneConfig(parseSceneConfig(data));
        }
        
        // 能力列表
        manifest.setCapabilities(parseCapabilities(data));
        
        // 协作配置
        manifest.setCollaboration(parseCollaboration(data));
        
        // 入口点
        manifest.setEntryPoint(getString(data, "entryPoint"));
        
        return manifest;
    }

    private SkillForm parseSkillForm(String value) {
        if (value == null) return SkillForm.STANDALONE;
        return SkillForm.valueOf(value.toUpperCase());
    }

    private SceneType parseSceneType(String value) {
        if (value == null) return null;
        return SceneType.valueOf(value.toUpperCase());
    }

    private SkillCategory parseSkillCategory(String value) {
        if (value == null) return SkillCategory.OTHER;
        return SkillCategory.fromCode(value);
    }

    private Set<ServicePurpose> parsePurposes(List<String> values) {
        if (values == null) return Collections.emptySet();
        return values.stream()
            .map(v -> ServicePurpose.fromCode(v.toLowerCase()))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    private SceneConfigV3 parseSceneConfig(Map<String, Object> data) {
        Map<String, Object> sceneConfigData = getMap(data, "sceneConfig");
        if (sceneConfigData == null) return null;
        
        SceneConfigV3 config = new SceneConfigV3();
        config.setSceneType(parseSceneType(getString(sceneConfigData, "sceneType")));
        config.setOrchestration(parseOrchestration(getMap(sceneConfigData, "orchestration")));
        config.setInternalCapabilities(getStringList(sceneConfigData, "internalCapabilities"));
        config.setChildSkills(getStringList(sceneConfigData, "childSkills"));
        
        return config;
    }

    private OrchestrationConfig parseOrchestration(Map<String, Object> data) {
        if (data == null) return null;
        
        OrchestrationConfig config = new OrchestrationConfig();
        config.setType(getString(data, "type"));
        config.setSteps(parseOrchestrationSteps(getMapList(data, "steps")));
        config.setParams(getMap(data, "params"));
        
        return config;
    }

    private List<OrchestrationStep> parseOrchestrationSteps(List<Map<String, Object>> stepsData) {
        if (stepsData == null) return Collections.emptyList();
        
        return stepsData.stream().map(stepData -> {
            OrchestrationStep step = new OrchestrationStep();
            step.setId(getString(stepData, "id"));
            step.setName(getString(stepData, "name"));
            step.setCapabilityId(getString(stepData, "capabilityId"));
            step.setType(getString(stepData, "type"));
            step.setParams(getMap(stepData, "params"));
            step.setNextStep(getString(stepData, "nextStep"));
            step.setCondition(getString(stepData, "condition"));
            return step;
        }).collect(Collectors.toList());
    }

    private List<CapabilityDeclaration> parseCapabilities(Map<String, Object> data) {
        List<Map<String, Object>> capsData = getMapList(data, "capabilities");
        if (capsData == null) return Collections.emptyList();
        
        return capsData.stream().map(capData -> {
            CapabilityDeclaration cap = new CapabilityDeclaration();
            cap.setId(getString(capData, "id"));
            cap.setName(getString(capData, "name"));
            cap.setType(getString(capData, "type"));
            cap.setDescription(getString(capData, "description"));
            cap.setParameters(getMap(capData, "parameters"));
            return cap;
        }).collect(Collectors.toList());
    }

    private CollaborationDeclaration parseCollaboration(Map<String, Object> data) {
        Map<String, Object> collabData = getMap(data, "collaboration");
        if (collabData == null) return null;
        
        CollaborationDeclaration collab = new CollaborationDeclaration();
        collab.setExternallyAccessible(getBoolean(collabData, "externallyAccessible", false));
        collab.setExposedCapabilities(getStringList(collabData, "exposedCapabilities"));
        collab.setExternalDependencies(parseExternalDependencies(getMapList(collabData, "externalDependencies")));
        
        return collab;
    }

    private List<ExternalDependency> parseExternalDependencies(List<Map<String, Object>> depsData) {
        if (depsData == null) return Collections.emptyList();
        
        return depsData.stream().map(depData -> {
            ExternalDependency dep = new ExternalDependency();
            dep.setSkillId(getString(depData, "skillId"));
            dep.setCapabilityId(getString(depData, "capabilityId"));
            dep.setVersion(getString(depData, "version"));
            dep.setRequired(getBoolean(depData, "required", true));
            return dep;
        }).collect(Collectors.toList());
    }

    // ========== 辅助方法 ==========

    private String getString(Map<String, Object> data, String key) {
        if (data == null) return null;
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> data, String key) {
        if (data == null) return null;
        Object value = data.get(key);
        return value instanceof List ? (List<String>) value : null;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getMapList(Map<String, Object> data, String key) {
        if (data == null) return null;
        Object value = data.get(key);
        return value instanceof List ? (List<Map<String, Object>>) value : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> data, String key) {
        if (data == null) return null;
        Object value = data.get(key);
        return value instanceof Map ? (Map<String, Object>) value : null;
    }

    private boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        if (data == null) return defaultValue;
        Object value = data.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }
}
