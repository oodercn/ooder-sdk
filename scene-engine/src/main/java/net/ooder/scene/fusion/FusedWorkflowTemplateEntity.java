package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.FusedWorkflowTemplate;
import net.ooder.sdk.api.fusion.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 融合工作流模板实体实现
 *
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class FusedWorkflowTemplateEntity implements FusedWorkflowTemplate {

    private static final long serialVersionUID = 1L;

    private String templateId;
    private String name;
    private String description;
    private String enterpriseProcedureId;
    private String skillId;
    private String skillTemplateId;
    private int matchScore;
    private FusionStrategy fusionStrategy;
    private List<FusionConflict> fusionConflicts = new ArrayList<>();
    private Long fusionTime;
    private String fusedBy;
    private List<FusedRole> roles = new ArrayList<>();
    private Map<String, Object> activationSteps = new HashMap<>();
    private Map<String, Object> menus = new HashMap<>();
    private List<Object> rules = new ArrayList<>();
    private List<CapabilityBindingDef> capabilities = new ArrayList<>();
    private Long createTime;
    private Long updateTime;
    private String version = "1.0";
    private TemplateStatus status = TemplateStatus.DRAFT;
    private Map<String, Object> extensions = new HashMap<>();

    public FusedWorkflowTemplateEntity() {
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    @Override
    public String getTemplateId() {
        return templateId;
    }

    @Override
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getEnterpriseProcedureId() {
        return enterpriseProcedureId;
    }

    @Override
    public void setEnterpriseProcedureId(String enterpriseProcedureId) {
        this.enterpriseProcedureId = enterpriseProcedureId;
    }

    @Override
    public String getSkillId() {
        return skillId;
    }

    @Override
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    @Override
    public String getSkillTemplateId() {
        return skillTemplateId;
    }

    @Override
    public void setSkillTemplateId(String skillTemplateId) {
        this.skillTemplateId = skillTemplateId;
    }

    @Override
    public int getMatchScore() {
        return matchScore;
    }

    @Override
    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    @Override
    public FusionStrategy getFusionStrategy() {
        return fusionStrategy;
    }

    @Override
    public void setFusionStrategy(FusionStrategy fusionStrategy) {
        this.fusionStrategy = fusionStrategy;
    }

    @Override
    public List<FusionConflict> getFusionConflicts() {
        return fusionConflicts;
    }

    @Override
    public void setFusionConflicts(List<FusionConflict> fusionConflicts) {
        this.fusionConflicts = fusionConflicts != null ? fusionConflicts : new ArrayList<>();
    }

    @Override
    public Long getFusionTime() {
        return fusionTime;
    }

    @Override
    public void setFusionTime(Long fusionTime) {
        this.fusionTime = fusionTime;
    }

    @Override
    public String getFusedBy() {
        return fusedBy;
    }

    @Override
    public void setFusedBy(String fusedBy) {
        this.fusedBy = fusedBy;
    }

    @Override
    public List<FusedRole> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(List<FusedRole> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    @Override
    public Map<String, Object> getActivationSteps() {
        return activationSteps;
    }

    @Override
    public void setActivationSteps(Map<String, Object> activationSteps) {
        this.activationSteps = activationSteps != null ? activationSteps : new HashMap<>();
    }

    @Override
    public Map<String, Object> getMenus() {
        return menus;
    }

    @Override
    public void setMenus(Map<String, Object> menus) {
        this.menus = menus != null ? menus : new HashMap<>();
    }

    @Override
    public List<Object> getRules() {
        return rules;
    }

    @Override
    public void setRules(List<Object> rules) {
        this.rules = rules != null ? rules : new ArrayList<>();
    }

    @Override
    public List<CapabilityBindingDef> getCapabilities() {
        return capabilities;
    }

    @Override
    public void setCapabilities(List<CapabilityBindingDef> capabilities) {
        this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
    }

    @Override
    public Long getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public Long getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public TemplateStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(TemplateStatus status) {
        this.status = status;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    public void touch() {
        this.updateTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "FusedWorkflowTemplateEntity{" +
                "templateId='" + templateId + '\'' +
                ", name='" + name + '\'' +
                ", matchScore=" + matchScore +
                ", status=" + status +
                '}';
    }
}
