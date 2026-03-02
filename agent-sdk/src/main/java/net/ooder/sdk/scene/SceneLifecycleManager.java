package net.ooder.sdk.scene;

/**
 * 场景生命周期管理器
 * 管理场景的创建、启动、停止和销毁
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SceneLifecycleManager {

    /**
     * 创建场景
     *
     * @param sceneId   场景ID
     * @param sceneName 场景名称
     * @return 是否创建成功
     */
    boolean createScene(String sceneId, String sceneName);

    /**
     * 启动场景
     *
     * @param sceneId 场景ID
     * @return 是否启动成功
     */
    boolean startScene(String sceneId);

    /**
     * 停止场景
     *
     * @param sceneId 场景ID
     * @return 是否停止成功
     */
    boolean stopScene(String sceneId);

    /**
     * 销毁场景
     *
     * @param sceneId 场景ID
     * @return 是否销毁成功
     */
    boolean destroyScene(String sceneId);

    /**
     * 获取场景状态
     *
     * @param sceneId 场景ID
     * @return 场景状态
     */
    String getSceneStatus(String sceneId);

    /**
     * 检查场景是否存在
     *
     * @param sceneId 场景ID
     * @return 是否存在
     */
    boolean hasScene(String sceneId);
}
