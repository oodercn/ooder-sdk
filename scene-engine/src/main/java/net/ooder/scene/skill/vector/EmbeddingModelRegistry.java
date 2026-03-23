package net.ooder.scene.skill.vector;

import java.util.List;

public interface EmbeddingModelRegistry {

    void register(EmbeddingModel model);

    void unregister(String modelId);

    EmbeddingModel getModel(String modelId);

    List<EmbeddingModel> getAllModels();

    List<EmbeddingModel> getModelsByProvider(String provider);

    EmbeddingModel getDefaultModel();

    void setDefaultModel(String modelId);

    boolean hasModel(String modelId);

    int getModelCount();
}
