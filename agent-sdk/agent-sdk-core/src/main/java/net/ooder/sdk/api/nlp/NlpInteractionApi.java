package net.ooder.sdk.api.memory;

// WillTransformer 和 WillExpression 已从外部 llm-sdk 导入
import net.ooder.sdk.will.WillTransformer;
import net.ooder.sdk.will.WillExpression;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * NLP 交互 API（泛型版本）
 *
 * @param <M> 元数据类型
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public interface NlpInteractionApi<M> {
    
    WillExpression parse(String naturalLanguage);
    
    CompletableFuture<WillExpression> parseAsync(String naturalLanguage);
    
    NlpResult<M> analyze(String text);
    
    String extractIntent(String text);
    
    List<String> extractEntities(String text);
    
    String extractDomain(String text);
    
    int estimatePriority(String text);
    
    M extractMetadata(String text);
    
    double calculateConfidence(WillExpression will);
    
    List<String> suggestCapabilities(String intent, String domain);
    
    String generateClarificationQuestion(WillExpression will);
    
    WillExpression mergeContext(WillExpression will, M context);
    
    /**
     * NLP 分析结果（泛型版本）
     * @param <M> 元数据类型
     */
    class NlpResult<M> {
        private String intent;
        private String domain;
        private List<String> entities;
        private M metadata;
        private double confidence;
        private String sentiment;
        private String language;
        
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
        
        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }
        
        public List<String> getEntities() { return entities; }
        public void setEntities(List<String> entities) { this.entities = entities; }
        
        public M getMetadata() { return metadata; }
        public void setMetadata(M metadata) { this.metadata = metadata; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public String getSentiment() { return sentiment; }
        public void setSentiment(String sentiment) { this.sentiment = sentiment; }
        
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        
        /**
         * 创建结果（向后兼容）
         */
        public static NlpResult<Map<String, Object>> create() {
            return new NlpResult<>();
        }
    }
}
