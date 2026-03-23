package net.ooder.scene.skill.vector.impl;

import net.ooder.scene.skill.vector.EmbeddingConfig;
import net.ooder.scene.skill.vector.EmbeddingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DashScopeEmbeddingModel implements EmbeddingModel {

    private static final Logger log = LoggerFactory.getLogger(DashScopeEmbeddingModel.class);

    private final String id;
    private final String name;
    private final int dimensions;
    private final EmbeddingConfig config;
    private volatile boolean available = false;

    public DashScopeEmbeddingModel(EmbeddingConfig config) {
        this.config = config;
        this.id = config.getModel() != null ? config.getModel() : "text-embedding-v2";
        this.name = "DashScope " + id;
        this.dimensions = config.getDimension();
        checkAvailability();
    }

    public DashScopeEmbeddingModel(String apiKey, String model) {
        this(EmbeddingConfig.dashscope(apiKey, model));
    }

    private void checkAvailability() {
        available = config.getApiKey() != null && !config.getApiKey().isEmpty();
        if (available) {
            log.info("DashScope embedding model {} is available", id);
        } else {
            log.warn("DashScope embedding model {} is not available: API key not configured", id);
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
        return EmbeddingConfig.PROVIDER_DASHSCOPE;
    }

    @Override
    public int getDimensions() {
        return dimensions;
    }

    @Override
    public int getMaxTokens() {
        return 8192;
    }

    @Override
    public List<Float> embed(String text) {
        if (!available) {
            throw new IllegalStateException("DashScope embedding model not available: API key not configured");
        }

        log.debug("Calling DashScope embedding API for model: {}", id);

        log.warn("DashScopeEmbeddingModel.embed() - DashScope API integration required. " +
                 "Please implement HTTP call to: {}/services/embeddings/text-embedding/text-embedding", 
                 config.getBaseUrl());

        return generateMockEmbedding(dimensions);
    }

    @Override
    public List<List<Float>> embedBatch(List<String> texts) {
        if (!available) {
            throw new IllegalStateException("DashScope embedding model not available: API key not configured");
        }

        log.debug("Calling DashScope batch embedding API for model: {}, count: {}", id, texts.size());

        log.warn("DashScopeEmbeddingModel.embedBatch() - DashScope API integration required. " +
                 "Please implement HTTP call to: {}/services/embeddings/text-embedding/text-embedding", 
                 config.getBaseUrl());

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
        EmbeddingModelInfo info = new EmbeddingModelInfo(id, name, getProvider(), dimensions, getMaxTokens());
        info.setAvailable(available);
        info.setDescription("Alibaba DashScope (通义千问) embedding model");
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
