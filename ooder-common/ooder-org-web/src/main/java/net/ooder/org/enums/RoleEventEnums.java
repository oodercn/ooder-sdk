package net.ooder.org.enums;

import net.ooder.annotation.EventEnums;

public enum RoleEventEnums implements EventEnums {

    CREATE("创建", "Create", 2006),

    ADDPERSON("添加人员", "ADDPERSON", 1002),

    ADDORG("添加部门", "ADDORG", 1003),

    REMOVEPERSON("移除人员", "REMOVEPERSON", 1010),

    REMOVEORG("移除部门", "REMOVEORG", 1009),

    RENAME("名称修改", "RENAME", 1005),

    DELETE("删除完成", "DELET", 1007),

    ADDROLE("添加角色", "ADDROLE", 1008);


    private String name;

    private Integer code;

    private String method;

    public Integer getCode() {
        return code;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    RoleEventEnums(String name, String method, Integer code) {

        this.name = name;
        this.method = method;
        this.code = code;

    }

    @Override
    public String toString() {
        return method.toString();
    }

    public static RoleEventEnums fromCode(Integer code) {
        for (RoleEventEnums type : RoleEventEnums.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public static RoleEventEnums fromMethod(String method) {
        for (RoleEventEnums type : RoleEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    public static RoleEventEnums fromType(String method) {
        for (RoleEventEnums type : RoleEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return method.toString();
    }

}
