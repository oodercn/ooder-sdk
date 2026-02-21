package net.ooder.annotation.menu;

import net.ooder.annotation.IconEnumstype;
import net.ooder.annotation.*;
import net.ooder.annotation.action.CustomTreeAction;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomMenu;
import net.ooder.annotation.ui.*;

import java.lang.annotation.Annotation;

public enum ContextMenu implements CustomMenu, IconEnumstype {

    Msg("消息", "ri-chat-3-line", "true", "left", ComboInputType.text, IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.MSG}),
    Editor("编辑", "ri-edit-line", "true", "left", ComboInputType.text, IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.EDITOR}),
    Config("配置", "ri-settings-line", "true", "left", ComboInputType.text, IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.CONFIG}),
    SortDown("向上", "ri-arrow-up-line", "true", "left", ComboInputType.text, IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.SORTUP}),
    SortUP("向下", "ri-arrow-down-line", "true", "left", ComboInputType.text, IconColorEnum.CYAN, new CustomAction[]{CustomTreeAction.SORTDOWN}),
    Delete("删除", "ri-close-line", "true", "left", ComboInputType.text, IconColorEnum.YELLOW, new CustomAction[]{CustomTreeAction.DELETE}),
    saveRow("保存", "ri-save-line", "true", "left", ComboInputType.text, IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.SAVEROW}),
    LoadMenu("菜单", "ri-menu-line", "true", "left", ComboInputType.text, IconColorEnum.NONE, new CustomAction[]{CustomTreeAction.LOADMENU});

    private String tag;

    private String type;

    private String caption;

    private String imageClass;

    private String expression;


    IconColorEnum iconColor;

    FontColorEnum fontColor;

    ItemColorEnum itemColor;


    TagCmdsAlign tagCmdsAlign = TagCmdsAlign.floatright;

    public ComboInputType itemType = ComboInputType.text;

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


    ContextMenu(CustomMenu menu) {
        this.type = menu.type();
        this.itemType = menu.itemType();
        this.tag = menu.type();
        this.caption = menu.caption();
        this.imageClass = menu.imageClass();
        this.expression = menu.expression();
        this.actions = menu.actions();
        this.iconColor = menu.iconColor();
        this.itemColor = menu.itemColor();
        this.fontColor = menu.fontColor();
    }


    ContextMenu(String caption, String imageClass, String expression, String tag, ComboInputType itemType, IconColorEnum iconColor, CustomAction[] actions) {
        this.type = name();
        this.caption = caption;
        this.iconColor = iconColor;
        this.imageClass = imageClass;
        this.expression = expression;
        this.actions = actions;
        this.itemType = itemType;
        this.tag = tag;


    }


    @Override
    public String toString() {
        return type;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CustomMenu.class;
    }

    public static ContextMenu fromType(String typeName) {
        for (ContextMenu type : ContextMenu.values()) {
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

}