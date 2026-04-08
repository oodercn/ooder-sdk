package net.ooder.scene.llm.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SqlSceneLlmConfigManager 单元测试
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
class SqlSceneLlmConfigManagerTest {

    private SqlSceneLlmConfigManager manager;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        String dbPath = tempDir.resolve("test-llm.db").toString();
        manager = new SqlSceneLlmConfigManager("jdbc:sqlite:" + dbPath);
        manager.initialize();
    }

    @AfterEach
    void tearDown() {
        if (manager != null) {
            manager.close();
        }
    }

    @Test
    void testSetAndGetLlmConfig() {
        // Given
        String sceneGroupId = "group-001";
        SceneLlmConfigInfo config = new SceneLlmConfigInfo();
        config.setProvider("openai");
        config.setModel("gpt-4");
        config.setTemperature(0.8);
        config.setMaxTokens(4096);
        config.setTimeout(30000L);

        // When
        manager.setLlmConfig(sceneGroupId, config);

        // Then
        SceneLlmConfigInfo retrieved = manager.getLlmConfig(sceneGroupId);
        assertNotNull(retrieved);
        assertEquals("openai", retrieved.getProvider());
        assertEquals("gpt-4", retrieved.getModel());
        assertEquals(0.8, retrieved.getTemperature(), 0.001);
        assertEquals(4096, retrieved.getMaxTokens());
    }

    @Test
    void testHasCustomConfig() {
        // Given
        String sceneGroupId = "group-002";
        assertFalse(manager.hasCustomConfig(sceneGroupId));

        // When
        SceneLlmConfigInfo config = createConfig("anthropic", "claude-3");
        manager.setLlmConfig(sceneGroupId, config);

        // Then
        assertTrue(manager.hasCustomConfig(sceneGroupId));
    }

    @Test
    void testResetLlmConfig() {
        // Given
        String sceneGroupId = "group-003";
        manager.setLlmConfig(sceneGroupId, createConfig("openai", "gpt-3.5"));
        assertTrue(manager.hasCustomConfig(sceneGroupId));

        // When
        manager.resetLlmConfig(sceneGroupId);

        // Then
        assertFalse(manager.hasCustomConfig(sceneGroupId));
    }

    @Test
    void testGetDefaultConfig() {
        // When
        SceneLlmConfigInfo defaultConfig = manager.getDefaultConfig();

        // Then
        assertNotNull(defaultConfig);
        assertNotNull(defaultConfig.getProvider());
        assertNotNull(defaultConfig.getModel());
    }

    @Test
    void testSetDefaultConfig() {
        // Given
        SceneLlmConfigInfo newDefault = new SceneLlmConfigInfo();
        newDefault.setProvider("custom-provider");
        newDefault.setModel("custom-model");
        newDefault.setTemperature(0.5);
        newDefault.setMaxTokens(2048);
        newDefault.setTimeout(60000L);

        // When
        manager.setDefaultConfig(newDefault);

        // Then
        SceneLlmConfigInfo retrieved = manager.getDefaultConfig();
        assertEquals("custom-provider", retrieved.getProvider());
        assertEquals("custom-model", retrieved.getModel());
    }

    @Test
    void testConfigWithExtensions() {
        // Given
        String sceneGroupId = "group-004";
        SceneLlmConfigInfo config = createConfig("openai", "gpt-4");
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("top_p", 0.9);
        extensions.put("frequency_penalty", 0.5);
        config.setExtensions(extensions);

        // When
        manager.setLlmConfig(sceneGroupId, config);

        // Then
        SceneLlmConfigInfo retrieved = manager.getLlmConfig(sceneGroupId);
        assertNotNull(retrieved.getExtensions());
        // 注意：JSON序列化后Double可能变成BigDecimal，使用Number比较
        Number topP = (Number) retrieved.getExtensions().get("top_p");
        assertEquals(0.9, topP.doubleValue(), 0.001);
    }

    @Test
    void testGetConfigCount() {
        // Given
        assertEquals(0, manager.getConfigCount());

        // When
        manager.setLlmConfig("group-005", createConfig("openai", "gpt-4"));
        manager.setLlmConfig("group-006", createConfig("anthropic", "claude-3"));

        // Then
        assertEquals(2, manager.getConfigCount());
    }

    @Test
    void testUpdateLlmConfig() {
        // Given
        String sceneGroupId = "group-007";
        SceneLlmConfigInfo initial = createConfig("openai", "gpt-3.5");
        initial.setTemperature(0.7);
        manager.setLlmConfig(sceneGroupId, initial);

        // When
        SceneLlmConfigInfo update = new SceneLlmConfigInfo();
        update.setTemperature(0.9);
        manager.updateLlmConfig(sceneGroupId, update);

        // Then
        SceneLlmConfigInfo retrieved = manager.getLlmConfig(sceneGroupId);
        assertEquals(0.9, retrieved.getTemperature(), 0.001);
        // 其他字段应保持不变
        assertEquals("openai", retrieved.getProvider());
    }

    private SceneLlmConfigInfo createConfig(String provider, String model) {
        SceneLlmConfigInfo config = new SceneLlmConfigInfo();
        config.setProvider(provider);
        config.setModel(model);
        config.setTemperature(0.7);
        config.setMaxTokens(2048);
        config.setTimeout(60000L);
        return config;
    }
}
