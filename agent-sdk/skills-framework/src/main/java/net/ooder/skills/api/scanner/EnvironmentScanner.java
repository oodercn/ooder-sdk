package net.ooder.skills.api.scanner;

import net.ooder.skills.api.context.EnvironmentContext;
import net.ooder.skills.api.context.OrganizationContext;

/**
 * 环境扫描器接口
 *
 * <p>用于扫描组织上下文和安装环境信息</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public interface EnvironmentScanner {

    /**
     * 扫描组织上下文
     *
     * <p>扫描内容：</p>
     * <ul>
     *   <li>组织基本信息 (orgId, orgName, industry)</li>
     *   <li>部门结构 (departments, hierarchy)</li>
     *   <li>现有系统 (systems, integrations)</li>
     *   <li>数据资产 (dataAssets, schemas)</li>
     *   <li>用户规模 (userCount, activeUsers)</li>
     * </ul>
     *
     * @return 组织上下文
     */
    OrganizationContext scanOrganization();

    /**
     * 扫描安装环境
     *
     * <p>扫描内容：</p>
     * <ul>
     *   <li>运行时信息 (Java版本、操作系统)</li>
     *   <li>数据库连接</li>
     *   <li>缓存服务</li>
     *   <li>消息队列</li>
     *   <li>存储空间</li>
     *   <li>网络配置</li>
     * </ul>
     *
     * @return 环境上下文
     */
    EnvironmentContext scanEnvironment();

    /**
     * 执行完整扫描
     *
     * @return 扫描结果
     */
    default ScanResult scanAll() {
        ScanResult result = new ScanResult();
        result.setOrganizationContext(scanOrganization());
        result.setEnvironmentContext(scanEnvironment());
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    /**
     * 扫描结果
     */
    class ScanResult {
        private OrganizationContext organizationContext;
        private EnvironmentContext environmentContext;
        private long timestamp;

        public OrganizationContext getOrganizationContext() { return organizationContext; }
        public void setOrganizationContext(OrganizationContext organizationContext) { this.organizationContext = organizationContext; }

        public EnvironmentContext getEnvironmentContext() { return environmentContext; }
        public void setEnvironmentContext(EnvironmentContext environmentContext) { this.environmentContext = environmentContext; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
