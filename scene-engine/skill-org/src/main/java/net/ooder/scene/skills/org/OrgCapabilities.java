package net.ooder.scene.skills.org;

import java.util.HashSet;
import java.util.Set;

/**
 * OrgCapabilities 组织能力配置
 * 
 * <p>定义组织技能支持的能力集合，用于判断哪些功能由外部 skills 提供，
 * 哪些需要本地 JSON 降级实现。</p>
 * 
 * <p>配置来源：scene 配置文件中的 orgCapabilities 部分。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class OrgCapabilities {

    private boolean supportOrgQuery = true;
    private boolean supportPersonQuery = true;
    private boolean supportPersonSync = false;
    private boolean supportOrgSync = false;
    
    private boolean supportOrgLevel = false;
    private boolean supportOrgRole = false;
    private boolean supportPersonDuty = false;
    private boolean supportPersonGroup = false;
    private boolean supportPersonLevel = false;
    private boolean supportPersonPosition = false;
    private boolean supportPersonRole = false;
    
    private boolean supportUserAuth = true;
    private boolean supportOrgAdmin = false;

    private String providerType = "json";
    private String skillEndpoint;
    private String skillId;

    public boolean isSupportOrgQuery() {
        return supportOrgQuery;
    }

    public void setSupportOrgQuery(boolean supportOrgQuery) {
        this.supportOrgQuery = supportOrgQuery;
    }

    public boolean isSupportPersonQuery() {
        return supportPersonQuery;
    }

    public void setSupportPersonQuery(boolean supportPersonQuery) {
        this.supportPersonQuery = supportPersonQuery;
    }

    public boolean isSupportPersonSync() {
        return supportPersonSync;
    }

    public void setSupportPersonSync(boolean supportPersonSync) {
        this.supportPersonSync = supportPersonSync;
    }

    public boolean isSupportOrgSync() {
        return supportOrgSync;
    }

    public void setSupportOrgSync(boolean supportOrgSync) {
        this.supportOrgSync = supportOrgSync;
    }

    public boolean isSupportOrgLevel() {
        return supportOrgLevel;
    }

    public void setSupportOrgLevel(boolean supportOrgLevel) {
        this.supportOrgLevel = supportOrgLevel;
    }

    public boolean isSupportOrgRole() {
        return supportOrgRole;
    }

    public void setSupportOrgRole(boolean supportOrgRole) {
        this.supportOrgRole = supportOrgRole;
    }

    public boolean isSupportPersonDuty() {
        return supportPersonDuty;
    }

    public void setSupportPersonDuty(boolean supportPersonDuty) {
        this.supportPersonDuty = supportPersonDuty;
    }

    public boolean isSupportPersonGroup() {
        return supportPersonGroup;
    }

    public void setSupportPersonGroup(boolean supportPersonGroup) {
        this.supportPersonGroup = supportPersonGroup;
    }

    public boolean isSupportPersonLevel() {
        return supportPersonLevel;
    }

    public void setSupportPersonLevel(boolean supportPersonLevel) {
        this.supportPersonLevel = supportPersonLevel;
    }

    public boolean isSupportPersonPosition() {
        return supportPersonPosition;
    }

    public void setSupportPersonPosition(boolean supportPersonPosition) {
        this.supportPersonPosition = supportPersonPosition;
    }

    public boolean isSupportPersonRole() {
        return supportPersonRole;
    }

    public void setSupportPersonRole(boolean supportPersonRole) {
        this.supportPersonRole = supportPersonRole;
    }

    public boolean isSupportUserAuth() {
        return supportUserAuth;
    }

    public void setSupportUserAuth(boolean supportUserAuth) {
        this.supportUserAuth = supportUserAuth;
    }

    public boolean isSupportOrgAdmin() {
        return supportOrgAdmin;
    }

    public void setSupportOrgAdmin(boolean supportOrgAdmin) {
        this.supportOrgAdmin = supportOrgAdmin;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getSkillEndpoint() {
        return skillEndpoint;
    }

    public void setSkillEndpoint(String skillEndpoint) {
        this.skillEndpoint = skillEndpoint;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public Set<String> getUnsupportedCapabilities() {
        Set<String> unsupported = new HashSet<String>();
        if (!supportOrgLevel) unsupported.add("org-level");
        if (!supportOrgRole) unsupported.add("org-role");
        if (!supportPersonDuty) unsupported.add("person-duty");
        if (!supportPersonGroup) unsupported.add("person-group");
        if (!supportPersonLevel) unsupported.add("person-level");
        if (!supportPersonPosition) unsupported.add("person-position");
        if (!supportPersonRole) unsupported.add("person-role");
        return unsupported;
    }

    public boolean requiresFallback() {
        return !supportOrgLevel || !supportOrgRole || !supportPersonDuty 
            || !supportPersonGroup || !supportPersonLevel 
            || !supportPersonPosition || !supportPersonRole;
    }

    /**
     * @deprecated 使用 OrgCapabilitiesFactory 和配置驱动的方式替代
     */
    @Deprecated
    public static OrgCapabilities forDingTalk() {
        return OrgCapabilitiesFactory.getInstance().createCapabilities("dingtalk");
    }

    /**
     * @deprecated 使用 OrgCapabilitiesFactory 和配置驱动的方式替代
     */
    @Deprecated
    public static OrgCapabilities forFeishu() {
        return OrgCapabilitiesFactory.getInstance().createCapabilities("feishu");
    }

    /**
     * @deprecated 使用 OrgCapabilitiesFactory 和配置驱动的方式替代
     */
    @Deprecated
    public static OrgCapabilities forWeCom() {
        return OrgCapabilitiesFactory.getInstance().createCapabilities("wecom");
    }

    /**
     * @deprecated 使用 OrgCapabilitiesFactory 和配置驱动的方式替代
     */
    @Deprecated
    public static OrgCapabilities forJson() {
        return OrgCapabilitiesFactory.getInstance().createCapabilities("json");
    }

    /**
     * @deprecated 使用 OrgCapabilitiesFactory 和配置驱动的方式替代
     */
    @Deprecated
    public static OrgCapabilities forDatabase() {
        return OrgCapabilitiesFactory.getInstance().createCapabilities("database");
    }
}
