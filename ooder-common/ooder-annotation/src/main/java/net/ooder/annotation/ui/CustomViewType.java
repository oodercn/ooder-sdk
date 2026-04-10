package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum CustomViewType implements Enumstype {

    FRAME("框架"), 
    NAV("导航"),
    MODULE("模块"), 
     LISTMODULE("模块"), 
     INNERMODULE("内部模块"), 
     COMPONENT("组件"), 
     COMBOBOX("复合组件");

    private final String name;


    public static CustomViewType[] getCustomModule() {
        return new CustomViewType[]{
                FRAME, NAV, MODULE, LISTMODULE
        };
    }

    public static CustomViewType[] getCustomComponent() {
        return new CustomViewType[]{
                INNERMODULE, COMPONENT, COMBOBOX
        };
    }


    CustomViewType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }

}
