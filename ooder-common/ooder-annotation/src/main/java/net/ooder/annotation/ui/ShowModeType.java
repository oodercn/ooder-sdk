package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum ShowModeType implements Enumstype {


    normal("normal"), compact("compact"), transparent("transparent"), none("不可选");

    private final String name;


    ShowModeType(String name) {
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

