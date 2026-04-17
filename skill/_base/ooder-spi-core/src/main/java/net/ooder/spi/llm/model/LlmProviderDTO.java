package net.ooder.spi.llm.model;

import lombok.Data;
import java.util.List;

@Data
public class LlmProviderDTO {

    private String id;

    private String name;

    private String type;

    private boolean enabled;

    private List<LlmModelDTO> models;
}
