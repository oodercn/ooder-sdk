package net.ooder.scene.core;

import net.ooder.scene.core.SceneAgent;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScenePersistenceService {
    private String storagePath;
    private Map<String, SceneAgent> cachedAgents;

    public ScenePersistenceService(String storagePath) {
        this.storagePath = storagePath;
        this.cachedAgents = new ConcurrentHashMap<>();
        // 确保存储目录存在
        File dir = new File(storagePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void saveSceneState(String sceneId, SceneAgent agent) {
        // 缓存场景代理
        cachedAgents.put(sceneId, agent);

        // 保存到文件
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(getSceneFile(sceneId)))) {
            // 这里可以序列化场景状态
            // 暂时只保存场景ID和状态
            Map<String, Object> state = new ConcurrentHashMap<>();
            state.put("sceneId", sceneId);
            state.put("agentId", agent.getAgentId());
            state.put("state", agent.getState());
            oos.writeObject(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SceneAgent loadSceneState(String sceneId) {
        // 先检查缓存
        SceneAgent agent = cachedAgents.get(sceneId);
        if (agent != null) {
            return agent;
        }

        // 从文件加载
        File sceneFile = getSceneFile(sceneId);
        if (sceneFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(sceneFile))) {
                // 这里可以反序列化场景状态
                // 暂时只加载场景ID和状态
                Map<String, Object> state = (Map<String, Object>) ois.readObject();
                // 创建新的场景代理
                agent = new net.ooder.scene.core.impl.DefaultSceneAgent();
                cachedAgents.put(sceneId, agent);
                return agent;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private File getSceneFile(String sceneId) {
        return new File(storagePath, "scene-" + sceneId + ".ser");
    }

    public void clearCache() {
        cachedAgents.clear();
    }

    public void deleteSceneState(String sceneId) {
        cachedAgents.remove(sceneId);
        File sceneFile = getSceneFile(sceneId);
        if (sceneFile.exists()) {
            sceneFile.delete();
        }
    }
}
