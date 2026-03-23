package net.ooder.scene.llm.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SceneLlmConfigManagerTest {

    private SceneLlmConfigManager configManager;

    @BeforeEach
    void setUp() {
        configManager = new SceneLlmConfigManagerImpl();
    }

    @Test
    void testSetLlmConfig() {
        SceneLlmConfigInfo config = new SceneLlmConfigInfo("scene-001");
        config.setProvider("openai");
        config.setModel("gpt-4");
        config.setTemperature(0.8);
        config.setMaxTokens(4096);

        configManager.setLlmConfig("scene-001", config);

        SceneLlmConfigInfo retrieved = configManager.getLlmConfig("scene-001");

        assertNotNull(retrieved);
        assertEquals("openai", retrieved.getProvider());
        assertEquals("gpt-4", retrieved.getModel());
        assertEquals(0.8, retrieved.getTemperature());
        assertEquals(4096, retrieved.getMaxTokens());
    }

    @Test
    void testGetLlmConfigWithDefault() {
        SceneLlmConfigInfo config = configManager.getLlmConfig("scene-001");

        assertNotNull(config);
        assertNotNull(config.getConfigId());
    }

    @Test
    void testResetLlmConfig() {
        SceneLlmConfigInfo config = new SceneLlmConfigInfo("scene-001");
        config.setProvider("azure");
        config.setModel("gpt-35-turbo");
        configManager.setLlmConfig("scene-001", config);

        assertNotNull(configManager.getLlmConfig("scene-001"));

        configManager.resetLlmConfig("scene-001");

        SceneLlmConfigInfo reset = configManager.getLlmConfig("scene-001");
        assertEquals("openai", reset.getProvider());
        assertEquals("gpt-4", reset.getModel());
    }

    @Test
    void testHasLlmConfig() {
        assertFalse(configManager.hasLlmConfig("scene-001"));

        configManager.setLlmConfig("scene-001", new SceneLlmConfigInfo("scene-001"));

        assertTrue(configManager.hasLlmConfig("scene-001"));
    }

    @Test
    void testRemoveLlmConfig() {
        configManager.setLlmConfig("scene-001", new SceneLlmConfigInfo("scene-001"));

        assertTrue(configManager.hasLlmConfig("scene-001"));

        configManager.removeLlmConfig("scene-001");

        assertFalse(configManager.hasLlmConfig("scene-001"));
    }

    @Test
    void testSetDefaultConfig() {
        SceneLlmConfigInfo defaultConfig = new SceneLlmConfigInfo();
        defaultConfig.setProvider("anthropic");
        defaultConfig.setModel("claude-3-opus");
        defaultConfig.setTemperature(0.5);

        configManager.setDefaultConfig(defaultConfig);

        SceneLlmConfigInfo config = configManager.getLlmConfig("new-scene");

        assertEquals("anthropic", config.getProvider());
        assertEquals("claude-3-opus", config.getModel());
        assertEquals(0.5, config.getTemperature());
    }

    @Test
    void testExtensions() {
        SceneLlmConfigInfo config = new SceneLlmConfigInfo("scene-001");
        config.addExtension("topP", 0.9);
        config.addExtension("frequencyPenalty", 0.5);

        configManager.setLlmConfig("scene-001", config);

        SceneLlmConfigInfo retrieved = configManager.getLlmConfig("scene-001");

        assertEquals(0.9, retrieved.getExtension("topP"));
        assertEquals(0.5, retrieved.getExtension("frequencyPenalty"));
    }
}
