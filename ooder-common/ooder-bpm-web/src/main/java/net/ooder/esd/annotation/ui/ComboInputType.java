package net.ooder.esd.annotation.ui;

public enum ComboInputType {
    TEXT("text"),
    NUMBER("number"),
    DATE("date"),
    SELECT("select"),
    RADIO("radio"),
    CHECKBOX("checkbox"),
    USERSELECT("userselect"),
    DEPARTMENTSELECT("departmentselect"),
    ROLESELECT("roleselect"),
    FILE("file"),
    IMAGE("image"),
    RICHTEXT("richtext"),
    TEXTAREA("textarea");

    private final String label;

    ComboInputType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
