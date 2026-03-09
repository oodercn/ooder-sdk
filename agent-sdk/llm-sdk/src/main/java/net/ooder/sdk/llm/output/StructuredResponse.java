package net.ooder.sdk.llm.output;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 结构化响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructuredResponse<T> {

    /**
     * 解析后的数据
     */
    private T data;

    /**
     * 是否有效
     */
    private boolean valid;

    /**
     * 验证错误列表
     */
    private List<String> validationErrors;

    /**
     * 原始响应内容
     */
    private String rawResponse;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 创建成功响应
     */
    public static <T> StructuredResponse<T> success(T data, String rawResponse) {
        return StructuredResponse.<T>builder()
                .data(data)
                .valid(true)
                .rawResponse(rawResponse)
                .build();
    }

    /**
     * 创建验证失败响应
     */
    public static <T> StructuredResponse<T> validationFailure(String rawResponse, List<String> errors) {
        return StructuredResponse.<T>builder()
                .valid(false)
                .rawResponse(rawResponse)
                .validationErrors(errors)
                .build();
    }
}
