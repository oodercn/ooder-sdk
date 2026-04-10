package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum FormLayModeType implements Enumstype {
    design("设计模式"), read("只读模式"), write("可读写"), none("默认");

    String name;

    FormLayModeType(String name) {
        this.name = name;
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
