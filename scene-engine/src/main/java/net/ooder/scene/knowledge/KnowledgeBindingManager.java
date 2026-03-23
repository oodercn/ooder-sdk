package net.ooder.scene.knowledge;

import java.util.List;

public interface KnowledgeBindingManager {

    String bindKnowledgeBase(String sceneGroupId, KnowledgeBindingInfo binding);

    void unbindKnowledgeBase(String sceneGroupId, String knowledgeBaseId);

    List<KnowledgeBindingInfo> getKnowledgeBindings(String sceneGroupId);

    KnowledgeBindingInfo getKnowledgeBinding(String sceneGroupId, String knowledgeBaseId);

    boolean hasKnowledgeBinding(String sceneGroupId, String knowledgeBaseId);

    void setBindingPriority(String sceneGroupId, String knowledgeBaseId, int priority);

    void clearAllBindings(String sceneGroupId);
}
