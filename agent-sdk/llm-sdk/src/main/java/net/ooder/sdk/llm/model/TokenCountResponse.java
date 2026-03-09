package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Token计数响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenCountResponse {
    
    /**
     * Token数量
     */
    private int tokenCount;
    
    /**
     * Token信息列表
     */
    private List<TokenInfo> tokens;
}
