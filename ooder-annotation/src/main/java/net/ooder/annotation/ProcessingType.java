package net.ooder.annotation;

/**
 * 数据处理策略枚举
 * 定义数据预处理方式
 */
public enum ProcessingType {
    /** 原始数据，不处理 */
    RAW,
    
    /** 标准化处理 */
    NORMALIZED,
    
    /** 加密处理 */
    ENCRYPTED,

    TEXT_GENERATION,
    
    /** 压缩处理 */
    COMPRESSED,
    
    /** 脱敏处理 */
    MASKED
}