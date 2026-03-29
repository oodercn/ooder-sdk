package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;

/**
 * Conflict Resolution Item Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface ConflictResolutionItem extends Serializable {

    String getConflictId();
    void setConflictId(String conflictId);

    ConflictResolution getResolution();
    void setResolution(ConflictResolution resolution);

    Object getResolvedValue();
    void setResolvedValue(Object resolvedValue);

    String getComment();
    void setComment(String comment);
}
