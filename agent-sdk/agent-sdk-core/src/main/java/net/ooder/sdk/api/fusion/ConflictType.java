package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

/**
 * Conflict Type Enum
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public enum ConflictType {

    VALUE_MISMATCH("value_mismatch", "Value mismatch"),

    TYPE_MISMATCH("type_mismatch", "Type mismatch"),

    MISSING_IN_ENTERPRISE("missing_in_enterprise", "Missing in enterprise procedure"),

    MISSING_IN_SKILL("missing_in_skill", "Missing in skill definition"),

    STRUCTURE_MISMATCH("structure_mismatch", "Structure mismatch");

    private final String code;
    private final String description;

    ConflictType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ConflictType fromCode(String code) {
        if (code == null) {
            return VALUE_MISMATCH;
        }
        for (ConflictType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return VALUE_MISMATCH;
    }
}
