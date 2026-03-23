package net.ooder.scene.event.workflow;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class WorkflowEvent extends SceneEvent {

    private final String workflowId;
    private final String workflowName;
    private final String executionId;
    private final String operatorId;
    private final String status;
    private final String errorMessage;
    private final boolean success;

    private WorkflowEvent(Builder builder) {
        super(builder.source, builder.eventType);
        this.workflowId = builder.workflowId;
        this.workflowName = builder.workflowName;
        this.executionId = builder.executionId;
        this.operatorId = builder.operatorId;
        this.status = builder.status;
        this.errorMessage = builder.errorMessage;
        this.success = builder.success;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
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
        private String workflowId;
        private String workflowName;
        private String executionId;
        private String operatorId;
        private String status;
        private String errorMessage;
        private boolean success = true;

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder eventType(SceneEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder workflowId(String workflowId) {
            this.workflowId = workflowId;
            return this;
        }

        public Builder workflowName(String workflowName) {
            this.workflowName = workflowName;
            return this;
        }

        public Builder executionId(String executionId) {
            this.executionId = executionId;
            return this;
        }

        public Builder operatorId(String operatorId) {
            this.operatorId = operatorId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public WorkflowEvent build() {
            return new WorkflowEvent(this);
        }
    }

    @Override
    public String toString() {
        return "WorkflowEvent{" +
                "eventType=" + getEventType() +
                ", workflowId='" + workflowId + '\'' +
                ", workflowName='" + workflowName + '\'' +
                ", executionId='" + executionId + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", status='" + status + '\'' +
                ", success=" + success +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
