package net.ooder.sdk.llm.token;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 配额请求
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaRequest {

    private QuotaScope scope;
    private int requestedTokens;
    private String model;
    private String operationType;

    public static QuotaRequest of(QuotaScope scope, int tokens) {
        return QuotaRequest.builder()
                .scope(scope)
                .requestedTokens(tokens)
                .build();
    }
}
