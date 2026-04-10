package net.ooder.skill.common.service;

import net.ooder.skill.common.model.RoleConfig;

import java.util.List;

public interface AuthServiceConfig {

    void setUserInfoProvider(AuthService.UserInfoProvider provider);
    
    void addRoleConfig(RoleConfig config);
    
    void setRoleConfigs(List<RoleConfig> configs);
    
    List<RoleConfig> getRoleConfigs();
    
    RoleConfig getRoleConfig(String roleId);
}
