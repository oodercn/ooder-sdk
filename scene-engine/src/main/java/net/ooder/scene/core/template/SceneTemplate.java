package net.ooder.scene.core.template;

import net.ooder.scene.ui.MenuConfig;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 场景模板
 * 
 * <p>定义场景技能的完整配置模板，包括角色、激活步骤、菜单、依赖等</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneTemplate implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 基本信息
    private String templateId;           // 模板ID
    private String templateName;         // 模板名称
    private String description;          // 描述
    private String version;              // 版本
    private String skillType;            // 技能类型（LightweightSkill, StandardSkill, HeavyweightSkill）
    private String participantMode;      // 参与者模式（single-user, multi-role）
    
    // 角色配置
    private List<RoleConfig> roles;      // 角色列表
    
    // 激活步骤配置（按角色区分）
    private Map<String, List<ActivationStepConfig>> activationSteps;  // key: roleId
    
    // 菜单配置（按角色区分）
    private Map<String, List<MenuConfig>> menus;  // key: roleId
    
    // 依赖配置
    private DependenciesConfig dependencies;  // 依赖配置
    
    // UI技能配置
    private List<UiSkillConfig> uiSkills;  // UI技能列表
    
    // 私有能力配置
    private List<PrivateCapabilityConfig> privateCapabilities;  // 私有能力列表
    
    // 扩展属性
    private Map<String, Object> metadata;
    
    public SceneTemplate() {
    }
    
    public SceneTemplate(String templateId, String templateName) {
        this.templateId = templateId;
        this.templateName = templateName;
    }
    
    // Getters and Setters
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getSkillType() {
        return skillType;
    }
    
    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }
    
    public String getParticipantMode() {
        return participantMode;
    }
    
    public void setParticipantMode(String participantMode) {
        this.participantMode = participantMode;
    }
    
    public List<RoleConfig> getRoles() {
        return roles;
    }
    
    public void setRoles(List<RoleConfig> roles) {
        this.roles = roles;
    }
    
    public Map<String, List<ActivationStepConfig>> getActivationSteps() {
        return activationSteps;
    }
    
    public void setActivationSteps(Map<String, List<ActivationStepConfig>> activationSteps) {
        this.activationSteps = activationSteps;
    }
    
    public Map<String, List<MenuConfig>> getMenus() {
        return menus;
    }
    
    public void setMenus(Map<String, List<MenuConfig>> menus) {
        this.menus = menus;
    }
    
    public DependenciesConfig getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(DependenciesConfig dependencies) {
        this.dependencies = dependencies;
    }
    
    public List<UiSkillConfig> getUiSkills() {
        return uiSkills;
    }
    
    public void setUiSkills(List<UiSkillConfig> uiSkills) {
        this.uiSkills = uiSkills;
    }
    
    public List<PrivateCapabilityConfig> getPrivateCapabilities() {
        return privateCapabilities;
    }
    
    public void setPrivateCapabilities(List<PrivateCapabilityConfig> privateCapabilities) {
        this.privateCapabilities = privateCapabilities;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    /**
     * 获取指定角色的激活步骤
     */
    public List<ActivationStepConfig> getActivationStepsForRole(String roleId) {
        if (activationSteps == null) {
            return null;
        }
        return activationSteps.get(roleId);
    }
    
    /**
     * 获取指定角色的菜单
     */
    public List<MenuConfig> getMenusForRole(String roleId) {
        if (menus == null) {
            return null;
        }
        return menus.get(roleId);
    }
    
    /**
     * 检查是否支持指定角色
     */
    public boolean supportsRole(String roleId) {
        if (roles == null) {
            return false;
        }
        return roles.stream().anyMatch(r -> r.getRoleId().equals(roleId));
    }
    
    /**
     * 获取必需角色列表
     */
    public List<RoleConfig> getRequiredRoles() {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .filter(RoleConfig::isRequired)
                .collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return "SceneTemplate{" +
                "templateId='" + templateId + '\'' +
                ", templateName='" + templateName + '\'' +
                ", skillType='" + skillType + '\'' +
                ", participantMode='" + participantMode + '\'' +
                ", rolesCount=" + (roles != null ? roles.size() : 0) +
                '}';
    }
    
    /**
     * UI技能配置
     */
    public static class UiSkillConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String skillId;
        private String skillName;
        private String entryPoint;  // 入口点（如：pages/index.html）
        private Map<String, Object> config;
        
        // Getters and Setters
        
        public String getSkillId() {
            return skillId;
        }
        
        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }
        
        public String getSkillName() {
            return skillName;
        }
        
        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }
        
        public String getEntryPoint() {
            return entryPoint;
        }
        
        public void setEntryPoint(String entryPoint) {
            this.entryPoint = entryPoint;
        }
        
        public Map<String, Object> getConfig() {
            return config;
        }
        
        public void setConfig(Map<String, Object> config) {
            this.config = config;
        }
    }
    
    /**
     * 私有能力配置
     */
    public static class PrivateCapabilityConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String capabilityId;
        private String capabilityName;
        private String description;
        private Map<String, Object> config;
        
        // Getters and Setters
        
        public String getCapabilityId() {
            return capabilityId;
        }
        
        public void setCapabilityId(String capabilityId) {
            this.capabilityId = capabilityId;
        }
        
        public String getCapabilityName() {
            return capabilityName;
        }
        
        public void setCapabilityName(String capabilityName) {
            this.capabilityName = capabilityName;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Map<String, Object> getConfig() {
            return config;
        }
        
        public void setConfig(Map<String, Object> config) {
            this.config = config;
        }
    }
}
