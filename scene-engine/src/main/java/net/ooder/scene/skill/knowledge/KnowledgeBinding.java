package net.ooder.scene.skill.knowledge;

/**
 * 知识库绑定信息
 *
 * <p>表示场景组与知识库的绑定关系，包含绑定状态、优先级和配置信息。</p>
 *
 * <p><b>版本历史：</b></p>
 * <ul>
 *   <li>3.1.0 - 合并自 net.ooder.scene.knowledge.KnowledgeBindingInfo</li>
 *   <li>新增字段：bindingId, boundBy, layer (替换 scope)</li>
 * </ul>
 *
 * <h3>状态说明：</h3>
 * <ul>
 *   <li><b>ACTIVE</b>: 激活状态，可用于检索</li>
 *   <li><b>INACTIVE</b>: 停用状态，不可用于检索</li>
 *   <li><b>ERROR</b>: 错误状态，绑定异常</li>
 * </ul>
 *
 * <h3>优先级说明：</h3>
 * <ul>
 *   <li>数值越高优先级越高</li>
 *   <li>默认优先级为 0</li>
 *   <li>范围：-100 到 100</li>
 * </ul>
 *
 * @author ooder
 * @version 3.2.0
 * @since 2.3.2
 */
public class KnowledgeBinding {

    /**
     * 绑定状态枚举
     */
    public enum Status {
        /** 激活状态 */
        ACTIVE,
        /** 停用状态 */
        INACTIVE,
        /** 错误状态 */
        ERROR
    }

    // ===== 合并自 KnowledgeBindingInfo 的字段 =====
    private String bindingId;
    private String sceneGroupId;
    private String knowledgeBaseId;
    private String knowledgeBaseName;
    private String boundBy;
    private int priority;
    private long bindTime;

    // ===== 来自原 KnowledgeBinding 的字段 =====
    private String kbId;
    private String kbName;
    private String layer;
    private Status status;
    private KnowledgeConfig config;

    public KnowledgeBinding() {
        this.bindingId = java.util.UUID.randomUUID().toString().replace("-", "");
        this.priority = 0;
        this.status = Status.ACTIVE;
        this.config = new KnowledgeConfig();
        this.bindTime = System.currentTimeMillis();
    }

    // ===== 合并字段的 Getter/Setter =====

    public String getBindingId() {
        return bindingId;
    }

    public void setBindingId(String bindingId) {
        this.bindingId = bindingId;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public String getKnowledgeBaseId() {
        return knowledgeBaseId != null ? knowledgeBaseId : kbId;
    }

    public void setKnowledgeBaseId(String knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
        if (this.kbId == null) {
            this.kbId = knowledgeBaseId;
        }
    }

    public String getKnowledgeBaseName() {
        return knowledgeBaseName != null ? knowledgeBaseName : kbName;
    }

    public void setKnowledgeBaseName(String knowledgeBaseName) {
        this.knowledgeBaseName = knowledgeBaseName;
        if (this.kbName == null) {
            this.kbName = knowledgeBaseName;
        }
    }

    public String getBoundBy() {
        return boundBy;
    }

    public void setBoundBy(String boundBy) {
        this.boundBy = boundBy;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        if (priority < -100 || priority > 100) {
            throw new IllegalArgumentException("priority must be between -100 and 100");
        }
        this.priority = priority;
    }

    public long getBindTime() {
        return bindTime;
    }

    public void setBindTime(long bindTime) {
        this.bindTime = bindTime;
    }

    // ===== 原 KnowledgeBinding 字段的 Getter/Setter =====

    @Deprecated
    public String getKbId() {
        return kbId;
    }

    @Deprecated
    public void setKbId(String kbId) {
        this.kbId = kbId;
    }

    @Deprecated
    public String getKbName() {
        return kbName;
    }

    @Deprecated
    public void setKbName(String kbName) {
        this.kbName = kbName;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public Status getStatus() {
        return status != null ? status : Status.ACTIVE;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public KnowledgeConfig getConfig() {
        return config != null ? config : new KnowledgeConfig();
    }

    public void setConfig(KnowledgeConfig config) {
        this.config = config;
    }

    // ===== 便捷方法 =====

    public boolean isActive() {
        return getStatus() == Status.ACTIVE;
    }

    public void activate() {
        this.status = Status.ACTIVE;
    }

    public void deactivate() {
        this.status = Status.INACTIVE;
    }

    public void markError() {
        this.status = Status.ERROR;
    }

    // ===== 向后兼容方法 =====

    @Deprecated
    public String getScope() {
        return layer;
    }

    @Deprecated
    public void setScope(String scope) {
        this.layer = scope;
    }

    @Override
    public String toString() {
        return "KnowledgeBinding{" +
            "bindingId='" + bindingId + '\'' +
            ", sceneGroupId='" + sceneGroupId + '\'' +
            ", knowledgeBaseId='" + getKnowledgeBaseId() + '\'' +
            ", knowledgeBaseName='" + getKnowledgeBaseName() + '\'' +
            ", layer='" + layer + '\'' +
            ", boundBy='" + boundBy + '\'' +
            ", priority=" + priority +
            ", bindTime=" + bindTime +
            ", status=" + status +
            '}';
    }
}
