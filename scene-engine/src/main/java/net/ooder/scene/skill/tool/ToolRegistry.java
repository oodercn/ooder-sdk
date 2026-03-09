package net.ooder.scene.skill.tool;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 工具注册表接口
 *
 * <p>管理所有可被 LLM Function Calling 调用的工具。</p>
 *
 * <p>架构层次：应用层 - 智能增强</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface ToolRegistry {
    
    /**
     * 注册工具
     *
     * @param tool 工具实例
     */
    void register(Tool tool);
    
    /**
     * 批量注册工具
     *
     * @param tools 工具列表
     */
    void registerAll(List<Tool> tools);
    
    /**
     * 注销工具
     *
     * @param name 工具名称
     */
    void unregister(String name);
    
    /**
     * 获取工具
     *
     * @param name 工具名称
     * @return 工具实例
     */
    Optional<Tool> getTool(String name);
    
    /**
     * 检查工具是否存在
     *
     * @param name 工具名称
     * @return 是否存在
     */
    boolean hasTool(String name);
    
    /**
     * 列出所有工具
     *
     * @return 工具列表
     */
    List<Tool> listAll();
    
    /**
     * 按类别列出工具
     *
     * @param category 类别
     * @return 工具列表
     */
    List<Tool> listByCategory(String category);
    
    /**
     * 按标签列出工具
     *
     * @param tag 标签
     * @return 工具列表
     */
    List<Tool> listByTag(String tag);
    
    /**
     * 获取所有工具的定义（用于 LLM Function Calling）
     *
     * @return 工具定义列表
     */
    List<Map<String, Object>> getToolDefinitions();
    
    /**
     * 获取指定工具的定义
     *
     * @param toolNames 工具名称列表
     * @return 工具定义列表
     */
    List<Map<String, Object>> getToolDefinitions(List<String> toolNames);
    
    /**
     * 清空所有工具
     */
    void clear();
}
