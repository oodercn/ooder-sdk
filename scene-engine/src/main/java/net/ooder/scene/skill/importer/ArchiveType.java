package net.ooder.scene.skill.importer;

/**
 * 压缩包类型
 *
 * @author ooder
 * @since 2.3
 */
public enum ArchiveType {
    
    ZIP("zip", "ZIP压缩包"),
    TAR("tar", "TAR归档"),
    GZIP("gzip", "GZIP压缩");
    
    private final String code;
    private final String description;
    
    ArchiveType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
}
