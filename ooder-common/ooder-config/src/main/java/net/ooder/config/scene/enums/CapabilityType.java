package net.ooder.config.scene.enums;

import net.ooder.config.scene.docs.EnumDocument;

@EnumDocument(
    name = "能力类型",
    description = "定义系统支持的服务能力类型，每个能力对应一个具体的功能接口",
    installGuide = "无需安装，系统内置枚举。服务端需要实现对应接口，客户端通过能力ID调用。",
    startupGuide = "1. 在场景配置文件的 capabilities 节点下配置能力\n" +
                   "2. 指定 interfaceId 和 endpoint\n" +
                   "3. 通过 factory.getCapability() 获取能力配置",
    configExample = "# YAML配置示例\n" +
                    "org:\n" +
                    "  capabilities:\n" +
                    "    org-query:\n" +
                    "      capabilityName: 组织查询服务\n" +
                    "      interfaceId: OrgWebManager\n" +
                    "      endpoint: http://localhost:8080/api/org/query"
)
public enum CapabilityType {
    
    ORG_QUERY("org-query", "组织查询服务", ServiceType.ORG, "OrgWebManager",
        "提供组织机构的查询功能，包括组织树、人员列表等",
        "/api/org/query", 30000, 3),
    
    ORG_ADMIN("org-admin", "组织管理服务", ServiceType.ORG, "OrgAdminService",
        "提供组织机构的管理功能，包括增删改等操作",
        "/api/org/admin", 30000, 3),
    
    USER_AUTH("user-auth", "用户认证服务", ServiceType.ORG, "UserService",
        "提供用户登录、验证、权限检查等功能",
        "/api/user/auth", 10000, 3),
    
    VFS_CLIENT("vfs-client", "虚拟目录服务", ServiceType.VFS, "VFSWebService",
        "提供虚拟目录的浏览、创建、删除等功能",
        "/api/vfs/client", 60000, 3),
    
    VFS_STORE("vfs-store", "实体存储服务", ServiceType.VFS, "VFSStoreService",
        "提供文件的存储、下载、版本管理等功能",
        "/api/vfs/store", 120000, 3),
    
    VFS_SYNC("vfs-sync", "文件同步服务", ServiceType.VFS, "VFSSyncService",
        "提供本地与服务器之间的文件同步功能",
        "/api/vfs/sync", 300000, 2),
    
    MSG_PUSH("msg-push", "消息推送服务", ServiceType.MSG, "MsgWebService",
        "提供消息推送功能，支持批量推送",
        "/api/msg/push", 30000, 3),
    
    MSG_P2P("msg-p2p", "P2P通信服务", ServiceType.MSG, "MsgWebService",
        "提供点对点消息通信功能",
        "/api/msg/p2p", 30000, 3),
    
    MSG_TOPIC("msg-topic", "Topic订阅服务", ServiceType.MSG, "MsgWebService",
        "提供Topic订阅和发布功能",
        "/api/msg/topic", 30000, 3),
    
    MQTT_PUB("mqtt-pub", "MQTT发布服务", ServiceType.MQTT, "MqttPublishService",
        "提供MQTT消息发布功能",
        "/api/mqtt/pub", 10000, 3),
    
    MQTT_SUB("mqtt-sub", "MQTT订阅服务", ServiceType.MQTT, "MqttSubscribeService",
        "提供MQTT消息订阅功能",
        "/api/mqtt/sub", 10000, 3),
    
    MQTT_BROADCAST("mqtt-broadcast", "MQTT广播服务", ServiceType.MQTT, "MqttBroadcastService",
        "提供MQTT消息广播功能",
        "/api/mqtt/broadcast", 10000, 3),
    
    AI_INFERENCE("ai-inference", "AI推理服务", ServiceType.AI, "AIInferenceService",
        "提供AI模型推理功能",
        "/api/ai/inference", 60000, 3),
    
    AI_MODEL("ai-model", "AI模型管理", ServiceType.AI, "AIModelService",
        "提供AI模型的加载、卸载、切换等功能",
        "/api/ai/model", 30000, 3),
    
    DB_QUERY("db-query", "数据库查询服务", ServiceType.DB, "DbQueryService",
        "提供数据库查询功能",
        "/api/db/query", 30000, 3),
    
    DB_ADMIN("db-admin", "数据库管理服务", ServiceType.DB, "DbAdminService",
        "提供数据库管理功能，包括表结构管理等",
        "/api/db/admin", 60000, 3);
    
    private final String code;
    private final String displayName;
    private final ServiceType serviceType;
    private final String defaultInterfaceId;
    private final String description;
    private final String defaultEndpoint;
    private final int defaultTimeout;
    private final int defaultRetryCount;
    
    CapabilityType(String code, String displayName, ServiceType serviceType,
                   String defaultInterfaceId, String description,
                   String defaultEndpoint, int defaultTimeout, int defaultRetryCount) {
        this.code = code;
        this.displayName = displayName;
        this.serviceType = serviceType;
        this.defaultInterfaceId = defaultInterfaceId;
        this.description = description;
        this.defaultEndpoint = defaultEndpoint;
        this.defaultTimeout = defaultTimeout;
        this.defaultRetryCount = defaultRetryCount;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public ServiceType getServiceType() {
        return serviceType;
    }
    
    public String getDefaultInterfaceId() {
        return defaultInterfaceId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getDefaultEndpoint() {
        return defaultEndpoint;
    }
    
    public int getDefaultTimeout() {
        return defaultTimeout;
    }
    
    public int getDefaultRetryCount() {
        return defaultRetryCount;
    }
    
    public static CapabilityType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (CapabilityType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
    
    public static CapabilityType fromServiceAndCode(ServiceType serviceType, String code) {
        if (serviceType == null || code == null) {
            return null;
        }
        for (CapabilityType type : values()) {
            if (type.serviceType == serviceType && type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
    
    public String getFullCode() {
        return serviceType.getCode() + ":" + code;
    }
}
