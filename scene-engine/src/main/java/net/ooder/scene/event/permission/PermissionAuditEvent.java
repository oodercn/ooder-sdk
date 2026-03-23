package net.ooder.scene.event.permission;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class PermissionAuditEvent extends SceneEvent {

    private final String kbId;
    private final String kbName;
    private final String userId;
    private final String permission;
    private final String grantedBy;
    private final String oldOwnerId;
    private final String newOwnerId;
    private final boolean success;

    private PermissionAuditEvent(Builder builder) {
        super(builder.source, builder.eventType);
        this.kbId = builder.kbId;
        this.kbName = builder.kbName;
        this.userId = builder.userId;
        this.permission = builder.permission;
        this.grantedBy = builder.grantedBy;
        this.oldOwnerId = builder.oldOwnerId;
        this.newOwnerId = builder.newOwnerId;
        this.success = builder.success;
    }

    public String getKbId() {
        return kbId;
    }

    public String getKbName() {
        return kbName;
    }

    public String getUserId() {
        return userId;
    }

    public String getPermission() {
        return permission;
    }

    public String getGrantedBy() {
        return grantedBy;
    }

    public String getOldOwnerId() {
        return oldOwnerId;
    }

    public String getNewOwnerId() {
        return newOwnerId;
    }

    public boolean isSuccess() {
        return success;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Object source;
        private SceneEventType eventType;
        private String kbId;
        private String kbName;
        private String userId;
        private String permission;
        private String grantedBy;
        private String oldOwnerId;
        private String newOwnerId;
        private boolean success = true;

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder eventType(SceneEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder kbId(String kbId) {
            this.kbId = kbId;
            return this;
        }

        public Builder kbName(String kbName) {
            this.kbName = kbName;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder grantedBy(String grantedBy) {
            this.grantedBy = grantedBy;
            return this;
        }

        public Builder oldOwnerId(String oldOwnerId) {
            this.oldOwnerId = oldOwnerId;
            return this;
        }

        public Builder newOwnerId(String newOwnerId) {
            this.newOwnerId = newOwnerId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public PermissionAuditEvent build() {
            return new PermissionAuditEvent(this);
        }
    }

    @Override
    public String toString() {
        return "PermissionAuditEvent{" +
                "eventType=" + getEventType() +
                ", kbId='" + kbId + '\'' +
                ", userId='" + userId + '\'' +
                ", permission='" + permission + '\'' +
                ", grantedBy='" + grantedBy + '\'' +
                ", success=" + success +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
