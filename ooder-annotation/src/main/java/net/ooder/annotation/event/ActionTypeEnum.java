package net.ooder.annotation.event;


import net.ooder.annotation.Enumstype;

public enum ActionTypeEnum implements Enumstype {
    page("当前页面"),
    control("内部调用"),
    module("模块间"),
    otherModuleCall("外部页面调用"),
    other("方法调用"),
    msg("发送消息"),
    var("对象定义"),
    callback("回调执行"),
    undefined("空实现");


    String name;

    ActionTypeEnum(String name) {

        this.name = name;
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
