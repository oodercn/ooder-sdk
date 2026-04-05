package net.ooder.bpm.enums.route;


import net.ooder.annotation.Enumstype;

public enum RouteCondition implements Enumstype {

    CONDITION("CONDITION", "执行条件"),

    OTHERWISE("OTHERWISE", "例外处理"),

    EXCEPTION("EXCEPTION", "异常处理"),

    DEFAULTEXCEPTION("DEFAULTEXCEPTION", "流程默认值 ");

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    RouteCondition(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static RouteCondition fromType(String typeName) {
        for (RouteCondition type : RouteCondition.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return DEFAULTEXCEPTION;
    }

}
