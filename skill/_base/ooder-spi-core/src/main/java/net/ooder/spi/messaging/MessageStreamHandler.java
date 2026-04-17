package net.ooder.spi.messaging;

import net.ooder.spi.messaging.model.UnifiedMessage;

public interface MessageStreamHandler {

    void onStart();

    void onMessage(UnifiedMessage message);

    void onChunk(String chunk);

    void onComplete(UnifiedMessage message);

    void onError(Throwable error);
}
