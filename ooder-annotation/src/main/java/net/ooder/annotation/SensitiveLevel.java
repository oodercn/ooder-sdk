package net.ooder.annotation;

/**
 * 数据敏感级别枚举
 * 定义AIGC处理数据的敏感程度
 */
public enum SensitiveLevel {
    /** 公开数据 - 隐私级别0 */
    PUBLIC(0),
    
    /** 内部数据 - 隐私级别1 */
    INTERNAL(1),

    /** 中等敏感 - 隐私级别2 */
    MEDIUM(2),

    /** 高敏感 - 隐私级别3 */
    HIGH(3),
    
    /** 保密数据 - 隐私级别4 */
    CONFIDENTIAL(4),
    
    /** 高度保密数据 - 隐私级别5 */
    HIGHLY_CONFIDENTIAL(5);

    private final int privacyLevel;

    SensitiveLevel(int privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public int getPrivacyLevel() {
        return privacyLevel;
    }

    public static SensitiveLevel fromPrivacyLevel(int level) {
        for (SensitiveLevel sl : values()) {
            if (sl.privacyLevel == level) {
                return sl;
            }
        }
        return MEDIUM;
    }
}