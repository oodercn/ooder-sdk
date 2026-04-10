package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum ResponsePathTypeEnum implements Enumstype {

    CTX("CTX", "环境变量", new ComponentType[]{ComponentType.FORMLAYOUT, ComponentType.BLOCK, ComponentType.DIALOG, ComponentType.PANEL}),

    FORM("FORM", "表单", new ComponentType[]{ComponentType.FORMLAYOUT, ComponentType.BLOCK, ComponentType.DIALOG, ComponentType.PANEL}),

    TREEVIEW("TREEVIEW", "树形数据填充", new ComponentType[]{ComponentType.TREEVIEW}),

    PAGEBAR("PAGEBAR", "填充分页栏", new ComponentType[]{ComponentType.PAGEBAR}),

    TREEGRID("TREEGRID", "列表数据填充", new ComponentType[]{ComponentType.TREEGRID}),

    GRIDNEXT("GRIDNEXT", "下滑翻页追加", new ComponentType[]{ComponentType.TREEGRID}),

    GALLERY("GALLERY", "画廊装载填充", new ComponentType[]{ComponentType.GALLERY}),

    OPINION("OPINION", "Opinion填充", new ComponentType[]{ComponentType.OPINION}),

    BUTTONLAYOUT("BUTTONLAYOUT", "buttonlayout填充", new ComponentType[]{ComponentType.BUTTONLAYOUT}),

    TITLEBLOCK("TITLEBLOCK", "摘要项填充", new ComponentType[]{ComponentType.TITLEBLOCK}),

    CONTENTBLOCK("CONTENTBLOCK", "内容块填充", new ComponentType[]{ComponentType.CONTENTBLOCK}),

    TABS("TABS", "动态装载TAB", new ComponentType[]{ComponentType.TABS, ComponentType.BUTTONVIEWS, ComponentType.STACKS, ComponentType.FOLDINGTABS}),

    COMPONENT("COMPONENT", "通用模块填充"),

    LIST("LIST", "设置选择项", ComponentType.getListComponents()),

    SVGPAPER("SVGPAPER", "填充图形", new ComponentType[]{ComponentType.SVGPAPER}),

    MENUBAR("MENUBAR", "菜单"),

    POPMENU("POPMENU", "弹出菜单"),

    EXPRESSION("EXPRESSION", "动态运算"),

    FCHART("FCHART", "视图数据填充", new ComponentType[]{ComponentType.FCHART}),

    FCHARTDATASET("FCHARTDATASET", "视图分组数据", new ComponentType[]{ComponentType.FCHART}),

    FCHARTCATEGORIES("FCHARTCATEGORIES", "视图分类填充", new ComponentType[]{ComponentType.FCHART}),

    FCHARTTRENDLINES("FCHARTTRENDLINES", "等高线填充", new ComponentType[]{ComponentType.FCHART}),

    SPA("SPA", "设计器参数填充"),

    LOG("LOG", "日志"),

    ALERT("ALERT", "alert");

    private final ComponentType[] componentTypes;

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }


    ResponsePathTypeEnum(String type, String name, ComponentType... componentTypes) {
        this.type = type;
        this.name = name;
        this.componentTypes = componentTypes;

    }


    @Override
    public String toString() {
        return type;
    }

    public static ResponsePathTypeEnum fromType(String typeName) {
        for (ResponsePathTypeEnum type : ResponsePathTypeEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
