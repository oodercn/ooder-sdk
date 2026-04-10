package net.ooder.annotation;

/**
 * 参数验证规则枚举
 * 用于{@link AggregationType.AgentParam}注解指定参数验证类型
 */
public enum ValidationRule {
    /** 非空验证 - 检查参数值不为null且不为空字符串 */
    NOT_EMPTY,
    
    /** 邮箱格式验证 - 检查参数是否符合邮箱格式 */
    EMAIL,
    
    /** 手机号格式验证 - 检查参数是否符合手机号格式 */
    PHONE,
    
    /** 数字范围验证 - 检查数字参数是否在指定范围内 */
    NUMBER_RANGE,
    
    /** 字符串长度验证 - 检查字符串参数长度是否在指定范围内 */
    STRING_LENGTH,
    
    /** 自定义正则表达式验证 - 使用自定义正则表达式验证参数 */
    REGEX
}