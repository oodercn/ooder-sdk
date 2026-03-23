package net.ooder.scene.event.share;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class ShareAuditEvent extends SceneEvent {

    private final String shareId;
    private final String shareCode;
    private final String kbId;
    private final String kbName;
    private final String creatorId;
    private final String visitorId;
    private final String action;
    private final boolean success;

    private ShareAuditEvent(Builder builder) {
        super(builder.source, builder.eventType);
        this.shareId = builder.shareId;
        this.shareCode = builder.shareCode;
        this.kbId = builder.kbId;
        this.kbName = builder.kbName;
        this.creatorId = builder.creatorId;
        this.visitorId = builder.visitorId;
        this.action = builder.action;
        this.success = builder.success;
    }

    public String getShareId() {
        return shareId;
    }

    public String getShareCode() {
        return shareCode;
    }

    public String getKbId() {
        return kbId;
    }

    public String getKbName() {
        return kbName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getVisitorId() {
        return visitorId;
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
        private String shareId;
        private String shareCode;
        private String kbId;
        private String kbName;
        private String creatorId;
        private String visitorId;
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

        public Builder shareId(String shareId) {
            this.shareId = shareId;
            return this;
        }

        public Builder shareCode(String shareCode) {
            this.shareCode = shareCode;
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

        public Builder creatorId(String creatorId) {
            this.creatorId = creatorId;
            return this;
        }

        public Builder visitorId(String visitorId) {
            this.visitorId = visitorId;
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

        public ShareAuditEvent build() {
            return new ShareAuditEvent(this);
        }
    }

    @Override
    public String toString() {
        return "ShareAuditEvent{" +
                "eventType=" + getEventType() +
                ", shareId='" + shareId + '\'' +
                ", shareCode='" + shareCode + '\'' +
                ", kbId='" + kbId + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", success=" + success +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
