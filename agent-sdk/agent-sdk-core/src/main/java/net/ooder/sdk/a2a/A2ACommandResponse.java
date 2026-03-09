package net.ooder.sdk.a2a;

import lombok.Builder;
import lombok.Data;

/**
 * A2A 命令响应
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
public class A2ACommandResponse {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 响应时间戳
     */
    private long timestamp;

    /**
     * 创建成功响应
     */
    public static A2ACommandResponse success(Object data) {
        return A2ACommandResponse.builder()
            .success(true)
            .data(data)
            .timestamp(System.currentTimeMillis())
            .build();
    }

    /**
     * 创建失败响应
     */
    public static A2ACommandResponse error(String error) {
        return A2ACommandResponse.builder()
            .success(false)
            .error(error)
            .timestamp(System.currentTimeMillis())
            .build();
    }
}
