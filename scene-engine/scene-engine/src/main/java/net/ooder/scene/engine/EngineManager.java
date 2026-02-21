package net.ooder.scene.engine;

import java.util.List;
import java.util.Map;

/**
 * EngineManager 引擎管理器接口
 * 
 * <p>负责管理所有Engine 的生命周期、注册、发现等。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface EngineManager {

    /**
     * 注册引擎
     * 
     * @param engine 引擎实例
     */
    void registerEngine(Engine engine);

    /**
     * 注销引擎
     * 
     * @param engineType 引擎类型
     */
    void unregisterEngine(EngineType engineType);

    /**
     * 获取引擎
     * 
     * @param engineType 引擎类型
     * @return 引擎实例
     */
    Engine getEngine(EngineType engineType);

    /**
     * 获取所有引擎
     * 
     * @return 引擎列表
     */
    List<Engine> getAllEngines();

    /**
     * 获取所有引擎类型
     * 
     * @return 引擎类型列表
     */
    List<EngineType> getEngineTypes();

    /**
     * 检查引擎是否存在
     * 
     * @param engineType 引擎类型
     * @return true 存在，false 不存在
     */
    boolean hasEngine(EngineType engineType);

    /**
     * 初始化所有引擎
     */
    void initializeAll();

    /**
     * 启动所有引擎
     */
    void startAll();

    /**
     * 停止所有引擎
     */
    void stopAll();

    /**
     * 销毁所有引擎
     */
    void destroyAll();

    /**
     * 获取所有引擎状态
     * 
     * @return 引擎状态映射
     */
    Map<EngineType, EngineStatus> getAllStatus();

    /**
     * 健康检查所有引擎
     * 
     * @return 不健康的引擎列表
     */
    List<EngineType> healthCheckAll();
}
