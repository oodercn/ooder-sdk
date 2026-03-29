package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;

/**
 * Fusion Conflict Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface FusionConflict extends Serializable {

    String getConflictId();
    void setConflictId(String conflictId);

    String getField();
    void setField(String field);

    ConflictType getType();
    void setType(ConflictType type);

    Object getEnterpriseValue();
    void setEnterpriseValue(Object enterpriseValue);

    Object getSkillValue();
    void setSkillValue(Object skillValue);

    ConflictResolution getResolution();
    void setResolution(ConflictResolution resolution);

    Object getResolvedValue();
    void setResolvedValue(Object resolvedValue);

    String getResolvedBy();
    void setResolvedBy(String resolvedBy);

    Long getResolvedAt();
    void setResolvedAt(Long resolvedAt);

    String getComment();
    void setComment(String comment);
}
