package net.ooder.scene.skill.tool.browser;

import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;
import net.ooder.scene.skill.tool.ext.ToolExtension;
import net.ooder.scene.skill.tool.ext.ToolExtensionConfig;

import java.util.*;

/**
 * 获取激活的Skills工具
 *
 * <p>获取当前页面激活的所有Skills信息，包括ID、名称、版本、能力等</p>
 *
 * <p>架构层次：应用层 - 浏览器工具</p>
 *
 * @author ooder
 * @since 2.3
 */
public class GetActiveSkillsTool implements ToolExtension {

    private static final String ID = "get_active_skills";
    private static final String NAME = "get_active_skills";
    private static final String DESCRIPTION = "获取当前页面激活的所有Skills信息，返回JSON格式数据。可用于了解当前可用的能力。";

    private final ToolExtensionConfig config;
    private final SkillsBridge bridge;

    public GetActiveSkillsTool(SkillsBridge bridge) {
        this.bridge = bridge;
        this.config = ToolExtensionConfig.defaults()
                .async(false)
                .requireConfirmation(false);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public ToolExtensionConfig getConfig() {
        return config;
    }

    @Override
    public Map<String, Object> getParameters() {
        return getParametersSchema();
    }

    @Override
    public Map<String, Object> getParametersSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();

        Map<String, Object> includeDetailsProp = new LinkedHashMap<>();
        includeDetailsProp.put("type", "boolean");
        includeDetailsProp.put("description", "是否包含详细信息");
        includeDetailsProp.put("default", false);
        properties.put("includeDetails", includeDetailsProp);

        Map<String, Object> categoryFilterProp = new LinkedHashMap<>();
        categoryFilterProp.put("type", "string");
        categoryFilterProp.put("description", "按类别过滤（可选）");
        properties.put("categoryFilter", categoryFilterProp);

        schema.put("properties", properties);
        schema.put("required", Collections.emptyList());

        return schema;
    }

    @Override
    public ToolResult execute(Map<String, Object> arguments, ToolContext context) {
        try {
            if (bridge == null) {
                return ToolResult.failure("BRIDGE_NOT_AVAILABLE", "Skills bridge is not available");
            }

            boolean includeDetails = (Boolean) arguments.getOrDefault("includeDetails", false);
            String categoryFilter = (String) arguments.get("categoryFilter");

            List<SkillInfo> skills = bridge.getActiveSkills();

            List<Map<String, Object>> skillList = new ArrayList<>();
            for (SkillInfo skill : skills) {
                // 类别过滤
                if (categoryFilter != null && !categoryFilter.equals(skill.getCategory())) {
                    continue;
                }

                Map<String, Object> skillData = new LinkedHashMap<>();
                skillData.put("id", skill.getId());
                skillData.put("name", skill.getName());
                skillData.put("version", skill.getVersion());
                skillData.put("category", skill.getCategory());
                skillData.put("description", skill.getDescription());

                if (includeDetails) {
                    skillData.put("capabilities", skill.getCapabilities());
                    skillData.put("config", skill.getConfig());
                    skillData.put("status", skill.getStatus());
                    skillData.put("activatedAt", skill.getActivatedAt());
                }

                skillList.add(skillData);
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("totalCount", skills.size());
            data.put("returnedCount", skillList.size());
            data.put("skills", skillList);

            return ToolResult.success(data);

        } catch (Exception e) {
            return ToolResult.failure("EXECUTION_ERROR", "Failed to get active skills: " + e.getMessage());
        }
    }

    @Override
    public String executeAsync(Map<String, Object> arguments, ToolContext context, ToolExecutionCallback callback) {
        ToolResult result = execute(arguments, context);
        callback.onComplete(result);
        return UUID.randomUUID().toString();
    }

    @Override
    public String getCategory() {
        return "browser";
    }

    @Override
    public List<String> getTags() {
        return Arrays.asList("browser", "skills", "info", "json");
    }

    /**
     * Skills桥接接口
     */
    public interface SkillsBridge {
        List<SkillInfo> getActiveSkills();
    }

    /**
     * Skill信息
     */
    public static class SkillInfo {
        private String id;
        private String name;
        private String version;
        private String category;
        private String description;
        private List<String> capabilities;
        private Map<String, Object> config;
        private String status;
        private long activatedAt;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getCapabilities() { return capabilities; }
        public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public long getActivatedAt() { return activatedAt; }
        public void setActivatedAt(long activatedAt) { this.activatedAt = activatedAt; }
    }
}
