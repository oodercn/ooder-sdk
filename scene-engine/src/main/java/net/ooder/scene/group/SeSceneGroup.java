package net.ooder.scene.group;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SE 场景组 - 简化版
 * 
 * <p>引用 SDK SceneGroup，只保留 SE 特有的业务字段。</p>
 * 
 * <h3>职责划分：</h3>
 * <ul>
 *   <li>SDK SceneGroup: Agent 集群管理、用户场景组、技能/能力/知识库绑定</li>
 *   <li>SE SceneGroup: 业务上下文、工作流状态、审计日志</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SeSceneGroup {
    
    // ========== SDK 引用 ==========
    
    /** SDK SceneGroup ID（引用） */
    private final String sdkSceneGroupId;
    
    /** 场景ID */
    private final String sceneId;
    
    // ========== SE 特有字段 ==========
    
    /** 业务上下文 */
    private final Map<String, Object> businessContext = new ConcurrentHashMap<>();
    
    /** 工作流状态 */
    private final Map<String, Object> workflowState = new ConcurrentHashMap<>();
    
    /** 审计日志 */
    private final List<SceneGroupEvent> auditLog = new CopyOnWriteArrayList<>();
    
    /** 审计日志最大条数 */
    private static final int MAX_AUDIT_LOG_SIZE = 1000;
    
    // ========== 时间信息 ==========
    
    /** 创建时间 */
    private final Instant createTime;
    
    /** 最后更新时间 */
    private volatile Instant lastUpdateTime;
    
    /**
     * 构造函数
     */
    public SeSceneGroup(String sdkSceneGroupId, String sceneId) {
        this.sdkSceneGroupId = sdkSceneGroupId;
        this.sceneId = sceneId;
        this.createTime = Instant.now();
        this.lastUpdateTime = this.createTime;
    }
    
    // ========== SDK 引用 ==========
    
    public String getSdkSceneGroupId() {
        return sdkSceneGroupId;
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    // ========== 业务上下文 ==========
    
    public Map<String, Object> getBusinessContext() {
        return businessContext;
    }
    
    public Object getBusinessContext(String key) {
        return businessContext.get(key);
    }
    
    public void setBusinessContext(String key, Object value) {
        businessContext.put(key, value);
        lastUpdateTime = Instant.now();
    }
    
    public void removeBusinessContext(String key) {
        businessContext.remove(key);
        lastUpdateTime = Instant.now();
    }
    
    // ========== 工作流状态 ==========
    
    public Map<String, Object> getWorkflowState() {
        return workflowState;
    }
    
    public Object getWorkflowState(String key) {
        return workflowState.get(key);
    }
    
    public void setWorkflowState(String key, Object value) {
        workflowState.put(key, value);
        lastUpdateTime = Instant.now();
    }
    
    public void removeWorkflowState(String key) {
        workflowState.remove(key);
        lastUpdateTime = Instant.now();
    }
    
    // ========== 审计日志 ==========
    
    public List<SceneGroupEvent> getAuditLog() {
        return auditLog;
    }
    
    public List<SceneGroupEvent> getAuditLog(int limit) {
        int size = Math.min(limit, auditLog.size());
        return auditLog.subList(0, size);
    }
    
    public void addAuditLog(SceneGroupEvent event) {
        if (event == null) {
            return;
        }
        
        auditLog.add(0, event);
        
        while (auditLog.size() > MAX_AUDIT_LOG_SIZE) {
            auditLog.remove(auditLog.size() - 1);
        }
        
        lastUpdateTime = Instant.now();
    }
    
    public void clearAuditLog() {
        auditLog.clear();
        lastUpdateTime = Instant.now();
    }
    
    // ========== 时间信息 ==========
    
    public Instant getCreateTime() {
        return createTime;
    }
    
    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    // ========== 工具方法 ==========
    
    @Override
    public String toString() {
        return String.format("SeSceneGroup{sdkSceneGroupId=%s, sceneId=%s, auditLogSize=%d}",
            sdkSceneGroupId, sceneId, auditLog.size());
    }
}
