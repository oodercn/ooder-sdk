package net.ooder.enums.iot;


import net.ooder.annotation.IconEnumstype;

/**
 * 智能家居设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum SmartHomeEnum implements IconEnumstype {
    // 常用设备
    SMART_LIGHT("light", "ri-lightbulb-line", "智能灯"),
    SMART_THERMOSTAT("thermostat", "ri-thermometer-line", "智能恒温器"),
    SMART_LOCK("lock", "ri-lock-line", "智能锁"),
    SMART_CURTAIN("curtain", "ri-window-line", "智能窗帘"),
    SMART_PLUG("plug", "ri-plug-line", "智能插座"),
    SMART_CAMERA("camera", "ri-video-line", "智能摄像头"),
    SMART_SPEAKER("speaker", "ri-volume-2-line", "智能音箱"),
    
    // 日常管理
    HOME_SECURITY("security", "ri-shield-check-line", "家庭安防"),
    ENERGY_MONITORING("energy", "ri-bolt-line", "能源监控"),
    HOME_AUTOMATION("automation", "ri-robot-line", "家庭自动化"),
    AIR_QUALITY("air", "ri-wind-line", "空气质量"),
    WATER_CONTROL("water", "ri-droplets-line", "用水控制"),
    APPLIANCE_CONTROL("appliance", "ri-television-line", "家电控制"),
    HOME_ASSISTANT("assistant", "ri-user-line", "家庭助手");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    SmartHomeEnum(String type, String imageClass, String name) {
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
}