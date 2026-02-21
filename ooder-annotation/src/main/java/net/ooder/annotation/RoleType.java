package net.ooder.annotation;

import java.util.LinkedHashSet;
import java.util.Set;


public enum RoleType implements IconEnumstype {

    Duty("职位", "ri-briefcase-line", RoleOtherType.Person),
    Role("角色", "ri-shield-user-line", RoleOtherType.Person),
    Position("岗位", "ri-stack-line", RoleOtherType.Person),
    PersonLevel("职级", "ri-node-tree", RoleOtherType.Person),
    Group("用户组", "ri-group-line", RoleOtherType.Person),
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

    RoleType(String name, String imageClass, RoleOtherType otherType) {
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

    public static RoleType fromType(String typeName) {
        for (RoleType type : RoleType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return Role;
    }

    public static Set<RoleType> fromOtherType(RoleOtherType type) {
        Set<RoleType> roleTypes = new LinkedHashSet<>();
        for (RoleType roleType : RoleType.values()) {
            if (roleType.getOtherType().equals(type)) {
                roleTypes.add(roleType);
            }
        }
        return roleTypes;
    }


}
