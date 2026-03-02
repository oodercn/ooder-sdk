package net.ooder.scene.monitor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 场景日志管理接口
 * 提供场景运行时日志的查询功能
 *
 * @author ooder
 * @since 2.3
 */
public interface SceneLogManager {
    
    /**
     * 获取场景日志
     * @param sceneId 场景ID
     * @param level 日志级别
     * @param limit 限制条数
     * @return 日志列表
     */
    CompletableFuture<List<SceneLog>> getLogs(String sceneId, String level, int limit);
    
    /**
     * 搜索日志
     * @param sceneId 场景ID
     * @param keyword 关键词
     * @param limit 限制条数
     * @return 日志列表
     */
    CompletableFuture<List<SceneLog>> searchLogs(String sceneId, String keyword, int limit);
}

/**
 * 场景日志
 */
class SceneLog {
    private long timestamp;
    private String level;
    private String message;
    private String sceneId;
    private String thread;
    private String logger;
    private String stackTrace;  // 如果是ERROR级别
    
    // Getters and Setters
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getThread() { return thread; }
    public void setThread(String thread) { this.thread = thread; }
    public String getLogger() { return logger; }
    public void setLogger(String logger) { this.logger = logger; }
    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }
}
