package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.Map;

/**
 * Source Metadata Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface SourceMetadata extends Serializable {

    String getSourceType();
    void setSourceType(String sourceType);

    String getSourceId();
    void setSourceId(String sourceId);

    String getSourceName();
    void setSourceName(String sourceName);

    Long getExtractedAt();
    void setExtractedAt(Long extractedAt);

    Double getConfidence();
    void setConfidence(Double confidence);

    Map<String, Object> getRaw();
    void setRaw(Map<String, Object> raw);
}
