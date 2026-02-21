package net.ooder.scene.skills.mqtt;

/**
 * MqttProviderType MQTT提供者类型枚举
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public enum MqttProviderType {
    LIGHTWEIGHT("lightweight", "轻量级内置Broker"),
    EMQX("emqx", "EMQX企业"),
    MOSQUITTO("mosquitto", "Mosquitto"),
    ALIYUN_IOT("aliyun-iot", "阿里云IoT"),
    TENCENT_IOT("tencent-iot", "腾讯云IoT"),
    HUAWEI_IOT("huawei-iot", "华为云IoT");
    
    private final String code;
    private final String displayName;
    
    MqttProviderType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static MqttProviderType fromCode(String code) {
        for (MqttProviderType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return LIGHTWEIGHT;
    }
}
