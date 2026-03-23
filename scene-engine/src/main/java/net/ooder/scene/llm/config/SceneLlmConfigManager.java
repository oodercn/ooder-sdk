package net.ooder.scene.llm.config;

public interface SceneLlmConfigManager {

    SceneLlmConfigInfo getLlmConfig(String sceneGroupId);

    void setLlmConfig(String sceneGroupId, SceneLlmConfigInfo config);

    void updateLlmConfig(String sceneGroupId, SceneLlmConfigInfo config);

    void resetLlmConfig(String sceneGroupId);

    boolean hasCustomConfig(String sceneGroupId);

    SceneLlmConfigInfo getDefaultConfig();

    void setDefaultConfig(SceneLlmConfigInfo defaultConfig);
}
