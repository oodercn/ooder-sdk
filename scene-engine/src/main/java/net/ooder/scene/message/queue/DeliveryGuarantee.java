package net.ooder.scene.message.queue;

/**
 * 投递保证枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum DeliveryGuarantee {
    
    AT_MOST_ONCE("at_most_once", "最多一次 - 不保证送达"),
    
    AT_LEAST_ONCE("at_least_once", "至少一次 - 可能重复"),
    
    EXACTLY_ONCE("exactly_once", "精确一次 - 保证不重不漏");
    
    private final String code;
    private final String description;
    
    DeliveryGuarantee(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static DeliveryGuarantee fromCode(String code) {
        for (DeliveryGuarantee guarantee : values()) {
            if (guarantee.code.equalsIgnoreCase(code)) {
                return guarantee;
            }
        }
        return AT_LEAST_ONCE;
    }
}
