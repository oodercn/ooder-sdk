package net.ooder.annotation.event;


import net.ooder.annotation.IconEnumstype;
import net.ooder.annotation.*;
import net.ooder.annotation.action.CustomDynModuleAction;
import net.ooder.annotation.action.CustomPageAction;
import net.ooder.annotation.action.CustomTreeGridAction;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.menu.TreeGridMenu;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomMenu;
import net.ooder.annotation.ui.*;

import java.lang.annotation.Annotation;

public enum CustomDynMenu implements CustomMenu, IconEnumstype {
    // 将所有"fa"前缀改为"fas"，并更新特定图标名称
    DELETE("Delete", "删除", "delPath", "ri-close-line", "true", IconColorEnum.YELLOW, new CustomAction[]{CustomTreeGridAction.DELETE, CustomPageAction.RELOAD}),
    DUMP("Save", "镜像", "saveUrl", "ri-add-line", "true", IconColorEnum.DARKBLUE, new CustomAction[]{CustomDynModuleAction.DUMP}),
    SAVE("Save", "保存", "saveUrl", "ri-calendar-event-line", "true", IconColorEnum.DARKBLUE, new CustomAction[]{CustomDynModuleAction.SAVE}),
    DYNRELOAD("DynReload", "刷新", "dataPath", "ri-refresh-line", "true", IconColorEnum.BABYBLUE, new CustomAction[]{CustomDynModuleAction.DYNRELOAD});

    TagCmdsAlign tagCmdsAlign = TagCmdsAlign.floatright;

    private String type;

    private String caption;

    private String methodName;

    private String imageClass;

    private String expression;

    IconColorEnum iconColor;

    FontColorEnum fontColor;

    ItemColorEnum itemColor;

    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return caption;
    }

    public String getCaption() {
        return caption;
    }

    public CustomAction[] actions;


    CustomDynMenu(CustomMenu menu) {
        this.type = menu.type();
        this.caption = menu.caption();
        this.imageClass = menu.imageClass();
        this.expression = menu.expression();
        this.actions = menu.actions();
        this.tagCmdsAlign = menu.tagCmdsAlign();
        this.iconColor = menu.iconColor();
        this.itemColor = menu.itemColor();
        this.fontColor = menu.fontColor();
    }


    CustomDynMenu(String type, String caption, String methodName, String imageClass, String expression,IconColorEnum iconColor, CustomAction[] actions) {
        this.type = type;
        this.caption = caption;
        this.methodName = methodName;
        this.imageClass = imageClass;
        this.expression = expression;
        this.actions = actions;
        this.iconColor=iconColor;


    }

    @Override
    public TagCmdsAlign tagCmdsAlign() {
        return tagCmdsAlign;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return type;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CustomMenu.class;
    }

    public static TreeGridMenu fromType(String typeName) {
        for (TreeGridMenu type : TreeGridMenu.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }


    public void setActions(CustomAction[] actions) {
        this.actions = actions;
    }
    @Override
    public IconColorEnum iconColor() {
        return iconColor;
    }

    @Override
    public ItemColorEnum itemColor() {
        return itemColor;
    }

    @Override
    public FontColorEnum fontColor() {
        return fontColor;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }


    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String type() {
        return type;
    }

    @Override
    public String caption() {
        return caption;
    }

    @Override
    public ComboInputType itemType() {
        return ComboInputType.button;
    }


    @Override
    public String imageClass() {
        return imageClass;
    }

    @Override
    public String expression() {
        return expression;
    }

    @Override
    public CustomAction[] actions() {
        return actions;
    }

    public String getImageClass() {
        return imageClass;
    }

    public String getExpression() {
        return expression;
    }

    public CustomAction[] getActions() {
        return actions;
    }
}