package net.ooder.scene.procedure;

import net.ooder.sdk.api.procedure.SourceMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * 来源元数据实体实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class SourceMetadataEntity implements SourceMetadata {

    private static final long serialVersionUID = 1L;

    private String sourceType;
    private String sourceId;
    private String sourceName;
    private Long extractedAt;
    private Double confidence;
    private Map<String, Object> raw = new HashMap<>();

    public SourceMetadataEntity() {
    }

    @Override
    public String getSourceType() {
        return sourceType;
    }

    @Override
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public String getSourceId() {
        return sourceId;
    }

    @Override
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public Long getExtractedAt() {
        return extractedAt;
    }

    @Override
    public void setExtractedAt(Long extractedAt) {
        this.extractedAt = extractedAt;
    }

    @Override
    public Double getConfidence() {
        return confidence;
    }

    @Override
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    @Override
    public Map<String, Object> getRaw() {
        return raw;
    }

    @Override
    public void setRaw(Map<String, Object> raw) {
        this.raw = raw != null ? raw : new HashMap<>();
    }

    @Override
    public String toString() {
        return "SourceMetadataEntity{" +
                "sourceType='" + sourceType + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
