package net.ooder.sdk.service.binding;

import java.util.*;

public class DeviceBinding {

    private final String bindingId;
    private final String sourceDevice;
    private final String sourceCap;
    private final String targetDevice;
    private final String targetCap;
    private BindingType bindingType;
    private BindingStatus status;
    private final Map<String, Object> config = new HashMap<>();
    private final List<BindingHistory> history = new ArrayList<>();
    private final long createdAt;
    private long updatedAt;

    public DeviceBinding(String sourceDevice, String sourceCap,
                         String targetDevice, String targetCap,
                         BindingType bindingType) {
        this.bindingId = generateBindingId();
        this.sourceDevice = sourceDevice;
        this.sourceCap = sourceCap;
        this.targetDevice = targetDevice;
        this.targetCap = targetCap;
        this.bindingType = bindingType;
        this.status = BindingStatus.ACTIVE;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    private static String generateBindingId() {
        return "bind-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String getBindingId() { return bindingId; }
    public String getSourceDevice() { return sourceDevice; }
    public String getSourceCap() { return sourceCap; }
    public String getTargetDevice() { return targetDevice; }
    public String getTargetCap() { return targetCap; }
    public BindingType getBindingType() { return bindingType; }
    public BindingStatus getStatus() { return status; }
    public Map<String, Object> getConfig() { return config; }
    public List<BindingHistory> getHistory() { return history; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    public void setBindingType(BindingType bindingType) {
        this.bindingType = bindingType;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setStatus(BindingStatus status) {
        BindingStatus oldStatus = this.status;
        this.status = status;
        this.updatedAt = System.currentTimeMillis();
        addHistory("STATUS_CHANGE", oldStatus.name() + " -> " + status.name());
    }

    public void setConfig(String key, Object value) {
        this.config.put(key, value);
        this.updatedAt = System.currentTimeMillis();
    }

    public void setConfig(Map<String, Object> config) {
        this.config.clear();
        if (config != null) {
            this.config.putAll(config);
        }
        this.updatedAt = System.currentTimeMillis();
    }

    public void addHistory(String action, String description) {
        history.add(new BindingHistory(action, description, System.currentTimeMillis()));
    }

    public boolean isStrongBinding() {
        return bindingType == BindingType.STRONG;
    }

    public boolean isWeakBinding() {
        return bindingType == BindingType.WEAK;
    }

    public boolean isActive() {
        return status == BindingStatus.ACTIVE;
    }

    public boolean isSuspended() {
        return status == BindingStatus.SUSPENDED;
    }

    public boolean isFailed() {
        return status == BindingStatus.FAILED;
    }

    public enum BindingType {
        STRONG("strong", "强绑定 - 不可拆分，只能故障设定"),
        WEAK("weak", "弱绑定 - 用户可调整");

        private final String code;
        private final String description;

        BindingType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }

        public static BindingType fromCode(String code) {
            for (BindingType type : values()) {
                if (type.code.equalsIgnoreCase(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown binding type: " + code);
        }
    }

    public enum BindingStatus {
        ACTIVE("active", "活动"),
        SUSPENDED("suspended", "暂停"),
        FAILED("failed", "故障"),
        REMOVED("removed", "已移除");

        private final String code;
        private final String description;

        BindingStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }

        public static BindingStatus fromCode(String code) {
            for (BindingStatus status : values()) {
                if (status.code.equalsIgnoreCase(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown binding status: " + code);
        }
    }

    public static class BindingHistory {
        private final String action;
        private final String description;
        private final long timestamp;

        public BindingHistory(String action, String description, long timestamp) {
            this.action = action;
            this.description = description;
            this.timestamp = timestamp;
        }

        public String getAction() { return action; }
        public String getDescription() { return description; }
        public long getTimestamp() { return timestamp; }
    }

    @Override
    public String toString() {
        return String.format("DeviceBinding{id=%s, source=%s/%s, target=%s/%s, type=%s, status=%s}",
            bindingId, sourceDevice, sourceCap, targetDevice, targetCap, bindingType, status);
    }
}
