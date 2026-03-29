package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.Map;

/**
 * Document Interface for LLM-assisted creation
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface Document extends Serializable {

    String getDocumentId();
    void setDocumentId(String documentId);

    String getTitle();
    void setTitle(String title);

    String getContent();
    void setContent(String content);

    String getType();
    void setType(String type);

    Map<String, Object> getMetadata();
    void setMetadata(Map<String, Object> metadata);
}
