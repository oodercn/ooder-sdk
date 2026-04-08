package net.ooder.scene.knowledge;

import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.skill.knowledge.persistence.SqlKnowledgeBindingManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqlKnowledgeBindingManagerTest {

    private SqlKnowledgeBindingManager manager;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        String dbPath = tempDir.resolve("test-kb.db").toString();
        manager = new SqlKnowledgeBindingManager("jdbc:sqlite:" + dbPath);
        manager.initialize();
    }

    @AfterEach
    void tearDown() {
        if (manager != null) {
            manager.close();
        }
    }

    @Test
    void testBindKnowledgeBase() {
        String sceneGroupId = "group-001";
        KnowledgeBinding binding = new KnowledgeBinding();
        binding.setKnowledgeBaseId("kb-001");
        binding.setKnowledgeBaseName("测试知识库");
        binding.setLayer("SCENE_GROUP");
        binding.setPriority(10);

        String bindingId = manager.bind(sceneGroupId, binding);

        assertNotNull(bindingId);
        assertTrue(manager.hasBinding(sceneGroupId, "kb-001"));
    }

    @Test
    void testUnbindKnowledgeBase() {
        String sceneGroupId = "group-002";
        KnowledgeBinding binding = createBinding("kb-002", "知识库2");
        manager.bind(sceneGroupId, binding);
        assertTrue(manager.hasBinding(sceneGroupId, "kb-002"));

        manager.unbind(sceneGroupId, "kb-002");

        assertFalse(manager.hasBinding(sceneGroupId, "kb-002"));
    }

    @Test
    void testGetKnowledgeBindings() {
        String sceneGroupId = "group-003";
        manager.bind(sceneGroupId, createBinding("kb-003", "知识库3"));
        manager.bind(sceneGroupId, createBinding("kb-004", "知识库4"));

        List<KnowledgeBinding> bindings = manager.getBindings(sceneGroupId);

        assertEquals(2, bindings.size());
    }

    @Test
    void testSetBindingPriority() {
        String sceneGroupId = "group-004";
        KnowledgeBinding binding = createBinding("kb-005", "知识库5");
        binding.setPriority(5);
        manager.bind(sceneGroupId, binding);

        manager.setPriority(sceneGroupId, "kb-005", 20);

        KnowledgeBinding updated = manager.getBinding(sceneGroupId, "kb-005");
        assertEquals(20, updated.getPriority());
    }

    @Test
    void testClearAllBindings() {
        String sceneGroupId = "group-005";
        manager.bind(sceneGroupId, createBinding("kb-006", "知识库6"));
        manager.bind(sceneGroupId, createBinding("kb-007", "知识库7"));
        assertEquals(2, manager.getBindings(sceneGroupId).size());

        manager.clearAllBindings(sceneGroupId);

        assertEquals(0, manager.getBindings(sceneGroupId).size());
    }

    @Test
    void testGetBindingCount() {
        String sceneGroupId = "group-006";
        manager.bind(sceneGroupId, createBinding("kb-008", "知识库8"));
        manager.bind(sceneGroupId, createBinding("kb-009", "知识库9"));

        assertEquals(2, manager.getBindingCount(sceneGroupId));
        assertEquals(2, manager.getTotalBindingCount());
    }

    private KnowledgeBinding createBinding(String kbId, String kbName) {
        KnowledgeBinding binding = new KnowledgeBinding();
        binding.setKnowledgeBaseId(kbId);
        binding.setKnowledgeBaseName(kbName);
        binding.setLayer("SCENE_GROUP");
        return binding;
    }
}