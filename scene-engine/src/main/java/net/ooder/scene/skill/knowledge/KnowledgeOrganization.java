package net.ooder.scene.skill.knowledge;

import net.ooder.scene.org.OrgCompany;
import net.ooder.scene.org.OrgDepartment;

import java.util.List;

public class KnowledgeOrganization {

    public static final String TYPE_COMPANY = "company";
    public static final String TYPE_DEPARTMENT = "department";
    public static final String TYPE_BUSINESS = "business";

    private String orgId;
    private String name;
    private String type;
    private String parentId;
    private String companyId;
    private String departmentId;
    private int sort;
    private String description;
    private List<String> kbIds;
    private long createdAt;
    private long updatedAt;

    private OrgCompany company;
    private OrgDepartment department;

    public KnowledgeOrganization() {
        this.sort = 0;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public KnowledgeOrganization(String orgId, String name, String type) {
        this();
        this.orgId = orgId;
        this.name = name;
        this.type = type;
    }

    public static KnowledgeOrganization fromCompany(OrgCompany company) {
        KnowledgeOrganization org = new KnowledgeOrganization();
        org.setOrgId("company_" + company.getCompanyId());
        org.setName(company.getName());
        org.setType(TYPE_COMPANY);
        org.setCompanyId(company.getCompanyId());
        org.setCompany(company);
        org.setDescription("公司级知识库");
        return org;
    }

    public static KnowledgeOrganization fromDepartment(OrgDepartment department) {
        KnowledgeOrganization org = new KnowledgeOrganization();
        org.setOrgId("dept_" + department.getDepartmentId());
        org.setName(department.getName());
        org.setType(TYPE_DEPARTMENT);
        org.setDepartmentId(department.getDepartmentId());
        org.setCompanyId(department.getCompanyId());
        org.setParentId(department.getParentId() != null ? "dept_" + department.getParentId() : null);
        org.setDepartment(department);
        org.setDescription("部门级知识库");
        return org;
    }

    public static KnowledgeOrganization createBusiness(String orgId, String name, String parentId) {
        KnowledgeOrganization org = new KnowledgeOrganization(orgId, name, TYPE_BUSINESS);
        org.setParentId(parentId);
        org.setDescription("专用业务知识库");
        return org;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKbIds() {
        return kbIds;
    }

    public void setKbIds(List<String> kbIds) {
        this.kbIds = kbIds;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OrgCompany getCompany() {
        return company;
    }

    public void setCompany(OrgCompany company) {
        this.company = company;
    }

    public OrgDepartment getDepartment() {
        return department;
    }

    public void setDepartment(OrgDepartment department) {
        this.department = department;
    }

    public boolean isCompanyLevel() {
        return TYPE_COMPANY.equals(type);
    }

    public boolean isDepartmentLevel() {
        return TYPE_DEPARTMENT.equals(type);
    }

    public boolean isBusinessLevel() {
        return TYPE_BUSINESS.equals(type);
    }

    public boolean hasKnowledgeBase(String kbId) {
        return kbIds != null && kbIds.contains(kbId);
    }

    public void addKnowledgeBase(String kbId) {
        if (kbIds == null) {
            kbIds = new java.util.ArrayList<>();
        }
        if (!kbIds.contains(kbId)) {
            kbIds.add(kbId);
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public void removeKnowledgeBase(String kbId) {
        if (kbIds != null) {
            kbIds.remove(kbId);
            this.updatedAt = System.currentTimeMillis();
        }
    }
}
