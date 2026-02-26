package net.ooder.scene.event.capability;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class CapabilityEvent extends SceneEvent {
    
    private final String capId;
    private final String capName;
    private final String providerId;
    private final String requestId;
    private final boolean success;
    private final String errorMessage;
    private final int syncedCount;
    
    private CapabilityEvent(Object source, SceneEventType eventType, String capId, String capName) {
        super(source, eventType);
        this.capId = capId;
        this.capName = capName;
        this.providerId = null;
        this.requestId = null;
        this.success = true;
        this.errorMessage = null;
        this.syncedCount = 0;
    }
    
    private CapabilityEvent(Object source, SceneEventType eventType, String capId, String capName,
                            String providerId, String requestId, boolean success, String errorMessage, int syncedCount) {
        super(source, eventType);
        this.capId = capId;
        this.capName = capName;
        this.providerId = providerId;
        this.requestId = requestId;
        this.success = success;
        this.errorMessage = errorMessage;
        this.syncedCount = syncedCount;
    }
    
    public static CapabilityEvent registered(Object source, String capId, String capName, String providerId) {
        return new CapabilityEvent(source, SceneEventType.CAPABILITY_REGISTERED, capId, capName,
                providerId, null, true, null, 0);
    }
    
    public static CapabilityEvent unregistered(Object source, String capId, String capName) {
        return new CapabilityEvent(source, SceneEventType.CAPABILITY_UNREGISTERED, capId, capName);
    }
    
    public static CapabilityEvent invoked(Object source, String capId, String capName, String requestId) {
        return new CapabilityEvent(source, SceneEventType.CAPABILITY_INVOKED, capId, capName,
                null, requestId, true, null, 0);
    }
    
    public static CapabilityEvent invocationFailed(Object source, String capId, String requestId, String error) {
        return new CapabilityEvent(source, SceneEventType.CAPABILITY_INVOCATION_FAILED, capId, null,
                null, requestId, false, error, 0);
    }
    
    public static CapabilityEvent syncCompleted(Object source, int syncedCount) {
        return new CapabilityEvent(source, SceneEventType.CAPABILITY_SYNC_COMPLETED, null, null,
                null, null, true, null, syncedCount);
    }
    
    public String getCapId() {
        return capId;
    }
    
    public String getCapName() {
        return capName;
    }
    
    public String getProviderId() {
        return providerId;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public int getSyncedCount() {
        return syncedCount;
    }
    
    @Override
    public String toString() {
        return "CapabilityEvent{" +
                "eventType=" + getEventType() +
                ", capId='" + capId + '\'' +
                ", capName='" + capName + '\'' +
                ", providerId='" + providerId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", success=" + success +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
