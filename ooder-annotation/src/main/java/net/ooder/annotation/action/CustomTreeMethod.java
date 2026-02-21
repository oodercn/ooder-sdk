package net.ooder.annotation.action;


import net.ooder.annotation.Enumstype;

public enum CustomTreeMethod implements Enumstype {
    toggleNode("toggleNode", "展开折叠"),
    openToNode("openToNode", "打开指定节点"),
    reloadNode("reloadNode", "刷新指定节点"),
    insertItems("insertItems", "插入子项"),
    disableNode("disableNode", "禁用子项"),
    removeItems("removeItems", "移除子项"),
    clearItems("clearItems", "清空数据"),
    fireItemClickEvent("fireItemClickEvent", "点击子项"),
    setValue("setValue", "设置值");

    private final String type;
    private final String name;

    CustomTreeMethod(String type, String name) {
        this.type = type;
        this.name = name;
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
