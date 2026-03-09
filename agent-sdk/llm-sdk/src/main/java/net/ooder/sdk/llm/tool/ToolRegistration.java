package net.ooder.sdk.llm.tool;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 工具注册结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolRegistration {

    /**
     * 工具ID
     */
    private String toolId;

    /**
     * 是否注册成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 注册时间戳
     */
    @Builder.Default
    private long registeredAt = System.currentTimeMillis();

    /**
     * 创建成功结果
     */
    public static ToolRegistration success(String toolId) {
        return ToolRegistration.builder()
                .toolId(toolId)
                .success(true)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static ToolRegistration failure(String toolId, String errorMessage) {
        return ToolRegistration.builder()
                .toolId(toolId)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }
}
