package net.ooder.scene.drivers.org;

import java.util.HashSet;
import java.util.Set;

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

    public static OrgCapabilities forJson() {
        OrgCapabilities caps = new OrgCapabilities();
        caps.setProviderType("json");
        caps.setSupportOrgQuery(true);
        caps.setSupportPersonQuery(true);
        caps.setSupportPersonSync(true);
        caps.setSupportOrgSync(true);
        caps.setSupportOrgLevel(true);
        caps.setSupportOrgRole(true);
        caps.setSupportPersonDuty(true);
        caps.setSupportPersonGroup(true);
        caps.setSupportPersonLevel(true);
        caps.setSupportPersonPosition(true);
        caps.setSupportPersonRole(true);
        caps.setSupportUserAuth(true);
        caps.setSupportOrgAdmin(true);
        return caps;
    }
}
