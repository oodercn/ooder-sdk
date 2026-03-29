package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;

/**
 * Template Version Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface TemplateVersion extends Serializable {

    int getVersion();
    void setVersion(int version);

    String getDescription();
    void setDescription(String description);

    Long getCreateTime();
    void setCreateTime(Long createTime);

    String getCreatedBy();
    void setCreatedBy(String createdBy);
}
