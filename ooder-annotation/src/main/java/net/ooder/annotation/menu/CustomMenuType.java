package net.ooder.annotation.menu;



import net.ooder.annotation.Enumstype;

import java.util.Arrays;
import java.util.List;

public enum CustomMenuType implements Enumstype {


    TOOLBAR("工具栏", SysMenuType.CustomToolsBar, "ri-tools-line"),
    LISTMENU("弹出菜单", SysMenuType.CustomListBar, "ri-tools-line"),
    BOTTOMBAR("下标栏", SysMenuType.CustomBottomBar, "ri-checkbox-blank-line"),
    MENUBAR("菜单栏", SysMenuType.CustomMenusBar, "ri-menu-line"),
    ROWCMD("行控制按钮", SysMenuType.CustomToolsBar, "ri-checkbox-blank-line"),
    CONTEXTMENU("右键菜单", SysMenuType.CustomContextBar, "ri-menu-line"),
    SUB("子菜单", SysMenuType.CustomSubBar, "ri-menu-line"),
    BPM("流程控制栏", SysMenuType.RouteInstToolBar, "ri-node-tree"),
    BPMBOTTOM("流程控制栏", SysMenuType.RouteInstBottomBar, "ri-node-tree"),
    //  java("JAVA调试", SysMenuType.RADTopMenu, "spafont spa-icon-tools"),
    COMPONENT("RAD通用组件", SysMenuType.CustomToolsBar, "ri-menu-line"),
    PLUGINS("RAD顶部插件", SysMenuType.RADTopMenu, "ri-menu-line"),
    //   page("RAD页面插件", SysMenuType.RADTopMenu, "spafont spa-icon-tools"),
    //   RADDebug("RAD主菜单", SysMenuType.RADTopMenu, "spafont spa-icon-tools"),
    TOP("RAD工具栏菜单", SysMenuType.CustomToolsBar, "ri-menu-line");


    private String name;

    private String imageClass;

    SysMenuType menuType;

    public static List<CustomMenuType> getCustomMenuType() {
        return Arrays.asList(new CustomMenuType[]{TOOLBAR, BOTTOMBAR, ROWCMD, CONTEXTMENU});

    }

    public String getImageClass() {
        return imageClass;
    }


    public SysMenuType getMenuType() {
        return menuType;
    }

    CustomMenuType(String name, SysMenuType viewBar, String imageClass) {
        this.name = name;
        this.menuType = viewBar;
        this.imageClass = imageClass;

    }


    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }


    public SysMenuType getSysMenuType() {
        return menuType;
    }

}
