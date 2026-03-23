package net.ooder.sdk.llm.token;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 配额范围
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaScope {

    private String sceneId;
    private String userId;
    private String departmentId;
    private String companyId;

    public static QuotaScope forUser(String userId) {
        return QuotaScope.builder().userId(userId).build();
    }

    public static QuotaScope forScene(String sceneId) {
        return QuotaScope.builder().sceneId(sceneId).build();
    }

    public static QuotaScope forDepartment(String departmentId) {
        return QuotaScope.builder().departmentId(departmentId).build();
    }

    public static QuotaScope forCompany(String companyId) {
        return QuotaScope.builder().companyId(companyId).build();
    }
}
