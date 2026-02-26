package net.ooder.sdk.llm.adapter;

import net.ooder.sdk.llm.adapter.model.AdaptedRequest;
import net.ooder.sdk.llm.adapter.model.FallbackRequest;
import net.ooder.sdk.llm.adapter.model.FallbackResult;
import net.ooder.sdk.llm.adapter.model.ModelInfo;
import net.ooder.sdk.llm.adapter.model.ModelSelectionCriteria;
import net.ooder.sdk.llm.adapter.model.OriginalRequest;
import net.ooder.sdk.llm.adapter.model.ProviderInfo;
import net.ooder.sdk.llm.adapter.model.RegisterResult;
import net.ooder.sdk.llm.adapter.model.RouteRequest;
import net.ooder.sdk.llm.adapter.model.RouteResult;

public interface MultiLlmAdapterApi {
    
    RegisterResult registerLLMProvider(ProviderInfo provider);
    
    ModelInfo selectModel(ModelSelectionCriteria criteria);
    
    AdaptedRequest adaptProtocol(OriginalRequest request, String providerId);
    
    RouteResult routeRequest(RouteRequest request);
    
    FallbackResult fallbackModel(FallbackRequest request);
}
