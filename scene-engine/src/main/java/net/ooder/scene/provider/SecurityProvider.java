package net.ooder.scene.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.security.AuditExportResult;
import net.ooder.scene.core.security.AuditLog;
import net.ooder.scene.core.security.AuditLogQuery;
import java.util.List;

/**
 * 安全Provider接口
 *
 * <p>定义安全管理相关的操作接口，由skill-security实现</p>
 */
public interface SecurityProvider extends BaseProvider {

    /**
     * 获取安全状态
     */
    SecurityStatus getStatus();

    /**
     * 获取安全统计
     */
    SecurityStats getStats();

    /**
     * 列出安全策略
     */
    List<SecurityPolicy> listPolicies();

    /**
     * 获取安全策略
     */
    SecurityPolicy getPolicy(String policyId);

    /**
     * 创建安全策略
     */
    SecurityPolicy createPolicy(SecurityPolicy policy);

    /**
     * 更新安全策略
     */
    boolean updatePolicy(SecurityPolicy policy);

    /**
     * 删除安全策略
     */
    boolean deletePolicy(String policyId);

    /**
     * 启用安全策略
     */
    boolean enablePolicy(String policyId);

    /**
     * 禁用安全策略
     */
    boolean disablePolicy(String policyId);

    /**
     * 列出访问控制规则
     */
    PageResult<AccessControl> listAcls(int page, int size);

    /**
     * 创建访问控制规则
     */
    AccessControl createAcl(AccessControl acl);

    /**
     * 删除访问控制规则
     */
    boolean deleteAcl(String aclId);

    /**
     * 检查权限
     */
    boolean checkPermission(String userId, String resource, String action);

    /**
     * 列出威胁信息
     */
    PageResult<ThreatInfo> listThreats(int page, int size);

    /**
     * 获取威胁信息
     */
    ThreatInfo getThreat(String threatId);

    /**
     * 解决威胁
     */
    boolean resolveThreat(String threatId);

    /**
     * 运行安全扫描
     */
    boolean runSecurityScan();

    /**
     * 切换防火墙状态
     */
    boolean toggleFirewall();

    /**
     * 检查防火墙是否启用
     */
    boolean isFirewallEnabled();

    /**
     * 查询审计日志
     * @param query 查询条件
     * @return 审计日志分页结果
     */
    PageResult<AuditLog> listAuditLogs(AuditLogQuery query);

    /**
     * 导出审计日志
     * @param query 查询条件
     * @return 导出结果
     */
    AuditExportResult exportAuditLogs(AuditLogQuery query);
}
