package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Enterprise Procedure Update Request Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface EnterpriseProcedureUpdateRequest extends Serializable {

    String getName();
    void setName(String name);

    String getCategory();
    void setCategory(String category);

    String getDescription();
    void setDescription(String description);

    List<String> getTags();
    void setTags(List<String> tags);

    ProcedureStatus getStatus();
    void setStatus(ProcedureStatus status);

    List<String> getDepartmentIds();
    void setDepartmentIds(List<String> departmentIds);

    List<ProcedureRole> getRoles();
    void setRoles(List<ProcedureRole> roles);

    List<ProcedureStep> getSteps();
    void setSteps(List<ProcedureStep> steps);

    List<ProcedureRule> getRules();
    void setRules(List<ProcedureRule> rules);

    List<String> getRequiredCapabilities();
    void setRequiredCapabilities(List<String> requiredCapabilities);

    List<String> getOptionalCapabilities();
    void setOptionalCapabilities(List<String> optionalCapabilities);

    List<String> getKnowledgeBaseIds();
    void setKnowledgeBaseIds(List<String> knowledgeBaseIds);

    Map<String, Object> getExtensions();
    void setExtensions(Map<String, Object> extensions);
}
