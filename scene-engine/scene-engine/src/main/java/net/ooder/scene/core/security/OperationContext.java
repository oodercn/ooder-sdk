package net.ooder.scene.core.security;

import java.util.Map;

/**
 * 操作上下文
 * 
 * <p>记录操作的完整上下文信息，包括用户、会话、IP等信息</p>
 */
public class OperationContext {
    private String userId;          // 操作用户ID
    private String userName;        // 用户名
    private String sessionId;       // 会话ID
    private String ipAddress;       // 客户端IP
    private String userAgent;       // 客户端标识
    private String sceneId;         // 场景ID
    private String groupId;         // 场景组ID
    private String skillId;         // 技能ID
    private long timestamp;         // 操作时间
    private String requestId;       // 请求ID
    private Map<String, Object> extra;  // 扩展信息

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}