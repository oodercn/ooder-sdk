package net.ooder.annotation;

public enum UserSpace implements IconEnumstype {
    CRUD("通用数据库应用", "ri-database-line"),
    FORM("工作表单", "ri-file-text-line"),
    USER("用户自定义", "ri-user-settings-line"),
    VIEW("视图定义", "ri-eye-line"),
    SYS("系统应用", "ri-settings-3-line");
    private final String name;
    private final String imageClass;


    UserSpace(String name, String imageClass) {
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
    public String getName() {
        return name;
    }

    public String getImageClass() {
        return imageClass;
    }
}
