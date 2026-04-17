package net.ooder.spi.llm;

import net.ooder.spi.llm.model.*;

import java.util.List;

public interface LlmProvider {

    String getProviderId();

    String getProviderName();

    List<LlmModel> getAvailableModels();

    LlmResponse chat(LlmRequest request);

    void chatStream(LlmRequest request, LlmStreamHandler handler);

    boolean isAvailable();

    LlmConfig getDefaultConfig();

    int getMaxTokens(String modelId);

    boolean supportsStreaming(String modelId);
}
