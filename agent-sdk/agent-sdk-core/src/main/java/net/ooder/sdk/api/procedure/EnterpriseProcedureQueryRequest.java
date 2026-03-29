package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;

/**
 * Enterprise Procedure Query Request Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface EnterpriseProcedureQueryRequest extends Serializable {

    String getOrganizationId();
    void setOrganizationId(String organizationId);

    String getCategory();
    void setCategory(String category);

    ProcedureStatus getStatus();
    void setStatus(ProcedureStatus status);

    ProcedureSource getSource();
    void setSource(ProcedureSource source);

    String getKeyword();
    void setKeyword(String keyword);

    int getMinCompleteness();
    void setMinCompleteness(int minCompleteness);

    int getPage();
    void setPage(int page);

    int getPageSize();
    void setPageSize(int pageSize);
}
