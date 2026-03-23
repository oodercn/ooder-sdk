package net.ooder.scene.event.knowledge;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class KnowledgeBaseAuditEvent extends SceneEvent {

    private final String kbId;
    private final String kbName;
    private final String ownerId;
    private final String docId;
    private final String docTitle;
    private final String operatorId;
    private final String action;
    private final boolean success;

    private KnowledgeBaseAuditEvent(Builder builder) {
        super(builder.source, builder.eventType);
        this.kbId = builder.kbId;
        this.kbName = builder.kbName;
        this.ownerId = builder.ownerId;
        this.docId = builder.docId;
        this.docTitle = builder.docTitle;
        this.operatorId = builder.operatorId;
        this.action = builder.action;
        this.success = builder.success;
    }

    public String getKbId() {
        return kbId;
    }

    public String getKbName() {
        return kbName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getDocId() {
        return docId;
    }

    public String getDocTitle() {
        return docTitle;
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
        private String kbId;
        private String kbName;
        private String ownerId;
        private String docId;
        private String docTitle;
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

        public Builder kbId(String kbId) {
            this.kbId = kbId;
            return this;
        }

        public Builder kbName(String kbName) {
            this.kbName = kbName;
            return this;
        }

        public Builder ownerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder docId(String docId) {
            this.docId = docId;
            return this;
        }

        public Builder docTitle(String docTitle) {
            this.docTitle = docTitle;
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

        public KnowledgeBaseAuditEvent build() {
            return new KnowledgeBaseAuditEvent(this);
        }
    }

    @Override
    public String toString() {
        return "KnowledgeBaseAuditEvent{" +
                "eventType=" + getEventType() +
                ", kbId='" + kbId + '\'' +
                ", kbName='" + kbName + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", docId='" + docId + '\'' +
                ", success=" + success +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
