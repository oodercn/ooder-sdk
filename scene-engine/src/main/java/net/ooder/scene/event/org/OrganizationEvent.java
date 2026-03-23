package net.ooder.scene.event.org;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class OrganizationEvent extends SceneEvent {

    private final String companyId;
    private final String companyName;
    private final String departmentId;
    private final String departmentName;
    private final String userId;
    private final String userName;
    private final String operatorId;
    private final String action;
    private final boolean success;

    private OrganizationEvent(Builder builder) {
        super(builder.source, builder.eventType);
        this.companyId = builder.companyId;
        this.companyName = builder.companyName;
        this.departmentId = builder.departmentId;
        this.departmentName = builder.departmentName;
        this.userId = builder.userId;
        this.userName = builder.userName;
        this.operatorId = builder.operatorId;
        this.action = builder.action;
        this.success = builder.success;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public String getAction() {
        return action;
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
        private String companyId;
        private String companyName;
        private String departmentId;
        private String departmentName;
        private String userId;
        private String userName;
        private String operatorId;
        private String action;
        private boolean success = true;

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder eventType(SceneEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder companyId(String companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder departmentId(String departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public Builder departmentName(String departmentName) {
            this.departmentName = departmentName;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder operatorId(String operatorId) {
            this.operatorId = operatorId;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public OrganizationEvent build() {
            return new OrganizationEvent(this);
        }
    }

    @Override
    public String toString() {
        return "OrganizationEvent{" +
                "eventType=" + getEventType() +
                ", companyId='" + companyId + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", userId='" + userId + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", success=" + success +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
