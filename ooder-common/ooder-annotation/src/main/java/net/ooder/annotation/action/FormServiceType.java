package net.ooder.annotation.action;

import net.ooder.annotation.IconEnumstype;

public enum FormServiceType implements IconEnumstype {
    CustomService("绑定服务", "CustomService", "ri-settings-3-line"),
    CustomMenuService("绑定菜单", "CustomMenuService", "ri-menu-line"),
    CustomBottombarService("底部工具栏", "CustomBottombarService", "ri-tools-line");

    private final String imageClass;
    private String name;

    private String methodName;

    private String type;


    FormServiceType(String name, String methodName, String imageClass) {

        this.name = name;
        this.methodName = methodName;
        this.type = name();
        this.imageClass = imageClass;

    }


    public String getMethodName() {
        return methodName;
    }


    @Override
    public String getType() {
        return name();
    }

    public String getName() {
        return name;
    }

    @Override
    public String getImageClass() {
        return imageClass;
    }

}