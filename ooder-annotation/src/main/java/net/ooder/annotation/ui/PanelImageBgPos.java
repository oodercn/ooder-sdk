package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum PanelImageBgPos implements Enumstype {
    toplerf("top left"), topcenter("top center"), topright("top right"), centerleft("center left"),
    centerright("center right"), bottomleft("bottom left"), bottomcenter("bottom center"),
    bottomright("bottom right"), auto("0% 0%"), pxauto("-0px -0px");
    private final String name;


    PanelImageBgPos(String name) {
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
