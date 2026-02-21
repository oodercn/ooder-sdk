package net.ooder.annotation.ui;


import net.ooder.annotation.CallBackPathAnnotation;

import java.lang.annotation.Annotation;

public enum CallBackPathEnum implements UrlPath<CallBackTypeEnum>, CallBackPathAnnotation {

    log(CallBackTypeEnum.log, "PAGECTX", ""),
    alert(CallBackTypeEnum.alert, "PAGECTX", "");


    CallBackPathEnum(CallBackTypeEnum type, String pathname, String path) {
        this.type = type;
        this.pathname = pathname;
        this.path = path;
    }

    CallBackTypeEnum type;
    String pathname;
    String path;

    @Override
    public CallBackTypeEnum getType() {
        return type;
    }

    @Override
    public void setType(CallBackTypeEnum type) {

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
    public CallBackTypeEnum type() {
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
        return CallBackPathAnnotation.class;
    }
}
