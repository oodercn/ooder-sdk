package net.ooder.scene.skill.tool.ext;

import net.ooder.scene.skill.tool.Tool;

import java.util.List;
import java.util.Optional;

/**
 * 工具扩展注册中心
 *
 * <p>支持热插拔的工具注册和管理</p>
 *
 * <p>架构层次：应用层 - 工具扩展</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface ToolExtensionRegistry {

    /**
     * 注册工具
     *
     * @param tool 工具扩展
     */
    void register(ToolExtension tool);

    /**
     * 注册工具（指定优先级）
     *
     * @param tool 工具扩展
     * @param priority 优先级（数字越小优先级越高）
     */
    void register(ToolExtension tool, int priority);

    /**
     * 注销工具
     *
     * @param toolId 工具ID
     * @return 是否成功注销
     */
    boolean unregister(String toolId);

    /**
     * 获取工具
     *
     * @param toolId 工具ID
     * @return 工具扩展
     */
    Optional<ToolExtension> getTool(String toolId);

    /**
     * 获取所有工具
     *
     * @return 工具列表
     */
    List<ToolExtension> getAllTools();

    /**
     * 按类别获取工具
     *
     * @param category 工具类别
     * @return 工具列表
     */
    List<ToolExtension> getToolsByCategory(String category);

    /**
     * 按标签获取工具
     *
     * @param tag 工具标签
     * @return 工具列表
     */
    List<ToolExtension> getToolsByTag(String tag);

    /**
     * 检查工具是否存在
     *
     * @param toolId 工具ID
     * @return 是否存在
     */
    boolean hasTool(String toolId);

    /**
     * 重新加载工具
     *
     * <p>用于热更新场景</p>
     *
     * @param toolId 工具ID
     */
    void reloadTool(String toolId);

    /**
     * 批量注册工具
     *
     * @param tools 工具列表
     */
    default void registerAll(List<ToolExtension> tools) {
        tools.forEach(this::register);
    }

    /**
     * 获取工具数量
     *
     * @return 工具数量
     */
    int getToolCount();

    /**
     * 清空所有工具
     */
    void clear();

    /**
     * 添加工具变更监听器
     *
     * @param listener 监听器
     */
    void addListener(ToolRegistryListener listener);

    /**
     * 移除工具变更监听器
     *
     * @param listener 监听器
     */
    void removeListener(ToolRegistryListener listener);

    /**
     * 工具注册中心监听器
     */
    interface ToolRegistryListener {
        /**
         * 工具注册时触发
         *
         * @param tool 工具
         */
        void onToolRegistered(ToolExtension tool);

        /**
         * 工具注销时触发
         *
         * @param toolId 工具ID
         */
        void onToolUnregistered(String toolId);

        /**
         * 工具重新加载时触发
         *
         * @param tool 工具
         */
        void onToolReloaded(ToolExtension tool);
    }
}
