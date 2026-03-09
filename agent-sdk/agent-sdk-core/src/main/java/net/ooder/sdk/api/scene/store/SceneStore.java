package net.ooder.sdk.api.scene.store;

import java.util.List;
import java.util.Map;

/**
 * Scene Store Interface（泛型版本）
 *
 * @param <C> 配置类型
 * @author ooder Team
 * @since 2.3
 */
public interface SceneStore<C> {
    
    void saveScene(String sceneId, Map<String, C> config);
    
    Map<String, C> loadScene(String sceneId);
    
    void deleteScene(String sceneId);
    
    List<String> listScenes();
    
    boolean sceneExists(String sceneId);
    
    void updateSceneConfig(String sceneId, String key, C value);
    
    C getSceneConfigValue(String sceneId, String key);
    
    /**
     * 创建通用场景存储（向后兼容）
     */
    static SceneStore<Object> createGeneric() {
        throw new UnsupportedOperationException("Use implementation class");
    }
}
