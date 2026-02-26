package net.ooder.scene.event.engine;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class EngineEvent extends SceneEvent {
    
    private final String engineId;
    private final String engineName;
    private final String engineType;
    private final String status;
    private final String healthStatus;
    private final boolean healthy;
    private final String errorMessage;
    
    private EngineEvent(Object source, SceneEventType eventType, String engineId, String engineName) {
        super(source, eventType);
        this.engineId = engineId;
        this.engineName = engineName;
        this.engineType = null;
        this.status = null;
        this.healthStatus = null;
        this.healthy = true;
        this.errorMessage = null;
    }
    
    private EngineEvent(Object source, SceneEventType eventType, String engineId, String engineName,
                        String engineType, String status, String healthStatus, boolean healthy, String errorMessage) {
        super(source, eventType);
        this.engineId = engineId;
        this.engineName = engineName;
        this.engineType = engineType;
        this.status = status;
        this.healthStatus = healthStatus;
        this.healthy = healthy;
        this.errorMessage = errorMessage;
    }
    
    public static EngineEvent initialized(Object source, String engineId, String engineName) {
        return new EngineEvent(source, SceneEventType.ENGINE_INITIALIZED, engineId, engineName);
    }
    
    public static EngineEvent started(Object source, String engineId, String engineName) {
        return new EngineEvent(source, SceneEventType.ENGINE_STARTED, engineId, engineName);
    }
    
    public static EngineEvent stopped(Object source, String engineId, String engineName) {
        return new EngineEvent(source, SceneEventType.ENGINE_STOPPED, engineId, engineName);
    }
    
    public static EngineEvent destroyed(Object source, String engineId, String engineName) {
        return new EngineEvent(source, SceneEventType.ENGINE_DESTROYED, engineId, engineName);
    }
    
    public static EngineEvent configUpdated(Object source, String engineId, String engineName) {
        return new EngineEvent(source, SceneEventType.ENGINE_CONFIG_UPDATED, engineId, engineName);
    }
    
    public static EngineEvent healthCheck(Object source, String engineId, String engineName, 
                                           boolean healthy, String healthStatus) {
        return new EngineEvent(source, SceneEventType.ENGINE_HEALTH_CHECK, engineId, engineName,
                null, null, healthStatus, healthy, null);
    }
    
    public static EngineEvent registered(Object source, String engineId, String engineName, String engineType) {
        return new EngineEvent(source, SceneEventType.ENGINE_REGISTERED, engineId, engineName,
                engineType, null, null, true, null);
    }
    
    public static EngineEvent unregistered(Object source, String engineId) {
        return new EngineEvent(source, SceneEventType.ENGINE_UNREGISTERED, engineId, null);
    }
    
    public String getEngineId() {
        return engineId;
    }
    
    public String getEngineName() {
        return engineName;
    }
    
    public String getEngineType() {
        return engineType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getHealthStatus() {
        return healthStatus;
    }
    
    public boolean isHealthy() {
        return healthy;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    @Override
    public String toString() {
        return "EngineEvent{" +
                "eventType=" + getEventType() +
                ", engineId='" + engineId + '\'' +
                ", engineName='" + engineName + '\'' +
                ", engineType='" + engineType + '\'' +
                ", status='" + status + '\'' +
                ", healthy=" + healthy +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
