package net.ooder.scene.event.scenegroup;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class SceneGroupAuditEvent extends SceneEvent {

    private final String groupId;
    private final String groupName;
    private final String operatorId;
    private final String participantId;
    private final String participantRole;
    private final String capabilityId;
    private final String knowledgeId;
    private final String reason;
    private final boolean success;

    private SceneGroupAuditEvent(Builder builder) {
        super(builder.source, builder.eventType);
        this.groupId = builder.groupId;
        this.groupName = builder.groupName;
        this.operatorId = builder.operatorId;
        this.participantId = builder.participantId;
        this.participantRole = builder.participantRole;
        this.capabilityId = builder.capabilityId;
        this.knowledgeId = builder.knowledgeId;
        this.reason = builder.reason;
        this.success = builder.success;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getParticipantRole() {
        return participantRole;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public String getKnowledgeId() {
        return knowledgeId;
    }

    public String getReason() {
        return reason;
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
        private String groupId;
        private String groupName;
        private String operatorId;
        private String participantId;
        private String participantRole;
        private String capabilityId;
        private String knowledgeId;
        private String reason;
        private boolean success = true;

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder eventType(SceneEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder groupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Builder operatorId(String operatorId) {
            this.operatorId = operatorId;
            return this;
        }

        public Builder participantId(String participantId) {
            this.participantId = participantId;
            return this;
        }

        public Builder participantRole(String participantRole) {
            this.participantRole = participantRole;
            return this;
        }

        public Builder capabilityId(String capabilityId) {
            this.capabilityId = capabilityId;
            return this;
        }

        public Builder knowledgeId(String knowledgeId) {
            this.knowledgeId = knowledgeId;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public SceneGroupAuditEvent build() {
            return new SceneGroupAuditEvent(this);
        }
    }

    @Override
    public String toString() {
        return "SceneGroupAuditEvent{" +
                "eventType=" + getEventType() +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", participantId='" + participantId + '\'' +
                ", success=" + success +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
