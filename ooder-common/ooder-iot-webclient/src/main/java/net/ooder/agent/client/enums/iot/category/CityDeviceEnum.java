package net.ooder.enums.iot.category;


import net.ooder.annotation.IconEnumstype;

/**
 * 智慧城市设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum CityDeviceEnum implements IconEnumstype {
    // 智能交通设备
    TRAFFIC_LIGHT("trafficLight", "ri-traffic-light-line", "交通信号灯"),
    SMART_PARKING("parking", "ri-parking-box-line", "智能停车系统"),
    TRAFFIC_SENSOR("trafficSensor", "ri-road-line", "交通流量传感器"),
    TRAFFIC_CAMERA("trafficCamera", "ri-video-line", "交通监控摄像头"),
    VEHICLE_RECOGNITION("vehicleRecognition", "ri-car-line", "车辆识别系统"),
    TRAFFIC_MANAGEMENT("trafficManage", "ri-road-line", "交通管理系统"),
    
    // 公共安全设备
    SURVEILLANCE_CAMERA("surveillance", "ri-video-line", "监控摄像头"),
    PUBLIC_SECURITY("security", "ri-shield-check-line", "公共安全系统"),
    EMERGENCY_BUTTON("emergencyButton", "ri-alarm-line", "紧急报警按钮"),
    FIRE_DETECTION("fireDetection", "ri-fire-line", "火灾探测系统"),
    
    // 智能能源设备
    ENERGY_MANAGEMENT("energy", "ri-bolt-line", "能源管理系统"),
    SMART_METER("smartMeter", "ri-calculator-line", "智能电表"),
    SOLAR_PANEL("solarPanel", "ri-sun-line", "太阳能板监控"),
    WIND_TURBINE("windTurbine", "ri-wind-line", "风力发电机监控"),
    
    // 智能照明设备
    SMART_STREETLIGHT("streetlight", "ri-lightbulb-line", "智能路灯"),
    AREA_LIGHTING("areaLighting", "ri-lightbulb-flash-line", "区域照明系统"),
    PARK_LIGHTING("parkLighting", "ri-tree-pine-line", "公园照明系统"),
    
    // 环境监测设备
    ENVIRONMENTAL_SENSOR("environment", "ri-leaf-line", "环境传感器"),
    AIR_QUALITY_SENSOR("airQuality", "ri-cloud-fog-line", "空气质量传感器"),
    NOISE_SENSOR("noise", "ri-volume-up-line", "噪音传感器"),
    WEATHER_STATION("weather", "ri-cloud-sun-line", "城市气象站"),
    
    // 智能水务设备
    WATER_MANAGEMENT("water", "ri-water-line", "水务管理系统"),
    WATER_METER("waterMeter", "ri-droplets-line", "智能水表"),
    WATER_QUALITY_SENSOR("waterQuality", "ri-glass-line", "水质传感器"),
    LEAK_DETECTION("leakDetection", "ri-split-square-vertical-line", "漏水检测系统"),
    
    // 智能环卫设备
    SMART_BIN("bin", "ri-recycle-bin-line", "智能垃圾桶"),
    WASTE_MANAGEMENT("waste", "ri-recycle-bin-2-line", "垃圾管理系统"),
    CLEANING_ROBOT("cleaningRobot", "ri-broom-line", "清洁机器人"),
    
    // 城市基础设施
    CITY_INFRASTRUCTURE("infrastructure", "ri-building-2-line", "城市基础设施管理"),
    BRIDGE_MONITOR("bridgeMonitor", "ri-bridge-line", "桥梁监测系统"),
    ROAD_MONITOR("roadMonitor", "ri-road-line", "道路监测系统"),
    TUNNEL_MONITOR("tunnelMonitor", "ri-cave-line", "隧道监测系统"),
    
    // 应急响应系统
    EMERGENCY_RESPONSE("emergency", "ri-alarm-line", "应急响应系统"),
    DISASTER_PREVENTION("disasterPrevention", "ri-shield-check-line", "防灾系统"),
    
    // 智慧市政服务
    SMART_CITY_HALL("cityHall", "ri-building-line", "智慧市政大厅"),
    PUBLIC_WIFI("publicWifi", "ri-wifi-line", "公共WiFi"),
    SMART_BENCH("smartBench", "ri-activity-line", "智能长椅");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    CityDeviceEnum(String type, String imageClass, String name) {
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
            case TRAFFIC_LIGHT:
            case SMART_PARKING:
            case TRAFFIC_SENSOR:
            case TRAFFIC_CAMERA:
            case VEHICLE_RECOGNITION:
            case TRAFFIC_MANAGEMENT:
                return "智能交通设备";
            case SURVEILLANCE_CAMERA:
            case PUBLIC_SECURITY:
            case EMERGENCY_BUTTON:
            case FIRE_DETECTION:
                return "公共安全设备";
            case ENERGY_MANAGEMENT:
            case SMART_METER:
            case SOLAR_PANEL:
            case WIND_TURBINE:
                return "智能能源设备";
            case SMART_STREETLIGHT:
            case AREA_LIGHTING:
            case PARK_LIGHTING:
                return "智能照明设备";
            case ENVIRONMENTAL_SENSOR:
            case AIR_QUALITY_SENSOR:
            case NOISE_SENSOR:
            case WEATHER_STATION:
                return "环境监测设备";
            case WATER_MANAGEMENT:
            case WATER_METER:
            case WATER_QUALITY_SENSOR:
            case LEAK_DETECTION:
                return "智能水务设备";
            case SMART_BIN:
            case WASTE_MANAGEMENT:
            case CLEANING_ROBOT:
                return "智能环卫设备";
            case CITY_INFRASTRUCTURE:
            case BRIDGE_MONITOR:
            case ROAD_MONITOR:
            case TUNNEL_MONITOR:
                return "城市基础设施";
            case EMERGENCY_RESPONSE:
            case DISASTER_PREVENTION:
                return "应急响应系统";
            case SMART_CITY_HALL:
            case PUBLIC_WIFI:
            case SMART_BENCH:
                return "智慧市政服务";
            default:
                return "未知子分类";
        }
    }
}