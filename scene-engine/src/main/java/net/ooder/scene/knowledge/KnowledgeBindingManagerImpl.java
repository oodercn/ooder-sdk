package net.ooder.scene.knowledge;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KnowledgeBindingManagerImpl implements KnowledgeBindingManager {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBindingManagerImpl.class);

    private final SceneGroupManager sceneGroupManager;
    private final Map<String, Map<String, KnowledgeBindingInfo>> bindingStore = new ConcurrentHashMap<>();

    public KnowledgeBindingManagerImpl(SceneGroupManager sceneGroupManager) {
        this.sceneGroupManager = sceneGroupManager;
    }

    @Override
    public String bindKnowledgeBase(String sceneGroupId, KnowledgeBindingInfo binding) {
        if (sceneGroupId == null || binding == null || binding.getKnowledgeBaseId() == null) {
            throw new IllegalArgumentException("sceneGroupId and binding.knowledgeBaseId are required");
        }

        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (sceneGroup == null) {
            throw new IllegalArgumentException("SceneGroup not found: " + sceneGroupId);
        }

        String knowledgeBaseId = binding.getKnowledgeBaseId();
        binding.setSceneGroupId(sceneGroupId);
        binding.setBindTime(System.currentTimeMillis());

        Map<String, KnowledgeBindingInfo> bindings = bindingStore.computeIfAbsent(
                sceneGroupId, k -> new ConcurrentHashMap<>());

        KnowledgeBindingInfo existing = bindings.get(knowledgeBaseId);
        if (existing != null) {
            log.info("Updating existing binding: sceneGroupId={}, kbId={}", sceneGroupId, knowledgeBaseId);
        }

        bindings.put(knowledgeBaseId, binding);

        log.info("Knowledge base bound: sceneGroupId={}, kbId={}, scope={}, priority={}",
                sceneGroupId, knowledgeBaseId, binding.getScope(), binding.getPriority());

        return binding.getBindingId();
    }

    @Override
    public void unbindKnowledgeBase(String sceneGroupId, String knowledgeBaseId) {
        if (sceneGroupId == null || knowledgeBaseId == null) {
            return;
        }

        Map<String, KnowledgeBindingInfo> bindings = bindingStore.get(sceneGroupId);
        if (bindings != null) {
            KnowledgeBindingInfo removed = bindings.remove(knowledgeBaseId);
            if (removed != null) {
                log.info("Knowledge base unbound: sceneGroupId={}, kbId={}", sceneGroupId, knowledgeBaseId);
            }
        }
    }

    @Override
    public List<KnowledgeBindingInfo> getKnowledgeBindings(String sceneGroupId) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }

        Map<String, KnowledgeBindingInfo> bindings = bindingStore.get(sceneGroupId);
        if (bindings == null || bindings.isEmpty()) {
            return new ArrayList<>();
        }

        List<KnowledgeBindingInfo> result = new ArrayList<>(bindings.values());
        result.sort(Comparator.comparingInt(KnowledgeBindingInfo::getPriority).reversed());

        return result;
    }

    @Override
    public KnowledgeBindingInfo getKnowledgeBinding(String sceneGroupId, String knowledgeBaseId) {
        if (sceneGroupId == null || knowledgeBaseId == null) {
            return null;
        }

        Map<String, KnowledgeBindingInfo> bindings = bindingStore.get(sceneGroupId);
        if (bindings == null) {
            return null;
        }

        return bindings.get(knowledgeBaseId);
    }

    @Override
    public boolean hasKnowledgeBinding(String sceneGroupId, String knowledgeBaseId) {
        if (sceneGroupId == null || knowledgeBaseId == null) {
            return false;
        }

        Map<String, KnowledgeBindingInfo> bindings = bindingStore.get(sceneGroupId);
        return bindings != null && bindings.containsKey(knowledgeBaseId);
    }

    @Override
    public void setBindingPriority(String sceneGroupId, String knowledgeBaseId, int priority) {
        if (sceneGroupId == null || knowledgeBaseId == null) {
            return;
        }

        Map<String, KnowledgeBindingInfo> bindings = bindingStore.get(sceneGroupId);
        if (bindings != null) {
            KnowledgeBindingInfo binding = bindings.get(knowledgeBaseId);
            if (binding != null) {
                binding.setPriority(priority);
                log.info("Binding priority updated: sceneGroupId={}, kbId={}, priority={}",
                        sceneGroupId, knowledgeBaseId, priority);
            }
        }
    }

    @Override
    public void clearAllBindings(String sceneGroupId) {
        if (sceneGroupId == null) {
            return;
        }

        Map<String, KnowledgeBindingInfo> removed = bindingStore.remove(sceneGroupId);
        if (removed != null) {
            log.info("All bindings cleared: sceneGroupId={}, count={}", sceneGroupId, removed.size());
        }
    }

    public int getBindingCount(String sceneGroupId) {
        Map<String, KnowledgeBindingInfo> bindings = bindingStore.get(sceneGroupId);
        return bindings != null ? bindings.size() : 0;
    }

    public int getTotalBindingCount() {
        return bindingStore.values().stream()
                .mapToInt(Map::size)
                .sum();
    }
}
