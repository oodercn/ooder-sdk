package net.ooder.annotation.ui;


import net.ooder.annotation.IconEnumstype;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public enum ComponentType implements IconEnumstype {
    UI("ood.UI", "UIComponent", "基础面板", new ComponentBaseType[]{ComponentBaseType.absObj}, "ri-stack-line"),
    WIDGET("ood.UI.Widget", "UIComponent", "基础面板", new ComponentBaseType[]{ComponentBaseType.absObj}, "ri-stack-line", ComponentType.UI),
    BAR("ood.UI.Bar", "UIComponent", "按钮组", new ComponentBaseType[]{ComponentBaseType.absObj}, "ri-tools-line", ComponentType.UI),
    LIST("ood.UI.List", "ListUIComponent", "选择器", new ComponentBaseType[]{ComponentBaseType.absValue, ComponentBaseType.absList}, "ri-list-check", ComponentType.UI),
    ELEMENT("ood.UI.Element", "ElementUIComponent", "HTML控件", new ComponentBaseType[]{}, "ri-code-line", ComponentType.UI),
    HTMLBUTTON("ood.UI.HTMLButton", "HTMLButtonUIComponent", "原生按钮", new ComponentBaseType[]{}, "ri-checkbox-blank-line", ComponentType.ELEMENT),
    SPAN("ood.UI.Span", "SpanUIComponent", "字体", new ComponentBaseType[]{}, "ri-font-size", ComponentType.UI),
    TABS("ood.UI.Tabs", "TabsUIComponent", "TAB容器", new ComponentBaseType[]{ComponentBaseType.absValue, ComponentBaseType.absList}, "ri-layout-column-line", ComponentType.UI),
    DIV("ood.UI.Div", "DivUIComponent", "层面板", new ComponentBaseType[]{}, "ri-stack-line", ComponentType.UI),
    PANEL("ood.UI.Panel", "PanelUIComponent", "普通面板", new ComponentBaseType[]{}, "ri-window-line", ComponentType.DIV),
    INPUT("ood.UI.Input", "InputUIComponent", "普通输入域", new ComponentBaseType[]{ComponentBaseType.Widget, ComponentBaseType.absValue}, "ri-keyboard-line"),
    COMBOINPUT("ood.UI.ComboInput", "ComboInputUIComponent", "复合输入框", new ComponentBaseType[]{ComponentBaseType.absValue}, "ri-search-line", ComponentType.INPUT),
    CHECKBOX("ood.UI.CheckBox", "CheckBoxUIComponent", "单选框", new ComponentBaseType[]{ComponentBaseType.absValue}, "ri-checkbox-line", ComponentType.UI),
    RADIOBOX("ood.UI.RadioBox", "RadioBoxUIComponent", "多选框", new ComponentBaseType[]{}, "ri-radio-button-line", ComponentType.LIST),
    MULTILINES("ood.UI.Input", "InputUIComponent", "多行输入框", new ComponentBaseType[]{ComponentBaseType.Widget, ComponentBaseType.absValue}, "ri-align-left"),
    RICHEDITOR("ood.UI.RichEditor", "RichEditorUIComponent", "富文本编辑", new ComponentBaseType[]{ComponentBaseType.absValue}, "ri-edit-line", ComponentType.UI),
    CODEEDITOR("RAD.expression.CodeEditor", "CodeEditorUIComponent", "代码编辑器", new ComponentBaseType[]{ComponentBaseType.Widget, ComponentBaseType.absValue}, "ri-code-line"),
    JAVAEDITOR("RAD.expression.JavaEditor", "JavaEditorUIComponent", "Java Editor", new ComponentBaseType[]{ComponentBaseType.Widget, ComponentBaseType.absValue}, "ri-code-line"),
    TEXTEDITOR("ood.UI.RichEditor", "RichEditorUIComponent", "文本编辑", new ComponentBaseType[]{ComponentBaseType.Widget, ComponentBaseType.absValue}, "ri-file-text-line"),
    HIDDENINPUT("ood.UI.HiddenInput", "HiddenInputUIComponent", "隐藏域", new ComponentBaseType[]{ComponentBaseType.Widget, ComponentBaseType.absValue}, "ri-eye-off-line"),
    BUTTON("ood.UI.Button", "ButtonUIComponent", "通用按钮", new ComponentBaseType[]{ComponentBaseType.absValue}, "ri-checkbox-blank-circle-line", ComponentType.HTMLBUTTON),
    PROGRESSBAR("ood.UI.ProgressBar", "ProgressBarUIComponent", "进度条", new ComponentBaseType[]{ComponentBaseType.Widget, ComponentBaseType.absValue}, "ri-speed-line"),
    SLIDER("ood.UI.Slider", "SliderUIComponent", "滑动块", new ComponentBaseType[]{ComponentBaseType.absValue}, "ri-slideshow-line", ComponentType.UI),
    FILEUPLOAD("ood.UI.FileUpload", "FileUploadUIComponent", "文件上传框", new ComponentBaseType[]{ComponentBaseType.absValue}, "ri-upload-line", ComponentType.UI),
    TENSOR("ood.UI.Tensor", "TensorUIComponent", "AI寻物", new ComponentBaseType[]{ComponentBaseType.absValue}, "ri-robot-line", ComponentType.UI),

    LABEL("ood.UI.Label", "LabelUIComponent", "文本标签", new ComponentBaseType[]{ComponentBaseType.absValue}, "ri-price-tag-line", ComponentType.UI),
    TIMEPICKER("ood.UI.TimePicker", "TimePickerUIComponent", "时间选择框", new ComponentBaseType[]{}, "ri-time-line", ComponentType.UI),
    LINK("ood.UI.Link", "LinkUIComponent", "HTML链接", new ComponentBaseType[]{}, "ri-link", ComponentType.UI),
    ICON("ood.UI.Icon", "IconUIComponent", "ICON图标", new ComponentBaseType[]{}, "ri-apps-line", ComponentType.UI),
    FLASH("ood.UI.Flash", "FlashUIComponent", "Flash播放器", new ComponentBaseType[]{}, "ri-flashlight-line", ComponentType.UI),
    COLORPICKER("ood.UI.ColorPicker", "ColorPickerUIComponent", "颜色选择器", new ComponentBaseType[]{}, "ri-palette-line", ComponentType.UI),
    DATEPICKER("ood.UI.DatePicker", "DatePickerUIComponent", "日期选择框", new ComponentBaseType[]{}, "ri-calendar-line", ComponentType.UI),
    FORMLAYOUT("ood.UI.FormLayout", "FormLayoutUIComponent", "计算表格", new ComponentBaseType[]{ComponentBaseType.absList}, "ri-layout-3-line", ComponentType.UI),
    MFORMLAYOUT("ood.UI.MFormLayout", "MFormLayoutUIComponent", "计算表格", new ComponentBaseType[]{ComponentBaseType.absList}, "ri-layout-3-line", ComponentType.UI),
    STATUSBUTTONS("ood.UI.StatusButtons", "StatusButtonsUIComponent", "状态按钮", new ComponentBaseType[]{ComponentBaseType.absList}, "ri-toggle-line", ComponentType.BAR),
    AUDIO("ood.UI.Audio", "AudioUIComponent", "音频播放", new ComponentBaseType[]{}, "ri-volume-up-line", ComponentType.UI),
    OPINION("ood.UI.Opinion", "OpinionUIComponent", "意见框", new ComponentBaseType[]{}, "ri-chat-3-line", ComponentType.LIST),
    MODULEPLACEHOLDER("ood.UI.ModulePlaceHolder", "ModulePlaceHolder", "Module Placeholder", new ComponentBaseType[]{}, "ri-box-2-line", ComponentType.COMBOINPUT),
    BUTTONVIEWS("ood.UI.ButtonViews", "ButtonViewsUIComponent", "Button Views", new ComponentBaseType[]{}, "ri-grid-line", ComponentType.TABS),
    MBUTTONVIEWS("ood.UI.MTabs", "MButtonViewsUIComponent", "Mobile Tabs", new ComponentBaseType[]{}, "ri-grid-line", ComponentType.TABS),
    PAGEBAR("ood.UI.PageBar", "PageBarUIComponent", "翻页栏", new ComponentBaseType[]{}, "ri-skip-left-line", ComponentType.UI),
    DIALOG("ood.UI.Dialog", "DialogUIComponent", "弹出框", new ComponentBaseType[]{ComponentBaseType.Widget}, "ri-window-line"),
    MDIALOG("ood.UI.MDialog", "MDialogUIComponent", "弹出框", new ComponentBaseType[]{ComponentBaseType.Widget}, "ri-window-line"),
    IMAGE("ood.UI.Image", "ImageUIComponent", "图片", new ComponentBaseType[]{}, "ri-image-line", ComponentType.UI),
    MENUBAR("ood.UI.MenuBar", "MenuBarUIComponent", "菜单", new ComponentBaseType[]{ComponentBaseType.absList}, "ri-menu-line", ComponentType.BAR),
    POPMENU("ood.UI.PopMenu", "PopMenuUIComponent", "弹出菜单", new ComponentBaseType[]{ComponentBaseType.Widget, ComponentBaseType.absList}, "ri-more-fill", ComponentType.BAR),
    RESIZER("ood.UI.Resizer", "ResizerUIComponent", "大小调整", new ComponentBaseType[]{}, "ri-drag-move-line", ComponentType.UI),
    STACKS("ood.UI.Stacks", "StacksUIComponent", "堆栈容器", new ComponentBaseType[]{}, "ri-stack-line", ComponentType.TABS),
    VIDEO("ood.UI.Video", "VideoUIComponent", "视频播放", new ComponentBaseType[]{}, "ri-video-line", ComponentType.AUDIO),
    CAMERA("ood.UI.Camera", "CameraUIComponent", "Camera", new ComponentBaseType[]{}, "ri-camera-line", ComponentType.AUDIO),
    TOOLBAR("ood.UI.ToolBar", "ToolBarUIComponent", "工具栏", new ComponentBaseType[]{ComponentBaseType.absList}, "ri-tools-line", ComponentType.BAR),
    TREEBAR("ood.UI.TreeBar", "TreeBarUIComponent", "树形导航", new ComponentBaseType[]{ComponentBaseType.absList}, "ri-menu-line", ComponentType.UI),
    CSSBOX("ood.UI.CSSBox", "CSSBoxUIComponent", "样式渲染器", new ComponentBaseType[]{}, "ri-paint-brush-line", ComponentType.SPAN),
    BLOCK("ood.UI.Block", "BlockUIComponent", "块容器", new ComponentBaseType[]{ComponentBaseType.Widget}, "ri-layout-5-line"),
    GROUP("ood.UI.Group", "GroupUIComponent", "组容器", new ComponentBaseType[]{}, "ri-group-line", ComponentType.UI),
    TREEVIEW("ood.UI.TreeView", "TreeViewUIComponent", "Tree数据导航", new ComponentBaseType[]{}, "ri-file-list-line", ComponentType.TREEBAR),
    MTREEVIEW("ood.UI.MTreeView", "MTreeViewUIComponent", "树形视图", new ComponentBaseType[]{}, "ri-file-list-line", ComponentType.TREEBAR),
    LAYOUT("ood.UI.Layout", "LayoutUIComponent", "布局容器", new ComponentBaseType[]{ComponentBaseType.absList}, "ri-layout-4-line", ComponentType.UI),
    DATABINDER("ood.DataBinder", "DataBinderUIComponent", "数据绑定器", new ComponentBaseType[]{ComponentBaseType.absObj}, "ri-link"),
    MQTT("ood.MQTT", "MQTTUIComponent", "MQTT通讯", new ComponentBaseType[]{ComponentBaseType.absObj}, "ri-message-2-line"),
    MESSAGESERVICE("ood.MessageService", "MessageUIComponent", "跨站点消息", new ComponentBaseType[]{ComponentBaseType.absObj}, "ri-chat-3-line"),
    APICALLER("ood.APICaller", "APICallerUIComponent", "远程调用", new ComponentBaseType[]{ComponentBaseType.absObj}, "ri-cloud-line"),
    TIMER("ood.Timer", "TimerUIComponent", "计时器", new ComponentBaseType[]{ComponentBaseType.absObj}, "ri-timer-line"),
    ANIMBINDER("ood.AnimBinder", "AnimBinderUIComponent", "动画编辑器", new ComponentBaseType[]{ComponentBaseType.absObj}, "ri-film-line"),
    MODULE("ood.Module", "ModuleUIComponent", "子模块", new ComponentBaseType[]{ComponentBaseType.absProfile, ComponentBaseType.absValue}, "ri-stack-line"),
    SVGCIRCLE("ood.svg.circle", "SVGCircleUIComponent", "SVG Circle", new ComponentBaseType[]{ComponentBaseType.svg}, "ri-checkbox-blank-circle-line"),
    SVGELLIPSE("ood.svg.ellipse", "SVGEllipseUIComponent", "SVG Ellipse", new ComponentBaseType[]{ComponentBaseType.svg}, "ri-checkbox-blank-line"),
    SVGRECT("ood.svg.rect", "SVGRectUIComponent", "SVG Rectangle", new ComponentBaseType[]{ComponentBaseType.svg}, "ri-checkbox-blank-line"),
    SVGIMAGE("ood.svg.image", "SVGImageUIComponent", "SVG Image", new ComponentBaseType[]{ComponentBaseType.svg}, "ri-image-line"),
    SVGTEXT("ood.svg.text", "SVGTextUIComponent", "SVG Text", new ComponentBaseType[]{ComponentBaseType.svg}, "ri-font-size"),
    SVGPATH("ood.svg.path", "SVGPathUIComponent", "SVG Path", new ComponentBaseType[]{ComponentBaseType.svg}, "ri-edit-2-line"),
    SVGRECTCOMB("ood.svg.rectComb", "SVGRectCombUIComponent", "SVG Rect Combination", new ComponentBaseType[]{ComponentBaseType.absComb}, "ri-layout-6-line"),
    SVGGROUP("ood.svg.group", "SVGGroupUIComponent", "SVG Group", new ComponentBaseType[]{ComponentBaseType.absComb}, "ri-group-line"),
    SVGIMAGECOMB("ood.svg.imageComb", "SVGImageCombUIComponent", "SVG Image Combination", new ComponentBaseType[]{ComponentBaseType.absComb}, "ri-image-line"),
    SVGELLIPSECOMB("ood.svg.ellipseComb", "SVGEllipseCombUIComponent", "SVG Ellipse Combination", new ComponentBaseType[]{ComponentBaseType.absComb}, "ri-group-line"),
    SVGCIRCLECOMB("ood.svg.circleComb", "SVGCircleCombUIComponent", "SVG Circle Combination", new ComponentBaseType[]{ComponentBaseType.absComb}, "ri-group-line"),
    SVGPATHCOMB("ood.svg.pathComb", "SVGPathCombUIComponent", "SVG Path Combination", new ComponentBaseType[]{ComponentBaseType.absComb}, "ri-group-line"),
    SVGCONNECTOR("ood.svg.connector", "SVGConnectorUIComponent", "SVG Connector", new ComponentBaseType[]{ComponentBaseType.absComb}, "ri-plug-line"),
    SVGPAPER("ood.UI.SVGPaper", "SVGPaperUIComponent", "画布", new ComponentBaseType[]{}, "ri-artboard-line", ComponentType.DIV),
    ECHARTS("ood.UI.ECharts", "EChartUIComponent", "ECharts统计图", new ComponentBaseType[]{}, "ri-bar-chart-line", ComponentType.UI),
    FOLDINGLIST("ood.UI.FoldingList", "FoldingListUIComponent", "Folding布局", new ComponentBaseType[]{}, "ri-arrow-down-s-line", ComponentType.LIST),
    FCHART("ood.UI.FusionChartsXT", "FChartUIComponent", "FChart统计图", new ComponentBaseType[]{}, "ri-bar-chart-line"),
    GALLERY("ood.UI.Gallery", "GalleryUIComponent", "Gallery画廊", new ComponentBaseType[]{}, "ri-image-line", ComponentType.LIST),
    BUTTONLAYOUT("ood.UI.ButtonLayout", "ButtonLayoutUIComponent", "按钮导航布局", new ComponentBaseType[]{}, "ri-menu-line", ComponentType.TABS),
    TITLEBLOCK("ood.UI.TitleBlock", "TitleBlockUIComponent", "磁贴布局", new ComponentBaseType[]{}, "ri-grid-line", ComponentType.LIST),
    CONTENTBLOCK("ood.UI.ContentBlock", "ContentBlockUIComponent", "内容列表", new ComponentBaseType[]{}, "ri-list-ordered", ComponentType.LIST),
    TREEGRID("ood.UI.TreeGrid", "TreeGridUIComponent", "列表视图", new ComponentBaseType[]{}, "ri-table-line",ComponentType.LIST),
    MTREEGRID("ood.UI.MTreeGrid", "MTreeGridUIComponent", "树形列表", new ComponentBaseType[]{}, "ri-table-line", ComponentType.TREEGRID),
    FOLDINGTABS("ood.UI.FoldingTabs", "FoldingTabsUIComponent", "FoldingTAB容器", new ComponentBaseType[]{}, "ri-layout-2-line", ComponentType.TABS),


    MBUTTON("ood.Mobile.Button", "MButtonUIComponent", "Mobile Button", new ComponentBaseType[]{ComponentBaseType.absValue, ComponentBaseType.absList}, "ri-phone-fill", ComponentType.UI),
    MINPUT("ood.Mobile.Input", "MInputUIComponent", "Mobile Input", new ComponentBaseType[]{ComponentBaseType.absValue}, "ri-keyboard-line", ComponentType.UI),
    MLIST("ood.Mobile.List", "MListUIComponent", "Mobile List", new ComponentBaseType[]{ComponentBaseType.absList, ComponentBaseType.absValue}, "ri-list-check", ComponentType.UI),
    MSWITCH("ood.Mobile.Switch", "MSwitchUIComponent", "移动端开关", new ComponentBaseType[]{ComponentBaseType.absValue}, "ri-toggle-line", ComponentType.UI),
    // 布局组件
    MPANEL("ood.Mobile.Panel", "MPanelUIComponent", "移动端面板", new ComponentBaseType[]{}, "ri-window-line", ComponentType.UI),
    MLAYOUT("ood.Mobile.Layout", "MLayoutUIComponent", "移动端布局", new ComponentBaseType[]{}, "ri-layout-4-line", ComponentType.UI),
    MGRID("ood.Mobile.TreeGrid", "MTreeGridUIComponent", "移动端网格", new ComponentBaseType[]{}, "ri-grid-fill", ComponentType.UI),

    // 导航组件
    MNAVBAR("ood.Mobile.NavBar", "MNavBarUIComponent", "移动端导航栏", new ComponentBaseType[]{}, "ri-menu-line", ComponentType.UI),
    MTABBAR("ood.Mobile.TabBar", "MTabBarUIComponent", "移动端标签栏", new ComponentBaseType[]{}, "ri-window-line", ComponentType.UI),
    MDRAWER("ood.Mobile.Drawer", "MDrawerUIComponent", "移动端抽屉", new ComponentBaseType[]{}, "ri-indent-decrease", ComponentType.UI),

    // 反馈组件
    MTOAST("ood.Mobile.Toast", "MToastUIComponent", "移动端提示", new ComponentBaseType[]{}, "ri-information-line", ComponentType.UI),
    MMODAL("ood.Mobile.Modal", "MModalUIComponent", "移动端模态框", new ComponentBaseType[]{}, "ri-window-line", ComponentType.UI),

    // 动作组件
    MACTIONSHEET("ood.Mobile.ActionSheet", "MActionSheetUIComponent", "移动端操作表", new ComponentBaseType[]{}, "ri-menu-3-line", ComponentType.UI),
    MFORM("ood.Mobile.Form", "MFormUIComponent", "移动表单", new ComponentBaseType[]{}, "ri-file-list-line", ComponentType.UI),
    MCARD("ood.Mobile.Card", "MCardUIComponent", "信息卡片", new ComponentBaseType[]{}, "ri-bank-card-line", ComponentType.UI),
    MAVATAR("ood.Mobile.Avatar", "MAvatarUIComponent", "用户头像", new ComponentBaseType[]{}, "ri-user-3-line", ComponentType.UI),
    MBADGE("ood.Mobile.Badge", "MBadgeUIComponent", "移动端徽标", new ComponentBaseType[]{}, "ri-notification-badge-line", ComponentType.UI),
    MPICKER("ood.Mobile.Picker", "MPickerUIComponent", "移动端选择器", new ComponentBaseType[]{}, "ri-list-check-2", ComponentType.UI);


    // 默认包路径 - 使用常量字符串避免枚举初始化问题
    private static final String DEFAULT_COMPONENT_PACKAGE = "net.ooder.ui.component.";
    
    // 包路径配置 - 用于多包查找
    private static final String[] COMPONENT_PACKAGES = {
        "net.ooder.ui.component.",
        "net.ooder.ui.component.mobile.",
        "net.ooder.esd.tool.component.",
        "net.ooder.presentation.component.",
        "net.ooder.esd.custom.component.form.field.",
        "net.ooder.esd.tool.properties."
    };
    
    private static Map<ComponentType, Set<ComponentBaseType>> baseTypeMap = new HashMap();
    private static Map<ComponentType, Set<ComponentType>> typeMap = new HashMap();
    private static final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();
    private final String name;
    private final ComponentBaseType[] baseType;
    private final ComponentType[] baseComponent;
    private String type;
    private String componentClassName;
    private String imageClass;
    private String className;

    ComponentType(String className, String componentClassName, String name, ComponentBaseType[] baseType, String imageClass, ComponentType... baseComponent) {

        this.type = name();
        this.className = className;
        this.name = name;
        this.baseType = baseType;
        this.baseComponent = baseComponent;
        this.componentClassName = DEFAULT_COMPONENT_PACKAGE + componentClassName;
        this.imageClass = imageClass;


    }

    public String getClassName() {
        return className;
    }

    public String getImageClass() {
        return imageClass;
    }

    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }

    @Override
    public String toString() {
        return type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public Class getClazz() {
        // 首先检查缓存
        Class<?> cachedClass = classCache.get(componentClassName);
        if (cachedClass != null) {
            return cachedClass;
        }
        
        // 首先尝试从默认包加载
        try {
            Class<?> clazz = Class.forName(componentClassName);
            classCache.put(componentClassName, clazz);
            return clazz;
        } catch (ClassNotFoundException ignored) {
        }
        
        // 如果默认包加载失败，尝试从其他包加载
        String simpleClassName = componentClassName.substring(componentClassName.lastIndexOf('.') + 1);
        for (String packageName : COMPONENT_PACKAGES) {
            String fullClassName = packageName + simpleClassName;
            try {
                Class<?> clazz = Class.forName(fullClassName);
                classCache.put(componentClassName, clazz);
                return clazz;
            } catch (ClassNotFoundException ignored) {
            }
        }
        
        // 所有包都尝试失败，打印错误并返回null
        System.err.println("无法找到组件类: " + componentClassName + "，已尝试以下包: " + String.join(", ", COMPONENT_PACKAGES));
        return null;
    }


    public Boolean isAbsValue() {
        return Arrays.asList(baseType).contains(ComponentBaseType.absValue);
    }

    public Boolean isAbsList() {
        return Arrays.asList(baseType).contains(ComponentBaseType.absList);
    }

    public Boolean isAbsUI() {
        return Arrays.asList(getUIComponents()).contains(this);
    }

    public Boolean isBar() {
        return Arrays.asList(getBarComponents()).contains(this);
    }


    public Boolean isAbsObj() {
        return Arrays.asList(baseType).contains(ComponentBaseType.absObj);
    }

    public Boolean isModuleObj() {
        return isAbsObj() && !isAbsUI();
    }

    public static ComponentType[] getUIComponents() {
        return getAllTypes(UI);
    }

    public static ComponentType[] getFormComponents() {
        return getAllTypes(ComponentBaseType.absValue);
    }

    public Boolean isDataObj() {
        ComponentType[] dataType = getCustomDataComponents();
        return Arrays.asList(dataType).contains(this);
    }

    public static ComponentType[] getCustomDataComponents() {
        return new ComponentType[]{
                COMBOINPUT, RICHEDITOR, HIDDENINPUT, CODEEDITOR, LABEL, IMAGE, INPUT, MODULE, GALLERY, GROUP, TREEBAR, TREEVIEW, TREEGRID, TABS, BUTTONLAYOUT, BLOCK, DIV, PANEL, BUTTONVIEWS, SVGPAPER, FCHART, ECHARTS
        };
    }

    public static ComponentType[] getCustomAPIComponents() {
        return new ComponentType[]{
                APICALLER, MQTT, MESSAGESERVICE, CSSBOX, TIMER, ANIMBINDER
        };
    }

    public static ComponentType[] getContainerComponents() {
        return new ComponentType[]{
                PANEL, DIV, BLOCK, FORMLAYOUT
        };
    }


    public static ComponentType[] getCustomConnComponents() {
        return new ComponentType[]{
                PANEL, DIV, BLOCK, GROUP, TIMER, ANIMBINDER
        };
    }


    public static ComponentType[] getListComponents() {
        return getAllTypes(ComponentBaseType.absList);
    }

    public static ComponentType[] getTreeGridComponents() {
        return getAllTypes(TREEGRID);
    }

    public static ComponentType[] getBarComponents() {
        return getAllTypes(BAR);
    }

    public static ComponentType[] getTabsComponents() {
        return getAllTypes(TABS);
    }


    public static ComponentType[] getAllTypes(ComponentType componentType) {
        Set<ComponentType> allTypes = new HashSet<>();
        allTypes.add(componentType);
        ComponentType[] types = ComponentType.values();
        for (ComponentType type : types) {
            Set<ComponentType> baseTypeSet = typeMap.get(type);
            if (baseTypeSet == null) {
                baseTypeSet = getAlltypes(type, null);
                typeMap.put(type, baseTypeSet);
            }
            if (baseTypeSet.contains(componentType)) {
                allTypes.add(type);
            }
        }
        return allTypes.toArray(new ComponentType[]{});
    }


    public static ComponentType[] getAllTypes(ComponentBaseType baseType) {
        Set<ComponentType> allTypes = new HashSet<>();
        ComponentType[] types = ComponentType.values();
        for (ComponentType type : types) {
            Set<ComponentBaseType> baseTypeSet = baseTypeMap.get(type);
            if (baseTypeSet == null) {
                baseTypeSet = getAllBaseTypes(type, null);
                baseTypeMap.put(type, baseTypeSet);
            }
            if (baseTypeSet.contains(baseType)) {
                allTypes.add(type);
            }
        }
        return allTypes.toArray(new ComponentType[]{});
    }

    public static Set<ComponentType> getAlltypes(ComponentType componentType, Set<ComponentType> baseTypeList) {
        if (baseTypeList == null) {
            baseTypeList = new HashSet<>();
        }
        baseTypeList.addAll(Arrays.asList(componentType.getBaseComponent()));
        ComponentType[] types = componentType.getBaseComponent();
        for (ComponentType baseType : types) {
            getAlltypes(baseType, baseTypeList);
        }
        return baseTypeList;
    }

    public static Set<ComponentBaseType> getAllBaseTypes(ComponentType componentType, Set<ComponentBaseType> baseTypeList) {
        if (baseTypeList == null) {
            baseTypeList = new HashSet<>();
        }
        baseTypeList.addAll(Arrays.asList(componentType.getBaseType()));
        ComponentType[] types = componentType.getBaseComponent();
        for (ComponentType baseType : types) {
            getAllBaseTypes(baseType, baseTypeList);
        }
        return baseTypeList;
    }

    public static ComponentType fromType(String typeName) {
        for (ComponentType type : ComponentType.values()) {
            if (type.getClassName().equals(typeName)) {
                return type;
            }
        }
        return MODULE;
    }

    public ComponentBaseType[] getBaseType() {
        return baseType;
    }

    public ComponentType[] getBaseComponent() {
        return baseComponent;
    }
}
