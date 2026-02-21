package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum RepeatType implements Enumstype {
    none(""), repeat("repeat"), repeatX("repeat-x"), repeatY("repeat-y"), norepeat("no-repeat");

    private final String name;


    RepeatType(String name) {
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
