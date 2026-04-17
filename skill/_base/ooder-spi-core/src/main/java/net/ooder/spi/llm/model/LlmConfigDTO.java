package net.ooder.spi.llm.model;

import lombok.Data;

@Data
public class LlmConfigDTO {

    private String provider;

    private String model;

    private double temperature;

    private int maxTokens;

    private boolean streamEnabled;

    private String apiKey;

    private String baseUrl;
}
