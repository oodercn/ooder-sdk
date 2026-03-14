package net.ooder.agent.client.command;

import  net.ooder.annotation.Enumstype;

public enum OperationCommandParamEnums implements Enumstype {

    on("on", "on"),
    on_off("on/off", "on/off"),
    off("off", "off");

    private String type;

    private String name;


    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    OperationCommandParamEnums(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static OperationCommandParamEnums fromType(String typeName) {

        for (OperationCommandParamEnums type : OperationCommandParamEnums.values()) {
            if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                return type;
            }
        }
        return on_off;
    }

}
