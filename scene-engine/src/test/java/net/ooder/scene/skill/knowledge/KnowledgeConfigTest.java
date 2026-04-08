package net.ooder.scene.skill.knowledge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KnowledgeConfig 单元测试
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
class KnowledgeConfigTest {

    @Test
    void testDefaultValues() {
        // When
        KnowledgeConfig config = new KnowledgeConfig();

        // Then
        assertEquals(KnowledgeConfig.DEFAULT_TOP_K, config.getTopK());
        assertEquals(KnowledgeConfig.DEFAULT_THRESHOLD, config.getThreshold(), 0.001);
        assertEquals(KnowledgeConfig.DEFAULT_MAX_TOKENS, config.getMaxTokens());
        assertFalse(config.getCrossLayerSearch());
        assertTrue(config.getRerankEnabled());
    }

    @Test
    void testSetAndGetTopK() {
        // Given
        KnowledgeConfig config = new KnowledgeConfig();

        // When
        config.setTopK(10);

        // Then
        assertEquals(10, config.getTopK());
    }

    @Test
    void testSetTopKInvalidRange() {
        // Given
        KnowledgeConfig config = new KnowledgeConfig();

        // Then
        assertThrows(IllegalArgumentException.class, () -> config.setTopK(0));
        assertThrows(IllegalArgumentException.class, () -> config.setTopK(101));
    }

    @Test
    void testSetAndGetThreshold() {
        // Given
        KnowledgeConfig config = new KnowledgeConfig();

        // When
        config.setThreshold(0.85);

        // Then
        assertEquals(0.85, config.getThreshold(), 0.001);
    }

    @Test
    void testSetThresholdInvalidRange() {
        // Given
        KnowledgeConfig config = new KnowledgeConfig();

        // Then
        assertThrows(IllegalArgumentException.class, () -> config.setThreshold(-0.1));
        assertThrows(IllegalArgumentException.class, () -> config.setThreshold(1.1));
    }

    @Test
    void testSetAndGetMaxTokens() {
        // Given
        KnowledgeConfig config = new KnowledgeConfig();

        // When
        config.setMaxTokens(4000);

        // Then
        assertEquals(4000, config.getMaxTokens());
    }

    @Test
    void testSetMaxTokensInvalidRange() {
        // Given
        KnowledgeConfig config = new KnowledgeConfig();

        // Then
        assertThrows(IllegalArgumentException.class, () -> config.setMaxTokens(50));
        assertThrows(IllegalArgumentException.class, () -> config.setMaxTokens(9000));
    }

    @Test
    void testCrossLayerSearch() {
        // Given
        KnowledgeConfig config = new KnowledgeConfig();
        assertFalse(config.getCrossLayerSearch());

        // When
        config.setCrossLayerSearch(true);

        // Then
        assertTrue(config.getCrossLayerSearch());
    }

    @Test
    void testRerankEnabled() {
        // Given
        KnowledgeConfig config = new KnowledgeConfig();
        assertTrue(config.getRerankEnabled());

        // When
        config.setRerankEnabled(false);

        // Then
        assertFalse(config.getRerankEnabled());
    }

    @Test
    void testExtendedConfig() {
        // Given
        KnowledgeConfig config = new KnowledgeConfig();

        // When
        config.addExtendedConfig("custom_key", "custom_value");

        // Then
        assertEquals("custom_value", config.getExtendedConfig("custom_key"));
        assertNotNull(config.getExtendedConfig());
    }

    @Test
    void testDefaultConfigFactory() {
        // When
        KnowledgeConfig config = KnowledgeConfig.defaultConfig();

        // Then
        assertNotNull(config);
        assertEquals(KnowledgeConfig.DEFAULT_TOP_K, config.getTopK());
    }

    @Test
    void testLenientConfigFactory() {
        // When
        KnowledgeConfig config = KnowledgeConfig.lenientConfig();

        // Then
        assertEquals(10, config.getTopK());
        assertEquals(0.5, config.getThreshold(), 0.001);
    }

    @Test
    void testStrictConfigFactory() {
        // When
        KnowledgeConfig config = KnowledgeConfig.strictConfig();

        // Then
        assertEquals(3, config.getTopK());
        assertEquals(0.85, config.getThreshold(), 0.001);
    }

    @Test
    void testToString() {
        // Given
        KnowledgeConfig config = new KnowledgeConfig();

        // When
        String str = config.toString();

        // Then
        assertNotNull(str);
        assertTrue(str.contains("KnowledgeConfig"));
        assertTrue(str.contains("topK"));
    }
}
