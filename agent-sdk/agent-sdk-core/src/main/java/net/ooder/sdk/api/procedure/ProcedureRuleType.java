package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

/**
 * Procedure Rule Type
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public enum ProcedureRuleType {

    COMPLIANCE("compliance", "Compliance rule"),

    APPROVAL("approval", "Approval rule"),

    CONSTRAINT("constraint", "Constraint rule"),

    VALIDATION("validation", "Validation rule"),

    BUSINESS("business", "Business rule");

    private final String code;
    private final String description;

    ProcedureRuleType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ProcedureRuleType fromCode(String code) {
        if (code == null) {
            return VALIDATION;
        }
        for (ProcedureRuleType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return VALIDATION;
    }
}
