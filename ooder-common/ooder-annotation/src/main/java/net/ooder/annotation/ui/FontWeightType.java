package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum FontWeightType implements Enumstype {

    none(""),
    normal("normal"),
    bolder("bolder"),
    bold("bold"),
    lighter("lighter"),
    n100("100"),
    n200("200"),
    n300("300"),
    n400("400"),
    n500("500"),
    n600("600"),
    n700("700"),
    n800("800"),
    n900("900");

    private final String name;


    FontWeightType(String name) {
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
