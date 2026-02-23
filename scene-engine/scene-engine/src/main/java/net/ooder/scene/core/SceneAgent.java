package net.ooder.scene.core;

import net.ooder.scene.core.SceneAgentState;
import net.ooder.scene.core.SceneContext;
import net.ooder.scene.core.SceneConfig;
import net.ooder.scene.core.CapRequest;
import net.ooder.scene.core.CapResponse;

public interface SceneAgent {
    String getAgentId();
    SceneAgentState getState();
    void initialize(SceneConfig config);
    void mountSkill(String skillId, SceneConfig config);
    void unmountSkill(String skillId);
    CapResponse invokeCap(String capId, CapRequest request);
    void shutdown();
}
