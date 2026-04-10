package net.ooder.annotation.ui;

import net.ooder.annotation.Enumstype;
import net.ooder.annotation.IconEnumstype;
import net.ooder.annotation.ViewGroupType;
import net.ooder.annotation.ViewType;
import net.ooder.annotation.event.*;
import net.ooder.annotation.menu.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ModuleViewType implements IconEnumstype {
    GRIDCONFIG("列表", "ri-table-line", AppendType.ref, "CustomTreeGridViewMeta", "CustomTreeGridDataMeta", new ViewType[]{ViewType.GRID}, new ComponentType[]{ComponentType.TREEGRID}, new Class[]{CustomTreeGridEvent.class}, TreeGridMenu.class),
    MGRIDCONFIG("列表", "ri-table-line", AppendType.ref, "CustomMTreeGridViewMeta", "CustomTreeGridDataMeta", new ViewType[]{ViewType.GRID}, new ComponentType[]{ComponentType.MTREEGRID}, new Class[]{CustomTreeGridEvent.class}, TreeGridMenu.class),
    GALLERYCONFIG("画廊", "ri-grid-line", AppendType.ref, "CustomGalleryViewMeta", "CustomGalleryDataMeta", new ViewType[]{ViewType.GALLERY}, new ComponentType[]{ComponentType.GALLERY}, new Class[]{CustomGalleryEvent.class}, TreeGridMenu.class),
    OPINIONCONFIG("意见", "ri-chat-3-line", AppendType.ref, "CustomOpinionViewMeta", "CustomOpinionDataMeta", new ViewType[]{ViewType.OPINIONBLOCK}, new ComponentType[]{ComponentType.OPINION}, new Class[]{CustomOpinionEvent.class}, OpinionMenu.class),
    BUTTONLAYOUTCONFIG("按钮布局", "ri-grid-line", AppendType.append, "CustomButtonLayoutViewMeta", "CustomButtonLayoutDataMeta", new ViewType[]{ViewType.MOBILELAYOUT}, new ComponentType[]{ComponentType.BUTTONLAYOUT}, new Class[]{CustomButtonLayoutEvent.class}, ButtonLayoutMenu.class),
    NAVBUTTONLAYOUTCONFIG("按钮布局", "ri-grid-line", AppendType.append, "NavButtonLayoutComboViewMeta", "NavButtonLayoutDataMeta", new ViewType[]{ViewType.MOBILELAYOUT}, new ComponentType[]{ComponentType.BUTTONLAYOUT}, new Class[]{CustomButtonLayoutEvent.class}, ButtonLayoutMenu.class),
    TITLEBLOCKCONFIG("磁贴块", "ri-grid-line", AppendType.ref, "CustomTitleBlockViewMeta", "CustomTitleBlockDataMeta", new ViewType[]{ViewType.TITLEBLOCK}, new ComponentType[]{ComponentType.TITLEBLOCK}, new Class[]{CustomTitleBlockEvent.class}, TitleBlockMenu.class),
    CONTENTBLOCKCONFIG("内容列表", "ri-list-check", AppendType.ref, "CustomContentBlockViewMeta", "CustomContentBlockDataMeta", new ViewType[]{ViewType.CONTENTBLOCK}, new ComponentType[]{ComponentType.CONTENTBLOCK}, new Class[]{CustomContentBlockEvent.class}, ContentBlockMenu.class),
    MODULECONFIG("模块视图", "ri-box-2-line", AppendType.ref, "CustomModuleMeta", "CustomBlockDataMeta", new ViewType[]{ViewType.MODULE}, new ComponentType[]{ComponentType.MODULE}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    BLOCKCONFIG("块视图", "ri-box-2-line", AppendType.append, "CustomBlockFormViewMeta", "CustomBlockDataMeta", new ViewType[]{ViewType.BLOCK}, new ComponentType[]{ComponentType.BLOCK}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    PANELCONFIG("面板视图", "ri-window-line", AppendType.append, "CustomPanelFormViewMeta", "CustomPanelDataMeta", new ViewType[]{ViewType.PANEL}, new ComponentType[]{ComponentType.PANEL}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    GROUPCONFIG("导航", "ri-file-list-line", AppendType.append, "CustomGroupFormViewMeta", "CustomGroupDataMeta", new ViewType[]{ViewType.NAVGROUP}, new ComponentType[]{ComponentType.GROUP}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    SVGPAPERCONFIG("SVGPage", "ri-edit-2-line", AppendType.append, "CustomSVGPaperViewMeta", "CustomSVGPaperDataMeta", new ViewType[]{ViewType.SVG}, new ComponentType[]{ComponentType.SVGPAPER}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    DIVCONFIG("层视图", "ri-square-line", AppendType.append, "CustomDivFormViewMeta", "CustomDivDataMeta", new ViewType[]{ViewType.DIV}, new ComponentType[]{ComponentType.DIV}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    MFORMCONFIG("表单", "ri-file-text-line", AppendType.ref, "CustomMFormViewMeta", "CustomFormDataMeta", new ViewType[]{ViewType.FORM}, new ComponentType[]{ComponentType.MFORMLAYOUT}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    FORMCONFIG("表单", "ri-file-text-line", AppendType.ref, "CustomFormViewMeta", "CustomFormDataMeta", new ViewType[]{ViewType.FORM}, new ComponentType[]{ComponentType.FORMLAYOUT}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    NAVGROUPCONFIG("导航", "ri-file-list-line", AppendType.append, "NavGroupViewMeta", "NavGroupDataMeta", new ViewType[]{ViewType.NAVGROUP}, new ComponentType[]{ComponentType.GROUP}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    DYNCONFIG("动态", "ri-magic-line", AppendType.ref, "CustomDynViewMeta", "CustomDynDataMeta", ViewType.values(), new ComponentType[]{}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    NONE("无界面", "ri-close-circle-line", AppendType.append, Void.class.getName(), Void.class.getName(), new ViewType[]{}, new ComponentType[]{}, new Class[]{CustomTreeEvent.class}),
    UPLOADCONFIG("文件上传", "ri-upload-line", AppendType.append, Void.class.getName(), Void.class.getName(), new ViewType[]{}, new ComponentType[]{}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    NAVGALLERYCONFIG("画廊导航", "ri-grid-line", AppendType.append, "NavGalleryComboViewMeta", "NavGalleryDataMeta", new ViewType[]{ViewType.NAVGALLERY}, new ComponentType[]{ComponentType.GALLERY}, new Class[]{CustomGalleryEvent.class}, TreeGridMenu.class),
    LAYOUTCONFIG("布局", "ri-grid-line", AppendType.append, "CustomLayoutViewMeta", "CustomLayoutDataMeta", new ViewType[]{ViewType.LAYOUT}, new ComponentType[]{ComponentType.LAYOUT}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    CHARTCONFIG("统计图", "ri-bar-chart-line", AppendType.ref, "CustomFChartViewMeta", "CustomFChartDataMeta", new ViewType[]{ViewType.FCHARTS}, new ComponentType[]{ComponentType.FCHART}, new Class[]{CustomFieldEvent.class}, CustomFormMenu.class),
    ECHARTCONFIG("统计图", "ri-line-chart-line", AppendType.ref, "CustomEChartViewMeta", "CustomEChartDataMeta", new ViewType[]{ViewType.ECHARTS}, new ComponentType[]{ComponentType.ECHARTS}, new Class[]{CustomFieldEvent.class}, CustomFormMenu.class),
    NAVMENUBARCONFIG("菜单导航", "ri-menu-line", AppendType.append, "NavMenuBarViewMeta", "NavMenuBarDataMeta", new ViewType[]{ViewType.NAVMENUBAR}, new ComponentType[]{ComponentType.LAYOUT, ComponentType.MENUBAR}, new Class[]{CustomFormEvent.class}, CustomFormMenu.class),
    NAVFOLDINGTREECONFIG("Tree导航", "ri-file-list-line", AppendType.append, "NavFoldingComboViewMeta", "NavFoldingTreeDataMeta", new ViewType[]{ViewType.NAVFOLDINGTREE}, new ComponentType[]{ComponentType.LAYOUT, ComponentType.FOLDINGTABS, ComponentType.TREEVIEW}, new Class[]{CustomTreeEvent.class}, TreeMenu.class),
    NAVFOLDINGTABSCONFIG("NavTabs导航", "ri-stack-line", AppendType.append, "NavFoldingTabsViewMeta", "NavFoldingTabsDataMeta", new ViewType[]{ViewType.NAVFOLDINGTABS}, new ComponentType[]{ComponentType.LAYOUT, ComponentType.FOLDINGTABS}, new Class[]{CustomTabsEvent.class}, TreeMenu.class),
    NAVBUTTONVIEWSCONFIG("按钮导航", "ri-grid-line", AppendType.append, "CustomButtonViewsViewMeta", "ButtonViewsDataMeta", new ViewType[]{ViewType.NAVBUTTONVIEWS}, new ComponentType[]{ComponentType.BUTTONVIEWS}, new Class[]{CustomTabsEvent.class}, CustomFormMenu.class),
    MBUTTONVIEWSCONFIG("按钮导航", "ri-grid-line", AppendType.append, "MButtonViewsViewMeta", "ButtonViewsDataMeta", new ViewType[]{ViewType.NAVBUTTONVIEWS}, new ComponentType[]{ComponentType.MBUTTONVIEWS}, new Class[]{CustomTabsEvent.class}, CustomFormMenu.class),
    NAVSTACKSCONFIG("按钮导航", "ri-stack-line", AppendType.append, "StacksViewMeta", "StacksDataMeta", new ViewType[]{ViewType.NAVSTACKS}, new ComponentType[]{ComponentType.STACKS}, new Class[]{CustomTabsEvent.class}, CustomFormMenu.class),
    NAVTABSCONFIG("TAB导航", "ri-stack-line", AppendType.append, "TabsViewMeta", "TabsDataMeta", new ViewType[]{ViewType.NAVTABS}, new ComponentType[]{ComponentType.TABS}, new Class[]{CustomTabsEvent.class}, CustomFormMenu.class),
    NAVTREECONFIG("Tree导航", "ri-file-list-line", AppendType.append, "NavTreeComboViewMeta", "NavTreeDataMeta", new ViewType[]{ViewType.NAVTREE}, new ComponentType[]{ComponentType.LAYOUT, ComponentType.TREEVIEW}, new Class[]{CustomTreeEvent.class}, TreeMenu.class),
    TREECONFIG("树形", "ri-tree-line", AppendType.ref, "CustomTreeViewMeta", "CustomTreeDataMeta", new ViewType[]{ViewType.TREE}, new ComponentType[]{ComponentType.TREEVIEW}, new Class[]{CustomTreeEvent.class}, TreeMenu.class),
    MTREECONFIG("树形", "ri-tree-line", AppendType.ref, "CustomMTreeViewMeta", "CustomTreeDataMeta", new ViewType[]{ViewType.TREE}, new ComponentType[]{ComponentType.MTREEVIEW}, new Class[]{CustomTreeEvent.class}, TreeMenu.class),
    POPTREECONFIG("弹出树", "ri-tree-line", AppendType.ref, "PopTreeViewMeta", "PopTreeDataMeta", new ViewType[]{ViewType.TREE}, new ComponentType[]{}, new Class[]{CustomTreeEvent.class}, TreeMenu.class);

    // 默认包路径 - 使用常量字符串避免枚举初始化问题
    private static final String DEFAULT_VIEW_META_PACKAGE = "net.ooder.engine.ub.component.tabs.";
    private static final String DATA_META_PACKAGE = "net.ooder.engine.ub.bridge.view.";

    // ViewMeta 类所在的包路径列表 - 用于多包查找
    private static final String[] VIEW_META_PACKAGES = {
            "net.ooder.engine.ub.component.tabs.",
            "net.ooder.engine.ub.component.form.",
            "net.ooder.engine.ub.component.tree.",
            "net.ooder.engine.ub.component.chart.",
            "net.ooder.engine.ub.component.gallery.",
            "net.ooder.engine.ub.component.bar.",
            "net.ooder.engine.ub.component.svg.",
            "net.ooder.engine.ub.component.grid.",
            "net.ooder.engine.ub.module.",
            "net.ooder.engine.ub.component."
    };

    // 类加载缓存
    private static final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    private final String beanClassName;
    private final String dataClassName;
    private final Class<? extends Enumstype>[] eventClass;
    private final ComponentType[] componentTypes;
    private final String name;
    private AppendType appendType;
    private final ViewType[] viewTypes;
    private final Class<? extends IconEnumstype>[] menuClasses;
    private final String imageClass;

    ModuleViewType(String name, String imageClass, AppendType appendType, String beanClassName, String dataClassName, ViewType[] viewTypes, ComponentType[] componentTypes, Class<? extends Enumstype>[] eventClass, Class<? extends IconEnumstype>... menuClasses) {
        this.name = name;
        this.appendType = appendType;
        this.componentTypes = componentTypes;
        this.eventClass = eventClass;
        this.imageClass = imageClass;
        if (beanClassName.startsWith("java.lang")) {
            this.beanClassName = beanClassName;
        } else {
            this.beanClassName = DEFAULT_VIEW_META_PACKAGE + beanClassName;
        }
        if (dataClassName.startsWith("java.lang")) {
            this.dataClassName = dataClassName;
        } else {
            this.dataClassName = DATA_META_PACKAGE + dataClassName;
        }

        this.viewTypes = viewTypes;
        this.menuClasses = menuClasses;
    }


    public ViewType[] getViewTypes() {
        return viewTypes;
    }

    public static ModuleViewType getModuleViewByCom(ComponentType... componentType) {

        for (ModuleViewType moduleViewType : ModuleViewType.values()) {
            List<ComponentType> componentTypeList = Arrays.asList(moduleViewType.getComponentTypes());
            if (componentTypeList.size() == 1 && componentTypeList.get(0).equals(componentType[0])) {
                return moduleViewType;
            }
        }


        for (ModuleViewType moduleViewType : ModuleViewType.values()) {
            List<ComponentType> componentTypeList = Arrays.asList(moduleViewType.getComponentTypes());
            if (componentTypeList.contains(componentType)) {
                return moduleViewType;
            }
        }
        return NONE;
    }

    public static ModuleViewType getModuleViewByViewType(ViewType viewType) {
        for (ModuleViewType moduleViewType : ModuleViewType.values()) {
            List<ViewType> viewTypeList = Arrays.asList(moduleViewType.getViewTypes());
            if (viewTypeList.contains(viewType) && !moduleViewType.equals(ModuleViewType.DYNCONFIG)) {
                return moduleViewType;
            }
        }
        return NONE;

    }

    public static List<ModuleViewType> getModuleViewByGroup(ViewGroupType groupType) {
        List<ModuleViewType> moduleViewTypes = new ArrayList<>();
        for (ModuleViewType moduleViewType : ModuleViewType.values()) {
            for (ViewType viewType : moduleViewType.getViewTypes()) {
                if (viewType.getGroupType().equals(groupType) && !moduleViewTypes.contains(moduleViewType)) {
                    moduleViewTypes.add(moduleViewType);
                }
                ;
            }
        }
        return moduleViewTypes;

    }

    public Class<? extends Enumstype>[] getEventClass() {
        return eventClass;
    }


    public ViewType getDefaultView() {

        if (viewTypes.length > 0) {
            return viewTypes[0];
        }
        return null;

    }

    public AppendType getAppendType() {
        return appendType;
    }

    public void setAppendType(AppendType appendType) {
        this.appendType = appendType;
    }

    public ComponentType[] getComponentTypes() {
        return componentTypes;
    }

    public Class<? extends IconEnumstype>[] getMenuClasses() {
        return menuClasses;
    }

    /**
     * 加载类（带缓存和多包查找）
     */
    private static Class<?> loadClassWithCache(String className, String[] packages) {
        // 检查缓存
        Class<?> cachedClass = classCache.get(className);
        if (cachedClass != null) {
            return cachedClass;
        }

        // 首先尝试从默认包加载
        try {
            Class<?> clazz = Class.forName(className);
            classCache.put(className, clazz);
            return clazz;
        } catch (ClassNotFoundException ignored) {
        }

        // 如果默认包加载失败，尝试从其他包加载
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        for (String packageName : packages) {
            String fullClassName = packageName + simpleClassName;
            try {
                Class<?> clazz = Class.forName(fullClassName);
                classCache.put(className, clazz);
                return clazz;
            } catch (ClassNotFoundException ignored) {
            }
        }

        // 所有包都尝试失败，打印错误并返回null
        System.err.println("无法找到类: " + className + "，已尝试以下包: " + String.join(", ", packages));
        return null;
    }

    public Class getDataClass() {
        return loadClassWithCache(dataClassName, new String[]{DATA_META_PACKAGE});
    }

    public Class getMetaClass() {
        return loadClassWithCache(beanClassName, VIEW_META_PACKAGES);
    }

    public String getMetaClassName() {
        String metaClassName = beanClassName;
        if (this.getMetaClass() != null) {
            metaClassName = this.getMetaClass().getName();
        }
        return metaClassName;
    }

    public String getDataClassName() {
        String className = dataClassName;
        if (this.getDataClass() != null) {
            className = this.getDataClass().getName();
        }
        return className;
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

    public String getImageClass() {
        return imageClass;
    }
}