package net.ooder.annotation;

import net.ooder.annotation.Enumstype;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.config.TreeListResultModel;

import java.util.HashSet;
import java.util.Set;

public enum ViewType implements Enumstype {
    FRAME("框架导航", "ri-layout-line", ViewGroupType.NAV, ResultModel.class),
    NAV("通用导航", "ri-compass-line", ViewGroupType.NAV, ResultModel.class),
    NAVGROUP("分组导航", "ri-group-line", ViewGroupType.LAYOUT, ListResultModel.class),
    NAVGALLERY("画廊导航", "ri-gallery-line", ViewGroupType.LAYOUT, ListResultModel.class),
    NAVMENUBAR("菜单导航", "ri-menu-line", ViewGroupType.NAV, TreeListResultModel.class),
    NAVFOLDINGTABS("导航(NavFoldingTabs)", "ri-menu-fold-line", ViewGroupType.LAYOUT, ListResultModel.class),
    NAVBUTTONVIEWS("按钮导航", "ri-square-line", ViewGroupType.LAYOUT, ListResultModel.class),
    NAVSTACKS("Stack导航", "ri-stack-line", ViewGroupType.LAYOUT, ListResultModel.class),
    NAVTABS("TAB导航", "ri-menu-unfold-line", ViewGroupType.LAYOUT, ListResultModel.class),
    NAVFOLDINGTREE("导航(FoldingTree)", "ri-tree-line", ViewGroupType.NAVTREE, TreeListResultModel.class),
    NAVTREE("导航树", "ri-tree-line", ViewGroupType.NAVTREE, ListResultModel.class),
    LAYOUT("布局", "ri-layout-2-line", ViewGroupType.LAYOUT, ResultModel.class),
    GRID("列表", "ri-table-line", ViewGroupType.VIEW, ListResultModel.class),
    GALLERY("画廊", "ri-gallery-line", ViewGroupType.VIEW, ListResultModel.class),
    FORM("表单", "ri-file-text-line", ViewGroupType.VIEW, ResultModel.class),
    BLOCK("块容器", "ri-box-2-line", ViewGroupType.VIEW, ResultModel.class),
    PANEL("面板容器", "ri-window-line", ViewGroupType.VIEW, ResultModel.class),
    GROUP("分组导航", "ri-group-line", ViewGroupType.VIEW, ResultModel.class),
    DIV("层容器", "ri-square-line", ViewGroupType.VIEW, ResultModel.class),
    TREE("数据树", "ri-tree-line", ViewGroupType.VIEW, TreeListResultModel.class),
    FCHARTS("FusionChartsXT", "ri-bar-chart-line", ViewGroupType.CHARTS, ResultModel.class),
    ECHARTS("EChart图表", "ri-line-chart-line", ViewGroupType.CHARTS, ResultModel.class),
    SVG("SVG绘图", "ri-edit-2-line", ViewGroupType.CHARTS, ResultModel.class),
    MODULE("内嵌模块", "ri-box-2-line", ViewGroupType.MODULE, ResultModel.class),
    TITLEBLOCK("磁贴布局", "ri-grid-line", ViewGroupType.MODULE, ListResultModel.class),
    CONTENTBLOCK("内容列表", "ri-list-check", ViewGroupType.MODULE, ListResultModel.class),
    OPINIONBLOCK("内容展示", "ri-chat-3-line", ViewGroupType.MODULE, ListResultModel.class),
    MOBILELAYOUT("移动首页", "ri-phone-line", ViewGroupType.MODULE, TreeListResultModel.class),
    MOBILEFORM("移动表单", "ri-file-text-line", ViewGroupType.MODULE, ResultModel.class),
    MOBILEGRID("移动列表", "ri-list-check", ViewGroupType.MODULE, ListResultModel.class),
    MOBILETREE("树形选择", "ri-tree-line", ViewGroupType.MODULE, TreeListResultModel.class),
    DIC("字典辅助", "ri-book-line", ViewGroupType.DIC, TreeListResultModel.class);
    
    private final String name;
    private final String imageClass;
    private ViewGroupType groupType;
    private Class<? extends ResultModel> dataClass;


    ViewType(String name, String imageClass, ViewGroupType groupType, Class<? extends ResultModel> dataClass) {
        this.name = name;
        this.imageClass = imageClass;
        this.groupType = groupType;
        this.dataClass = dataClass;

    }

    public static ViewType fromType(String type) {
        ViewType defaultViewType = DIC;
        if (type != null) {
            for (ViewType viewType : ViewType.values()) {
                if (viewType.getType().equals(type)) {
                    defaultViewType = viewType;
                }
            }
        }
        return defaultViewType;
    }

    public static Set<ViewType> getGroupView(ViewGroupType groupType) {
        Set<ViewType> viewTypes = new HashSet<>();
        if (groupType != null) {
            for (ViewType viewType : ViewType.values()) {
                if (viewType.getGroupType().equals(groupType)) {
                    viewTypes.add(viewType);
                }
            }
        }
        return viewTypes;
    }

    public Class<? extends ResultModel> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<? extends ResultModel> dataClass) {
        this.dataClass = dataClass;
    }

    public ViewGroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(ViewGroupType groupType) {
        this.groupType = groupType;
    }

    public String getImageClass() {
        return imageClass;
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
    public String getName() {
        return name;
    }
}