package net.ooder.skill.org.api.entity;

import java.util.List;

/**
 * Role API 实体类
 * 保持与原有 Role 接口兼容
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class Role {
    
    private String roleId;
    private String name;
    private String roleName;
    private RoleType type;
    private String roleNum;
    private String sysId;
    private List<String> orgIdList;
    private List<String> personIdList;
    
    // 通过 Facade 延迟加载
    private transient List<Person> personList;
    private transient List<Org> orgList;
    
    // Getters and Setters
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRoleName() { return roleName != null ? roleName : name; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    
    public RoleType getType() { return type; }
    public void setType(RoleType type) { this.type = type; }
    
    public String getRoleNum() { return roleNum; }
    public void setRoleNum(String roleNum) { this.roleNum = roleNum; }
    
    public String getSysId() { return sysId; }
    public void setSysId(String sysId) { this.sysId = sysId; }
    
    public List<String> getOrgIdList() { return orgIdList; }
    public void setOrgIdList(List<String> orgIdList) { this.orgIdList = orgIdList; }
    
    public List<String> getPersonIdList() { return personIdList; }
    public void setPersonIdList(List<String> personIdList) { this.personIdList = personIdList; }
    
    /**
     * 获取人员列表 - 通过 Facade 延迟加载
     */
    public List<Person> getPersonList() {
        if (personList == null && roleId != null) {
            personList = OrgEntityFacade.getInstance().getPersonListByRoleId(roleId);
        }
        return personList;
    }
    
    public void setPersonList(List<Person> personList) { this.personList = personList; }
    
    /**
     * 获取组织列表 - 通过 Facade 延迟加载
     */
    public List<Org> getOrgList() {
        if (orgList == null && roleId != null) {
            orgList = OrgEntityFacade.getInstance().getOrgListByRoleId(roleId);
        }
        return orgList;
    }
    
    public void setOrgList(List<Org> orgList) { this.orgList = orgList; }
}
