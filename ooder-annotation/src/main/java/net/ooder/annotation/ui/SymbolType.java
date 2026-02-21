package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum SymbolType implements Enumstype {

    defined("defined", "存在"),
    undefined("defined", "不存在"),
    empty("empty", "为空"),
    nonempty("non-empty", "非空"),
    equal("=", "等于"),
    notequal("!=", "不等于"),
    than(">", "大于"),
    less("<", "小于"),
    thanorequal(">=", "大于等于"),
    lessorequal("<=", "小于等于"),
    include("include", "包含"),
    exclude("exclude", "不包含"),
    start("start", "开始与"),
    end("end", "结束于"),
    objhaskey("objhaskey", "对象存在键"),
    objnokey("objnokey", "对象不存在键"),
    arrhasvalue("arrhasvalue", "对象存在值"),
    arrnovalue("arrnovalue", "对象不存在值"),
    objarrhaskey("objarrhaskey", "对象不含值"),
    objarrnokey("objarrnokey", "数组不含值");

    String type;
    String name;

    SymbolType(String type, String name) {

        this.type = type;
        this.name = name;
    }

    ;

    public static SymbolType formType(String type) {
        for (SymbolType symbolType : SymbolType.values()) {
            if (symbolType.getType().equals(type)) {
                return symbolType;
            }
        }
        return defined;
    }


    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
