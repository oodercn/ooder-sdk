package net.ooder.scene.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.user.Permission;
import net.ooder.scene.provider.model.user.SecurityLog;
import net.ooder.scene.provider.model.user.UserInfo;
import net.ooder.scene.provider.model.user.UserStatus;

import java.util.List;
import java.util.Map;

public interface UserProvider extends BaseProvider {
    
    Result<UserStatus> getStatus();
    
    Result<PageResult<UserInfo>> listUsers(int page, int size);
    
    Result<UserInfo> getUser(String userId);
    
    Result<UserInfo> createUser(Map<String, Object> userData);
    
    Result<UserInfo> updateUser(String userId, Map<String, Object> userData);
    
    Result<Boolean> deleteUser(String userId);
    
    Result<UserInfo> enableUser(String userId);
    
    Result<UserInfo> disableUser(String userId);
    
    Result<PageResult<Permission>> listPermissions(int page, int size);
    
    Result<Boolean> savePermissions(String userId, List<String> permissions);
    
    Result<PageResult<SecurityLog>> listSecurityLogs(int page, int size);
}
