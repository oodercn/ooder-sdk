package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum DockStretchType implements Enumstype {
    fixed("fixed"),
    forward("forward"),
    rearward("rearward"),
    stretch("stretch"),
    onefourth("0.25"),
    onethird("0.33"), ahalf("0.5");

    private final String name;


    DockStretchType(String name) {
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
