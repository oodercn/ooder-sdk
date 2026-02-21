package net.ooder.annotation.ui;


import net.ooder.annotation.ResponsePathAnnotation;

import java.lang.annotation.Annotation;

public enum ResponsePathEnum implements UrlPath<ResponsePathTypeEnum>, ResponsePathAnnotation {

    CTX(ResponsePathTypeEnum.CTX, "PAGECTX", "ctx"),
    EXPRESSION(ResponsePathTypeEnum.EXPRESSION, "PAGECTX", "data"),
    SPACOMPONENT(ResponsePathTypeEnum.SPA, "PAGECTX", "data"),
    POPMENU(ResponsePathTypeEnum.POPMENU, "PAGECTX", "data"),
    LIST(ResponsePathTypeEnum.LIST, "PAGECTX", "data"),
    SVGCOMPONENT(ResponsePathTypeEnum.SVGPAPER, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", "data"),
    BLOCKCOMPONENT(ResponsePathTypeEnum.COMPONENT, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", "data"),
    TREEGRID(ResponsePathTypeEnum.TREEGRID, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", "data"),
    TITLEBLOCK(ResponsePathTypeEnum.TITLEBLOCK, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", "data"),
    OPINION(ResponsePathTypeEnum.OPINION, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", "data"),
    GRIDNEXT(ResponsePathTypeEnum.GRIDNEXT, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", "data"),
    FORM(ResponsePathTypeEnum.FORM, "@{(this.moduleComponent!=null && this.moduleComponent.topComponentBox!=null)? this.moduleComponent.topComponentBox.alias}", "data"),
    TREEVIEW(ResponsePathTypeEnum.TREEVIEW, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", "data"),
    GALLERY(ResponsePathTypeEnum.GALLERY, "@{this.currComponent!=null?this.currComponent.alias：(this.moduleComponent!=null?this.moduleComponent.alias)}", "data"),
    PAGEBAR(ResponsePathTypeEnum.PAGEBAR, "@{(this.moduleComponent!=null&& this.moduleComponent.pageBarComponent!=null)?this.moduleComponent.pageBarComponent.alias：(this.moduleComponent!=null?this.moduleComponent.alias)}", "size");


    ResponsePathEnum(ResponsePathTypeEnum type, String pathname, String path) {
        this.type = type;
        this.pathname = pathname;
        this.path = path;
    }

    ResponsePathTypeEnum type;
    String pathname;
    String path;

    @Override
    public ResponsePathTypeEnum getType() {
        return type;
    }

    @Override
    public void setType(ResponsePathTypeEnum type) {

        this.type = type;
    }

    @Override
    public String getName() {
        return pathname;
    }

    @Override
    public void setName(String pathname) {
        this.pathname = pathname;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {

        this.path = path;
    }

    @Override
    public ResponsePathTypeEnum type() {
        return type;
    }

    @Override
    public String paramsname() {
        return pathname;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return ResponsePathAnnotation.class;
    }
}
