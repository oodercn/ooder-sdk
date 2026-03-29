package net.ooder.scene.procedure;

import net.ooder.sdk.api.procedure.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnterpriseProcedureUpdateRequestEntity implements EnterpriseProcedureUpdateRequest {

    private static final long serialVersionUID = 1L;

    private String name;
    private String category;
    private String description;
    private List<String> tags = new ArrayList<>();
    private ProcedureStatus status;
    private List<String> departmentIds = new ArrayList<>();
    private List<ProcedureRole> roles = new ArrayList<>();
    private List<ProcedureStep> steps = new ArrayList<>();
    private List<ProcedureRule> rules = new ArrayList<>();
    private List<String> requiredCapabilities = new ArrayList<>();
    private List<String> optionalCapabilities = new ArrayList<>();
    private List<String> knowledgeBaseIds = new ArrayList<>();
    private Map<String, Object> extensions = new HashMap<>();

    public EnterpriseProcedureUpdateRequestEntity() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    @Override
    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    @Override
    public ProcedureStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ProcedureStatus status) {
        this.status = status;
    }

    @Override
    public List<String> getDepartmentIds() {
        return departmentIds;
    }

    @Override
    public void setDepartmentIds(List<String> departmentIds) {
        this.departmentIds = departmentIds != null ? departmentIds : new ArrayList<>();
    }

    @Override
    public List<ProcedureRole> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(List<ProcedureRole> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    @Override
    public List<ProcedureStep> getSteps() {
        return steps;
    }

    @Override
    public void setSteps(List<ProcedureStep> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    @Override
    public List<ProcedureRule> getRules() {
        return rules;
    }

    @Override
    public void setRules(List<ProcedureRule> rules) {
        this.rules = rules != null ? rules : new ArrayList<>();
    }

    @Override
    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }

    @Override
    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities != null ? requiredCapabilities : new ArrayList<>();
    }

    @Override
    public List<String> getOptionalCapabilities() {
        return optionalCapabilities;
    }

    @Override
    public void setOptionalCapabilities(List<String> optionalCapabilities) {
        this.optionalCapabilities = optionalCapabilities != null ? optionalCapabilities : new ArrayList<>();
    }

    @Override
    public List<String> getKnowledgeBaseIds() {
        return knowledgeBaseIds;
    }

    @Override
    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) {
        this.knowledgeBaseIds = knowledgeBaseIds != null ? knowledgeBaseIds : new ArrayList<>();
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }
}
