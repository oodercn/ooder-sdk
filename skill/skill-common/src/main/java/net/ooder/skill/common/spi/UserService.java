package net.ooder.skill.common.spi;

import net.ooder.skill.common.spi.user.UserInfo;

import java.util.List;

public interface UserService {
    
    UserInfo getCurrentUser();
    
    UserInfo getUserById(String userId);
    
    UserInfo getUserByUsername(String username);
    
    List<UserInfo> searchUsers(String keyword);
    
    List<UserInfo> getDepartmentUsers(String departmentId);
    
    boolean authenticate(String username, String password);
    
    void updateUserInfo(String userId, UserInfo userInfo);
}
