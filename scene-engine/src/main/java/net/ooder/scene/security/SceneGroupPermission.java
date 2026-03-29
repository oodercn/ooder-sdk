package net.ooder.scene.security;

/**
 * 场景组权限枚举
 * 
 * <p>定义场景组级别的权限类型。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public enum SceneGroupPermission {
    
    // ========== 场景组管理 ==========
    
    SCENE_GROUP_VIEW("scene_group_view", "查看场景组"),
    SCENE_GROUP_EDIT("scene_group_edit", "编辑场景组"),
    SCENE_GROUP_DELETE("scene_group_delete", "删除场景组"),
    SCENE_GROUP_ACTIVATE("scene_group_activate", "激活场景组"),
    SCENE_GROUP_SUSPEND("scene_group_suspend", "暂停场景组"),
    
    // ========== 参与者管理 ==========
    
    PARTICIPANT_VIEW("participant_view", "查看参与者"),
    PARTICIPANT_INVITE("participant_invite", "邀请参与者"),
    PARTICIPANT_REMOVE("participant_remove", "移除参与者"),
    PARTICIPANT_CHANGE_ROLE("participant_change_role", "变更参与者角色"),
    
    // ========== 能力管理 ==========
    
    CAPABILITY_VIEW("capability_view", "查看能力"),
    CAPABILITY_BIND("capability_bind", "绑定能力"),
    CAPABILITY_UNBIND("capability_unbind", "解绑能力"),
    CAPABILITY_INVOKE("capability_invoke", "调用能力"),
    
    // ========== 知识库管理 ==========
    
    KNOWLEDGE_VIEW("knowledge_view", "查看知识库"),
    KNOWLEDGE_BIND("knowledge_bind", "绑定知识库"),
    KNOWLEDGE_UNBIND("knowledge_unbind", "解绑知识库"),
    KNOWLEDGE_EDIT("knowledge_edit", "编辑知识库"),
    
    // ========== 待办管理 ==========
    
    TODO_VIEW("todo_view", "查看待办"),
    TODO_CREATE("todo_create", "创建待办"),
    TODO_ASSIGN("todo_assign", "分配待办"),
    TODO_APPROVE("todo_approve", "审批待办"),
    
    // ========== 快照管理 ==========
    
    SNAPSHOT_VIEW("snapshot_view", "查看快照"),
    SNAPSHOT_CREATE("snapshot_create", "创建快照"),
    SNAPSHOT_RESTORE("snapshot_restore", "恢复快照");
    
    private final String code;
    private final String displayName;
    
    SceneGroupPermission(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static SceneGroupPermission fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (SceneGroupPermission permission : values()) {
            if (permission.code.equals(code)) {
                return permission;
            }
        }
        return null;
    }
}
