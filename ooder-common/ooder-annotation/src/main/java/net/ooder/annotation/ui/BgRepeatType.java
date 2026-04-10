package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum BgRepeatType implements Enumstype {

    none(""),
    topleft("top left"),
    topcenter("top center"),
    topright("top right"),
    centerleft("center left"),
    centercenter("center center"),
    centerright("center right"),
    bottomleft("bottom left"),
    bottomcenter("bottom center"),
    bottomright("bottom right"),
    zero("0% 0%"),
    px("-0px -0px");

    private final String name;


    BgRepeatType(String name) {
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
