package net.ooder.annotation.ui;

public enum DebugButtonType implements ItemType {
    clear("clear", "清空", "ri-refresh-line"),
    config("config", "配置", "ri-settings-3-line"),
    edit("edit", "编辑页面", "ri-code-line");

    private String id;
    private String caption;
    private String imageClass;


    DebugButtonType(String id, String caption, String imageClass) {
        this.id = id;
        this.caption = caption;
        this.imageClass = imageClass;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public String getImageClass() {
        return imageClass;
    }

    @Override
    public String getCaption() {
        return caption;
    }

}