package net.ooder.annotation.ui;

import net.ooder.annotation.Enumstype;

public enum ThemesType implements Enumstype {
    // default("default", "default", "default"),
    none(""),
    ndefault("default"),
    army("army"),
    classic("classic"),
    darkblue("darkblue"),
    electricity("electricity"),
    lightblue("lightblue"),
    moonify("moonify"),
    orange("orange"),
    pink("pink"),
    red("red"),
    vista("vista"),
    webflat("webflat");


    private final String name;


    ThemesType(String name) {
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
