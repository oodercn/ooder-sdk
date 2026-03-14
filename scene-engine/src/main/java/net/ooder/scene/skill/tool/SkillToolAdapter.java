package net.ooder.scene.skill.tool;

import java.util.*;

/**
 * Skill 工具适配器
 * <p>将 Skill 配置适配为 Tool 接口</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class SkillToolAdapter implements Tool {

    private final String skillId;
    private final String name;
    private final String description;
    private final Map<String, Object> config;

    public SkillToolAdapter(String skillId, String name, String description, Map<String, Object> config) {
        this.skillId = skillId;
        this.name = name;
        this.description = description != null ? description : "";
        this.config = config != null ? config : new HashMap<>();
    }

    @Override
    public String getId() {
        return skillId + ":" + name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCategory() {
        return (String) config.getOrDefault("category", "skill");
    }

    @Override
    public List<String> getTags() {
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) config.get("tags");
        return tags != null ? tags : Collections.emptyList();
    }

    @Override
    public Map<String, Object> getParameters() {
        @SuppressWarnings("unchecked")
        Map<String, Object> parameters = (Map<String, Object>) config.get("parameters");
        return parameters != null ? parameters : Collections.emptyMap();
    }

    @Override
    public Map<String, Object> getParametersSchema() {
        // 构建 JSON Schema 格式的参数定义
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> parameters = getParameters();
        
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            Map<String, Object> property = new HashMap<>();
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> paramConfig = (Map<String, Object>) entry.getValue();
                property.put("type", paramConfig.getOrDefault("type", "string"));
                property.put("description", paramConfig.getOrDefault("description", ""));
            } else {
                property.put("type", "string");
                property.put("description", entry.getValue() != null ? entry.getValue().toString() : "");
            }
            properties.put(entry.getKey(), property);
        }
        
        schema.put("properties", properties);
        
        // 提取 required 参数
        List<String> required = new ArrayList<>();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> paramConfig = (Map<String, Object>) entry.getValue();
                Boolean isRequired = (Boolean) paramConfig.get("required");
                if (isRequired != null && isRequired) {
                    required.add(entry.getKey());
                }
            }
        }
        if (!required.isEmpty()) {
            schema.put("required", required);
        }
        
        return schema;
    }

    @Override
    public ToolResult validateArguments(Map<String, Object> arguments) {
        // 简化验证，实际应该根据参数 schema 验证
        if (arguments == null) {
            return ToolResult.error("Arguments cannot be null");
        }
        return ToolResult.success(new HashMap<>());
    }

    @Override
    public ToolResult execute(Map<String, Object> parameters, ToolContext context) {
        // 简化实现，实际应该调用 Skill 的能力执行
        // 这里返回模拟结果
        Map<String, Object> result = new HashMap<>();
        result.put("skillId", skillId);
        result.put("toolName", name);
        result.put("parameters", parameters);
        result.put("status", "executed");
        result.put("message", "Tool execution simulated for " + name);
        
        return ToolResult.success(result);
    }

    /**
     * 获取关联的 Skill ID
     */
    public String getSkillId() {
        return skillId;
    }

    /**
     * 获取原始配置
     */
    public Map<String, Object> getConfig() {
        return new HashMap<>(config);
    }

    @Override
    public String toString() {
        return "SkillToolAdapter{" +
                "skillId='" + skillId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
