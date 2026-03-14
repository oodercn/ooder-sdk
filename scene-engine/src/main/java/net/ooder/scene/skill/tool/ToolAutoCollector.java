package net.ooder.scene.skill.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 工具自动采集器
 * <p>自动从 Skills 配置中扫描和注册工具</p>
 *
 * <p>功能：</p>
 * <ul>
 *   <li>扫描 Skill 配置中的工具定义</li>
 *   <li>自动注册到 ToolRegistry</li>
 *   <li>支持注解驱动的工具发现</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class ToolAutoCollector {

    private static final Logger log = LoggerFactory.getLogger(ToolAutoCollector.class);

    private final ToolRegistry toolRegistry;
    private final Map<String, Object> skillConfigs;

    public ToolAutoCollector(ToolRegistry toolRegistry) {
        this(toolRegistry, new HashMap<>());
    }

    public ToolAutoCollector(ToolRegistry toolRegistry, Map<String, Object> skillConfigs) {
        this.toolRegistry = toolRegistry;
        this.skillConfigs = skillConfigs;
    }

    /**
     * 从 Skills 配置中自动采集工具
     *
     * @return 采集到的工具数量
     */
    public int collectFromSkills() {
        log.info("Starting tool auto-collection from {} skill configs", skillConfigs.size());
        int count = 0;

        for (Map.Entry<String, Object> entry : skillConfigs.entrySet()) {
            String skillId = entry.getKey();
            Object config = entry.getValue();

            try {
                List<Tool> tools = extractToolsFromSkill(skillId, config);
                for (Tool tool : tools) {
                    toolRegistry.register(tool);
                    count++;
                    log.debug("Registered tool '{}' from skill '{}'", tool.getName(), skillId);
                }
            } catch (Exception e) {
                log.error("Failed to extract tools from skill '{}': {}", skillId, e.getMessage(), e);
            }
        }

        log.info("Tool auto-collection completed. Registered {} tools", count);
        return count;
    }

    /**
     * 从单个 Skill 配置中提取工具
     *
     * @param skillId Skill ID
     * @param config Skill 配置
     * @return 工具列表
     */
    @SuppressWarnings("unchecked")
    protected List<Tool> extractToolsFromSkill(String skillId, Object config) {
        List<Tool> tools = new ArrayList<>();

        if (!(config instanceof Map)) {
            return tools;
        }

        Map<String, Object> configMap = (Map<String, Object>) config;

        // 1. 提取 capabilities 中的工具
        if (configMap.containsKey("capabilities")) {
            Object capabilities = configMap.get("capabilities");
            if (capabilities instanceof List) {
                for (Object capability : (List<?>) capabilities) {
                    Tool tool = convertCapabilityToTool(skillId, capability);
                    if (tool != null) {
                        tools.add(tool);
                    }
                }
            }
        }

        // 2. 提取 functions 中的工具（Function Calling 格式）
        if (configMap.containsKey("functions")) {
            Object functions = configMap.get("functions");
            if (functions instanceof List) {
                for (Object function : (List<?>) functions) {
                    Tool tool = convertFunctionToTool(skillId, function);
                    if (tool != null) {
                        tools.add(tool);
                    }
                }
            }
        }

        // 3. 提取 tools 字段中的工具
        if (configMap.containsKey("tools")) {
            Object toolsConfig = configMap.get("tools");
            if (toolsConfig instanceof List) {
                for (Object toolConfig : (List<?>) toolsConfig) {
                    Tool tool = convertToolConfig(skillId, toolConfig);
                    if (tool != null) {
                        tools.add(tool);
                    }
                }
            }
        }

        return tools;
    }

    /**
     * 将 Capability 转换为 Tool
     */
    @SuppressWarnings("unchecked")
    protected Tool convertCapabilityToTool(String skillId, Object capability) {
        if (!(capability instanceof Map)) {
            return null;
        }

        Map<String, Object> capMap = (Map<String, Object>) capability;
        String name = (String) capMap.get("name");
        String description = (String) capMap.get("description");

        if (name == null || name.isEmpty()) {
            return null;
        }

        return new SkillToolAdapter(skillId, name, description, capMap);
    }

    /**
     * 将 Function 转换为 Tool
     */
    @SuppressWarnings("unchecked")
    protected Tool convertFunctionToTool(String skillId, Object function) {
        if (!(function instanceof Map)) {
            return null;
        }

        Map<String, Object> funcMap = (Map<String, Object>) function;
        String name = (String) funcMap.get("name");
        String description = (String) funcMap.get("description");

        if (name == null || name.isEmpty()) {
            return null;
        }

        return new SkillToolAdapter(skillId, name, description, funcMap);
    }

    /**
     * 将 Tool Config 转换为 Tool
     */
    @SuppressWarnings("unchecked")
    protected Tool convertToolConfig(String skillId, Object toolConfig) {
        if (!(toolConfig instanceof Map)) {
            return null;
        }

        Map<String, Object> toolMap = (Map<String, Object>) toolConfig;
        String name = (String) toolMap.get("name");
        String description = (String) toolMap.get("description");

        if (name == null || name.isEmpty()) {
            return null;
        }

        return new SkillToolAdapter(skillId, name, description, toolMap);
    }

    /**
     * 添加 Skill 配置
     */
    public void addSkillConfig(String skillId, Object config) {
        skillConfigs.put(skillId, config);
    }

    /**
     * 批量添加 Skill 配置
     */
    public void addSkillConfigs(Map<String, Object> configs) {
        skillConfigs.putAll(configs);
    }

    /**
     * 清除所有 Skill 配置
     */
    public void clearSkillConfigs() {
        skillConfigs.clear();
    }

    /**
     * 获取已注册的 Skill 配置数量
     */
    public int getSkillConfigCount() {
        return skillConfigs.size();
    }
}
