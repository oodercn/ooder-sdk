package net.ooder.skill.common.spi;

import java.util.List;

public interface PermissionService {
    
    boolean hasPermission(String userId, String permission);
    
    boolean hasScenePermission(String userId, String sceneId, String permission);
    
    List<String> getUserPermissions(String userId, String sceneId);
    
    void grantPermission(String userId, String sceneId, String permission);
    
    void revokePermission(String userId, String sceneId, String permission);
}
