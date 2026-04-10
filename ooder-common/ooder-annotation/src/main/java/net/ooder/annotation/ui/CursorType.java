package net.ooder.annotation.ui;

import net.ooder.annotation.Enumstype;

public enum CursorType implements Enumstype {
    // default("default", "default", "default"),
    text("text"),
    pointer("pointer"),
    move("move"),
    wait("wait"),
    eresize("e-resize"),
    neresize("ne-resize"),
    nwresize("nw-resize"),
    nresize("n-resize"),
    seresize("se-resize"),
    swresize("sw-resize"),
    sresize("s-resize"),

    wresize("w-resize"),

    crosshair("crosshair");


    private final String name;


    CursorType(String name) {
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
