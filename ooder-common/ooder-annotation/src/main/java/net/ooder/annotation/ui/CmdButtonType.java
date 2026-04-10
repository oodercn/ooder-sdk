package net.ooder.annotation.ui;


import net.ooder.annotation.IconEnumstype;

public enum CmdButtonType implements IconEnumstype {
    button("按钮", "ri-checkbox-blank-line"),
    image("图片", "ri-image-line"),
    text("文本", "ri-font-size"),
    split("分隔符", "ri-separator");
    private final String name;
    private final String imageClass;

    CmdButtonType(String name, String imageClass) {

        this.name = name;
        this.imageClass = imageClass;
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
    public String getImageClass() {
        return imageClass;
    }

    @Override
    public String getName() {
        return name;
    }

}