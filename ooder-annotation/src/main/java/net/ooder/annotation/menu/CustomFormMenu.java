package net.ooder.annotation.menu;



import net.ooder.annotation.IconEnumstype;
import net.ooder.annotation.*;
import net.ooder.annotation.action.CustomFormAction;
import net.ooder.annotation.action.CustomGlobalAction;
import net.ooder.annotation.action.CustomPageAction;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomMenu;
import net.ooder.annotation.ui.*;

import java.lang.annotation.Annotation;

public enum CustomFormMenu implements CustomMenu, IconEnumstype {


    RELOAD("刷新", "ri-refresh-line", "true",IconColorEnum.BABYBLUE, new CustomAction[]{CustomFormAction.RELOAD}),

    SAVE("保存", "ri-save-line", "true",IconColorEnum.DARKBLUE, new CustomAction[]{CustomFormAction.SAVE}),

    SAVEANACLOSE("保存", "ri-save-line", "true",IconColorEnum.DARKBLUE, new CustomAction[]{CustomFormAction.SAVE, CustomPageAction.CLOSETOP}),

    SUBMITFORM("确定", "ri-check-line", "true",IconColorEnum.GREEN, new CustomAction[]{CustomFormAction.SAVE}),

    CLOSE("关闭", "ri-close-line", "true",IconColorEnum.CYAN, new CustomAction[]{CustomPageAction.CLOSE}),

    CLOSETOP("关闭", "ri-close-line", "true",IconColorEnum.CYAN, new CustomAction[]{CustomPageAction.CLOSETOP}),

    SEARCH("查找", "ri-search-line", "true",IconColorEnum.BABYBLUE, new CustomAction[]{CustomFormAction.SEARCH}),

    RESET("重置", "ri-refresh-line", "true",IconColorEnum.YELLOW, new CustomAction[]{CustomFormAction.RESET}),

    PRINT("打印", "ri-printer-line", "true",IconColorEnum.DARKBLUE, new CustomAction[]{CustomGlobalAction.Print}),;

    TagCmdsAlign tagCmdsAlign = TagCmdsAlign.floatright;

    IconColorEnum iconColor;

    FontColorEnum fontColor;

    ItemColorEnum itemColor;

    private String type;

    private String caption;

    private String imageClass;

    private String expression;

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

    CustomFormMenu(CustomMenu menu) {
        this.type = menu.type();
        this.iconColor = menu.iconColor();
        this.itemColor = menu.itemColor();
        this.fontColor = menu.fontColor();
        this.caption = menu.caption();
        this.imageClass = menu.imageClass();
        this.expression = menu.expression();
        this.actions = menu.actions();
        this.tagCmdsAlign = menu.tagCmdsAlign();
    }


    CustomFormMenu(String caption, String imageClass, String expression, IconColorEnum iconColor, CustomAction[] actions) {
        this.type = this.name();
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
        return null;
    }

    public static CustomFormMenu fromType(String typeName) {
        for (CustomFormMenu type : CustomFormMenu.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }


    public CustomAction[] getActions() {
        return actions;
    }

    public void setActions(CustomAction[] actions) {
        this.actions = actions;
    }


    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getImageClass() {
        return imageClass;
    }

    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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
    public TagCmdsAlign tagCmdsAlign() {
        return tagCmdsAlign;
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

    @Override
    public ComboInputType itemType() {
        return ComboInputType.button;
    }
}