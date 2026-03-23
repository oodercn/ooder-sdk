package net.ooder.scene.knowledge;

import java.util.UUID;

public class KnowledgeBindingInfo {

    private String bindingId;
    private String sceneGroupId;
    private String knowledgeBaseId;
    private String knowledgeBaseName;
    private BindingScope scope;
    private int priority;
    private long bindTime;
    private String boundBy;

    public KnowledgeBindingInfo() {
        this.bindingId = UUID.randomUUID().toString().replace("-", "");
        this.scope = BindingScope.SCENE_GROUP;
        this.priority = 0;
        this.bindTime = System.currentTimeMillis();
    }

    public KnowledgeBindingInfo(String sceneGroupId, String knowledgeBaseId) {
        this();
        this.sceneGroupId = sceneGroupId;
        this.knowledgeBaseId = knowledgeBaseId;
    }

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
        return knowledgeBaseId;
    }

    public void setKnowledgeBaseId(String knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public String getKnowledgeBaseName() {
        return knowledgeBaseName;
    }

    public void setKnowledgeBaseName(String knowledgeBaseName) {
        this.knowledgeBaseName = knowledgeBaseName;
    }

    public BindingScope getScope() {
        return scope;
    }

    public void setScope(BindingScope scope) {
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

    public String getBoundBy() {
        return boundBy;
    }

    public void setBoundBy(String boundBy) {
        this.boundBy = boundBy;
    }

    @Override
    public String toString() {
        return "KnowledgeBindingInfo{" +
                "bindingId='" + bindingId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", knowledgeBaseId='" + knowledgeBaseId + '\'' +
                ", scope=" + scope +
                ", priority=" + priority +
                '}';
    }
}
