package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum OverflowType implements Enumstype {
    visible("visible"),
    hidden("hidden"),
    scroll("scroll"),
    auto( "auto"),
    overflowX("overflow-x:hidden;overflow-y:auto"),
    overflowY("overflow-x:auto;overflow-y:hidden");

    private final String name;


    OverflowType(String name) {
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
