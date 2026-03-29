package net.ooder.scene.procedure;

import net.ooder.sdk.api.procedure.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业规范流程实体实现
 *
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class EnterpriseProcedureEntity implements EnterpriseProcedure {

    private static final long serialVersionUID = 1L;

    private String procedureId;
    private String name;
    private String category;
    private String description;
    private List<String> tags = new ArrayList<>();
    private ProcedureSource source;
    private SourceMetadata sourceMetadata;
    private ProcedureStatus status = ProcedureStatus.DRAFT;
    private int completeness;
    private String organizationId;
    private List<String> departmentIds = new ArrayList<>();
    private List<ProcedureRole> roles = new ArrayList<>();
    private List<ProcedureStep> steps = new ArrayList<>();
    private List<ProcedureRule> rules = new ArrayList<>();
    private List<String> requiredCapabilities = new ArrayList<>();
    private List<String> optionalCapabilities = new ArrayList<>();
    private List<String> knowledgeBaseIds = new ArrayList<>();
    private Long createTime;
    private Long updateTime;
    private String author;
    private String version = "1.0";
    private Map<String, Object> extensions = new HashMap<>();

    public EnterpriseProcedureEntity() {
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    @Override
    public String getProcedureId() {
        return procedureId;
    }

    @Override
    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
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
    public ProcedureSource getSource() {
        return source;
    }

    @Override
    public void setSource(ProcedureSource source) {
        this.source = source;
    }

    @Override
    public SourceMetadata getSourceMetadata() {
        return sourceMetadata;
    }

    @Override
    public void setSourceMetadata(SourceMetadata sourceMetadata) {
        this.sourceMetadata = sourceMetadata;
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
    public int getCompleteness() {
        return completeness;
    }

    @Override
    public void setCompleteness(int completeness) {
        this.completeness = completeness;
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
    public Long getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public Long getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
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
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    public void touch() {
        this.updateTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "EnterpriseProcedureEntity{" +
                "procedureId='" + procedureId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", status=" + status +
                ", completeness=" + completeness +
                '}';
    }
}
