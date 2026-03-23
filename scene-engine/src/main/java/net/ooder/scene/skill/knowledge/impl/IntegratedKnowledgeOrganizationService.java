package net.ooder.scene.skill.knowledge.impl;

import net.ooder.scene.org.*;
import net.ooder.scene.skill.knowledge.KnowledgeOrganization;
import net.ooder.scene.skill.knowledge.KnowledgeOrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class IntegratedKnowledgeOrganizationService implements KnowledgeOrganizationService {

    private static final Logger log = LoggerFactory.getLogger(IntegratedKnowledgeOrganizationService.class);

    private final OrganizationService organizationService;
    private final Map<String, KnowledgeOrganization> knowledgeOrganizations = new ConcurrentHashMap<>();

    public IntegratedKnowledgeOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
        initializeDefaultOrganizations();
    }

    private void initializeDefaultOrganizations() {
        KnowledgeOrganization company = new KnowledgeOrganization("org-company", "公司级", KnowledgeOrganization.TYPE_COMPANY);
        company.setDescription("全公司共享知识");
        company.setSort(1);
        knowledgeOrganizations.put(company.getOrgId(), company);

        KnowledgeOrganization dept = new KnowledgeOrganization("org-department", "部门级", KnowledgeOrganization.TYPE_DEPARTMENT);
        dept.setDescription("部门内共享知识");
        dept.setSort(2);
        knowledgeOrganizations.put(dept.getOrgId(), dept);

        KnowledgeOrganization business = new KnowledgeOrganization("org-business", "专用业务", KnowledgeOrganization.TYPE_BUSINESS);
        business.setDescription("特定业务场景知识");
        business.setSort(3);
        knowledgeOrganizations.put(business.getOrgId(), business);

        log.info("Initialized default knowledge organizations: company, department, business");
    }

    @Override
    public void syncFromOrganizationService() {
        syncFromOrgService();
    }

    @Override
    public SyncResult syncFromOrgService() {
        if (organizationService == null) {
            log.warn("OrganizationService not available, skipping sync");
            return SyncResult.failure("OrganizationService not available");
        }

        log.info("Syncing knowledge organizations from OrganizationService");

        try {
            List<OrgCompany> companies = organizationService.listCompanies(new CompanyQuery()).join();
            int companyCount = 0;
            int departmentCount = 0;

            for (OrgCompany company : companies) {
                getOrCreateCompanyOrganization(company);
                companyCount++;
            }

            for (OrgCompany company : companies) {
                List<OrgDepartment> departments = organizationService.getDepartmentTree(company.getCompanyId()).join();
                for (OrgDepartment dept : departments) {
                    getOrCreateDepartmentOrganization(dept);
                    departmentCount++;
                }
            }

            log.info("Synced {} companies and {} departments", companyCount, departmentCount);
            return SyncResult.success(companyCount, departmentCount);
        } catch (Exception e) {
            log.error("Failed to sync from OrganizationService: {}", e.getMessage(), e);
            return SyncResult.failure("同步失败: " + e.getMessage());
        }
    }

    @Override
    public List<KnowledgeOrganization> getCompanyOrganizations(String companyId) {
        List<KnowledgeOrganization> result = new ArrayList<>();

        for (KnowledgeOrganization org : knowledgeOrganizations.values()) {
            if (companyId.equals(org.getCompanyId())) {
                result.add(org);
            }
        }

        return result.stream()
                .sorted((a, b) -> Integer.compare(a.getSort(), b.getSort()))
                .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeOrganization> getDepartmentOrganizations(String departmentId) {
        List<KnowledgeOrganization> result = new ArrayList<>();

        for (KnowledgeOrganization org : knowledgeOrganizations.values()) {
            if (departmentId.equals(org.getDepartmentId())) {
                result.add(org);
            }
        }

        return result;
    }

    @Override
    public KnowledgeOrganization getOrCreateCompanyOrganization(OrgCompany company) {
        String orgId = "company_" + company.getCompanyId();

        KnowledgeOrganization existing = knowledgeOrganizations.get(orgId);
        if (existing != null) {
            existing.setCompany(company);
            existing.setName(company.getName());
            return existing;
        }

        KnowledgeOrganization org = KnowledgeOrganization.fromCompany(company);
        knowledgeOrganizations.put(orgId, org);
        log.info("Created knowledge organization for company: {}", company.getName());
        return org;
    }

    @Override
    public KnowledgeOrganization getOrCreateDepartmentOrganization(OrgDepartment department) {
        String orgId = "dept_" + department.getDepartmentId();

        KnowledgeOrganization existing = knowledgeOrganizations.get(orgId);
        if (existing != null) {
            existing.setDepartment(department);
            existing.setName(department.getName());
            return existing;
        }

        KnowledgeOrganization org = KnowledgeOrganization.fromDepartment(department);
        knowledgeOrganizations.put(orgId, org);
        log.info("Created knowledge organization for department: {}", department.getName());
        return org;
    }

    @Override
    public CompletableFuture<List<OrgUser>> getMembers(String orgId) {
        if (organizationService == null) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        KnowledgeOrganization org = get(orgId);
        if (org == null) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        if (org.isCompanyLevel() && org.getCompanyId() != null) {
            UserQuery query = new UserQuery();
            query.setCompanyId(org.getCompanyId());
            return organizationService.listUsers(query);
        }

        if (org.isDepartmentLevel() && org.getDepartmentId() != null) {
            return organizationService.getDepartmentMembers(org.getDepartmentId());
        }

        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    @Override
    public CompletableFuture<Boolean> hasAccess(String orgId, String userId) {
        if (orgId == null || userId == null) {
            return CompletableFuture.completedFuture(false);
        }

        KnowledgeOrganization org = get(orgId);
        if (org == null) {
            return CompletableFuture.completedFuture(false);
        }

        if ("org-company".equals(orgId)) {
            return CompletableFuture.completedFuture(true);
        }

        return getMembers(orgId).thenApply(members -> {
            for (OrgUser user : members) {
                if (userId.equals(user.getUserId())) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<List<String>> getAccessibleKnowledgeBases(String userId) {
        if (userId == null) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        List<String> kbIds = new ArrayList<>();

        for (KnowledgeOrganization org : knowledgeOrganizations.values()) {
            if (org.getKbIds() != null) {
                if ("org-company".equals(org.getOrgId())) {
                    kbIds.addAll(org.getKbIds());
                }
            }
        }

        return CompletableFuture.completedFuture(kbIds);
    }

    @Override
    public int getTotalDocs(String orgId) {
        KnowledgeOrganization org = get(orgId);
        if (org == null || org.getKbIds() == null) {
            return 0;
        }

        return org.getKbIds().size() * 10;
    }

    @Override
    public KnowledgeOrganization create(KnowledgeOrganization organization) {
        if (organization == null) {
            throw new IllegalArgumentException("Organization cannot be null");
        }

        String orgId = organization.getOrgId();
        if (orgId == null || orgId.isEmpty()) {
            orgId = "org_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            organization.setOrgId(orgId);
        }

        if (knowledgeOrganizations.containsKey(orgId)) {
            throw new IllegalArgumentException("Organization already exists: " + orgId);
        }

        organization.setCreatedAt(System.currentTimeMillis());
        organization.setUpdatedAt(System.currentTimeMillis());

        knowledgeOrganizations.put(orgId, organization);
        log.info("Created knowledge organization: {} ({})", orgId, organization.getName());

        return organization;
    }

    @Override
    public KnowledgeOrganization get(String orgId) {
        if (orgId == null) {
            return null;
        }
        return knowledgeOrganizations.get(orgId);
    }

    @Override
    public KnowledgeOrganization update(String orgId, KnowledgeOrganization organization) {
        if (orgId == null || organization == null) {
            throw new IllegalArgumentException("OrgId and organization cannot be null");
        }

        KnowledgeOrganization existing = knowledgeOrganizations.get(orgId);
        if (existing == null) {
            throw new IllegalArgumentException("Organization not found: " + orgId);
        }

        if (organization.getName() != null) {
            existing.setName(organization.getName());
        }
        if (organization.getType() != null) {
            existing.setType(organization.getType());
        }
        if (organization.getParentId() != null) {
            existing.setParentId(organization.getParentId());
        }
        if (organization.getSort() != 0) {
            existing.setSort(organization.getSort());
        }
        if (organization.getDescription() != null) {
            existing.setDescription(organization.getDescription());
        }
        if (organization.getKbIds() != null) {
            existing.setKbIds(organization.getKbIds());
        }

        existing.setUpdatedAt(System.currentTimeMillis());

        log.info("Updated knowledge organization: {}", orgId);
        return existing;
    }

    @Override
    public void delete(String orgId) {
        if (orgId == null) {
            return;
        }

        if (orgId.startsWith("company_") || orgId.startsWith("dept_")) {
            log.warn("Cannot delete organization synced from OrganizationService: {}", orgId);
            return;
        }

        KnowledgeOrganization removed = knowledgeOrganizations.remove(orgId);
        if (removed != null) {
            log.info("Deleted knowledge organization: {}", orgId);
        }
    }

    @Override
    public List<KnowledgeOrganization> listAll() {
        return knowledgeOrganizations.values().stream()
                .sorted((a, b) -> Integer.compare(a.getSort(), b.getSort()))
                .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeOrganization> listByType(String type) {
        if (type == null) {
            return new ArrayList<>();
        }

        return knowledgeOrganizations.values().stream()
                .filter(org -> type.equals(org.getType()))
                .sorted((a, b) -> Integer.compare(a.getSort(), b.getSort()))
                .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeOrganization> listByParent(String parentId) {
        return knowledgeOrganizations.values().stream()
                .filter(org -> parentId == null ? org.getParentId() == null : parentId.equals(org.getParentId()))
                .sorted((a, b) -> Integer.compare(a.getSort(), b.getSort()))
                .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeOrganization> listChildren(String orgId) {
        if (orgId == null) {
            return new ArrayList<>();
        }

        return knowledgeOrganizations.values().stream()
                .filter(org -> orgId.equals(org.getParentId()))
                .sorted((a, b) -> Integer.compare(a.getSort(), b.getSort()))
                .collect(Collectors.toList());
    }

    @Override
    public void addKnowledgeBase(String orgId, String kbId) {
        if (orgId == null || kbId == null) {
            return;
        }

        KnowledgeOrganization org = knowledgeOrganizations.get(orgId);
        if (org != null) {
            org.addKnowledgeBase(kbId);
            log.info("Added knowledge base {} to organization {}", kbId, orgId);
        }
    }

    @Override
    public void removeKnowledgeBase(String orgId, String kbId) {
        if (orgId == null || kbId == null) {
            return;
        }

        KnowledgeOrganization org = knowledgeOrganizations.get(orgId);
        if (org != null) {
            org.removeKnowledgeBase(kbId);
            log.info("Removed knowledge base {} from organization {}", kbId, orgId);
        }
    }

    @Override
    public List<KnowledgeOrganization> findByKnowledgeBase(String kbId) {
        if (kbId == null) {
            return new ArrayList<>();
        }

        return knowledgeOrganizations.values().stream()
                .filter(org -> org.hasKnowledgeBase(kbId))
                .collect(Collectors.toList());
    }

    @Override
    public void moveOrganization(String orgId, String newParentId) {
        if (orgId == null) {
            return;
        }

        KnowledgeOrganization org = knowledgeOrganizations.get(orgId);
        if (org != null) {
            org.setParentId(newParentId);
            org.setUpdatedAt(System.currentTimeMillis());
            log.info("Moved organization {} to parent {}", orgId, newParentId);
        }
    }

    @Override
    public void updateSort(String orgId, int sort) {
        if (orgId == null) {
            return;
        }

        KnowledgeOrganization org = knowledgeOrganizations.get(orgId);
        if (org != null) {
            org.setSort(sort);
            org.setUpdatedAt(System.currentTimeMillis());
            log.info("Updated sort for organization {} to {}", orgId, sort);
        }
    }

    @Override
    public int countByType(String type) {
        if (type == null) {
            return 0;
        }

        return (int) knowledgeOrganizations.values().stream()
                .filter(org -> type.equals(org.getType()))
                .count();
    }

    @Override
    public boolean exists(String orgId) {
        return orgId != null && knowledgeOrganizations.containsKey(orgId);
    }
}
