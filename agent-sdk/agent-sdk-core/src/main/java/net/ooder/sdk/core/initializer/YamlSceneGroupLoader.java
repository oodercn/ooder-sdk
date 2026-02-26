package net.ooder.sdk.core.initializer;

import net.ooder.sdk.api.initializer.NexusInitializer;
import net.ooder.sdk.api.initializer.SceneGroupLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 基于 YAML 配置文件的 SceneGroupLoader 实现
 */
public class YamlSceneGroupLoader implements SceneGroupLoader {

    private static final Logger logger = LoggerFactory.getLogger(YamlSceneGroupLoader.class);

    private final Path configPath;
    private final Map<String, NexusInitializer.SceneGroupInfo> sceneGroups = new LinkedHashMap<>();

    public YamlSceneGroupLoader(Path configPath) {
        this.configPath = configPath;
        loadFromYaml();
    }

    public YamlSceneGroupLoader() {
        this(Paths.get("config", "scene-groups.yaml"));
    }

    @Override
    public List<NexusInitializer.SceneGroupInfo> loadSceneGroups() {
        return new ArrayList<>(sceneGroups.values());
    }

    @Override
    public NexusInitializer.SceneGroupInfo loadSceneGroup(String sceneGroupId) {
        return sceneGroups.get(sceneGroupId);
    }

    @Override
    public void reload() {
        sceneGroups.clear();
        loadFromYaml();
    }

    @Override
    public void registerSceneGroup(NexusInitializer.SceneGroupInfo groupInfo) {
        sceneGroups.put(groupInfo.getSceneGroupId(), groupInfo);
        saveToYaml();
    }

    @Override
    public void unregisterSceneGroup(String sceneGroupId) {
        sceneGroups.remove(sceneGroupId);
        saveToYaml();
    }

