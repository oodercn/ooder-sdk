package net.ooder.scene.provider;

import net.ooder.scene.core.SceneEngine;

/**
 * Provider基础接口
 *
 * <p>所有Provider接口都继承此接口，提供统一的初始化和生命周期管理</p>
 */
public interface BaseProvider {

    /**
     * 获取Provider名称
     */
    String getProviderName();

    /**
     * 获取Provider版本
     */
    String getVersion();

    /**
     * 初始化Provider
     *
     * @param engine 场景引擎实例
     */
    void initialize(SceneEngine engine);

    /**
     * 启动Provider
     */
    void start();

    /**
     * 停止Provider
     */
    void stop();

    /**
     * 检查是否已初始化
     */
    boolean isInitialized();

    /**
     * 检查是否正在运行
     */
    boolean isRunning();

    /**
     * 获取Provider优先级
     */
    default int getPriority() {
        return 100;
    }
}
