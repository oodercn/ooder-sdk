package net.ooder.scene.core;

import net.ooder.scene.core.SceneAgentCore;
import net.ooder.scene.core.SceneAgentState;
import net.ooder.scene.core.SceneConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SceneLifecycleManager {
    private Map<String, SceneAgentCore> agents;
    private ScenePersistenceService persistenceService;

    public SceneLifecycleManager(ScenePersistenceService persistenceService) {
        this.agents = new ConcurrentHashMap<String, SceneAgentCore>();
        this.persistenceService = persistenceService;
    }

    public SceneAgentCore createSceneAgent(String sceneId) {
        SceneAgentCore agent = new net.ooder.scene.core.impl.SceneAgentBridge();
        agents.put(sceneId, agent);
        return agent;
    }

    public SceneAgentCore getSceneAgent(String sceneId) {
        return agents.get(sceneId);
    }

    public void startSceneAgent(String sceneId, SceneConfig config) {
        SceneAgentCore agent = agents.get(sceneId);
        if (agent != null && agent.getAgentCoreState() == SceneAgentState.INITIALIZED) {
            agent.initialize(config);
        }
    }

    public void pauseSceneAgent(String sceneId) {
        // 这里可以添加暂停逻辑
        // 暂时简单实现
    }

    public void resumeSceneAgent(String sceneId) {
        // 这里可以添加恢复逻辑
        // 暂时简单实现
    }

    public void stopSceneAgent(String sceneId) {
        SceneAgentCore agent = agents.get(sceneId);
        if (agent != null) {
            agent.shutdown();
            agents.remove(sceneId);
        }
    }

    public void saveSceneState(String sceneId) {
        SceneAgentCore agent = agents.get(sceneId);
        if (agent != null) {
            // 保存场景状态
            if (persistenceService != null) {
                persistenceService.saveSceneState(sceneId, agent);
            }
        }
    }

    public void loadSceneState(String sceneId) {
        // 加载场景状态
        if (persistenceService != null) {
            SceneAgentCore agent = persistenceService.loadSceneState(sceneId);
            if (agent != null) {
                agents.put(sceneId, agent);
            }
        }
    }

    public Map<String, SceneAgentCore> getAllSceneAgents() {
        return agents;
    }
}
