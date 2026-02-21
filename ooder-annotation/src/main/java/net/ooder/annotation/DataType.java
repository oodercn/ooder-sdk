package net.ooder.annotation;

/**
 * AIGC数据类型枚举
 * 定义支持的输入输出数据格式
 */
public enum DataType {
    /** 文本数据 */
    TEXT,
    
    /** JSON数据 */
    JSON,
    
    /** 图像数据 */
    IMAGE,
    
    /** 音频数据 */
    AUDIO,
    
    /** 视频数据 */
    VIDEO,
    
    /** 二进制数据 */
    BINARY
}