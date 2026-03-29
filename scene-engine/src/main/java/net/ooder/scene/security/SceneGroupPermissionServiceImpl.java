package net.ooder.scene.security;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.participant.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景组权限服务实现
 * 
 * <p>基于角色的权限控制实现。</p>
 * 
 * <h3>默认权限配置：</h3>
 * <ul>
 *   <li>OWNER - 所有权限</li>
 *   <li>MANAGER - 管理权限</li>
 *   <li>COORDINATOR - 协调权限</li>
 *   <li>EMPLOYEE - 基本权限</li>
 *   <li>OBSERVER - 只读权限</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneGroupPermissionServiceImpl implements SceneGroupPermissionService {
    
    private static final Logger logger = LoggerFactory.getLogger(SceneGroupPermissionServiceImpl.class);
    
    private final SceneGroupManager sceneGroupManager;
    private final Map<Participant.Role, Set<String>> rolePermissions = new ConcurrentHashMap<>();
    
    public SceneGroupPermissionServiceImpl(SceneGroupManager sceneGroupManager) {
        this.sceneGroupManager = sceneGroupManager;
        initDefaultPermissions();
    }
    
    private void initDefaultPermissions() {
        Set<String> ownerPermissions = new HashSet<>(Arrays.asList(
            SceneGroupPermission.SCENE_GROUP_VIEW.getCode(),
            SceneGroupPermission.SCENE_GROUP_EDIT.getCode(),
            SceneGroupPermission.SCENE_GROUP_DELETE.getCode(),
            SceneGroupPermission.SCENE_GROUP_ACTIVATE.getCode(),
            SceneGroupPermission.SCENE_GROUP_SUSPEND.getCode(),
            SceneGroupPermission.PARTICIPANT_VIEW.getCode(),
            SceneGroupPermission.PARTICIPANT_INVITE.getCode(),
            SceneGroupPermission.PARTICIPANT_REMOVE.getCode(),
            SceneGroupPermission.PARTICIPANT_CHANGE_ROLE.getCode(),
            SceneGroupPermission.CAPABILITY_VIEW.getCode(),
            SceneGroupPermission.CAPABILITY_BIND.getCode(),
            SceneGroupPermission.CAPABILITY_UNBIND.getCode(),
            SceneGroupPermission.CAPABILITY_INVOKE.getCode(),
            SceneGroupPermission.KNOWLEDGE_VIEW.getCode(),
            SceneGroupPermission.KNOWLEDGE_BIND.getCode(),
            SceneGroupPermission.KNOWLEDGE_UNBIND.getCode(),
            SceneGroupPermission.KNOWLEDGE_EDIT.getCode(),
            SceneGroupPermission.TODO_VIEW.getCode(),
            SceneGroupPermission.TODO_CREATE.getCode(),
            SceneGroupPermission.TODO_ASSIGN.getCode(),
            SceneGroupPermission.TODO_APPROVE.getCode(),
            SceneGroupPermission.SNAPSHOT_VIEW.getCode(),
            SceneGroupPermission.SNAPSHOT_CREATE.getCode(),
            SceneGroupPermission.SNAPSHOT_RESTORE.getCode()
        ));
        rolePermissions.put(Participant.Role.OWNER, ownerPermissions);
        
        Set<String> managerPermissions = new HashSet<>(Arrays.asList(
            SceneGroupPermission.SCENE_GROUP_VIEW.getCode(),
            SceneGroupPermission.SCENE_GROUP_EDIT.getCode(),
            SceneGroupPermission.SCENE_GROUP_ACTIVATE.getCode(),
            SceneGroupPermission.SCENE_GROUP_SUSPEND.getCode(),
            SceneGroupPermission.PARTICIPANT_VIEW.getCode(),
            SceneGroupPermission.PARTICIPANT_INVITE.getCode(),
            SceneGroupPermission.PARTICIPANT_REMOVE.getCode(),
            SceneGroupPermission.PARTICIPANT_CHANGE_ROLE.getCode(),
            SceneGroupPermission.CAPABILITY_VIEW.getCode(),
            SceneGroupPermission.CAPABILITY_BIND.getCode(),
            SceneGroupPermission.CAPABILITY_UNBIND.getCode(),
            SceneGroupPermission.CAPABILITY_INVOKE.getCode(),
            SceneGroupPermission.KNOWLEDGE_VIEW.getCode(),
            SceneGroupPermission.KNOWLEDGE_BIND.getCode(),
            SceneGroupPermission.KNOWLEDGE_UNBIND.getCode(),
            SceneGroupPermission.KNOWLEDGE_EDIT.getCode(),
            SceneGroupPermission.TODO_VIEW.getCode(),
            SceneGroupPermission.TODO_CREATE.getCode(),
            SceneGroupPermission.TODO_ASSIGN.getCode(),
            SceneGroupPermission.TODO_APPROVE.getCode(),
            SceneGroupPermission.SNAPSHOT_VIEW.getCode(),
            SceneGroupPermission.SNAPSHOT_CREATE.getCode()
        ));
        rolePermissions.put(Participant.Role.MANAGER, managerPermissions);
        
        Set<String> coordinatorPermissions = new HashSet<>(Arrays.asList(
            SceneGroupPermission.SCENE_GROUP_VIEW.getCode(),
            SceneGroupPermission.PARTICIPANT_VIEW.getCode(),
            SceneGroupPermission.PARTICIPANT_INVITE.getCode(),
            SceneGroupPermission.CAPABILITY_VIEW.getCode(),
            SceneGroupPermission.CAPABILITY_INVOKE.getCode(),
            SceneGroupPermission.KNOWLEDGE_VIEW.getCode(),
            SceneGroupPermission.TODO_VIEW.getCode(),
            SceneGroupPermission.TODO_CREATE.getCode(),
            SceneGroupPermission.TODO_ASSIGN.getCode(),
            SceneGroupPermission.SNAPSHOT_VIEW.getCode()
        ));
        rolePermissions.put(Participant.Role.COORDINATOR, coordinatorPermissions);
        
        Set<String> employeePermissions = new HashSet<>(Arrays.asList(
            SceneGroupPermission.SCENE_GROUP_VIEW.getCode(),
            SceneGroupPermission.PARTICIPANT_VIEW.getCode(),
            SceneGroupPermission.CAPABILITY_VIEW.getCode(),
            SceneGroupPermission.CAPABILITY_INVOKE.getCode(),
            SceneGroupPermission.KNOWLEDGE_VIEW.getCode(),
            SceneGroupPermission.TODO_VIEW.getCode(),
            SceneGroupPermission.TODO_CREATE.getCode(),
            SceneGroupPermission.SNAPSHOT_VIEW.getCode()
        ));
        rolePermissions.put(Participant.Role.EMPLOYEE, employeePermissions);
        
        Set<String> observerPermissions = new HashSet<>(Arrays.asList(
            SceneGroupPermission.SCENE_GROUP_VIEW.getCode(),
            SceneGroupPermission.PARTICIPANT_VIEW.getCode(),
            SceneGroupPermission.CAPABILITY_VIEW.getCode(),
            SceneGroupPermission.KNOWLEDGE_VIEW.getCode(),
            SceneGroupPermission.TODO_VIEW.getCode(),
            SceneGroupPermission.SNAPSHOT_VIEW.getCode()
        ));
        rolePermissions.put(Participant.Role.OBSERVER, observerPermissions);
        
        Set<String> llmAssistantPermissions = new HashSet<>(Arrays.asList(
            SceneGroupPermission.SCENE_GROUP_VIEW.getCode(),
            SceneGroupPermission.CAPABILITY_VIEW.getCode(),
            SceneGroupPermission.CAPABILITY_INVOKE.getCode(),
            SceneGroupPermission.KNOWLEDGE_VIEW.getCode(),
            SceneGroupPermission.TODO_VIEW.getCode()
        ));
        rolePermissions.put(Participant.Role.LLM_ASSISTANT, llmAssistantPermissions);
    }
    
    @Override
    public boolean checkPermission(String sceneGroupId, String userId, String permission) {
        Participant.Role role = getRole(sceneGroupId, userId);
        if (role == null) {
            return false;
        }
        
        Set<String> permissions = rolePermissions.get(role);
        return permissions != null && permissions.contains(permission);
    }
    
    @Override
    public boolean checkPermission(String sceneGroupId, String userId, SceneGroupPermission permission) {
        return checkPermission(sceneGroupId, userId, permission.getCode());
    }
    
    @Override
    public Participant.Role getRole(String sceneGroupId, String userId) {
        SceneGroup sceneGroup = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (sceneGroup == null) {
            return null;
        }
        
        if (userId.equals(sceneGroup.getCreatorId())) {
            return Participant.Role.OWNER;
        }
        
        for (Participant participant : sceneGroup.getAllParticipants()) {
            if (userId.equals(participant.getUserId())) {
                return participant.getRole();
            }
        }
        
        return null;
    }
    
    @Override
    public List<String> getRolePermissions(Participant.Role role) {
        Set<String> permissions = rolePermissions.get(role);
        return permissions != null ? new ArrayList<>(permissions) : Collections.emptyList();
    }
    
    @Override
    public void setRolePermissions(Participant.Role role, List<String> permissions) {
        rolePermissions.put(role, new HashSet<>(permissions));
        logger.info("Updated permissions for role: {}", role);
    }
    
    @Override
    public boolean canExecute(String sceneGroupId, String userId, String operation, String resource) {
        String permission = resource.toLowerCase() + "_" + operation.toLowerCase();
        return checkPermission(sceneGroupId, userId, permission);
    }
    
    @Override
    public List<String> getAccessibleSceneGroups(String userId) {
        List<String> accessibleGroups = new ArrayList<>();
        
        for (SceneGroup sceneGroup : sceneGroupManager.getAllSceneGroups()) {
            if (getRole(sceneGroup.getSceneGroupId(), userId) != null) {
                accessibleGroups.add(sceneGroup.getSceneGroupId());
            }
        }
        
        return accessibleGroups;
    }
    
    @Override
    public boolean isMember(String sceneGroupId, String userId) {
        return getRole(sceneGroupId, userId) != null;
    }
    
    @Override
    public boolean isManager(String sceneGroupId, String userId) {
        Participant.Role role = getRole(sceneGroupId, userId);
        return role == Participant.Role.OWNER || role == Participant.Role.MANAGER;
    }
    
    @Override
    public boolean isOwner(String sceneGroupId, String userId) {
        Participant.Role role = getRole(sceneGroupId, userId);
        return role == Participant.Role.OWNER;
    }
}
