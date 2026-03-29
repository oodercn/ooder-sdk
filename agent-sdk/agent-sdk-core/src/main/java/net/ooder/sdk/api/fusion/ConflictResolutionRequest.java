package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;

/**
 * Conflict Resolution Request Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface ConflictResolutionRequest extends Serializable {

    List<ConflictResolutionItem> getResolutions();
    void setResolutions(List<ConflictResolutionItem> resolutions);

    String getResolvedBy();
    void setResolvedBy(String resolvedBy);
}
