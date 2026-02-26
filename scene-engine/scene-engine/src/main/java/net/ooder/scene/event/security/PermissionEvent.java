package net.ooder.scene.event.security;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class PermissionEvent extends SceneEvent {
    
    private final String roleId;
    private final String permission;
    private final boolean granted;
    
    private PermissionEvent(Object source, SceneEventType eventType, String roleId, String permission, boolean granted) {
        super(source, eventType);
        this.roleId = roleId;
        this.permission = permission;
        this.granted = granted;
    }
    
    public static PermissionEvent granted(Object source, String roleId, String permission) {
        return new PermissionEvent(source, SceneEventType.PERMISSION_GRANTED, roleId, permission, true);
    }
    
    public static PermissionEvent revoked(Object source, String roleId, String permission) {
        return new PermissionEvent(source, SceneEventType.PERMISSION_REVOKED, roleId, permission, false);
    }
    
    public String getRoleId() {
        return roleId;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public boolean isGranted() {
        return granted;
    }
    
    @Override
    public String toString() {
        return "PermissionEvent{" +
                "eventType=" + getEventType() +
                ", roleId='" + roleId + '\'' +
                ", permission='" + permission + '\'' +
                ", granted=" + granted +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
