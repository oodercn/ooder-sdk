package net.ooder.scene.llm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SceneLlmConfigManagerImpl implements SceneLlmConfigManager {

    private static final Logger log = LoggerFactory.getLogger(SceneLlmConfigManagerImpl.class);

    private final Map<String, SceneLlmConfigInfo> configStore = new ConcurrentHashMap<>();
    private SceneLlmConfigInfo defaultConfig;

    public SceneLlmConfigManagerImpl() {
        this.defaultConfig = createDefaultConfig();
    }

    @Override
    public SceneLlmConfigInfo getLlmConfig(String sceneGroupId) {
        if (sceneGroupId == null) {
            return getDefaultConfig();
        }

        SceneLlmConfigInfo config = configStore.get(sceneGroupId);
        if (config == null) {
            log.debug("No custom config for sceneGroup: {}, using default", sceneGroupId);
            return getDefaultConfig();
        }

        return config;
    }

    @Override
    public void setLlmConfig(String sceneGroupId, SceneLlmConfigInfo config) {
        if (sceneGroupId == null) {
            throw new IllegalArgumentException("sceneGroupId is required");
        }

        if (config == null) {
            resetLlmConfig(sceneGroupId);
            return;
        }

        config.setSceneGroupId(sceneGroupId);
        config.setConfigId(UUID.randomUUID().toString().replace("-", ""));

        configStore.put(sceneGroupId, config);

        log.info("LLM config set: sceneGroupId={}, provider={}, model={}",
                sceneGroupId, config.getProvider(), config.getModel());
    }

    @Override
    public void updateLlmConfig(String sceneGroupId, SceneLlmConfigInfo config) {
        if (sceneGroupId == null || config == null) {
            return;
        }

        SceneLlmConfigInfo existing = configStore.get(sceneGroupId);
        if (existing != null) {
            if (config.getProvider() != null) {
                existing.setProvider(config.getProvider());
            }
            if (config.getModel() != null) {
                existing.setModel(config.getModel());
            }
            if (config.getTemperature() > 0) {
                existing.setTemperature(config.getTemperature());
            }
            if (config.getMaxTokens() > 0) {
                existing.setMaxTokens(config.getMaxTokens());
            }
            if (config.getTimeout() > 0) {
                existing.setTimeout(config.getTimeout());
            }
            if (config.getExtensions() != null) {
                existing.getExtensions().putAll(config.getExtensions());
            }

            log.info("LLM config updated: sceneGroupId={}", sceneGroupId);
        } else {
            setLlmConfig(sceneGroupId, config);
        }
    }

    @Override
    public void resetLlmConfig(String sceneGroupId) {
        if (sceneGroupId == null) {
            return;
        }

        SceneLlmConfigInfo removed = configStore.remove(sceneGroupId);
        if (removed != null) {
            log.info("LLM config reset: sceneGroupId={}", sceneGroupId);
        }
    }

    @Override
    public boolean hasCustomConfig(String sceneGroupId) {
        return sceneGroupId != null && configStore.containsKey(sceneGroupId);
    }

    @Override
    public SceneLlmConfigInfo getDefaultConfig() {
        SceneLlmConfigInfo copy = new SceneLlmConfigInfo();
        copy.setConfigId(defaultConfig.getConfigId());
        copy.setProvider(defaultConfig.getProvider());
        copy.setModel(defaultConfig.getModel());
        copy.setTemperature(defaultConfig.getTemperature());
        copy.setMaxTokens(defaultConfig.getMaxTokens());
        copy.setTimeout(defaultConfig.getTimeout());
        if (defaultConfig.getExtensions() != null) {
            copy.getExtensions().putAll(defaultConfig.getExtensions());
        }
        return copy;
    }

    @Override
    public void setDefaultConfig(SceneLlmConfigInfo defaultConfig) {
        if (defaultConfig != null) {
            this.defaultConfig = defaultConfig;
            log.info("Default LLM config updated: provider={}, model={}",
                    defaultConfig.getProvider(), defaultConfig.getModel());
        }
    }

    private SceneLlmConfigInfo createDefaultConfig() {
        SceneLlmConfigInfo config = new SceneLlmConfigInfo();
        config.setConfigId("default-llm-config");
        config.setProvider("openai");
        config.setModel("gpt-4");
        config.setTemperature(0.7);
        config.setMaxTokens(2048);
        config.setTimeout(60000);
        return config;
    }

    public int getConfigCount() {
        return configStore.size();
    }

    public void clearAllConfigs() {
        configStore.clear();
        log.info("All LLM configs cleared");
    }
}
