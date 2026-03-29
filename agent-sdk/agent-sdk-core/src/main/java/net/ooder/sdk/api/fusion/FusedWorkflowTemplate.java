package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Fused Workflow Template Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface FusedWorkflowTemplate extends Serializable {

    String getTemplateId();
    void setTemplateId(String templateId);

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    String getEnterpriseProcedureId();
    void setEnterpriseProcedureId(String enterpriseProcedureId);

    String getSkillId();
    void setSkillId(String skillId);

    String getSkillTemplateId();
    void setSkillTemplateId(String skillTemplateId);

    int getMatchScore();
    void setMatchScore(int matchScore);

    FusionStrategy getFusionStrategy();
    void setFusionStrategy(FusionStrategy fusionStrategy);

    List<FusionConflict> getFusionConflicts();
    void setFusionConflicts(List<FusionConflict> fusionConflicts);

    Long getFusionTime();
    void setFusionTime(Long fusionTime);

    String getFusedBy();
    void setFusedBy(String fusedBy);

    List<FusedRole> getRoles();
    void setRoles(List<FusedRole> roles);

    Map<String, Object> getActivationSteps();
    void setActivationSteps(Map<String, Object> activationSteps);

    Map<String, Object> getMenus();
    void setMenus(Map<String, Object> menus);

    List<Object> getRules();
    void setRules(List<Object> rules);

    List<CapabilityBindingDef> getCapabilities();
    void setCapabilities(List<CapabilityBindingDef> capabilities);

    Long getCreateTime();
    void setCreateTime(Long createTime);

    Long getUpdateTime();
    void setUpdateTime(Long updateTime);

    String getVersion();
    void setVersion(String version);

    TemplateStatus getStatus();
    void setStatus(TemplateStatus status);

    Map<String, Object> getExtensions();
    void setExtensions(Map<String, Object> extensions);
}
