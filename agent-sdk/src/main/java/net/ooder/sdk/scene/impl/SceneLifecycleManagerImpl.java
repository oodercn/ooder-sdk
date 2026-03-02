package net.ooder.sdk.scene.impl;

import net.ooder.sdk.scene.SceneLifecycleManager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景生命周期管理器实现
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SceneLifecycleManagerImpl implements SceneLifecycleManager {

    private final Map<String, String> sceneStatuses = new ConcurrentHashMap<>();
    private static final String STATUS_CREATED = "CREATED";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_STOPPED = "STOPPED";
    private static final String STATUS_DESTROYED = "DESTROYED";

    @Override
    public boolean createScene(String sceneId, String sceneName) {
        if (sceneStatuses.containsKey(sceneId)) {
            return false;
        }
        sceneStatuses.put(sceneId, STATUS_CREATED);
        return true;
    }

    @Override
    public boolean startScene(String sceneId) {
        if (!sceneStatuses.containsKey(sceneId)) {
            return false;
        }
        sceneStatuses.put(sceneId, STATUS_RUNNING);
        return true;
    }

    @Override
    public boolean stopScene(String sceneId) {
        if (!sceneStatuses.containsKey(sceneId)) {
            return false;
        }
        sceneStatuses.put(sceneId, STATUS_STOPPED);
        return true;
    }

    @Override
    public boolean destroyScene(String sceneId) {
        sceneStatuses.remove(sceneId);
        return true;
    }

    @Override
    public String getSceneStatus(String sceneId) {
        return sceneStatuses.get(sceneId);
    }

    @Override
    public boolean hasScene(String sceneId) {
        return sceneStatuses.containsKey(sceneId);
    }
}
