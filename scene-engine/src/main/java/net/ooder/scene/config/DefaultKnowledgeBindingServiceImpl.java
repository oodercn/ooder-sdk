package net.ooder.scene.config;

import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.skill.knowledge.KnowledgeBindingService;
import net.ooder.scene.skill.knowledge.KnowledgeChunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 默认知识库绑定服务实现
 *
 * @author ooder
 * @since 2.3.2
 */
public class DefaultKnowledgeBindingServiceImpl implements KnowledgeBindingService {

    private static final Logger log = LoggerFactory.getLogger(DefaultKnowledgeBindingServiceImpl.class);

    @Override
    public void bindToScene(String sceneGroupId, String kbId, String layer) {
        log.info("[Knowledge] Binding kb: {} to scene: {} at layer: {}", kbId, sceneGroupId, layer);
    }

    @Override
    public void unbindFromScene(String sceneGroupId, String kbId) {
        log.info("[Knowledge] Unbinding kb: {} from scene: {}", kbId, sceneGroupId);
    }

    @Override
    public List<KnowledgeChunk> searchKnowledge(String sceneGroupId, String query, int topK) {
        log.info("[Knowledge] Searching in scene: {}, query: {}, topK: {}", sceneGroupId, query, topK);
        return Collections.emptyList();
    }

    @Override
    public List<KnowledgeChunk> crossLayerSearch(String sceneGroupId, String query, List<String> layers, int topK) {
        log.info("[Knowledge] Cross-layer search in scene: {}, layers: {}", sceneGroupId, layers);
        return Collections.emptyList();
    }

    @Override
    public List<KnowledgeBinding> getBindings(String sceneGroupId) {
        log.info("[Knowledge] Getting bindings for scene: {}", sceneGroupId);
        return new ArrayList<>();
    }
}
