package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum FontStyleType implements Enumstype {

    none(""),
    px12("12px"),
    px14("14px"),
    px16("16px"),
    px20("13px"),
    px28("28px");
    private final String name;


    FontStyleType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
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
