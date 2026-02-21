package net.ooder.annotation.menu;

import net.ooder.annotation.IconEnumstype;
import net.ooder.annotation.*;
import net.ooder.annotation.action.CustomPageAction;
import net.ooder.annotation.action.CustomTreeAction;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomMenu;
import net.ooder.annotation.ui.*;

import java.lang.annotation.Annotation;

public enum TreeMenu implements CustomMenu, IconEnumstype {

    SAVEROW("保存", CustomImageType.save.getImageClass(), "true", IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.SAVEROW}),

    SORTDOWN("向下", CustomImageType.down.getImageClass(), "true", IconColorEnum.CYAN, new CustomAction[]{CustomTreeAction.SORTDOWN}),

    SORTUP("向上", CustomImageType.up.getImageClass(), "true", IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.SORTUP}),

    RESET("重置", CustomImageType.resetpath.getImageClass(), "true", IconColorEnum.YELLOW, new CustomAction[]{CustomTreeAction.RESET}),

    SAVE("确定", CustomImageType.save.getImageClass(), "true", IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.SAVE}),

    DELETE("删除", CustomImageType.delete.getImageClass(), "true", IconColorEnum.PINK, new CustomAction[]{CustomTreeAction.DELETE, CustomPageAction.RELOAD}),

    ADD("添加", CustomImageType.add.getImageClass(), "true", IconColorEnum.GREEN, new CustomAction[]{CustomPageAction.ADD}),

    LOADCHILD("刷新", CustomImageType.refresh.getImageClass(), "true", IconColorEnum.CYAN, new CustomAction[]{CustomTreeAction.RELOADCHILD}),

    CLOSE("关闭", "ri-close-line", "true", IconColorEnum.CYAN, new CustomAction[]{CustomPageAction.CLOSE}),

    RELOAD("刷新", CustomImageType.refresh.getImageClass(), "true", IconColorEnum.CYAN, new CustomAction[]{CustomTreeAction.RELOAD});

    TagCmdsAlign tagCmdsAlign = TagCmdsAlign.floatright;

    private String type;

    private String caption;


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


    TreeMenu(CustomMenu menu) {
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


    TreeMenu(String caption, String imageClass, String expression, IconColorEnum iconColor, CustomAction[] actions) {
        this.type = name();
        this.caption = caption;

        this.imageClass = imageClass;
        this.expression = expression;
        this.actions = actions;
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

    public static TreeMenu fromType(String typeName) {
        for (TreeMenu type : TreeMenu.values()) {
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

    public IconColorEnum getIconColor() {
        return iconColor;
    }

    public void setIconColor(IconColorEnum iconColor) {
        this.iconColor = iconColor;
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