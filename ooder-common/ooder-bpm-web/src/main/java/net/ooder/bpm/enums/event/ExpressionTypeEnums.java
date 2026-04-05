package net.ooder.bpm.enums.event;


import net.ooder.annotation.Enumstype;

public enum ExpressionTypeEnums implements Enumstype {

    // ClientExpression("clientexpression", "EL脚本"),

    Expression("expression", "服务端EL表达式"),

    Listener("listener", "自定义监听器"),

    Script("clientexpression", "客户端script脚本");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ExpressionTypeEnums(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ExpressionTypeEnums fromType(String typeName) {
	for (ExpressionTypeEnums type : ExpressionTypeEnums.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
