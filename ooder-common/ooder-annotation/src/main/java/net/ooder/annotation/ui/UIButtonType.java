package net.ooder.annotation.ui;

public enum UIButtonType implements ItemType {
    add("add", "增加", "ri-add-line"),
    edit("edit", "修改", "ri-edit-line"),
    delete("delete", "删除", "ri-close-line");

    private String id;
    private String caption;
    private String imageClass;


    UIButtonType(String id, String caption, String imageClass) {
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
