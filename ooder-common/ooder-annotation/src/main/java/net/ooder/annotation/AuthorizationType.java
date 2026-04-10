package net.ooder.annotation;

/**
 * 认证授权类型枚举
 * 定义MCP服务支持的认证方式
 */
public enum AuthorizationType {
    /** 无认证 */
    NONE,
    
    /** JWT令牌认证 */
    JWT,
    
    /** OAuth2.0认证 */
    OAUTH2,
    
    /** API密钥认证 */
    API_KEY,
    
    /** 数字签名认证 */
    DIGITAL_SIGNATURE
}