package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum BarLocationType implements Enumstype {
    top("顶部对齐"), bottom("底部对齐"), left("左对齐"), right("右对齐");

    private final String name;


    BarLocationType(String name) {
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
