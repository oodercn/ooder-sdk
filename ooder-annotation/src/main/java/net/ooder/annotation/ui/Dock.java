package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum Dock implements Enumstype {
    none("默认"), top("顶部对齐"), bottom("底部对齐"), left("左对齐"), right("右对齐"),
    center("居中对齐"), middle("垂直居中"), origin("自适应排列"),
    width("宽度自适应"), height("高度自适应"), fill("自动填充"), cover("覆盖");

    private final String name;


    Dock(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }

}
