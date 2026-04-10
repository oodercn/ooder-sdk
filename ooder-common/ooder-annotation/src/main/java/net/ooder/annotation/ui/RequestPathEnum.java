package net.ooder.annotation.ui;


import net.ooder.annotation.RequestPathAnnotation;

import java.lang.annotation.Annotation;

public enum RequestPathEnum implements UrlPath<RequestPathTypeEnum>, RequestPathAnnotation {

    CTX(RequestPathTypeEnum.FORM, "PAGECTX", ""),

    STAGVAR(RequestPathTypeEnum.STAGVAR, "STAGVAR", ""),

    CURRFORM(RequestPathTypeEnum.FORM, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    TOPFORM(RequestPathTypeEnum.FORM, "@{(this.topCurrComponent!=null && this.moduleComponent.topComponentBox!=null)?this.moduleComponent.topComponentBox.alias}", ""),

    TREEGRID(RequestPathTypeEnum.TREEGRID, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    TREEVIEW(RequestPathTypeEnum.TREEVIEW, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    TREEGRIDROW(RequestPathTypeEnum.TREEGRIDROW, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    TREEGRIDROWVALUE(RequestPathTypeEnum.TREEGRIDROWVALUE, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    TREEGRIDALLVALUE(RequestPathTypeEnum.TREEGRIDALLVALUE, "@{this.currComponent!=null?this.currComponent.alias:(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    GALLERY(RequestPathTypeEnum.GALLERY, "@{this.currComponent!=null?this.currComponent.alias：(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    TITLEBLOCK(RequestPathTypeEnum.TITLEBLOCK, "@{this.currComponent!=null?this.currComponent.alias：(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    OPINION(RequestPathTypeEnum.OPINION, "@{this.currComponent!=null?this.currComponent.alias：(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    PAGEBAR(RequestPathTypeEnum.PAGEBAR, "@{(this.moduleComponent!=null && this.moduleComponent.pageBarComponent!=null)?this.moduleComponent.pageBarComponent.alias：(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    PAGENEXT(RequestPathTypeEnum.PAGENEXT, "@{(this.moduleComponent!=null && this.moduleComponent.pageBarComponent!=null)?this.moduleComponent.pageBarComponent.alias：(this.moduleComponent!=null?this.moduleComponent.alias)}", ""),

    SPA_PROJECTNAME(RequestPathTypeEnum.SPA, "curProjectName", "projectName"),

    SPA_CLASSNAME(RequestPathTypeEnum.SPA, "currentClassName", "currentClassName"),

    RAD_SELECTITEMS(RequestPathTypeEnum.RAD, "select", "selectItems"),

    RAD_JSON(RequestPathTypeEnum.RAD, "json", "json");


    RequestPathEnum(RequestPathTypeEnum type, String pathname, String path) {
        this.type = type;
        this.pathname = pathname;
        this.path = path;
    }

    RequestPathTypeEnum type;
    String pathname;
    String path;

    @Override
    public RequestPathTypeEnum getType() {
        return type;
    }

    @Override
    public void setType(RequestPathTypeEnum type) {

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
    public RequestPathTypeEnum type() {
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
        return RequestPathAnnotation.class;
    }
}
