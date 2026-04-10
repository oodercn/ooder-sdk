package net.ooder.annotation.action;


import net.ooder.annotation.Enumstype;

public enum CustomModuleMethod implements Enumstype {
    postMessage("postMessage", "发送消息"),
    show("show", "显示模块"),
    checkValid("checkValid", "检查必填项"),
    show2("show2", "显示模块远程"),
    destroy("destroy", "销毁模块"),
    reloadParent("reloadParent", "刷新父级"),
    reloadMenu("reloadMenu", "刷新窗体菜单"),
    autoSave("autoSave", "自动保存"),
    destroyCurrDio("destroyCurrDio", "销毁当前窗口"),
    destroyParent("destroyParent", "销毁父级"),
    initData("initData", "初始化"),
    setData("setData", "填充数据");

    private final String type;
    private final String name;

    CustomModuleMethod(String type, String name) {
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
