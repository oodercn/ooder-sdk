package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum InputType implements Enumstype {
    text("文本框"), password("密码框");

    private final String name;


    InputType(String name) {
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
