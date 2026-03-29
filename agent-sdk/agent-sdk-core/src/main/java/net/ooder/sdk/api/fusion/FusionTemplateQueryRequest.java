package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;

/**
 * Fusion Template Query Request Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface FusionTemplateQueryRequest extends Serializable {

    String getSkillId();
    void setSkillId(String skillId);

    String getProcedureId();
    void setProcedureId(String procedureId);

    TemplateStatus getStatus();
    void setStatus(TemplateStatus status);

    String getKeyword();
    void setKeyword(String keyword);

    int getPage();
    void setPage(int page);

    int getPageSize();
    void setPageSize(int pageSize);
}
