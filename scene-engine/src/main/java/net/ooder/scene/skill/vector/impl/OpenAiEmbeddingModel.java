package net.ooder.scene.skill.vector.impl;

import net.ooder.scene.skill.vector.EmbeddingConfig;
import net.ooder.scene.skill.vector.EmbeddingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OpenAiEmbeddingModel implements EmbeddingModel {

    private static final Logger log = LoggerFactory.getLogger(OpenAiEmbeddingModel.class);

    private final String id;
    private final String name;
    private final int dimensions;
    private final int maxTokens;
    private final EmbeddingConfig config;
    private volatile boolean available = false;

    public OpenAiEmbeddingModel(EmbeddingConfig config) {
        this.config = config;
        this.id = config.getModel();
        this.name = "OpenAI " + config.getModel();
        this.dimensions = config.getDimension();
        this.maxTokens = 8191;
        checkAvailability();
    }

    public OpenAiEmbeddingModel(String apiKey, String model) {
        this(EmbeddingConfig.openai(apiKey, model));
    }

    private void checkAvailability() {
        available = config.getApiKey() != null && !config.getApiKey().isEmpty();
        if (available) {
            log.info("OpenAI embedding model {} is available", id);
        } else {
            log.warn("OpenAI embedding model {} is not available: API key not configured", id);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvider() {
        return EmbeddingConfig.PROVIDER_OPENAI;
    }

    @Override
    public int getDimensions() {
        return dimensions;
    }

    @Override
    public int getMaxTokens() {
        return maxTokens;
    }

    @Override
    public List<Float> embed(String text) {
        if (!available) {
            throw new IllegalStateException("OpenAI embedding model not available: API key not configured");
        }

        log.debug("Calling OpenAI embedding API for model: {}", id);

        log.warn("OpenAiEmbeddingModel.embed() - OpenAI API integration required. " +
                 "Please add openai-java or httpclient dependency and implement this method. " +
                 "API endpoint: {}/embeddings", config.getBaseUrl());

        return generateMockEmbedding(dimensions);
    }

    @Override
    public List<List<Float>> embedBatch(List<String> texts) {
        if (!available) {
            throw new IllegalStateException("OpenAI embedding model not available: API key not configured");
        }

        log.debug("Calling OpenAI batch embedding API for model: {}, count: {}", id, texts.size());

        log.warn("OpenAiEmbeddingModel.embedBatch() - OpenAI API integration required. " +
                 "Please add openai-java or httpclient dependency and implement this method. " +
                 "API endpoint: {}/embeddings", config.getBaseUrl());

        List<List<Float>> results = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            results.add(generateMockEmbedding(dimensions));
        }
        return results;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public EmbeddingModelInfo getInfo() {
        EmbeddingModelInfo info = new EmbeddingModelInfo(id, name, getProvider(), dimensions, maxTokens);
        info.setAvailable(available);
        info.setDescription("OpenAI text embedding model");
        return info;
    }

    private List<Float> generateMockEmbedding(int dim) {
        List<Float> embedding = new ArrayList<>();
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < dim; i++) {
            embedding.add((float) random.nextGaussian());
        }
        return embedding;
    }
}
