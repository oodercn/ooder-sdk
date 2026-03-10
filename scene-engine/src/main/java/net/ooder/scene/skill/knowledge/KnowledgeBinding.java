package net.ooder.scene.skill.knowledge;

/**
 * 知识库绑定信息
 *
 * @author ooder
 * @since 2.3.2
 */
public class KnowledgeBinding {

    private String sceneGroupId;
    private String kbId;
    private String kbName;
    private String layer;
    private long bindTime;

    public KnowledgeBinding() {}

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

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

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public long getBindTime() {
        return bindTime;
    }

    public void setBindTime(long bindTime) {
        this.bindTime = bindTime;
    }
}
