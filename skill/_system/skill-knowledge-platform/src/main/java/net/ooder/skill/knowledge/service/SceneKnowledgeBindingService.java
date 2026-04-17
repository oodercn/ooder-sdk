package net.ooder.skill.knowledge.service;

import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.skill.knowledge.KnowledgeBindingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SceneKnowledgeBindingService {

    private static final Logger log = LoggerFactory.getLogger(SceneKnowledgeBindingService.class);

    @Autowired(required = false)
    private KnowledgeBindingService knowledgeBindingService;

    public void bindToScene(String sceneGroupId, String kbId, String layer, int priority) {
        log.info("[bindToScene] Binding kb {} to scene {} at layer {} with priority {}", 
            kbId, sceneGroupId, layer, priority);
        if (knowledgeBindingService == null) {
            log.warn("[bindToScene] KnowledgeBindingService not available");
            return;
        }
        knowledgeBindingService.bindToScene(sceneGroupId, kbId, layer);
    }

    public void unbindFromScene(String sceneGroupId, String kbId, String layer) {
        log.info("[unbindFromScene] Unbinding kb {} from scene {}", kbId, sceneGroupId);
        if (knowledgeBindingService == null) {
            return;
        }
        knowledgeBindingService.unbindFromScene(sceneGroupId, kbId);
    }

    public List<KnowledgeBinding> getBindings(String sceneGroupId) {
        if (knowledgeBindingService == null) {
            log.warn("[getBindings] KnowledgeBindingService not available");
            return List.of();
        }
        return knowledgeBindingService.getBindings(sceneGroupId);
    }

    public void initDefaultBindingsForScene(String sceneGroupId) {
        log.info("[initDefaultBindingsForScene] Initialized for scene: {}", sceneGroupId);
    }
}
