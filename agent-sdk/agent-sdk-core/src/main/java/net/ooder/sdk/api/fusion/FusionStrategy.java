package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.Map;

/**
 * Fusion Strategy Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface FusionStrategy extends Serializable {

    FusionPriority getRolePriority();
    void setRolePriority(FusionPriority rolePriority);

    FusionPriority getActivationStepPriority();
    void setActivationStepPriority(FusionPriority activationStepPriority);

    FusionPriority getMenuPriority();
    void setMenuPriority(FusionPriority menuPriority);

    FusionPriority getCapabilityPriority();
    void setCapabilityPriority(FusionPriority capabilityPriority);

    FusionPriority getRulePriority();
    void setRulePriority(FusionPriority rulePriority);

    boolean isAutoResolveConflict();
    void setAutoResolveConflict(boolean autoResolveConflict);

    Map<String, Object> getCustomRules();
    void setCustomRules(Map<String, Object> customRules);
}
