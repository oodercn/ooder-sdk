package net.ooder.skill.org.api.entity;

import java.util.List;

/**
 * Org API 实体类
 * 组织信息
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class Org {
    
    private String orgId;
    private String name;
    private String code;
    private String parentId;
    private String status;
    private Integer index;
    private String sysId;
    
    // 通过 Facade 延迟加载
    private transient List<Person> personList;
    private transient List<Role> roleList;
    private transient Org parent;
    private transient List<Org> children;
    
    // Getters and Setters
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }
    
    public String getSysId() { return sysId; }
    public void setSysId(String sysId) { this.sysId = sysId; }
    
    /**
     * 获取人员列表 - 通过 Facade 延迟加载
     */
    public List<Person> getPersonList() {
        if (personList == null && orgId != null) {
            personList = OrgEntityFacade.getInstance().getPersonListByOrgId(orgId);
        }
        return personList;
    }
    
    public void setPersonList(List<Person> personList) { this.personList = personList; }
    
    /**
     * 获取角色列表 - 通过 Facade 延迟加载
     */
    public List<Role> getRoleList() {
        if (roleList == null && orgId != null) {
            roleList = OrgEntityFacade.getInstance().getRoleListByOrgId(orgId);
        }
        return roleList;
    }
    
    public void setRoleList(List<Role> roleList) { this.roleList = roleList; }
    
    /**
     * 获取父组织 - 通过 Facade 延迟加载
     */
    public Org getParent() {
        if (parent == null && parentId != null) {
            parent = OrgEntityFacade.getInstance().getOrgById(parentId);
        }
        return parent;
    }
    
    public void setParent(Org parent) { this.parent = parent; }
    
    /**
     * 获取子组织列表 - 通过 Facade 延迟加载
     */
    public List<Org> getChildren() {
        if (children == null && orgId != null) {
            children = OrgEntityFacade.getInstance().getChildrenByOrgId(orgId);
        }
        return children;
    }
    
    public void setChildren(List<Org> children) { this.children = children; }
}
