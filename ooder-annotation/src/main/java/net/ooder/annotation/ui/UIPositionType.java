package net.ooder.annotation.ui;

import net.ooder.annotation.Enumstype;

public enum UIPositionType implements Enumstype {
    STATIC("static"),
    RELATIVE("relative"),
    ABSOLUTE("absolute");

    private final String name;


    UIPositionType(String name) {
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
