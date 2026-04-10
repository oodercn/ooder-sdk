package net.ooder.annotation.event;


import net.ooder.annotation.IconEnumstype;

public enum APIParamsType implements IconEnumstype {

    RequestData("请求参数", "ri-send-plane-line"),
    ResponseData("数据绑定", "ri-plug-line"),
    ResponseCall("回调函数", "ri-code-line");

    private final String imageClass;

    private String name;

    private String type;


    APIParamsType(String name, String imageClass) {

        this.name = name;
        this.type = name();
        this.imageClass = imageClass;

    }

    @Override
    public String getType() {
        return name();
    }

    public String getName() {
        return name;
    }

    @Override
    public String getImageClass() {
        return imageClass;
    }

}