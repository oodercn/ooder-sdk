package net.ooder.skills.api;

import java.util.List;
import java.util.Map;

/**
 * 场景依赖解析器
 * 支持场景模板依赖解析和安装顺序计算
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SceneDependencyResolver {

    /**
     * 从 SkillManifest 解析依赖 (原有方法)
     *
     * @param manifest Skill 清单
     * @return 依赖列表
     */
    List<SceneDependency> resolve(SkillManifest manifest);

    /**
     * 从场景模板解析依赖 (新增)
     *
     * @param template 场景模板
     * @return 依赖列表
     */
    List<SceneDependency> resolveFromTemplate(SceneTemplate template);

    /**
     * 获取安装顺序 (新增)
     * 使用拓扑排序计算正确的安装顺序
     *
     * @param template 场景模板
     * @return 按顺序排列的 Skill ID 列表
     */
    List<String> getInstallOrder(SceneTemplate template);

    /**
     * 检查所有依赖状态 (新增)
     *
     * @param template 场景模板
     * @return Skill ID -> 依赖状态的映射
     */
    Map<String, DependencyStatus> checkAllDependencies(SceneTemplate template);

    /**
     * 检查依赖是否满足 (原有方法)
     *
     * @param sceneName 场景名称
     * @return 是否满足
     */
    boolean checkDependencySatisfied(String sceneName);

    /**
     * 获取未满足的依赖 (原有方法)
     *
     * @return 未满足的依赖列表
     */
    List<SceneDependency> getUnsatisfiedDependencies();

    /**
     * 获取依赖顺序 (原有方法)
     *
     * @return 依赖顺序列表
     */
    List<String> getDependencyOrder();

    /**
     * 添加依赖监听器 (原有方法)
     *
     * @param listener 监听器
     */
    void addDependencyListener(DependencyListener listener);

    /**
     * 移除依赖监听器 (原有方法)
     *
     * @param listener 监听器
     */
    void removeDependencyListener(DependencyListener listener);

    /**
     * 依赖监听器接口
     */
    interface DependencyListener {
        void onDependencyResolved(String sceneName);
        void onDependencyFailed(String sceneName, String reason);
    }
}
