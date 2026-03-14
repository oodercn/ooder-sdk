package net.ooder.enums.iot.category;

import net.ooder.annotation.IconEnumstype;

/**
 * 家电设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum ApplianceDeviceEnum implements IconEnumstype {
    // 厨房家电
    SMART_REFRIGERATOR("smartRefrigerator", "ri-refrigerator-line", "智能冰箱"),
    SMART_OVEN("smartOven", "ri-fire-line", "智能烤箱"),
    SMART_MICROWAVE("smartMicrowave", "ri-microwave-line", "智能微波炉"),
    SMART_STOVE("smartStove", "ri-flame-line", "智能炉灶"),
    SMART_DISHWASHER("smartDishwasher", "ri-tap-line", "智能洗碗机"),
    SMART_COFFEE_MAKER("smartCoffeeMaker", "ri-coffee-line", "智能咖啡机"),
    
    // 清洁家电
    SMART_VACUUM("smartVacuum", "ri-broom-line", "智能扫地机器人"),
    SMART_WASHER("smartWasher", "ri-shuffle-2-line", "智能洗衣机"),
    SMART_DRYER("smartDryer", "ri-wind-line", "智能烘干机"),
    
    // 娱乐家电
    SMART_TV("smartTv", "ri-television-line", "智能电视"),
    SMART_SPEAKER("smartSpeaker", "ri-volume-2-line", "智能音箱"),
    SMART_PROJECTOR("smartProjector", "ri-cast-line", "智能投影仪"),
    GAME_CONSOLE("gameConsole", "ri-gamepad-line", "游戏主机"),
    
    // 空气处理家电
    SMART_AIR_CONDITIONER("smartAirConditioner", "ri-snowflake-line", "智能空调"),
    SMART_AIR_PURIFIER("smartAirPurifier", "ri-wind-line", "智能空气净化器"),
    SMART_HUMIDIFIER("smartHumidifier", "ri-droplets-line", "智能加湿器"),
    SMART_DEHUMIDIFIER("smartDehumidifier", "ri-dehumidifier-line", "智能除湿机"),
    
    // 其他家电
    SMART_WATER_HEATER("smartWaterHeater", "ri-showers-line", "智能热水器"),
    SMART_IRON("smartIron", "ri-fire-line", "智能电熨斗"),
    SMART_TOASTER("smartToaster", "ri-toast-line", "智能烤面包机"),
    SMART_BLENDER("smartBlender", "ri-blender-line", "智能料理机");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    ApplianceDeviceEnum(String type, String imageClass, String name) {
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
            case SMART_REFRIGERATOR:
            case SMART_OVEN:
            case SMART_MICROWAVE:
            case SMART_STOVE:
            case SMART_DISHWASHER:
            case SMART_COFFEE_MAKER:
                return "厨房家电";
            case SMART_VACUUM:
            case SMART_WASHER:
            case SMART_DRYER:
                return "清洁家电";
            case SMART_TV:
            case SMART_SPEAKER:
            case SMART_PROJECTOR:
            case GAME_CONSOLE:
                return "娱乐家电";
            case SMART_AIR_CONDITIONER:
            case SMART_AIR_PURIFIER:
            case SMART_HUMIDIFIER:
            case SMART_DEHUMIDIFIER:
                return "空气处理家电";
            case SMART_WATER_HEATER:
            case SMART_IRON:
            case SMART_TOASTER:
            case SMART_BLENDER:
                return "其他家电";
            default:
                return "未知子分类";
        }
    }
}