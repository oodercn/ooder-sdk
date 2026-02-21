package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum TensorEventEnum implements EventKey {

    uploadfile("uploadfile", "上传文件"),
    uploadfail("uploadfail", "上传失败"),
    uploadcomplete("uploadcomplete", "上传完成"),
    uploadprogress("uploadprogress", "上传进度");

    private String event;
    private String[] params;
    private String name;

    TensorEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    TensorEventEnum(String event, String name, String... args) {
        this.event = event;
        this.name = name;
        this.params = args;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return event;
    }
}