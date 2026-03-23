
package net.ooder.skills.common.enums;

public enum SceneRoleType {
    PRIMARY("primary", "Primary scene with core functionality"),
    COLLABORATIVE("collaborative", "Collaborative scene as dependency");
    
    private final String code;
    private final String description;
    
    SceneRoleType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isPrimary() {
        return this == PRIMARY;
    }
    
    public boolean isCollaborative() {
        return this == COLLABORATIVE;
    }
    
    public static SceneRoleType fromCode(String code) {
        for (SceneRoleType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown scene role type: " + code);
    }
}
