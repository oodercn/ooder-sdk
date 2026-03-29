package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.*;

public class FusionRequestEntity implements FusionRequest {

    private static final long serialVersionUID = 1L;

    private String enterpriseProcedureId;
    private String skillId;
    private String skillTemplateId;
    private FusionStrategy fusionStrategy;
    private String name;
    private String description;
    private String fusedBy;

    public FusionRequestEntity() {
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
    public FusionStrategy getFusionStrategy() {
        return fusionStrategy;
    }

    @Override
    public void setFusionStrategy(FusionStrategy fusionStrategy) {
        this.fusionStrategy = fusionStrategy;
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
    public String getFusedBy() {
        return fusedBy;
    }

    @Override
    public void setFusedBy(String fusedBy) {
        this.fusedBy = fusedBy;
    }
}
