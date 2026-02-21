package net.ooder.annotation;

/**
 * AIGC任务类型枚举
 * 定义支持的AI生成任务类别
 */
public enum TaskType {
    /** 文本生成 */
    TEXT_GENERATION,
    
    /** 图像生成 */
    IMAGE_GENERATION,
    
    /** 文本摘要 */
    SUMMARY,
    
    /** 翻译 */
    TRANSLATION,
    
    /** 代码生成 */
    CODE_GENERATION,
    
    /** 多模态生成 */
    MULTI_MODAL,
    
    /** 语音生成 */
    AUDIO_GENERATION,
    
    /** 视频生成 */
    VIDEO_GENERATION,
    
    /** 数据分析 */
    DATA_ANALYSIS
}