package net.ooder.agent.client.command;

import com.alibaba.fastjson.annotation.JSONField;
import  net.ooder.annotation.Enumstype;

public enum CmdRule implements Enumstype {

    delay("delay", "delay"),

    loop("loop", "loop");

    private String name;
    private String type;

    CmdRule(String name, String type) {

        this.name = name;
        this.type = type;

    }

    public static CmdRule fromByName(String typename) {
        for (CmdRule type : CmdRule.values()) {
            if (type.getType().toUpperCase().equals(typename.toUpperCase())) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return type;
    }

    @JSONField
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }
}
