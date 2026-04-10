package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum HoverPopType implements Enumstype {
    outer("outer"),
    inner("inner"),
    outerleftoutertop("outerleft-outertop"),
    leftoutertop("left-outertop"),
    centeroutertop("center-outertop"),
    rightoutertop("right-outertop"),
    outerrightoutertop("outerright-outertop"),
    outerlefttop("outerleft-top"),
    lefttop("left-top"),
    centertop("center-top"),
    righttop("right-top"),
    outerrighttop("outerright-top"),
    outerleftmiddle("outerleft-middle"),
    leftmiddle("left-middle"),
    centermiddle("center-middle"),
    rightmiddle("right-middle"),
    outerrightmiddle("outerright-middle"),
    outerleftbottom("outerleft-bottom"),
    leftbottom("left-bottom"),
    centerbottom("center-bottom"),
    rightbottom("right-bottom"),
    outerrightbottom("outerright-bottom"),
    outerleftouterbottom("outerleft-outerbottom"),
    leftouterbottom("left-outerbottom"),
    centerouterbottom("center-outerbottom"),
    rightouterbottom("right-outerbottom"),
    outerrightouterbottom("outerright-outerbottom");

    private final String name;




    HoverPopType(String name) {
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
