package net.ooder.skill.org.api.entity;

import java.util.List;
import java.util.Set;

/**
 * Person API 实体类
 * 保持与原有 Person 接口兼容
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class Person {
    
    private String id;
    private String account;
    private String name;
    private String mobile;
    private String email;
    private String nickName;
    private String status;
    private String orgId;
    private Integer index;
    private String password;
    private String cloudDiskPath;
    
    // 通过 Facade 延迟加载
    private transient Org org;
    private transient List<Org> orgList;
    private transient Set<String> orgIdList;
    private transient Set<String> roleIdList;
    private transient List<Role> roleList;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    
    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getCloudDiskPath() { return cloudDiskPath; }
    public void setCloudDiskPath(String cloudDiskPath) { this.cloudDiskPath = cloudDiskPath; }
    
    /**
     * 兼容原有接口：getID()
     */
    public String getID() { return id; }
    
    /**
     * 获取所属组织 - 通过 Facade 延迟加载
     */
    public Org getOrg() {
        if (org == null && orgId != null) {
            org = OrgEntityFacade.getInstance().getOrgById(orgId);
        }
        return org;
    }
    
    public void setOrg(Org org) { this.org = org; }
    
    /**
     * 获取组织列表 - 通过 Facade 延迟加载
     */
    public List<Org> getOrgList() {
        if (orgList == null && id != null) {
            orgList = OrgEntityFacade.getInstance().getOrgListByPersonId(id);
        }
        return orgList;
    }
    
    public void setOrgList(List<Org> orgList) { this.orgList = orgList; }
    
    /**
     * 获取组织ID列表 - 通过 Facade 延迟加载
     */
    public Set<String> getOrgIdList() {
        if (orgIdList == null && id != null) {
            orgIdList = OrgEntityFacade.getInstance().getOrgIdListByPersonId(id);
        }
        return orgIdList;
    }
    
    public void setOrgIdList(Set<String> orgIdList) { this.orgIdList = orgIdList; }
    
    /**
     * 获取角色ID列表 - 通过 Facade 延迟加载
     */
    public Set<String> getRoleIdList() {
        if (roleIdList == null && id != null) {
            roleIdList = OrgEntityFacade.getInstance().getRoleIdListByPersonId(id);
        }
        return roleIdList;
    }
    
    public void setRoleIdList(Set<String> roleIdList) { this.roleIdList = roleIdList; }
    
    /**
     * 获取角色列表 - 通过 Facade 延迟加载
     */
    public List<Role> getRoleList() {
        if (roleList == null && id != null) {
            roleList = OrgEntityFacade.getInstance().getRoleListByPersonId(id);
        }
        return roleList;
    }
    
    public void setRoleList(List<Role> roleList) { this.roleList = roleList; }
}
