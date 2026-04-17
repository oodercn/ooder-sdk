package net.ooder.spi.llm.model;

import lombok.Data;

@Data
public class LlmModelDTO {

    private String id;

    private String name;

    private String providerId;

    private String description;

    private int maxTokens;

    private boolean supportsStreaming;

    private boolean supportsFunctionCalling;
}
