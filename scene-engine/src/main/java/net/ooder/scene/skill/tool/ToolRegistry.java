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
 * <h3>Skills Context 动态注入</h3>
 * <p>支持从 SkillActivationContext 动态注入工具，当工具过多时自动清理不常用工具。</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface ToolRegistry {
    
    /**
     * 默认最大工具数量
     */
    int DEFAULT_MAX_TOOLS = 50;

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

    /**
     * 从 Skill 上下文动态注入工具
     *
     * <p>当工具数量超过阈值时，自动清理不常用工具</p>
     *
     * @param skillId 技能ID
     * @param tools 工具列表
     */
    void injectFromSkill(String skillId, List<Tool> tools);

    /**
     * 清理指定技能的工具
     *
     * @param skillId 技能ID
     */
    void clearSkillTools(String skillId);

    /**
     * 记录工具使用
     *
     * @param toolName 工具名称
     */
    void recordToolUsage(String toolName);

    /**
     * 获取工具使用统计
     *
     * @param toolName 工具名称
     * @return 使用次数
     */
    int getToolUsageCount(String toolName);

    /**
     * 智能清理不常用工具
     *
     * @param maxTools 最大保留工具数量
     * @return 清理的工具数量
     */
    int cleanupUnusedTools(int maxTools);

    /**
     * 获取当前工具数量
     *
     * @return 工具数量
     */
    int getToolCount();

    /**
     * 设置最大工具数量
     *
     * @param maxTools 最大工具数量
     */
    void setMaxTools(int maxTools);

    /**
     * 获取最大工具数量
     *
     * @return 最大工具数量
     */
    int getMaxTools();
}
