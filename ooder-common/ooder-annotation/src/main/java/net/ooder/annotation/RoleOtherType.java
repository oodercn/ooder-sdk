package net.ooder.annotation;


public enum RoleOtherType implements IconEnumstype {

    Person("用户角色", "ri-user-shared-line"),
    Org("部门角色", "ri-building-line");
    private String type;

    private String name;

    private String imageClass;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    RoleOtherType(String name, String imageClass) {
        this.type = name();
        this.name = name;
        this.imageClass = imageClass;
    }


    @Override
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


}
