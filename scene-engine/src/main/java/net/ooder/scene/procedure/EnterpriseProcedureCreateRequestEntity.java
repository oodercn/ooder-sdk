package net.ooder.scene.procedure;

import net.ooder.sdk.api.procedure.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业规范流程创建请求实体实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class EnterpriseProcedureCreateRequestEntity implements EnterpriseProcedureCreateRequest {

    private static final long serialVersionUID = 1L;

    private String name;
    private String category;
    private String description;
    private List<String> tags = new ArrayList<>();
    private ProcedureSource source = ProcedureSource.MANUAL;
    private String organizationId;
    private List<String> departmentIds = new ArrayList<>();
    private List<ProcedureRole> roles = new ArrayList<>();
    private List<ProcedureStep> steps = new ArrayList<>();
    private List<ProcedureRule> rules = new ArrayList<>();
    private List<String> requiredCapabilities = new ArrayList<>();
    private List<String> knowledgeBaseIds = new ArrayList<>();
    private String author;
    private Map<String, Object> extensions = new HashMap<>();

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
    public ProcedureSource getSource() {
        return source;
    }

    @Override
    public void setSource(ProcedureSource source) {
        this.source = source;
    }

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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
    public List<String> getKnowledgeBaseIds() {
        return knowledgeBaseIds;
    }

    @Override
    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) {
        this.knowledgeBaseIds = knowledgeBaseIds != null ? knowledgeBaseIds : new ArrayList<>();
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
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
