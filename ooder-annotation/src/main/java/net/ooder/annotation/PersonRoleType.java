package net.ooder.annotation;


public enum PersonRoleType implements IconEnumstype {

    Duty("职位", "ri-briefcase-line", RoleOtherType.Person),
    Role("角色", "ri-shield-user-line", RoleOtherType.Person),
    Position("岗位", "ri-stack-line", RoleOtherType.Person),
    PersonLevel("职级", "ri-node-tree", RoleOtherType.Person),
    Group("用户组", "ri-group-line", RoleOtherType.Person);
    private String type;

    private String name;

    private RoleOtherType otherType;

    private String imageClass;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    PersonRoleType(String name, String imageClass, RoleOtherType otherType) {
        this.type = name();
        this.name = name;
        this.imageClass = imageClass;
        this.otherType = otherType;


    }

    public RoleOtherType getOtherType() {
        return otherType;
    }


    @Override
    public String getImageClass() {
        return imageClass;
    }


    @Override
    public String toString() {
        return type;
    }

    public static PersonRoleType fromType(String typeName) {
        for (PersonRoleType type : PersonRoleType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return Role;
    }

}
