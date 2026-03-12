package net.ooder.skills.config.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.skills.config.ConfigNode;
import net.ooder.skills.config.SdkConfigStorage;
import net.ooder.skills.config.exception.ConfigException;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

/**
 * JSON 配置存储实现
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public class JsonConfigStorageImpl implements SdkConfigStorage {

    private final Path configRoot;
    private final ObjectMapper objectMapper;

    public JsonConfigStorageImpl(String configRootPath) {
        this.configRoot = Paths.get(configRootPath);
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        initDirectories();
    }

    private void initDirectories() {
        try {
            Files.createDirectories(configRoot);
            Files.createDirectories(configRoot.resolve("profiles"));
            Files.createDirectories(configRoot.resolve("runtime"));
        } catch (IOException e) {
            throw new ConfigException("Failed to create config directories", e);
        }
    }

    @Override
    public ConfigNode loadSystemConfig() {
        Path configFile = configRoot.resolve("system-config.json");
        if (!Files.exists(configFile)) {
            return new ConfigNode();
        }
        return readJson(configFile);
    }

    @Override
    public ConfigNode loadProfile(String profileName) {
        Path profileFile = configRoot.resolve("profiles/" + profileName + ".json");
        if (!Files.exists(profileFile)) {
            throw new ConfigException("Profile not found: " + profileName);
        }
        return readJson(profileFile);
    }

    @Override
    public ConfigNode loadSkillConfig(String skillId) {
        Path configFile = configRoot.resolve("runtime/skill-" + skillId + ".json");
        if (!Files.exists(configFile)) {
            return null;
        }
        return readJson(configFile);
    }

    @Override
    public ConfigNode loadSceneConfig(String sceneId) {
        Path configFile = configRoot.resolve("runtime/scene-" + sceneId + ".json");
        if (!Files.exists(configFile)) {
            return null;
        }
        return readJson(configFile);
    }

    @Override
    public ConfigNode loadInternalSkillConfig(String sceneId, String skillId) {
        Path configFile = configRoot.resolve("runtime/internal-" + sceneId + "-" + skillId + ".json");
        if (!Files.exists(configFile)) {
            return null;
        }
        return readJson(configFile);
    }

    @Override
    public void saveSystemConfig(ConfigNode config) {
        Path configFile = configRoot.resolve("system-config.json");
        writeJson(configFile, config);
    }

    @Override
    public void saveSkillConfig(String skillId, ConfigNode config) {
        Path configFile = configRoot.resolve("runtime/skill-" + skillId + ".json");
        writeJson(configFile, config);
    }

    @Override
    public void saveSceneConfig(String sceneId, ConfigNode config) {
        Path configFile = configRoot.resolve("runtime/scene-" + sceneId + ".json");
        writeJson(configFile, config);
    }

    @Override
    public void saveInternalSkillConfig(String sceneId, String skillId, ConfigNode config) {
        Path configFile = configRoot.resolve("runtime/internal-" + sceneId + "-" + skillId + ".json");
        writeJson(configFile, config);
    }

    @Override
    public void deleteConfig(String targetType, String targetId) {
        Path configFile = getConfigPath(targetType, targetId);
        try {
            Files.deleteIfExists(configFile);
        } catch (IOException e) {
            throw new ConfigException("Failed to delete config: " + targetType + "/" + targetId, e);
        }
    }

    @Override
    public boolean exists(String targetType, String targetId) {
        Path configFile = getConfigPath(targetType, targetId);
        return Files.exists(configFile);
    }

    private Path getConfigPath(String targetType, String targetId) {
        switch (targetType) {
            case "system":
                return configRoot.resolve("system-config.json");
            case "profile":
                return configRoot.resolve("profiles/" + targetId + ".json");
            case "skill":
                return configRoot.resolve("runtime/skill-" + targetId + ".json");
            case "scene":
                return configRoot.resolve("runtime/scene-" + targetId + ".json");
            case "internal_skill":
                String[] parts = targetId.split(":");
                return configRoot.resolve("runtime/internal-" + parts[0] + "-" + parts[1] + ".json");
            default:
                throw new ConfigException("Unknown target type: " + targetType);
        }
    }

    private ConfigNode readJson(Path path) {
        try {
            Map<String, Object> data = objectMapper.readValue(
                    path.toFile(),
                    new TypeReference<Map<String, Object>>() {}
            );
            return new ConfigNode(data);
        } catch (IOException e) {
            throw new ConfigException("Failed to load config: " + path, e);
        }
    }

    private void writeJson(Path path, ConfigNode config) {
        try {
            Files.createDirectories(path.getParent());
            objectMapper.writeValue(path.toFile(), config.getData());
        } catch (IOException e) {
            throw new ConfigException("Failed to save config: " + path, e);
        }
    }
}
