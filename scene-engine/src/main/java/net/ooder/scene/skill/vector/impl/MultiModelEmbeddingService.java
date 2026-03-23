package net.ooder.scene.skill.vector.impl;

import net.ooder.scene.skill.vector.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MultiModelEmbeddingService implements SceneEmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(MultiModelEmbeddingService.class);

    private final EmbeddingModelRegistry registry;
    private volatile String currentModelId;

    public MultiModelEmbeddingService(EmbeddingModelRegistry registry) {
        this.registry = registry;
    }

    public MultiModelEmbeddingService(EmbeddingModelRegistry registry, String defaultModelId) {
        this.registry = registry;
        this.currentModelId = defaultModelId;
    }

    public void setCurrentModel(String modelId) {
        if (!registry.hasModel(modelId)) {
            throw new IllegalArgumentException("Model not found: " + modelId);
        }
        this.currentModelId = modelId;
        log.info("Switched to embedding model: {}", modelId);
    }

    public String getCurrentModelId() {
        return currentModelId != null ? currentModelId : registry.getDefaultModel().getId();
    }

    private EmbeddingModel getCurrentModel() {
        if (currentModelId != null) {
            EmbeddingModel model = registry.getModel(currentModelId);
            if (model != null) {
                return model;
            }
        }
        return registry.getDefaultModel();
    }

    @Override
    public float[] embed(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        EmbeddingModel model = getCurrentModel();
        log.debug("Embedding text with model: {}, length: {}", model.getId(), text.length());

        try {
            List<Float> embedding = model.embed(text);
            return toFloatArray(embedding);
        } catch (Exception e) {
            log.error("Failed to embed text with model {}: {}", model.getId(), e.getMessage(), e);
            throw new RuntimeException("Embedding failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }

        EmbeddingModel model = getCurrentModel();
        log.debug("Batch embedding {} texts with model: {}", texts.size(), model.getId());

        try {
            List<List<Float>> embeddings = model.embedBatch(texts);
            List<float[]> results = new ArrayList<>();
            for (List<Float> embedding : embeddings) {
                results.add(toFloatArray(embedding));
            }
            return results;
        } catch (Exception e) {
            log.error("Failed to batch embed texts with model {}: {}", model.getId(), e.getMessage(), e);
            throw new RuntimeException("Batch embedding failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int getDimension() {
        return getCurrentModel().getDimensions();
    }

    @Override
    public String getModel() {
        return getCurrentModel().getId();
    }

    public EmbeddingModelRegistry getRegistry() {
        return registry;
    }

    public List<EmbeddingModel.EmbeddingModelInfo> getAvailableModels() {
        List<EmbeddingModel.EmbeddingModelInfo> infos = new ArrayList<>();
        for (EmbeddingModel model : registry.getAllModels()) {
            infos.add(model.getInfo());
        }
        return infos;
    }

    public EmbeddingTestResult testModel(String modelId) {
        EmbeddingModel model = registry.getModel(modelId);
        if (model == null) {
            return EmbeddingTestResult.failure("Model not found: " + modelId);
        }

        try {
            String testText = "This is a test sentence for embedding.";
            long startTime = System.currentTimeMillis();

            List<Float> embedding = model.embed(testText);

            long elapsed = System.currentTimeMillis() - startTime;

            float[] sampleVector = toFloatArray(embedding);
            float[] sample = new float[Math.min(10, sampleVector.length)];
            System.arraycopy(sampleVector, 0, sample, 0, sample.length);

            return EmbeddingTestResult.success(
                modelId,
                model.getDimensions(),
                sample,
                testText.length(),
                elapsed
            );
        } catch (Exception e) {
            log.error("Failed to test model {}: {}", modelId, e.getMessage(), e);
            return EmbeddingTestResult.failure(e.getMessage());
        }
    }

    private float[] toFloatArray(List<Float> list) {
        if (list == null) {
            return new float[0];
        }
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static class EmbeddingTestResult {
        private boolean success;
        private String modelId;
        private int dimensions;
        private float[] sampleVector;
        private int textLength;
        private long elapsed;
        private String errorMessage;

        private EmbeddingTestResult() {}

        public static EmbeddingTestResult success(String modelId, int dimensions, float[] sampleVector, int textLength, long elapsed) {
            EmbeddingTestResult result = new EmbeddingTestResult();
            result.success = true;
            result.modelId = modelId;
            result.dimensions = dimensions;
            result.sampleVector = sampleVector;
            result.textLength = textLength;
            result.elapsed = elapsed;
            return result;
        }

        public static EmbeddingTestResult failure(String errorMessage) {
            EmbeddingTestResult result = new EmbeddingTestResult();
            result.success = false;
            result.errorMessage = errorMessage;
            return result;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getModelId() {
            return modelId;
        }

        public int getDimensions() {
            return dimensions;
        }

        public float[] getSampleVector() {
            return sampleVector;
        }

        public int getTextLength() {
            return textLength;
        }

        public long getElapsed() {
            return elapsed;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
