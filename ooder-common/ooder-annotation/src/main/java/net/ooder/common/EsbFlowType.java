package net.ooder.common;


import net.ooder.annotation.Enumstype;

public enum EsbFlowType implements Enumstype {

    msgRepeat("消息转发", "msgRepeat"),

    comet("长链接维持", "comet"),

    function("自定义函数", "function"),

    localAction("本地服务", "localAction"),

    expression("本地表达式调用", "expression"),

    remoteAction("远程服务", "remoteAction"),

    clusterAction("远程下发", "clusterAction"),

    command("命令", "command"),

    listener("监听器", "listener");


    private String name;


    private String type;


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    EsbFlowType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }


    public static EsbFlowType fromMethod(String method) {
        for (EsbFlowType type : EsbFlowType.values()) {
            if (type.getType().equals(method)) {
                return type;
            }
        }
        return localAction;
    }

    public static EsbFlowType fromType(String method) {
        for (EsbFlowType type : EsbFlowType.values()) {
            if (type.getType().equals(method)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return type.toString();
    }


}
