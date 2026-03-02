package net.ooder.scene.skill.vector;

import java.util.Map;

/**
 * 向量数据
 * 包含向量数据和元数据信息
 *
 * @author ooder
 * @since 2.3
 */
public class VectorData {
    
    /** 向量唯一标识 */
    private String id;
    
    /** 向量数据 */
    private float[] vector;
    
    /** 元数据信息 */
    private Map<String, Object> metadata;
    
    public VectorData() {}
    
    public VectorData(String id, float[] vector, Map<String, Object> metadata) {
        this.id = id;
        this.vector = vector;
        this.metadata = metadata;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public float[] getVector() {
        return vector;
    }
    
    public void setVector(float[] vector) {
        this.vector = vector;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
