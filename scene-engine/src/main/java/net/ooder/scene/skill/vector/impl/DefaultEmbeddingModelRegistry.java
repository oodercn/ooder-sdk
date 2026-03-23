package net.ooder.scene.skill.vector.impl;

import net.ooder.scene.skill.vector.EmbeddingModel;
import net.ooder.scene.skill.vector.EmbeddingModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultEmbeddingModelRegistry implements EmbeddingModelRegistry {

    private static final Logger log = LoggerFactory.getLogger(DefaultEmbeddingModelRegistry.class);

    private final Map<String, EmbeddingModel> models = new ConcurrentHashMap<>();
    private volatile String defaultModelId;

    @Override
    public void register(EmbeddingModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }

        String modelId = model.getId();
        if (modelId == null || modelId.isEmpty()) {
            throw new IllegalArgumentException("Model ID cannot be null or empty");
        }

        models.put(modelId, model);

        if (defaultModelId == null) {
            defaultModelId = modelId;
        }

        log.info("Registered embedding model: {} ({})", modelId, model.getName());
    }

    @Override
    public void unregister(String modelId) {
        if (modelId == null) {
            return;
        }

        EmbeddingModel removed = models.remove(modelId);

        if (removed != null) {
            log.info("Unregistered embedding model: {}", modelId);

            if (modelId.equals(defaultModelId)) {
                defaultModelId = models.isEmpty() ? null : models.keySet().iterator().next();
                if (defaultModelId != null) {
                    log.info("Default model changed to: {}", defaultModelId);
                }
            }
        }
    }

    @Override
    public EmbeddingModel getModel(String modelId) {
        if (modelId == null) {
            return getDefaultModel();
        }
        return models.get(modelId);
    }

    @Override
    public List<EmbeddingModel> getAllModels() {
        return new ArrayList<>(models.values());
    }

    @Override
    public List<EmbeddingModel> getModelsByProvider(String provider) {
        return models.values().stream()
                .filter(m -> provider.equals(m.getProvider()))
                .collect(Collectors.toList());
    }

    @Override
    public EmbeddingModel getDefaultModel() {
        if (defaultModelId == null) {
            throw new IllegalStateException("No embedding models registered");
        }
        return models.get(defaultModelId);
    }

    @Override
    public void setDefaultModel(String modelId) {
        if (modelId == null) {
            throw new IllegalArgumentException("Model ID cannot be null");
        }

        if (!models.containsKey(modelId)) {
            throw new IllegalArgumentException("Model not found: " + modelId);
        }

        this.defaultModelId = modelId;
        log.info("Default embedding model set to: {}", modelId);
    }

    @Override
    public boolean hasModel(String modelId) {
        return modelId != null && models.containsKey(modelId);
    }

    @Override
    public int getModelCount() {
        return models.size();
    }
}
