package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum SideBarType implements Enumstype {
    none("none"),
    left("left"),
    right("right"),
    top("top"),
    bottom("bottom"),
    lefttop("left-top"),
    leftbottom("left-bottom"),
    righttop("right-top"),
    rightbottom("right-bottom"),
    topleft("top-left"),
    topright("top-right"),
    bottomleft("bottom-left"),
    bottomright("bottom-right");

    private final String name;


    SideBarType(String name) {
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
