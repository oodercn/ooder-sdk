package net.ooder.enums.iot.category;


import net.ooder.annotation.IconEnumstype;

/**
 * 照明设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum LightingDeviceEnum implements IconEnumstype {
    // 智能灯具
    SMART_LIGHT("smartLight", "ri-lightbulb-line", "智能灯"),
    SMART_BULB("smartBulb", "ri-lightbulb-flash-line", "智能灯泡"),
    LED_STRIP("ledStrip", "ri-magic-line", "LED灯带"),
    DOWNLIGHT("downlight", "ri-cast-line", "筒灯"),
    SPOTLIGHT("spotlight", "ri-focus-2-line", "射灯"),
    PANEL_LIGHT("panelLight", "ri-window-2-line", "面板灯"),
    
    // 智能照明控制
    LIGHT_SWITCH("lightSwitch", "ri-toggle-line", "智能照明开关"),
    DIMMER_SWITCH("dimmerSwitch", "ri-volume-2-line", "调光器"),
    SCENE_CONTROLLER("sceneController", "ri-layout-grid-line", "场景控制器"),
    MOTION_SENSOR_LIGHT("motionSensorLight", "ri-move-line", "人体感应灯"),
    
    // 环境照明
    AMBIENT_LIGHT("ambientLight", "ri-sun-cloud-line", "环境灯"),
    NIGHT_LIGHT("nightLight", "ri-moon-line", "夜灯"),
    EMERGENCY_LIGHT("emergencyLight", "ri-exclamation-line", "应急灯"),
    
    // 户外照明
    SMART_STREETLIGHT("smartStreetlight", "ri-street-light-line", "智能路灯"),
    GARDEN_LIGHT("gardenLight", "ri-leaf-line", "庭院灯"),
    SECURITY_LIGHT("securityLight", "ri-shield-line", "安防灯"),
    
    // 照明传感器
    LIGHT_SENSOR("lightSensor", "ri-sun-line", "光照传感器"),
    DAYLIGHT_SENSOR("daylightSensor", "ri-cloud-sun-line", "日光传感器");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    LightingDeviceEnum(String type, String imageClass, String name) {
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
            case SMART_LIGHT:
            case SMART_BULB:
            case LED_STRIP:
            case DOWNLIGHT:
            case SPOTLIGHT:
            case PANEL_LIGHT:
                return "智能灯具";
            case LIGHT_SWITCH:
            case DIMMER_SWITCH:
            case SCENE_CONTROLLER:
            case MOTION_SENSOR_LIGHT:
                return "智能照明控制";
            case AMBIENT_LIGHT:
            case NIGHT_LIGHT:
            case EMERGENCY_LIGHT:
                return "环境照明";
            case SMART_STREETLIGHT:
            case GARDEN_LIGHT:
            case SECURITY_LIGHT:
                return "户外照明";
            case LIGHT_SENSOR:
            case DAYLIGHT_SENSOR:
                return "照明传感器";
            default:
                return "未知子分类";
        }
    }
}