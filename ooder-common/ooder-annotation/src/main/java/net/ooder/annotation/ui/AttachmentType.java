package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum AttachmentType implements Enumstype {
    none(""), scroll("scroll"), fixed("fixed");
    private final String name;





    AttachmentType(String name) {
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
