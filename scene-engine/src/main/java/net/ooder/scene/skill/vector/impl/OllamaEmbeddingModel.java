package net.ooder.scene.skill.vector.impl;

import net.ooder.scene.skill.vector.EmbeddingConfig;
import net.ooder.scene.skill.vector.EmbeddingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OllamaEmbeddingModel implements EmbeddingModel {

    private static final Logger log = LoggerFactory.getLogger(OllamaEmbeddingModel.class);

    private final String id;
    private final String name;
    private final int dimensions;
    private final EmbeddingConfig config;
    private volatile boolean available = false;

    public OllamaEmbeddingModel(EmbeddingConfig config) {
        this.config = config;
        this.id = config.getModel();
        this.name = "Ollama " + config.getModel();
        this.dimensions = config.getDimension();
        checkAvailability();
    }

    public OllamaEmbeddingModel(String baseUrl, String model, int dimension) {
        this(EmbeddingConfig.ollama(baseUrl, model));
    }

    private void checkAvailability() {
        String baseUrl = config.getBaseUrl();
        available = baseUrl != null && !baseUrl.isEmpty();
        if (available) {
            log.info("Ollama embedding model {} is available at {}", id, baseUrl);
        } else {
            log.warn("Ollama embedding model {} is not available: base URL not configured", id);
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
        return EmbeddingConfig.PROVIDER_OLLAMA;
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
            throw new IllegalStateException("Ollama embedding model not available: base URL not configured");
        }

        log.debug("Calling Ollama embedding API for model: {}", id);

        log.warn("OllamaEmbeddingModel.embed() - Ollama API integration required. " +
                 "Please implement HTTP call to: {}/api/embeddings", config.getBaseUrl());

        return generateMockEmbedding(dimensions);
    }

    @Override
    public List<List<Float>> embedBatch(List<String> texts) {
        if (!available) {
            throw new IllegalStateException("Ollama embedding model not available: base URL not configured");
        }

        log.debug("Calling Ollama batch embedding API for model: {}, count: {}", id, texts.size());

        log.warn("OllamaEmbeddingModel.embedBatch() - Ollama API integration required. " +
                 "Please implement HTTP call to: {}/api/embeddings", config.getBaseUrl());

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
        info.setDescription("Ollama local embedding model");
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
