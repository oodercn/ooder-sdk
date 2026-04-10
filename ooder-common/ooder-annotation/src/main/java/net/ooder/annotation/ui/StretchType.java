package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum StretchType implements Enumstype {
    none("默认"),last("延申最后一行"), all("平均分配");

    private final String name;


    StretchType(String name) {
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
