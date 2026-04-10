package net.ooder.annotation;


public enum OrgRoleType implements IconEnumstype {

    OrgLevel("部门级别", "ri-node-tree", RoleOtherType.Org),
    OrgRole("部门角色", "ri-building-line", RoleOtherType.Org);;
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

    OrgRoleType(String name, String imageClass, RoleOtherType otherType) {
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

    public static OrgRoleType fromType(String typeName) {
        for (OrgRoleType type : OrgRoleType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return OrgRole;
    }

}
