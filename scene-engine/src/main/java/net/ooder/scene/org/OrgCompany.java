package net.ooder.scene.org;

import java.util.List;
import java.util.Map;

/**
 * 组织-公司
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class OrgCompany {
    
    private String companyId;
    private String name;
    private String code;
    private String industry;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private int maxUsers;
    private int maxDepartments;
    private long createTime;
    private long updateTime;
    private boolean active;
    private Map<String, Object> settings;
    
    public OrgCompany() {}
    
    public OrgCompany(String companyId, String name) {
        this.companyId = companyId;
        this.name = name;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
        this.active = true;
    }
    
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getMaxUsers() { return maxUsers; }
    public void setMaxUsers(int maxUsers) { this.maxUsers = maxUsers; }
    public int getMaxDepartments() { return maxDepartments; }
    public void setMaxDepartments(int maxDepartments) { this.maxDepartments = maxDepartments; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Map<String, Object> getSettings() { return settings; }
    public void setSettings(Map<String, Object> settings) { this.settings = settings; }
}
