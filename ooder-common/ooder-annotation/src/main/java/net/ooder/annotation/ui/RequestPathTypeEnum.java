package net.ooder.annotation.ui;

import net.ooder.annotation.Enumstype;

;

public enum RequestPathTypeEnum implements Enumstype {

    GALLERY("GALLERY", "画廊选择项", new ComponentType[]{ComponentType.GALLERY}),

    OPINION("OPINION", "Opinion建议项", new ComponentType[]{ComponentType.OPINION}),

    BUTTONLAYOUT("BUTTONLAYOUT", "buttonlayout标签", new ComponentType[]{ComponentType.BUTTONLAYOUT}),

    TITLEBLOCK("TITLEBLOCK", "摘要选择项", new ComponentType[]{ComponentType.TITLEBLOCK}),

    CONTENTBLOCK("CONTENTBLOCK", "内容块选择项", new ComponentType[]{ComponentType.CONTENTBLOCK}),

    TREEVIEW("TREEVIEW", "选中树形节点", new ComponentType[]{ComponentType.TREEVIEW}),

    TREEGRID("TREEGRID", "当前列表选中行ID", new ComponentType[]{ComponentType.TREEGRID}),

    TREEGRIDROW("TREEGRIDROW", "当前选中行对象", new ComponentType[]{ComponentType.TREEGRID, ComponentType.GALLERY}),

    TREEGRIDALLVALUE("TREEGRIDALLVALUE", "当前选中表格值", new ComponentType[]{ComponentType.TREEGRID, ComponentType.GALLERY}),

    TREEGRIDROWVALUE("TREEGRIDROWVALUE", "当前行数据数据", new ComponentType[]{ComponentType.TREEGRID, ComponentType.GALLERY}),

    PAGENEXT("PAGENEXT", "列表当前页码", new ComponentType[]{ComponentType.TREEGRID}),

    PAGEBAR("PAGEBAR", "分页数据", new ComponentType[]{ComponentType.PAGEBAR}),

    SPA("SPA", "设计器参数", new ComponentType[]{ComponentType.BLOCK}),

    POPMENU("POPMENU", "选中弹出菜单值", new ComponentType[]{ComponentType.BLOCK}),

    STAGVAR("STAGVAR", "节点扩展属性", new ComponentType[]{ComponentType.TREEGRID, ComponentType.GALLERY}),

    RAD("RAD", "设计器环境变量", new ComponentType[]{ComponentType.BLOCK}),

    FORM("FORM", "表单可输入域", new ComponentType[]{ComponentType.FORMLAYOUT, ComponentType.BLOCK, ComponentType.DIALOG, ComponentType.PANEL});


    private final ComponentType[] componentTypes;

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }


    RequestPathTypeEnum(String type, String name, ComponentType... componentTypes) {
        this.type = type;
        this.name = name;
        this.componentTypes = componentTypes;

    }


    @Override
    public String toString() {
        return type;
    }

    public static RequestPathTypeEnum fromType(String typeName) {
        for (RequestPathTypeEnum type : RequestPathTypeEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
