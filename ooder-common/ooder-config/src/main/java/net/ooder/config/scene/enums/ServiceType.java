package net.ooder.config.scene.enums;

import net.ooder.config.scene.docs.EnumDocument;

@EnumDocument(
    name = "服务类型",
    description = "定义系统支持的服务类型，每个服务类型对应一个功能模块",
    installGuide = "无需安装，系统内置枚举。各服务模块需要实现对应的客户端类。",
    startupGuide = "1. 在场景配置文件中配置对应服务节点\n" +
                   "2. 通过 SceneFactory.getXxxClient() 获取客户端\n" +
                   "3. 调用客户端方法使用服务",
    configExample = "# YAML配置示例\n" +
                    "scene:\n" +
                    "  org:\n" +
                    "    configName: org\n" +
                    "    capabilities:\n" +
                    "      org-query:\n" +
                    "        endpoint: http://localhost:8080/api/org"
)
public enum ServiceType {
    
    ORG("org", "组织机构服务", "OrgClient", 
        "提供组织机构、人员、角色等管理功能",
        new String[]{"org-query", "org-admin", "user-auth"}),
    
    VFS("vfs", "虚拟文件系统", "VfsClient",
        "提供文件存储、版本管理、同步等功能",
        new String[]{"vfs-client", "vfs-store", "vfs-sync"}),
    
    MSG("msg", "消息服务", "MsgServiceClient",
        "提供消息推送、P2P通信、Topic订阅等功能",
        new String[]{"msg-push", "msg-p2p", "msg-topic"}),
    
    MQTT("mqtt", "MQTT服务", "MqttClient",
        "提供MQTT协议的消息通信功能",
        new String[]{"mqtt-pub", "mqtt-sub", "mqtt-broadcast"}),
    
    AI("ai", "AI服务", "AIClient",
        "提供AI推理、模型管理等功能",
        new String[]{"ai-inference", "ai-model", "ai-training"}),
    
    DB("db", "数据库服务", "DbClient",
        "提供数据库连接和管理功能",
        new String[]{"db-query", "db-admin", "db-backup"});
    
    private final String code;
    private final String displayName;
    private final String clientClass;
    private final String description;
    private final String[] defaultCapabilities;
    
    ServiceType(String code, String displayName, String clientClass, 
                String description, String[] defaultCapabilities) {
        this.code = code;
        this.displayName = displayName;
        this.clientClass = clientClass;
        this.description = description;
        this.defaultCapabilities = defaultCapabilities;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getClientClass() {
        return clientClass;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String[] getDefaultCapabilities() {
        return defaultCapabilities;
    }
    
    public static ServiceType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ServiceType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
    
    public boolean hasCapability(String capabilityCode) {
        if (capabilityCode == null || defaultCapabilities == null) {
            return false;
        }
        for (String cap : defaultCapabilities) {
            if (cap.equalsIgnoreCase(capabilityCode)) {
                return true;
            }
        }
        return false;
    }
}
