package net.ooder.scene.core;

import net.ooder.scene.core.SceneAgent;
import net.ooder.scene.core.SceneAgentState;
import net.ooder.scene.core.SceneConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SceneLifecycleManager {
    private Map<String, SceneAgent> agents;
    private ScenePersistenceService persistenceService;

    public SceneLifecycleManager(ScenePersistenceService persistenceService) {
        this.agents = new ConcurrentHashMap<>();
        this.persistenceService = persistenceService;
    }

    public SceneAgent createSceneAgent(String sceneId) {
        SceneAgent agent = new net.ooder.scene.core.impl.DefaultSceneAgent();
        agents.put(sceneId, agent);
        return agent;
    }

    public SceneAgent getSceneAgent(String sceneId) {
        return agents.get(sceneId);
    }

    public void startSceneAgent(String sceneId, SceneConfig config) {
        SceneAgent agent = agents.get(sceneId);
        if (agent != null && agent.getState() == SceneAgentState.INITIALIZED) {
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
        SceneAgent agent = agents.get(sceneId);
        if (agent != null) {
            agent.shutdown();
            agents.remove(sceneId);
        }
    }

    public void saveSceneState(String sceneId) {
        SceneAgent agent = agents.get(sceneId);
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
            SceneAgent agent = persistenceService.loadSceneState(sceneId);
            if (agent != null) {
                agents.put(sceneId, agent);
            }
        }
    }

    public Map<String, SceneAgent> getAllSceneAgents() {
        return agents;
    }
}
