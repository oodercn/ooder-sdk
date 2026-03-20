package net.ooder.scene.org;

import java.util.Map;

public class CreateCompanyRequest {
    private String name;
    private String code;
    private String description;
    private String industry;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private int maxUsers;
    private int maxDepartments;
    private Map<String, Object> metadata;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
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
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
