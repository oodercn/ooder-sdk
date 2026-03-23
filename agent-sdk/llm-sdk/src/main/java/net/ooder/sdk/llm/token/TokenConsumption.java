package net.ooder.sdk.llm.token;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Token 消耗详情
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenConsumption {

    private QuotaScope scope;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private String model;
    private String operationId;
    private long timestamp;

    public static TokenConsumption of(QuotaScope scope, int prompt, int completion, String model) {
        return TokenConsumption.builder()
                .scope(scope)
                .promptTokens(prompt)
                .completionTokens(completion)
                .totalTokens(prompt + completion)
                .model(model)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
