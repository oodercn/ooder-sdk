package net.ooder.annotation;

/**
 * MCP通信协议枚举
 * 定义服务间通信支持的协议类型
 */
public enum ProtocolType {
    /** HTTP协议 */
    HTTP,
    
    /** HTTPS协议 */
    HTTPS,
    
    /** TCP协议 */
    TCP,
    
    /** UDP协议 */
    UDP,

    STDIO,

    SSE,
    
    /** gRPC协议 */
    GRPC
}