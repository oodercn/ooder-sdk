package net.ooder.annotation.menu;


import net.ooder.annotation.IconEnumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomMenu;
import net.ooder.annotation.action.CustomTreeGridAction;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.*;

import java.lang.annotation.Annotation;

public enum GroupCmdMenu implements CustomMenu, IconEnumstype {
    Reload("刷新", "ri-refresh-line", "true", ComboInputType.button, IconColorEnum.BABYBLUE, new CustomAction[]{CustomTreeGridAction.RELOAD});

    private String type;

    private String caption;

    private String imageClass;

    private String expression;

    IconColorEnum iconColor;

    FontColorEnum fontColor;

    ItemColorEnum itemColor;

    TagCmdsAlign tagCmdsAlign = TagCmdsAlign.floatright;

    public ComboInputType itemType = ComboInputType.button;

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


    GroupCmdMenu(CustomMenu menu) {
        this.type = menu.type();
        this.itemType = menu.itemType();
        this.caption = menu.caption();
        this.imageClass = menu.imageClass();
        this.expression = menu.expression();
        this.actions = menu.actions();
        this.tagCmdsAlign = menu.tagCmdsAlign();
        this.iconColor = menu.iconColor();
        this.itemColor = menu.itemColor();
        this.fontColor = menu.fontColor();
    }


    GroupCmdMenu(String caption, String imageClass, String expression, ComboInputType itemType, IconColorEnum iconColor, CustomAction[] actions) {
        this.type = name();
        this.caption = caption;

        this.imageClass = imageClass;
        this.expression = expression;
        this.actions = actions;
        this.itemType = itemType;
        this.iconColor = iconColor;

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
    public String toString() {
        return type;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CustomMenu.class;
    }

    public static TreeRowMenu fromType(String typeName) {
        for (TreeRowMenu type : TreeRowMenu.values()) {
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
    public String type() {
        return type;
    }

    @Override
    public String caption() {
        return caption;
    }

    @Override
    public ComboInputType itemType() {
        return itemType;
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

    public ComboInputType getItemType() {
        return itemType;
    }

    public void setItemType(ComboInputType itemType) {
        this.itemType = itemType;
    }


    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public TagCmdsAlign tagCmdsAlign() {
        return tagCmdsAlign;
    }


}