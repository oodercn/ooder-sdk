package net.ooder.sdk.llm.common.enums;

public enum MetricsType {
    PERFORMANCE("performance", "性能指标"),
    RESOURCE("resource", "资源指标"),
    BUSINESS("business", "业务指标"),
    ALL("all", "所有指标");

    private final String code;
    private final String description;

    MetricsType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MetricsType fromCode(String code) {
        for (MetricsType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown metrics type: " + code);
    }
}
