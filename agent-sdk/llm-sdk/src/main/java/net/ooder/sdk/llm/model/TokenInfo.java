package net.ooder.sdk.llm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Token信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {
    
    /**
     * Token字符串
     */
    private String token;
    
    /**
     * Token ID
     */
    private int id;
    
    /**
     * 概率
     */
    private double probability;
}
