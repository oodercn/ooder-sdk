package net.ooder.annotation.menu;

import net.ooder.annotation.IconEnumstype;
import net.ooder.annotation.*;
import net.ooder.annotation.action.CustomTreeGridAction;
import net.ooder.annotation.action.CustomPageAction;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomMenu;
import net.ooder.annotation.ui.*;

import java.lang.annotation.Annotation;

public enum TreeGridRowMenu implements CustomMenu, IconEnumstype {

    DELETE("删除", "ri-close-line", "true", TagCmdsAlign.floatright, ComboInputType.button, IconColorEnum.YELLOW, new CustomAction[]{CustomTreeGridAction.DELETE}),

    SORTDOWN("向上", "ri-arrow-up-line", "true", TagCmdsAlign.left, ComboInputType.button, IconColorEnum.CYAN, new CustomAction[]{CustomTreeGridAction.SORTUP}),

    SORTUP("向下", "ri-arrow-down-line", "true", TagCmdsAlign.left, ComboInputType.button, IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeGridAction.SORTDOWN}),

    EDITOR("编辑", "ri-edit-line", "true", TagCmdsAlign.left, ComboInputType.button, IconColorEnum.GREEN, new CustomAction[]{CustomPageAction.EDITOR}),

    SAVEALLROW("保存全部", "ri-save-line", "true", TagCmdsAlign.floatright, ComboInputType.button, IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeGridAction.SAVEALLROW}),

    SAVEROW("保存", "ri-save-line", "true", TagCmdsAlign.floatright, ComboInputType.button, IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeGridAction.SAVEROW});

    private TagCmdsAlign tagCmdsAlign;

    private String type;

    private String caption;

    private String imageClass;

    private String expression;


    IconColorEnum iconColor;

    FontColorEnum fontColor;

    ItemColorEnum itemColor;


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


    TreeGridRowMenu(CustomMenu menu) {
        this.type = menu.type();
        this.itemType = menu.itemType();
        this.tagCmdsAlign = menu.tagCmdsAlign();
        this.caption = menu.caption();
        this.imageClass = menu.imageClass();
        this.expression = menu.expression();
        this.actions = menu.actions();
        this.iconColor = menu.iconColor();
        this.itemColor = menu.itemColor();
        this.fontColor = menu.fontColor();
    }


    TreeGridRowMenu(String caption, String imageClass, String expression, TagCmdsAlign tagCmdsAlign, ComboInputType itemType, IconColorEnum iconColor, CustomAction[] actions) {
        this.type = name();
        this.caption = caption;
        this.imageClass = imageClass;
        this.expression = expression;
        this.actions = actions;
        this.itemType = itemType;
        this.tagCmdsAlign = tagCmdsAlign;
        this.iconColor = iconColor;


    }


    @Override
    public String toString() {
        return type;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CustomMenu.class;
    }

    public static TreeGridRowMenu fromType(String typeName) {
        for (TreeGridRowMenu type : TreeGridRowMenu.values()) {
            if (type.getType().equals(typeName.toUpperCase())) {
                return type;
            }
        }
        return null;
    }

    public IconColorEnum getIconColor() {
        return iconColor;
    }

    public void setIconColor(IconColorEnum iconColor) {
        this.iconColor = iconColor;
    }

    public FontColorEnum getFontColor() {
        return fontColor;
    }

    public void setFontColor(FontColorEnum fontColor) {
        this.fontColor = fontColor;
    }

    public ItemColorEnum getItemColor() {
        return itemColor;
    }

    public void setItemColor(ItemColorEnum itemColor) {
        this.itemColor = itemColor;
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
    public TagCmdsAlign tagCmdsAlign() {
        return tagCmdsAlign;
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

    public TagCmdsAlign getTagCmdsAlign() {
        return tagCmdsAlign;
    }

    public void setTagCmdsAlign(TagCmdsAlign tagCmdsAlign) {
        this.tagCmdsAlign = tagCmdsAlign;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

}