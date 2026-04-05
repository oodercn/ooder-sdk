package net.ooder.esd.annotation;

import net.ooder.annotation.Enumstype;

public enum RouteToType implements Enumstype {
    RouteTo("RouteTo", "路由到"),
    Multirouteto("Multirouteto", "多路路由");

    private final String name;
    private final String displayName;

    RouteToType(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    @Override
    public String getType() {
        return name;
    }

    @Override
    public String getName() {
        return displayName;
    }

    public static RouteToType fromType(String type) {
        if (type == null) return null;
        for (RouteToType v : values()) {
            if (v.name.equals(type)) return v;
        }
        return null;
    }
}
