package net.ooder.scene.core.secure;

import net.ooder.scene.core.SceneEngine;

/**
 * SceneEngine持有者（单例）
 *
 * <p>用于在JDSServer反射创建SecureSceneEngineProxy时获取SceneEngine实例。</p>
 *
 * <p>设计模式：单例模式 + 持有者模式</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SceneEngineHolder {

    private static volatile SceneEngineHolder instance;
    private SceneEngine sceneEngine;

    private SceneEngineHolder() {}

    /**
     * 获取单例实例
     *
     * @return SceneEngineHolder实例
     */
    public static SceneEngineHolder getInstance() {
        if (instance == null) {
            synchronized (SceneEngineHolder.class) {
                if (instance == null) {
                    instance = new SceneEngineHolder();
                }
            }
        }
        return instance;
    }

    /**
     * 设置SceneEngine实例
     *
     * @param sceneEngine SceneEngine实例
     */
    public void setSceneEngine(SceneEngine sceneEngine) {
        this.sceneEngine = sceneEngine;
    }

    /**
     * 获取SceneEngine实例
     *
     * @return SceneEngine实例，如果未设置则返回null
     */
    public SceneEngine getSceneEngine() {
        return this.sceneEngine;
    }

    /**
     * 检查SceneEngine是否已初始化
     *
     * @return true 已初始化
     */
    public boolean isInitialized() {
        return this.sceneEngine != null;
    }

    /**
     * 重置（主要用于测试）
     */
    public void reset() {
        this.sceneEngine = null;
    }
}
