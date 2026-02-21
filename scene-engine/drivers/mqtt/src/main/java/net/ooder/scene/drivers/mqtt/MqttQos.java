package net.ooder.scene.drivers.mqtt;

public enum MqttQos {
    AT_MOST_ONCE(0, "At Most Once"),
    AT_LEAST_ONCE(1, "At Least Once"),
    EXACTLY_ONCE(2, "Exactly Once");
    
    private final int level;
    private final String description;
    
    MqttQos(int level, String description) {
        this.level = level;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static MqttQos fromLevel(int level) {
        switch (level) {
            case 0: return AT_MOST_ONCE;
            case 1: return AT_LEAST_ONCE;
            case 2: return EXACTLY_ONCE;
            default: return AT_LEAST_ONCE;
        }
    }
}