    @SuppressWarnings("unchecked")
    private void loadFromYaml() {
        if (!Files.exists(configPath)) {
            logger.warn("Scene group config not found: {}, using defaults", configPath);
            createDefaultConfig();
            return;
        }

        try (InputStream is = Files.newInputStream(configPath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(is);

            if (config == null || !config.containsKey("sceneGroups")) {
                createDefaultConfig();
                return;
            }

            List<Map<String, Object>> groups = (List<Map<String, Object>>) config.get("sceneGroups");
            for (Map<String, Object> groupMap : groups) {
                NexusInitializer.SceneGroupInfo group = parseSceneGroup(groupMap);
                sceneGroups.put(group.getSceneGroupId(), group);
            }

            logger.info("Loaded {} scene groups from {}", sceneGroups.size(), configPath);

        } catch (Exception e) {
            logger.error("Failed to load scene groups from {}", configPath, e);
            createDefaultConfig();
        }
    }

    @SuppressWarnings("unchecked")
    private NexusInitializer.SceneGroupInfo parseSceneGroup(Map<String, Object> map) {
        NexusInitializer.SceneGroupInfo group = new NexusInitializer.SceneGroupInfo();
        group.setSceneGroupId((String) map.get("id"));
        group.setName((String) map.get("name"));
        group.setDescription((String) map.get("description"));
        group.setType((String) map.get("type"));

        if (map.containsKey("scenes")) {
            List<Map<String, Object>> scenes = (List<Map<String, Object>>) map.get("scenes");
            List<NexusInitializer.SceneInfo> sceneInfos = new ArrayList<>();
            for (Map<String, Object> sceneMap : scenes) {
                NexusInitializer.SceneInfo scene = new NexusInitializer.SceneInfo();
                scene.setSceneId((String) sceneMap.get("id"));
                scene.setName((String) sceneMap.get("name"));
                scene.setDescription((String) sceneMap.get("description"));
                scene.setRequired(Boolean.TRUE.equals(sceneMap.get("required")));
                if (sceneMap.containsKey("skills")) {
                    scene.setSkills((List<String>) sceneMap.get("skills"));
                }
                sceneInfos.add(scene);
            }
            group.setScenes(sceneInfos);
        }

        if (map.containsKey("requiredSkills")) {
            group.setRequiredSkills((List<String>) map.get("requiredSkills"));
        }

        if (map.containsKey("estimatedTime")) {
            group.setEstimatedTime(((Number) map.get("estimatedTime")).longValue());
        }

        if (map.containsKey("estimatedSize")) {
            group.setEstimatedSize(((Number) map.get("estimatedSize")).longValue());
        }

        return group;
    }

    private void createDefaultConfig() {
        // 企业版
        NexusInitializer.SceneGroupInfo enterprise = new NexusInitializer.SceneGroupInfo();
        enterprise.setSceneGroupId("enterprise-nexus");
        enterprise.setName("Enterprise Nexus Platform");
        enterprise.setDescription("企业级Nexus平台，包含完整的协作功能");
        enterprise.setType("enterprise");
        enterprise.setScenes(Arrays.asList(
            createSceneInfo("vfs", "Virtual File System", "虚拟文件系统", true),
            createSceneInfo("auth", "Authentication", "认证授权", true),
            createSceneInfo("msg", "Message Service", "消息服务", false),
            createSceneInfo("workflow", "Workflow Management", "工作流管理", false),
            createSceneInfo("a2ui", "UI Generation", "UI生成", false)
        ));
        enterprise.setRequiredSkills(Arrays.asList(
            "skill-vfs", "skill-user-auth", "skill-msg", "skill-workflow", "skill-a2ui"
        ));
        enterprise.setEstimatedTime(300000);
        enterprise.setEstimatedSize(104857600);
        sceneGroups.put(enterprise.getSceneGroupId(), enterprise);

        // 个人版
        NexusInitializer.SceneGroupInfo personal = new NexusInitializer.SceneGroupInfo();
        personal.setSceneGroupId("personal-nexus");
        personal.setName("Personal Nexus");
        personal.setDescription("个人版Nexus，适合个人用户");
        personal.setType("personal");
        personal.setScenes(Arrays.asList(
            createSceneInfo("vfs", "Virtual File System", "虚拟文件系统", true),
            createSceneInfo("auth", "Authentication", "认证授权", true),
            createSceneInfo("a2ui", "UI Generation", "UI生成", false)
        ));
        personal.setRequiredSkills(Arrays.asList(
            "skill-vfs", "skill-user-auth", "skill-a2ui"
        ));
        personal.setEstimatedTime(120000);
        personal.setEstimatedSize(52428800);
        sceneGroups.put(personal.getSceneGroupId(), personal);

        saveToYaml();
    }

    private NexusInitializer.SceneInfo createSceneInfo(String sceneId, String name, String description, boolean required) {
        NexusInitializer.SceneInfo info = new NexusInitializer.SceneInfo();
        info.setSceneId(sceneId);
        info.setName(name);
        info.setDescription(description);
        info.setRequired(required);
        return info;
    }

    private void saveToYaml() {
        try {
            Files.createDirectories(configPath.getParent());

            StringBuilder yaml = new StringBuilder();
            yaml.append("# Scene Groups Configuration\n");
            yaml.append("# Auto-generated, modify with caution\n\n");
            yaml.append("sceneGroups:\n");

            for (NexusInitializer.SceneGroupInfo group : sceneGroups.values()) {
                yaml.append("  - id: ").append(group.getSceneGroupId()).append("\n");
                yaml.append("    name: ").append(group.getName()).append("\n");
                yaml.append("    description: ").append(group.getDescription()).append("\n");
                yaml.append("    type: ").append(group.getType()).append("\n");

                yaml.append("    scenes:\n");
                for (NexusInitializer.SceneInfo scene : group.getScenes()) {
                    yaml.append("      - id: ").append(scene.getSceneId()).append("\n");
                    yaml.append("        name: ").append(scene.getName()).append("\n");
                    yaml.append("        description: ").append(scene.getDescription()).append("\n");
                    yaml.append("        required: ").append(scene.isRequired()).append("\n");
                }

                yaml.append("    requiredSkills:\n");
                for (String skill : group.getRequiredSkills()) {
                    yaml.append("      - ").append(skill).append("\n");
                }

                yaml.append("    estimatedTime: ").append(group.getEstimatedTime()).append("\n");
                yaml.append("    estimatedSize: ").append(group.getEstimatedSize()).append("\n");
            }

            Files.write(configPath, yaml.toString().getBytes());

        } catch (Exception e) {
            logger.error("Failed to save scene groups to {}", configPath, e);
        }
    }
}
