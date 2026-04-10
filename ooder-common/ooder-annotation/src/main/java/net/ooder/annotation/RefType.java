package net.ooder.annotation;

public enum RefType implements IconEnumstype {
    NONE("无", "ri-forbid-line"),
    REF("引用", "ri-link"),
    O2M("一对多", "ri-arrow-right-line"),
    M2O("多对一", "ri-arrow-left-line"),
    F2F("自循环", "ri-refresh-line"),
    O2O("一对一", "ri-swap-line"),
    FIND("查找", "ri-search-line"),
    M2M("多对多", "ri-swap-line");

    private final String imageClass;
    private final String name;

    RefType(String name, String imageClass) {
        this.name = name;
        this.imageClass = imageClass;
    }

    public String getImageClass() {
        return imageClass;
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
