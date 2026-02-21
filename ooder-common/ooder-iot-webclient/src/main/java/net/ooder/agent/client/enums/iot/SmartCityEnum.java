package net.ooder.enums.iot;


import net.ooder.annotation.IconEnumstype;

/**
 * 智慧城市设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum SmartCityEnum implements IconEnumstype {
    // 常用设备
    TRAFFIC_LIGHT("trafficLight", "ri-traffic-light-line", "交通信号灯"),
    SURVEILLANCE_CAMERA("surveillance", "ri-video-line", "监控摄像头"),
    SMART_PARKING("parking", "ri-parking-box-line", "智能停车系统"),
    ENVIRONMENTAL_SENSOR("environment", "ri-leaf-line", "环境传感器"),
    SMART_BIN("bin", "ri-recycle-bin-line", "智能垃圾桶"),
    SMART_STREETLIGHT("streetlight", "ri-lightbulb-line", "智能路灯"),
    WEATHER_STATION("weather", "ri-cloud-sun-line", "气象站"),
    
    // 日常管理
    TRAFFIC_MANAGEMENT("trafficManage", "ri-road-line", "交通管理"),
    PUBLIC_SECURITY("security", "ri-shield-check-line", "公共安全"),
    ENERGY_MANAGEMENT("energy", "ri-bolt-line", "能源管理"),
    WATER_MANAGEMENT("water", "ri-water-line", "水务管理"),
    WASTE_MANAGEMENT("waste", "ri-recycle-bin-2-line", "垃圾管理"),
    EMERGENCY_RESPONSE("emergency", "ri-alarm-line", "应急响应"),
    CITY_INFRASTRUCTURE("infrastructure", "ri-building-2-line", "城市基础设施");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    SmartCityEnum(String type, String imageClass, String name) {
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