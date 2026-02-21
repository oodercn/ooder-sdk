package net.ooder.annotation.menu;


import net.ooder.annotation.IconEnumstype;
import net.ooder.annotation.*;
import net.ooder.annotation.action.CustomFormAction;
import net.ooder.annotation.action.CustomPageAction;
import net.ooder.annotation.action.CustomTreeGridAction;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomMenu;
import net.ooder.annotation.ui.*;

import java.lang.annotation.Annotation;

public enum ContentBlockMenu implements CustomMenu, IconEnumstype {

    DELETE("删除", "delPath", "ri-close-line", "true",IconColorEnum.YELLOW, new CustomAction[]{CustomTreeGridAction.DELETE, CustomPageAction.RELOAD}),

    ADD("添加", "addPath", "ri-add-line", "true",IconColorEnum.GREEN, new CustomAction[]{CustomPageAction.ADD}),

    SORT("排序", "sortUrl", "ri-sort-asc", "true",IconColorEnum.DARKBLUE, new CustomAction[]{CustomFormAction.SAVE}),

    RELOAD("刷新", "dataPath", "ri-refresh-line", "true",IconColorEnum.BABYBLUE, new CustomAction[]{CustomTreeGridAction.RELOAD});


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


    ContentBlockMenu(CustomMenu menu) {
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


    ContentBlockMenu(String caption, String methodName, String imageClass, String expression,IconColorEnum iconColor, CustomAction[] actions) {
        this.type = name();
        this.caption = caption;
        this.methodName = methodName;
        this.imageClass = imageClass;
        this.expression = expression;
        this.actions = actions;
        this.iconColor=iconColor;

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

    @Override
    public String type() {
        return type;
    }

    @Override
    public String caption() {
        return caption;
    }

    @Override
    public TagCmdsAlign tagCmdsAlign() {
        return tagCmdsAlign;
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