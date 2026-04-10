package net.ooder.annotation.event;



import net.ooder.annotation.Enumstype;
import net.ooder.annotation.IconEnumstype;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomImageType;

public enum CustomEventEnum implements IconEnumstype {
    FormEvent("表单事件", CustomFormEvent.class, ComponentType.getFormComponents()),
    TreeEvent("树形节点事件", CustomTreeEvent.class, new ComponentType[]{ComponentType.TREEBAR, ComponentType.TREEVIEW}),
    TabsEvent("Tab页事件", CustomTabsEvent.class, ComponentType.getTabsComponents()),
    TitleBlockEvent("磁铁布局", CustomTitleBlockEvent.class, new ComponentType[]{ComponentType.CONTENTBLOCK, ComponentType.TITLEBLOCK}),
    FieldEvent("字段事件", CustomFieldEvent.class),
    HotKeyEvent("键盘热键", CustomHotKeyEvent.class),
    GalleryEvent("画廊视图", CustomGalleryEvent.class, ComponentType.GALLERY),
    TreeGridEvent("列表事件", CustomTreeGridEvent.class, ComponentType.TREEGRID);


    private final String type;

    private final String name;

    private final String imageClass;

    Class<? extends Enumstype> eventClass;

    private final ComponentType[] bindTypes;


    CustomEventEnum(String name, Class eventClass, ComponentType... bindTypes) {
        this.type = name();
        this.name = name;
        if (bindTypes.length > 0) {
            this.imageClass = bindTypes[0].getImageClass();
        } else {
            this.imageClass = CustomImageType.event.getImageClass();
        }


        this.eventClass = eventClass;
        this.bindTypes = bindTypes;
    }

    public static CustomEventEnum getCustomEventEnum(Class eventClass) {
        if (eventClass != null) {
            for (CustomEventEnum eventEnum : CustomEventEnum.values()) {
                if (eventClass.equals(eventEnum.eventClass)) {
                    return eventEnum;
                }
            }
        }
        return null;

    }


    @Override
    public String getImageClass() {
        return imageClass;
    }

    public Class getEventClass() {
        return eventClass;
    }


    public void setEventClass(Class<? extends Enumstype> eventClass) {
        this.eventClass = eventClass;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public ComponentType[] getBindTypes() {
        return bindTypes;
    }
}
