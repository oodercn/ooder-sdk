package net.ooder.annotation.ui;

import net.ooder.annotation.IconEnumstype;
import net.ooder.annotation.CustomMenu;
import net.ooder.annotation.event.CustomDynMenu;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.menu.TreeGridMenu;
import net.ooder.annotation.menu.TreeGridRowMenu;
import net.ooder.annotation.menu.TreeMenu;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum CustomMenuItem implements IconEnumstype {

    ADD("添加", "addPath", "Add", new ComponentType[]{ComponentType.TREEGRID, ComponentType.GALLERY}, TreeGridMenu.ADD, ModuleViewType.FORMCONFIG, true),

    EDITOR("编辑", "editorPath", "Editor", new ComponentType[]{ComponentType.TREEGRID, ComponentType.GALLERY}, TreeGridRowMenu.EDITOR, ModuleViewType.FORMCONFIG, true),

    RELOAD("列表", "dataUrl", "DataList", new ComponentType[]{ComponentType.TREEGRID, ComponentType.GALLERY}, TreeGridMenu.RELOAD, ModuleViewType.NONE, false),

    CATEGORIESURL("图表", "categoriesUrl", "DataList", new ComponentType[]{ComponentType.FCHART}, TreeGridMenu.RELOAD, ModuleViewType.NONE, false),

    DATASETURL("图表", "datasetUrl", "DataList", new ComponentType[]{ComponentType.FCHART}, TreeGridMenu.RELOAD, ModuleViewType.NONE, false),

    DATAURL("图表", "dataUrl", "DataList", new ComponentType[]{ComponentType.FCHART}, TreeGridMenu.RELOAD, ModuleViewType.NONE, false),

    TRENDLINESURL("图表", "trendlinesUrl", "DataList", new ComponentType[]{ComponentType.FCHART}, TreeGridMenu.RELOAD, ModuleViewType.NONE, false),

    DYNRELOAD("刷新", "dynReloadUrl", "dynReload", new ComponentType[]{ComponentType.BLOCK}, TreeGridMenu.RELOAD, ModuleViewType.DYNCONFIG, true),

    SEARCH("查找", "searchUrl", "Search", new ComponentType[]{ComponentType.FORMLAYOUT}, CustomFormMenu.SEARCH, ModuleViewType.GRIDCONFIG, false, true),

    SAVE("保存", "saveUrl", "save", new ComponentType[]{ComponentType.FORMLAYOUT, ComponentType.TREEBAR, ComponentType.TREEVIEW, ComponentType.GALLERY, ComponentType.TREEGRID}, CustomFormMenu.SAVE, ModuleViewType.NONE, false, true),

    SAVEROW("保存记录", "saveRowPath", "save", new ComponentType[]{ComponentType.TREEBAR, ComponentType.TREEVIEW, ComponentType.GALLERY, ComponentType.TREEGRID}, TreeGridRowMenu.SAVEROW, ModuleViewType.NONE, false, true),

    SAVEALLROW("保存表格记录", "saveAllRowPath", "save", new ComponentType[]{ComponentType.TREEBAR, ComponentType.TREEVIEW, ComponentType.GALLERY, ComponentType.TREEGRID}, TreeGridRowMenu.SAVEROW, ModuleViewType.NONE, false, true),

    TREESAVE("保存选择对象", "saveUrl", "treeSave", new ComponentType[]{ComponentType.TREEBAR, ComponentType.TREEVIEW}, CustomFormMenu.SAVE, ModuleViewType.NONE, false, true),

    GRIDSAVE("保存选中对象", "saveUrl", "gridSave", new ComponentType[]{ComponentType.TREEGRID, ComponentType.GALLERY}, CustomFormMenu.SAVE, ModuleViewType.NONE, false, true),

    GRIDRELOAD("刷新列表", "dataUrl", "gridReload", new ComponentType[]{ComponentType.TREEGRID}, TreeGridMenu.RELOAD, ModuleViewType.GRIDCONFIG, true),

    GALLERYRELOAD("刷新视图", "dataUrl", "galleryReload", new ComponentType[]{ComponentType.GALLERY}, TreeGridMenu.RELOAD, ModuleViewType.GALLERYCONFIG, true),

    INDEX("首页", "Index", "Index", new ComponentType[]{}, CustomDynMenu.DYNRELOAD, ModuleViewType.DYNCONFIG, false),

    DIC("字典表", "selectItem", "selectItem", new ComponentType[]{ComponentType.TREEBAR, ComponentType.TREEVIEW}, TreeMenu.LOADCHILD, ModuleViewType.TREECONFIG, false),

    INDEXS("首页", "Indexs", "Indexs", new ComponentType[]{}, TreeGridMenu.RELOAD, ModuleViewType.DYNCONFIG, false),

    DELETE("删除", "delPath", "delete", new ComponentType[]{ComponentType.GALLERY, ComponentType.TREEGRID}, TreeGridMenu.DELETE, ModuleViewType.NONE, false);


    private final String type;

    private final String name;

    //前端AJAX对应名称
    private final String methodName;

    private final String defaultMethodName;

    private final ComponentType[] bindTypes;

    private final CustomMenu menu;

    private final ModuleViewType returnView;

    private final Boolean isDefaultView;

    private final Boolean requestBody;

    CustomMenuItem(String name, String methodName, String defaultMethodName, ComponentType[] bindTypes, CustomMenu menu, ModuleViewType returnView, Boolean isDefaultView, Boolean requestBody) {
        this.methodName = methodName;
        this.type = name();
        this.name = name;
        this.defaultMethodName = defaultMethodName;
        this.menu = menu;
        this.bindTypes = bindTypes;
        this.returnView = returnView;
        this.isDefaultView = isDefaultView;
        this.requestBody = requestBody;

    }


    CustomMenuItem(String name, String methodName, String defaultMethodName, ComponentType[] bindTypes, CustomMenu menu, ModuleViewType returnView, Boolean isDefaultView) {
        this.methodName = methodName;
        this.type = name();
        this.name = name;
        this.defaultMethodName = defaultMethodName;
        this.menu = menu;
        this.bindTypes = bindTypes;
        this.returnView = returnView;
        this.isDefaultView = isDefaultView;
        this.requestBody = false;
    }


    public static CustomMenuItem[] getAllViewTypes() {
        Set<CustomMenuItem> allCustomMenuItems = new HashSet<>();
        CustomMenuItem[] types = CustomMenuItem.values();
        for (CustomMenuItem type : types) {
            if (!type.returnView.equals(ModuleViewType.NONE)) {
                allCustomMenuItems.add(type);
            }
        }
        return allCustomMenuItems.toArray(new CustomMenuItem[]{});
    }


    public static CustomMenuItem[] getDefaultViewTypes() {
        Set<CustomMenuItem> allCustomMenuItems = new HashSet<>();
        CustomMenuItem[] types = CustomMenuItem.values();

        for (CustomMenuItem type : types) {
            if (type.isDefaultView) {
                allCustomMenuItems.add(type);
            }
        }
        return allCustomMenuItems.toArray(new CustomMenuItem[]{});
    }


    public static ComponentType[] getAllTypes() {
        Set<ComponentType> allTypes = new HashSet<>();
        CustomMenuItem[] types = CustomMenuItem.values();
        for (CustomMenuItem type : types) {
            allTypes.addAll(Arrays.asList(type.getBindTypes()));
        }
        return allTypes.toArray(new ComponentType[]{});
    }


    public static CustomMenuItem[] getAllCustomItems(ComponentType componentType) {
        Set<CustomMenuItem> allTypes = new HashSet<>();
        CustomMenuItem[] types = CustomMenuItem.values();
        for (CustomMenuItem type : types) {
            Set<ComponentType> componentTypes = new HashSet<>();
            componentTypes.addAll(Arrays.asList(type.getBindTypes()));
            if (componentTypes.contains(componentType)) {
                allTypes.add(type);
            }
        }
        return allTypes.toArray(new CustomMenuItem[]{});
    }

    public Boolean getDefaultView() {
        return isDefaultView;
    }

    public Boolean getRequestBody() {
        return requestBody;
    }


    public ModuleViewType getReturnView() {
        return returnView;
    }

    public String getDefaultMethodName() {
        return defaultMethodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public CustomMenu getMenu() {
        return menu;
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
        return menu.imageClass();
    }

    public ComponentType[] getBindTypes() {
        return bindTypes;
    }
}
