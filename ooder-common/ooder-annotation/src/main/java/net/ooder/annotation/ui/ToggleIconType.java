package net.ooder.annotation.ui;

public enum ToggleIconType implements UIType {

    taggle("ood-uicmd-toggle"),
    check("ood-uicmd-check");

    private String type;

    ToggleIconType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static ToggleIconType fromType(String typeName) {
        for (ToggleIconType type : ToggleIconType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return taggle;
    }

}
