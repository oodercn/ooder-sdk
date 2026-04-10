package net.ooder.common;


import net.ooder.annotation.Enumstype;

public enum ContextType implements Enumstype {

    Action("调用更新", "action"),

    Context("线程安全变量", "context"),

    Session("当前会话有效", "session"),

    Static("静态变量", "Static"),

    Function("静态方法", "Function"),

    Command("第三方命令", "Command"),

    Server("集群服务", "Server");

    private String name;

    private String type;


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    ContextType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }


    public static ContextType fromType(String type) {
        for (ContextType contextType : ContextType.values()) {
            if (contextType.getType().toUpperCase().equals(type.toUpperCase())) {
                return contextType;
            }
        }
        return Action;
    }

    @Override
    public String getType() {
        return type.toString();
    }


}
