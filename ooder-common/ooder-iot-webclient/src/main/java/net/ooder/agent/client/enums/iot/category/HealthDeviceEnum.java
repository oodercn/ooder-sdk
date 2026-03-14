package net.ooder.enums.iot.category;

import net.ooder.annotation.IconEnumstype;

/**
 * 健康设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum HealthDeviceEnum implements IconEnumstype {
    // 可穿戴设备
    SMART_WATCH("smartWatch", "ri-watch-line", "智能手表"),
    FITNESS_TRACKER("fitnessTracker", "ri-heart-pulse-line", "运动追踪器"),
    SMART_BAND("smartBand", "ri-heart-fill", "智能手环"),
    SMART_GLASSES("smartGlasses", "ri-glasses-line", "智能眼镜"),
    
    // 健康监测设备
    HEART_RATE_MONITOR("heartRateMonitor", "ri-heart-line", "心率监测器"),
    BLOOD_PRESSURE_MONITOR("bloodPressureMonitor", "ri-heart-pulse-line", "血压计"),
    BLOOD_GLUCOSE_MONITOR("bloodGlucoseMonitor", "ri-activity-line", "血糖仪"),
    BODY_TEMPERATURE_MONITOR("bodyTemperatureMonitor", "ri-thermometer-line", "体温计"),
    WEIGHT_SCALE("weightScale", "ri-balance-line", "体重秤"),
    SLEEP_TRACKER("sleepTracker", "ri-moon-line", "睡眠追踪器"),
    
    // 医疗设备
    NEBULIZER("nebulizer", "ri-lungs-line", "雾化器"),
    OXYGEN_CONCENTRATOR("oxygenConcentrator", "ri-breathe-line", "制氧机"),
    BLOOD_OXYGEN_MONITOR("bloodOxygenMonitor", "ri-heart-pulse-line", "血氧监测仪"),
    ECG_MONITOR("ecgMonitor", "ri-waveform-line", "心电图监测仪"),
    
    // 健康管理设备
    SMART_PILL_DISPENSER("smartPillDispenser", "ri-pill-line", "智能药盒"),
    UV_SANITIZER("uvSanitizer", "ri-shield-virus-line", "紫外线消毒器"),
    AIR_QUALITY_MONITOR("airQualityMonitor", "ri-wind-line", "空气质量监测器");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    HealthDeviceEnum(String type, String imageClass, String name) {
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
            case SMART_WATCH:
            case FITNESS_TRACKER:
            case SMART_BAND:
            case SMART_GLASSES:
                return "可穿戴设备";
            case HEART_RATE_MONITOR:
            case BLOOD_PRESSURE_MONITOR:
            case BLOOD_GLUCOSE_MONITOR:
            case BODY_TEMPERATURE_MONITOR:
            case WEIGHT_SCALE:
            case SLEEP_TRACKER:
                return "健康监测设备";
            case NEBULIZER:
            case OXYGEN_CONCENTRATOR:
            case BLOOD_OXYGEN_MONITOR:
            case ECG_MONITOR:
                return "医疗设备";
            case SMART_PILL_DISPENSER:
            case UV_SANITIZER:
            case AIR_QUALITY_MONITOR:
                return "健康管理设备";
            default:
                return "未知子分类";
        }
    }
}