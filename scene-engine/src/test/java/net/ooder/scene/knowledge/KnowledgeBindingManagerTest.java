package net.ooder.scene.knowledge;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnowledgeBindingManagerTest {

    private KnowledgeBindingManager bindingManager;
    private SceneGroupManager sceneGroupManager;

    @BeforeEach
    void setUp() {
        sceneGroupManager = new SceneGroupManager();
        SceneGroup group = new SceneGroup("scene-001", "template-001", "user-001", SceneGroup.CreatorType.USER);
        sceneGroupManager.createSceneGroup(group);
        
        bindingManager = new KnowledgeBindingManagerImpl(sceneGroupManager);
    }

    @Test
    void testBindKnowledgeBase() {
        KnowledgeBindingInfo binding = new KnowledgeBindingInfo("scene-001", "kb-001");
        binding.setKnowledgeBaseName("Test Knowledge Base");
        binding.setScope(BindingScope.SCENE_GROUP);
        binding.setPriority(10);

        String bindingId = bindingManager.bindKnowledgeBase("scene-001", binding);

        assertNotNull(bindingId);
        assertTrue(bindingManager.hasKnowledgeBinding("scene-001", "kb-001"));
    }

    @Test
    void testGetKnowledgeBindings() {
        KnowledgeBindingInfo binding1 = new KnowledgeBindingInfo("scene-001", "kb-001");
        binding1.setPriority(5);
        bindingManager.bindKnowledgeBase("scene-001", binding1);

        KnowledgeBindingInfo binding2 = new KnowledgeBindingInfo("scene-001", "kb-002");
        binding2.setPriority(10);
        bindingManager.bindKnowledgeBase("scene-001", binding2);

        List<KnowledgeBindingInfo> bindings = bindingManager.getKnowledgeBindings("scene-001");

        assertEquals(2, bindings.size());
        assertEquals("kb-002", bindings.get(0).getKnowledgeBaseId());
        assertEquals("kb-001", bindings.get(1).getKnowledgeBaseId());
    }

    @Test
    void testUnbindKnowledgeBase() {
        KnowledgeBindingInfo binding = new KnowledgeBindingInfo("scene-001", "kb-001");
        bindingManager.bindKnowledgeBase("scene-001", binding);

        assertTrue(bindingManager.hasKnowledgeBinding("scene-001", "kb-001"));

        bindingManager.unbindKnowledgeBase("scene-001", "kb-001");

        assertFalse(bindingManager.hasKnowledgeBinding("scene-001", "kb-001"));
    }

    @Test
    void testSetBindingPriority() {
        KnowledgeBindingInfo binding = new KnowledgeBindingInfo("scene-001", "kb-001");
        binding.setPriority(5);
        bindingManager.bindKnowledgeBase("scene-001", binding);

        bindingManager.setBindingPriority("scene-001", "kb-001", 20);

        KnowledgeBindingInfo updated = bindingManager.getKnowledgeBinding("scene-001", "kb-001");
        assertNotNull(updated);
        assertEquals(20, updated.getPriority());
    }

    @Test
    void testClearAllBindings() {
        bindingManager.bindKnowledgeBase("scene-001", new KnowledgeBindingInfo("scene-001", "kb-001"));
        bindingManager.bindKnowledgeBase("scene-001", new KnowledgeBindingInfo("scene-001", "kb-002"));

        assertEquals(2, bindingManager.getBindingCount("scene-001"));

        bindingManager.clearAllBindings("scene-001");

        assertEquals(0, bindingManager.getBindingCount("scene-001"));
    }
}
