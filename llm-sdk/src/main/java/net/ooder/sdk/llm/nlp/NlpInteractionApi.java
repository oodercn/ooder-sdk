package net.ooder.sdk.llm.nlp;

import net.ooder.sdk.llm.nlp.model.ContextOperation;
import net.ooder.sdk.llm.nlp.model.ContextOperationResult;
import net.ooder.sdk.llm.nlp.model.Entity;
import net.ooder.sdk.llm.nlp.model.Intent;
import net.ooder.sdk.llm.nlp.model.NlpInput;
import net.ooder.sdk.llm.nlp.model.NlpParseResult;
import net.ooder.sdk.llm.nlp.model.NlpResponse;
import net.ooder.sdk.llm.nlp.model.NlpResponseRequest;
import net.ooder.sdk.llm.nlp.model.SentimentResult;

import java.util.List;

public interface NlpInteractionApi {
    
    NlpParseResult processNLPInput(NlpInput input);
    
    NlpResponse generateNLPResponse(NlpResponseRequest request);
    
    ContextOperationResult manageContext(ContextOperation operation);
    
    List<Intent> extractIntent(String text);
    
    List<Entity> extractEntity(String text);
    
    SentimentResult sentimentAnalysis(String text);
}
