package net.ooder.spi.llm;

import net.ooder.spi.llm.model.LlmResponse;

public interface LlmStreamHandler {

    void onChunk(String chunk);

    void onComplete(LlmResponse response);

    void onError(Throwable error);
}
