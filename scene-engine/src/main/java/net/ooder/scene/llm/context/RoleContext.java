package net.ooder.scene.llm.context;

import java.io.Serializable;
import java.util.*;

/**
 * 角色上下文
 * 
 * <p>定义 AI 助手的角色、行为准则和能力范围</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class RoleContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roleId;
    private String roleName;
    private String definition;
    private List<String> guidelines = new ArrayList<>();
    private List<String> capabilities = new ArrayList<>();
    private Map<String, Object> config = new HashMap<>();

    private static final Map<String, RoleContext> BUILTIN_ROLES = new HashMap<>();

    static {
        RoleContext assistant = new RoleContext();
        assistant.roleId = "assistant";
        assistant.roleName = "智能助手";
        assistant.definition = "你是一个智能助手，帮助用户完成各种任务。";
        assistant.guidelines = Arrays.asList(
            "保持专业和友好的态度",
            "提供准确和有帮助的回答",
            "在不确定时主动询问澄清"
        );
        BUILTIN_ROLES.put("assistant", assistant);

        RoleContext hrAssistant = new RoleContext();
        hrAssistant.roleId = "hr-assistant";
        hrAssistant.roleName = "HR 助手";
        hrAssistant.definition = "你是招聘场景的智能助手，专门帮助HR处理招聘相关事务。";
        hrAssistant.guidelines = Arrays.asList(
            "熟悉招聘流程和最佳实践",
            "保护候选人隐私信息",
            "提供客观公正的候选人评估"
        );
        hrAssistant.capabilities = Arrays.asList(
            "resume_scan",
            "interview_schedule",
            "candidate_query"
        );
        BUILTIN_ROLES.put("hr-assistant", hrAssistant);
    }

    public RoleContext() {
    }

    /**
     * 获取默认角色
     */
    public static RoleContext defaultRole() {
        return BUILTIN_ROLES.get("assistant");
    }

    /**
     * 加载角色
     */
    public static RoleContext load(String roleId) {
        RoleContext role = BUILTIN_ROLES.get(roleId);
        if (role != null) {
            return role;
        }
        RoleContext defaultRole = new RoleContext();
        defaultRole.roleId = roleId;
        defaultRole.roleName = roleId;
        defaultRole.definition = "你是一个智能助手。";
        return defaultRole;
    }

    /**
     * 构建提示词部分
     */
    public String buildPromptSection() {
        StringBuilder sb = new StringBuilder();

        sb.append("# 角色定义\n\n");
        sb.append(definition).append("\n\n");

        if (!guidelines.isEmpty()) {
            sb.append("## 行为准则\n\n");
            for (String guideline : guidelines) {
                sb.append("- ").append(guideline).append("\n");
            }
            sb.append("\n");
        }

        if (!capabilities.isEmpty()) {
            sb.append("## 可用能力\n\n");
            for (String capability : capabilities) {
                sb.append("- ").append(capability).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // Getters and Setters

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }
    public List<String> getGuidelines() { return guidelines; }
    public void setGuidelines(List<String> guidelines) { this.guidelines = guidelines != null ? guidelines : new ArrayList<>(); }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities != null ? capabilities : new ArrayList<>(); }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config != null ? config : new HashMap<>(); }
}
