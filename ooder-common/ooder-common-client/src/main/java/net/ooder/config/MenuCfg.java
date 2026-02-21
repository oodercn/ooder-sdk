/**
 * $RCSfile: MenuCfg.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.config;

import java.util.List;

public class MenuCfg {

    public final static String JCheckBoxMenuItem = "CheckBoxMenu";
    public final static String JRadioButtonMenuItem = "RadioButtonMenu";
    public final static String MenuItem = "MenuItem";
    public final static String Separator = "Separator";


    String id;
    String text;
    String rightMenuUrl;
    String selectedScript;
    String iconCls;
    String script = "";
    String icon = "";
    boolean disabled = false;
    String type = MenuItem;
    String handler;
    String State = "";
    String url = "";

    List<MenuCfg> menu;

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MenuCfg> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuCfg> menu) {
        this.menu = menu;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {


        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRightMenuUrl() {
        return rightMenuUrl;
    }

    public void setRightMenuUrl(String rightMenuUrl) {
        this.rightMenuUrl = rightMenuUrl;
    }

    public String getSelectedScript() {
        return selectedScript;
    }

    public void setSelectedScript(String selectedScript) {
        this.selectedScript = selectedScript;
    }

    public String toString() {
        return this.text;
    }

}
