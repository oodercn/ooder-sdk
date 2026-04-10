package net.ooder.annotation.ui;


import net.ooder.annotation.IconEnumstype;

public enum NavComboType implements IconEnumstype {
    CUSTOM("通用布局", ModuleViewType.LAYOUTCONFIG),
    GALLERYNAV("画廊导航", ModuleViewType.NAVGALLERYCONFIG),
    MENUBARNAV("菜单导航", ModuleViewType.NAVMENUBARCONFIG),
    TREENAV("属性导航", ModuleViewType.NAVTREECONFIG),
    FOLDINGNAV("堆叠页导航", ModuleViewType.NAVFOLDINGTREECONFIG);

    private final String name;
    private final String imageClass;
    private final ModuleViewType moduleViewType;

    NavComboType(String name, ModuleViewType moduleViewType) {

        this.name = name;
        this.moduleViewType = moduleViewType;
        this.imageClass = moduleViewType.getImageClass();

    }

    public ModuleViewType getModuleViewType() {
        return moduleViewType;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getImageClass() {
        return imageClass;
    }

    @Override
    public String getName() {
        return name;
    }

}
