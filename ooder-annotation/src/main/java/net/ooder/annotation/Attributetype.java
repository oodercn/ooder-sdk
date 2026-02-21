package net.ooder.annotation;


import net.ooder.common.EngineType;

public enum Attributetype implements IconEnumstype {

    ADVANCE("ri-headphone-line", "引擎内部属性", EngineType.ESD),

    TOOL("ri-video-line", "自动活动属性", EngineType.ESD),

    LISTENER("ri-smartphone-line", "监听器属性", EngineType.ESD),

    APPLICATION("ri-stack-line", "应用解释", EngineType.ESD),

    CUSTOMIZE("ri-magic-line", "应用定制属性", EngineType.ESD),

    PDT("ri-road-map-line", "工具使用的属性", EngineType.ESD),

    BPD("ri-road-map-line", "定义工具", EngineType.ESD),

    RIGHT("ri-keyboard-line", "办理权限", EngineType.ESD),

    CONDITION("ri-function-line", "执行条件", EngineType.CUSTOM),

    SAFE("ri-road-map-line", "数据安全", EngineType.CUSTOM),

    PAGE("ri-file-text-line", "页面属性", EngineType.CUSTOM),

    DB("ri-table-line", "数据库", EngineType.CUSTOM),

    PAGERIGHT("ri-keyboard-line", "页面权限", EngineType.CUSTOM),

    MODULERIGHT("ri-keyboard-line", "页面权限", EngineType.CUSTOM),

    TASK("ri-list-check", "任务调度", EngineType.CUSTOM),

    EXPRESSION("ri-function-line", "表达式调用", EngineType.CUSTOM),

    SERVICEEVENT("SERVICEEVENT", "服务事件属性", EngineType.ESB),

    SERVICE("ri-notification-line", "服务属性", EngineType.ESB),

    EVENT("ri-notification-line", "事件属性", EngineType.IOT),

    DEVICE("ri-cpu-line", "事件属性", EngineType.IOT),

    DEVICEEVENT("ri-cpu-line", "设备事件属性", EngineType.IOT),

    USEREVENT("ri-user-line", "用户事件属性", EngineType.IOT),

    COMMAND("ri-base-station-line", "命令", EngineType.IOT);

    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    private String type;

    private String name;

    private String imageClass;

    private EngineType engineType;

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    @Override
    public String getImageClass() {
        return imageClass;
    }

    Attributetype(String imageClass, String name, EngineType engineType) {
        this.imageClass = imageClass;
        this.type = name();
        this.name = name;
        this.engineType = engineType;

    }

    @Override
    public String toString() {
        return type;
    }

    public static Attributetype fromType(String typeName) {
        for (Attributetype type : Attributetype.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return CUSTOMIZE;
    }

}
