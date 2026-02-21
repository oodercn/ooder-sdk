package net.ooder.org.datasource;

public enum DataSourceType {
    JSON("json", "本地JSON文件", "开发测试、离线环境"),
    DATABASE("database", "数据库", "传统企业应用"),
    DINGTALK("dingtalk", "钉钉组织架构", "钉钉用户"),
    FEISHU("feishu", "飞书组织架构", "飞书用户"),
    WECOM("wecom", "企业微信通讯录", "企业微信用户");
    
    private final String code;
    private final String name;
    private final String description;
    
    DataSourceType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static DataSourceType fromCode(String code) {
        if (code == null) {
            return JSON;
        }
        for (DataSourceType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return JSON;
    }
    
    public boolean isSkillsBased() {
        return this == DINGTALK || this == FEISHU || this == WECOM;
    }
    
    public boolean isLocalMock() {
        return this == JSON;
    }
}
