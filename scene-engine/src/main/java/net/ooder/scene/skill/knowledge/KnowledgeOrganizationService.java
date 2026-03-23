package net.ooder.scene.skill.knowledge;

import net.ooder.scene.org.OrgCompany;
import net.ooder.scene.org.OrgDepartment;
import net.ooder.scene.org.OrgUser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface KnowledgeOrganizationService {

    KnowledgeOrganization create(KnowledgeOrganization organization);

    KnowledgeOrganization get(String orgId);

    KnowledgeOrganization update(String orgId, KnowledgeOrganization organization);

    void delete(String orgId);

    List<KnowledgeOrganization> listAll();

    List<KnowledgeOrganization> listByType(String type);

    List<KnowledgeOrganization> listByParent(String parentId);

    List<KnowledgeOrganization> listChildren(String orgId);

    void addKnowledgeBase(String orgId, String kbId);

    void removeKnowledgeBase(String orgId, String kbId);

    List<KnowledgeOrganization> findByKnowledgeBase(String kbId);

    void moveOrganization(String orgId, String newParentId);

    void updateSort(String orgId, int sort);

    int countByType(String type);

    boolean exists(String orgId);

    List<KnowledgeOrganization> getCompanyOrganizations(String companyId);

    List<KnowledgeOrganization> getDepartmentOrganizations(String departmentId);

    KnowledgeOrganization getOrCreateCompanyOrganization(OrgCompany company);

    KnowledgeOrganization getOrCreateDepartmentOrganization(OrgDepartment department);

    void syncFromOrganizationService();

    CompletableFuture<List<OrgUser>> getMembers(String orgId);

    CompletableFuture<Boolean> hasAccess(String orgId, String userId);

    CompletableFuture<List<String>> getAccessibleKnowledgeBases(String userId);

    int getTotalDocs(String orgId);

    SyncResult syncFromOrgService();

    class SyncResult {
        private boolean success;
        private int syncedCompanies;
        private int syncedDepartments;
        private String message;

        public static SyncResult success(int companies, int departments) {
            SyncResult result = new SyncResult();
            result.success = true;
            result.syncedCompanies = companies;
            result.syncedDepartments = departments;
            result.message = String.format("同步完成: %d 个公司, %d 个部门", companies, departments);
            return result;
        }

        public static SyncResult failure(String message) {
            SyncResult result = new SyncResult();
            result.success = false;
            result.message = message;
            return result;
        }

        public boolean isSuccess() { return success; }
        public int getSyncedCompanies() { return syncedCompanies; }
        public int getSyncedDepartments() { return syncedDepartments; }
        public String getMessage() { return message; }
    }
}
