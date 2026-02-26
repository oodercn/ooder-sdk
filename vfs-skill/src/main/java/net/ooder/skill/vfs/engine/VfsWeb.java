package net.ooder.skill.vfs.engine;

/**
 * VFS Web 层
 * 可视化配置
 */
public class VfsWeb {
    
    private final VfsEngine engine;
    
    public VfsWeb(VfsEngine engine) {
        this.engine = engine;
    }
    
    /**
     * 获取可视化 Schema
     * @return Schema 配置
     */
    public String getSchema() {
        // 返回可视化配置（简化版）
        return "{\"type\":\"vfs\",\"forms\":[],\"tables\":[]}";
    }
}
