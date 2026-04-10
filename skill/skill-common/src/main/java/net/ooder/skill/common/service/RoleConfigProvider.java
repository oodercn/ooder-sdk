package net.ooder.skill.common.service;

import net.ooder.skill.common.model.RoleConfig;

import java.util.List;

public interface RoleConfigProvider {

    List<RoleConfig> getRoleConfigs();
    
    RoleConfig getRoleConfig(String roleId);
    
    default boolean hasRole(String roleId) {
        return getRoleConfig(roleId) != null;
    }
}
