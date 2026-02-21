package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum SelModeType implements Enumstype {
    single("单选"), multi("多选"), multibycheckbox("多选CheckBox"), singlecheckbox("单选CheckBox"), none("不可选");

    private final String name;


    SelModeType(String name) {
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

