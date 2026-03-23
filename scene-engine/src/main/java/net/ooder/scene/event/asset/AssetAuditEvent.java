package net.ooder.scene.event.asset;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class AssetAuditEvent extends SceneEvent {

    private final String assetId;
    private final String assetName;
    private final String assetType;
    private final String ownerId;
    private final String oldOwnerId;
    private final String newOwnerId;
    private final String oldStatus;
    private final String newStatus;
    private final String operatorId;
    private final boolean success;

    private AssetAuditEvent(Builder builder) {
        super(builder.source, builder.eventType);
        this.assetId = builder.assetId;
        this.assetName = builder.assetName;
        this.assetType = builder.assetType;
        this.ownerId = builder.ownerId;
        this.oldOwnerId = builder.oldOwnerId;
        this.newOwnerId = builder.newOwnerId;
        this.oldStatus = builder.oldStatus;
        this.newStatus = builder.newStatus;
        this.operatorId = builder.operatorId;
        this.success = builder.success;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOldOwnerId() {
        return oldOwnerId;
    }

    public String getNewOwnerId() {
        return newOwnerId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getOperatorId() {
        return operatorId;
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
        private String assetId;
        private String assetName;
        private String assetType;
        private String ownerId;
        private String oldOwnerId;
        private String newOwnerId;
        private String oldStatus;
        private String newStatus;
        private String operatorId;
        private boolean success = true;

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder eventType(SceneEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder assetId(String assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder assetName(String assetName) {
            this.assetName = assetName;
            return this;
        }

        public Builder assetType(String assetType) {
            this.assetType = assetType;
            return this;
        }

        public Builder ownerId(String ownerId) {
            this.ownerId = ownerId;
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

        public Builder oldStatus(String oldStatus) {
            this.oldStatus = oldStatus;
            return this;
        }

        public Builder newStatus(String newStatus) {
            this.newStatus = newStatus;
            return this;
        }

        public Builder operatorId(String operatorId) {
            this.operatorId = operatorId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public AssetAuditEvent build() {
            return new AssetAuditEvent(this);
        }
    }

    @Override
    public String toString() {
        return "AssetAuditEvent{" +
                "eventType=" + getEventType() +
                ", assetId='" + assetId + '\'' +
                ", assetName='" + assetName + '\'' +
                ", assetType='" + assetType + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", success=" + success +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
