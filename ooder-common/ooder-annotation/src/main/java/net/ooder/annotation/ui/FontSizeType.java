package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum FontSizeType implements Enumstype {

    none(""),
    normal("normal"),
    italic("italic"),
    oblique("oblique");
    private final String name;


    FontSizeType(String name) {
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
