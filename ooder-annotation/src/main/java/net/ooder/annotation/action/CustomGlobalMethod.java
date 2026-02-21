package net.ooder.annotation.action;


import net.ooder.annotation.Enumstype;

public enum CustomGlobalMethod implements Enumstype {
    print("print", "打印"),
    call("call", "调用"),
    page("pege", "页面设置"),
    open_self("open----_self", "跳转到新窗口"),
    showModule2("showModule2", "弹窗"),
    download("download", "下载文件"),
    invoke("invoke", "调用");

    private final String type;
    private final String name;

    CustomGlobalMethod(String type, String name) {
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
