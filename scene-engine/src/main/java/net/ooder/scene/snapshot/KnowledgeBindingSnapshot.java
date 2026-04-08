package net.ooder.scene.snapshot;

/**
 * 知识库绑定快照
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class KnowledgeBindingSnapshot {

    private String kbId;
    private String kbName;
    private String scope;
    private int priority;
    private long bindTime;

    public KnowledgeBindingSnapshot() {}

    public String getKbId() {
        return kbId;
    }

    public void setKbId(String kbId) {
        this.kbId = kbId;
    }

    public String getKbName() {
        return kbName;
    }

    public void setKbName(String kbName) {
        this.kbName = kbName;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getBindTime() {
        return bindTime;
    }

    public void setBindTime(long bindTime) {
        this.bindTime = bindTime;
    }
}
