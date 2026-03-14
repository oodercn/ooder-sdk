package net.ooder.enums.iot.category;


import net.ooder.annotation.IconEnumstype;

/**
 * 安防设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum SecurityDeviceEnum implements IconEnumstype {
    // 家庭安防
    SMART_LOCK("smartLock", "ri-lock-line", "智能锁"),
    SECURITY_CAMERA("securityCamera", "ri-video-line", "安防摄像头"),
    DOOR_SENSOR("doorSensor", "ri-door-open-line", "门磁传感器"),
    WINDOW_SENSOR("windowSensor", "ri-window-line", "窗磁传感器"),
    MOTION_SENSOR("motionSensor", "ri-move-line", "人体感应传感器"),
    GLASS_BREAK_SENSOR("glassBreakSensor", "ri-glass-water-line", "玻璃破碎传感器"),
    ALARM_SYSTEM("alarmSystem", "ri-alarm-line", "报警系统"),
    SECURITY_KEYPAD("securityKeypad", "ri-keyboard-line", "安防键盘"),
    EMERGENCY_BUTTON("emergencyButton", "ri-alert-line", "紧急按钮"),
    
    // 公共安全
    SURVEILLANCE_CAMERA("surveillanceCamera", "ri-video-2-line", "监控摄像头"),
    ACCESS_CONTROL("accessControl", "ri-user-follow-line", "门禁系统"),
    FIRE_ALARM("fireAlarm", "ri-fire-line", "火灾报警器"),
    SMOKE_DETECTOR("smokeDetector", "ri-smoke-line", "烟雾探测器"),
    GAS_DETECTOR("gasDetector", "ri-gas-station-line", "燃气探测器"),
    BODY_TEMPERATURE_MONITOR("bodyTemperatureMonitor", "ri-thermometer-line", "体温监测器");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    SecurityDeviceEnum(String type, String imageClass, String name) {
        this.type = type;
        this.name = name;
        this.imageClass = imageClass;
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getImageClass() {
        return imageClass;
    }
    
    /**
     * 获取设备所属子分类
     * @return 子分类名称
     */
    public String getSubCategory() {
        switch (this) {
            case SMART_LOCK:
            case SECURITY_CAMERA:
            case DOOR_SENSOR:
            case WINDOW_SENSOR:
            case MOTION_SENSOR:
            case GLASS_BREAK_SENSOR:
            case ALARM_SYSTEM:
            case SECURITY_KEYPAD:
            case EMERGENCY_BUTTON:
                return "家庭安防";
            case SURVEILLANCE_CAMERA:
            case ACCESS_CONTROL:
            case FIRE_ALARM:
            case SMOKE_DETECTOR:
            case GAS_DETECTOR:
            case BODY_TEMPERATURE_MONITOR:
                return "公共安全";
            default:
                return "未知子分类";
        }
    }
}