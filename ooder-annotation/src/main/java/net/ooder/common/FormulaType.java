package net.ooder.common;

import net.ooder.annotation.Attributetype;
import net.ooder.annotation.IconEnumstype;

import java.util.ArrayList;
import java.util.List;

public enum FormulaType implements IconEnumstype {

    ItemRight(Attributetype.PAGERIGHT, "ri-list-check", "选择项授权"),

    TreeGridRight(Attributetype.PAGERIGHT, "ri-table-line", "列表项组件"),

    UIRight(Attributetype.PAGERIGHT, "ri-layout-grid-line", "显示项授权"),

    FormRight(Attributetype.PAGERIGHT, "ri-file-text-line", "表单项授权"),

    BarRight(Attributetype.PAGERIGHT, "ri-tools-line", "按钮组授权"),

    ActionRight(Attributetype.PAGERIGHT, "ri-play-line", "动作组授权"),

    TabRight(Attributetype.PAGERIGHT, "ri-table-line", "TAB过滤"),


    UserRightCon(Attributetype.CONDITION, "ri-shield-user-line", "用户权限"),

    WorkFlowCon(Attributetype.CONDITION, "ri-arrow-left-right-line", "流程条件"),

    ExpressionCon(Attributetype.CONDITION, "ri-code-line", "表达式权限"),

    PageCon(Attributetype.CONDITION, "ri-file-line", "页面条件"),


    ModuleRight(Attributetype.MODULERIGHT, "ri-file-line", "模块授权"),

    MenuRight(Attributetype.MODULERIGHT, "ri-menu-line", "菜单授权"),

    ReaderSelectedID(Attributetype.RIGHT, "ri-eye-line", "阅办人"),

    PerformerSelectedID(Attributetype.RIGHT, "ri-user-star-line", "办理人"),

    InsteadSignSelectedID(Attributetype.RIGHT, "ri-edit-line", "代签人"),

    TaskID(Attributetype.TASK, "ri-task-line", "任务管理"),

    WebAPISelectedID(Attributetype.TASK, "ri-plug-line", "接口调用"),

    ESDCOM(Attributetype.PAGE, "ri-puzzle-line", "页面组件"),

    ActionSelectedID(Attributetype.PAGE, "ri-play-line", "页面动作"),

    TableSelectedID(Attributetype.DB, "ri-table-line", "数据库表"),

    ColInfoID(Attributetype.DB, "ri-database-line", "数据字段"),

    SafeID(Attributetype.SAFE, "ri-lock-line", "安全控制"),

    CommandSelectedID(Attributetype.COMMAND, "ri-terminal-line", "执行命令"),

    DeviceSelectedID(Attributetype.DEVICE, "ri-cpu-line", "选择设备"),

    ServiceSelectedID(Attributetype.SERVICE, "ri-server-line", "内部接口"),

    EventSelectID(Attributetype.EVENT, "ri-notification-line", "监听事件"),

    DeviceEventSelectedID(Attributetype.DEVICEEVENT, "ri-notification-line", "设备事件"),

    UserPerformSelectedID(Attributetype.USEREVENT, "ri-notification-line", "用户事件"),

    ServiceEventSelectedID(Attributetype.SERVICEEVENT, "ri-link", "系统服务"),

    UNKNOW(Attributetype.CUSTOMIZE, "ri-question-line", "未知类型");


    private Attributetype baseType;

    private String name;

    private String imageClass;

    private String type;

    public void setBaseType(Attributetype baseType) {
        this.baseType = baseType;
    }

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

    FormulaType(Attributetype baseType, String imageClass, String name) {
        this.baseType = baseType;
        this.name = name;
        this.type = name();
        this.imageClass = imageClass;

    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Attributetype getBaseType() {
        return baseType;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    public static FormulaType fromType(String type) {
        if (type == null) {
            return UNKNOW;
        }
        for (FormulaType ftype : FormulaType.values()) {
            if (ftype.getType().equals(type)) {
                return ftype;
            }
        }
        return UNKNOW;
    }

    public static List<FormulaType> fromBaseType(Attributetype baseType) {
        List<FormulaType> formulaTypeList = new ArrayList<>();
        for (FormulaType ftype : FormulaType.values()) {
            if (ftype.getBaseType().equals(baseType)) {
                formulaTypeList.add(ftype);
            }
        }
        return formulaTypeList;
    }

}
