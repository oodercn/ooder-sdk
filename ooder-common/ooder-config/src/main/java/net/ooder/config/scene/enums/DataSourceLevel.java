package net.ooder.config.scene.enums;

public enum DataSourceLevel {
    
    READONLY(1, "只读", "仅允许查询操作"),
    READ_WRITE(2, "读写", "允许查询和修改操作"),
    ADMIN(3, "管理", "允许所有操作包括DDL"),
    SUPER(4, "超级", "无限制，包括系统操作");
    
    private final int level;
    private final String name;
    private final String description;
    
    DataSourceLevel(int level, String name, String description) {
        this.level = level;
        this.name = name;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean canRead() {
        return level >= READONLY.level;
    }
    
    public boolean canWrite() {
        return level >= READ_WRITE.level;
    }
    
    public boolean canAdmin() {
        return level >= ADMIN.level;
    }
    
    public boolean canSuper() {
        return level >= SUPER.level;
    }
    
    public boolean includes(DataSourceLevel other) {
        return this.level >= other.level;
    }
    
    public static DataSourceLevel fromLevel(int level) {
        for (DataSourceLevel dsLevel : values()) {
            if (dsLevel.level == level) {
                return dsLevel;
            }
        }
        return READONLY;
    }
    
    public static DataSourceLevel fromName(String name) {
        if (name == null) {
            return READONLY;
        }
        for (DataSourceLevel level : values()) {
            if (level.name.equalsIgnoreCase(name) || 
                level.name().equalsIgnoreCase(name)) {
                return level;
            }
        }
        return READONLY;
    }
}
