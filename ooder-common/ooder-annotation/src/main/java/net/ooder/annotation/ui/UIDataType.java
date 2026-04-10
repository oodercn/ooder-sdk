package net.ooder.annotation.ui;

public enum UIDataType implements UIType {
    DataBinder("ood.DataBinder"),
    MQTT("ood.MQTT"),
    APICall("ood.APICall");

    private String type;

    UIDataType(String type) {
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

    public static UIDataType fromType(String typeName) {
        for (UIDataType type : UIDataType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }

        return APICall;
    }

}
