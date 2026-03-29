package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;

/**
 * Fusion Request Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface FusionRequest extends Serializable {

    String getEnterpriseProcedureId();
    void setEnterpriseProcedureId(String enterpriseProcedureId);

    String getSkillId();
    void setSkillId(String skillId);

    String getSkillTemplateId();
    void setSkillTemplateId(String skillTemplateId);

    FusionStrategy getFusionStrategy();
    void setFusionStrategy(FusionStrategy fusionStrategy);

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    String getFusedBy();
    void setFusedBy(String fusedBy);
}
