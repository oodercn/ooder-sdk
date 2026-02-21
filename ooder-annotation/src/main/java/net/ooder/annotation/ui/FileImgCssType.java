package net.ooder.annotation.ui;


public enum FileImgCssType implements UIType {
    html("html", "ri-code-line"),
    css("css", "ri-css3-line"),
    js("js", "ri-javascript-line"),
    cls("cls", "ri-file-code-line"),
    json("json", "ri-code-line"),
    xml("xml", "ri-code-line"),
    swf("swf", "ri-file-video-line"),
    flv("flv", "ri-file-video-line"),
    fla("fla", "ri-file-video-line"),
    jpg("jpg", "ri-image-line"),
    png("png", "ri-image-line"),
    gif("gif", "ri-image-line"),
    bmp("bmp", "ri-image-line"),
    unkown("unkown", "ri-question-line");


    private String type;

    private String imageClass;

    FileImgCssType(String type, String imageClass) {

        this.type = type;
        this.imageClass = imageClass;


    }


    public String getImageClass() {
        return imageClass;
    }

    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }

    @Override
    public String toString() {
        return type;
    }

    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }

    public static FileImgCssType fromType(String typeName) {
        for (FileImgCssType type : FileImgCssType.values()) {
            if (type.getType().equals(typeName.toLowerCase())) {
                return type;
            }
        }
        return unkown;
    }

}