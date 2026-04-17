package net.ooder.spi.llm;

import net.ooder.spi.llm.model.*;

import java.util.List;

public interface LlmManager {

    String getSkillId();

    List<LlmProviderDTO> getProviders();

    List<LlmModelDTO> getModels();

    LlmModelDTO getModel(String modelId);

    LlmConfigDTO getConfig();

    void updateConfig(LlmConfigDTO config);

    String getDefaultProvider();

    String getDefaultModel();
}
