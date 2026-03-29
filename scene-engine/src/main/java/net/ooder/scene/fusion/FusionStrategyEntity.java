package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.FusionPriority;
import net.ooder.sdk.api.fusion.FusionStrategy;

import java.util.HashMap;
import java.util.Map;

public class FusionStrategyEntity implements FusionStrategy {

    private static final long serialVersionUID = 1L;

    private FusionPriority rolePriority = FusionPriority.ENTERPRISE_FIRST;
    private FusionPriority activationStepPriority = FusionPriority.MERGE;
    private FusionPriority menuPriority = FusionPriority.MERGE;
    private FusionPriority capabilityPriority = FusionPriority.SKILL_FIRST;
    private FusionPriority rulePriority = FusionPriority.ENTERPRISE_FIRST;
    private boolean autoResolveConflict = false;
    private Map<String, Object> customRules = new HashMap<>();

    public FusionStrategyEntity() {
    }

    @Override
    public FusionPriority getRolePriority() {
        return rolePriority;
    }

    @Override
    public void setRolePriority(FusionPriority rolePriority) {
        this.rolePriority = rolePriority;
    }

    @Override
    public FusionPriority getActivationStepPriority() {
        return activationStepPriority;
    }

    @Override
    public void setActivationStepPriority(FusionPriority activationStepPriority) {
        this.activationStepPriority = activationStepPriority;
    }

    @Override
    public FusionPriority getMenuPriority() {
        return menuPriority;
    }

    @Override
    public void setMenuPriority(FusionPriority menuPriority) {
        this.menuPriority = menuPriority;
    }

    @Override
    public FusionPriority getCapabilityPriority() {
        return capabilityPriority;
    }

    @Override
    public void setCapabilityPriority(FusionPriority capabilityPriority) {
        this.capabilityPriority = capabilityPriority;
    }

    @Override
    public FusionPriority getRulePriority() {
        return rulePriority;
    }

    @Override
    public void setRulePriority(FusionPriority rulePriority) {
        this.rulePriority = rulePriority;
    }

    @Override
    public boolean isAutoResolveConflict() {
        return autoResolveConflict;
    }

    @Override
    public void setAutoResolveConflict(boolean autoResolveConflict) {
        this.autoResolveConflict = autoResolveConflict;
    }

    @Override
    public Map<String, Object> getCustomRules() {
        return customRules;
    }

    @Override
    public void setCustomRules(Map<String, Object> customRules) {
        this.customRules = customRules != null ? customRules : new HashMap<>();
    }
}
