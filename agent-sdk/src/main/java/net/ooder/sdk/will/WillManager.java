package net.ooder.sdk.will;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface WillManager {
    
    WillExpression expressWill(String willText, WillExpression.WillType type, int priority);
    
    WillExpression expressWill(String willText);
    
    CompletableFuture<WillExpression> expressAndTransform(String willText);
    
    Optional<WillExpression> getWill(String willId);
    
    List<WillExpression> getWillsByType(WillExpression.WillType type);
    
    List<WillExpression> getWillsByStatus(WillExpression.WillStatus status);
    
    List<WillExpression> getActiveWills();
    
    List<WillExpression> getAllWills();
    
    void cancelWill(String willId);
    
    void updateWill(String willId, Map<String, Object> updates);
    
    CompletableFuture<WillExecutor.WillExecutionResult> executeWill(String willId);
    
    CompletableFuture<WillExecutor.WillExecutionResult> executeWill(WillExpression will);
    
    CompletableFuture<WillExecutor.ExecutionStatus> getExecutionStatus(String willId);
    
    CompletableFuture<WillExecutor.EffectEvaluation> evaluateWill(String willId);
    
    void addWillListener(WillListener listener);
    
    void removeWillListener(WillListener listener);
    
    int getWillCount();
    
    void clear();
    
    interface WillListener {
        
        void onWillExpressed(WillExpression will);
        
        void onWillTransformed(WillExpression will, WillTransformer.WillTransformResult result);
        
        void onWillExecuting(WillExpression will);
        
        void onWillCompleted(WillExpression will, WillExecutor.WillExecutionResult result);
        
        void onWillFailed(WillExpression will, Throwable error);
        
        void onWillCancelled(WillExpression will);
    }
}
