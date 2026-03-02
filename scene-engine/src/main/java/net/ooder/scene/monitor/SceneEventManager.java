package net.ooder.scene.monitor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 场景事件管理接口
 * 提供场景运行时事件的查询功能
 *
 * @author ooder
 * @since 2.3
 */
public interface SceneEventManager {
    
    /**
     * 获取场景事件
     * @param sceneId 场景ID
     * @param limit 限制条数
     * @return 事件列表
     */
    CompletableFuture<List<SceneEvent>> getEvents(String sceneId, int limit);
    
    /**
     * 根据级别获取事件
     * @param sceneId 场景ID
     * @param level 事件级别 (INFO, WARNING, ERROR)
     * @param limit 限制条数
     * @return 事件列表
     */
    CompletableFuture<List<SceneEvent>> getEventsByLevel(String sceneId, String level, int limit);
}

/**
 * 场景事件
 */
class SceneEvent {
    private String eventId;
    private String sceneId;
    private long timestamp;
    private String type;       // SCENE_START, SERVICE_READY, CAPABILITY_REGISTER, etc.
    private String level;      // INFO, WARNING, ERROR
    private String message;
    private String source;     // 事件来源
    private Map<String, Object> details;
    
    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
