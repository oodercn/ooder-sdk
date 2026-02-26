package net.ooder.skill.org.api.entity;

import java.util.*;

/**
 * Org 实体 Facade
 * 提供延迟加载和实体转换功能
 * 兼容原有 ooder-common 的 Org/Person/Role 接口
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class OrgEntityFacade {
    
    private static volatile OrgEntityFacade instance;
    private static final Object LOCK = new Object();
    
    // 数据访问接口，由 Engine 层实现
    private OrgDataProvider dataProvider;
    
    private OrgEntityFacade() {}
    
    public static OrgEntityFacade getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new OrgEntityFacade();
                }
            }
        }
        return instance;
    }
    
    /**
     * 设置数据提供者
     * @param provider 数据提供者
     */
    public void setDataProvider(OrgDataProvider provider) {
        this.dataProvider = provider;
    }
    
    /**
     * 获取数据提供者
     */
    public OrgDataProvider getDataProvider() {
        if (dataProvider == null) {
            throw new IllegalStateException("OrgDataProvider not initialized");
        }
        return dataProvider;
    }
    
    // ==================== Person 相关查询 ====================
    
    public Org getOrgById(String orgId) {
        return getDataProvider().getOrgById(orgId);
    }
    
    public List<Org> getOrgListByPersonId(String personId) {
        return getDataProvider().getOrgListByPersonId(personId);
    }
    
    public Set<String> getOrgIdListByPersonId(String personId) {
        return getDataProvider().getOrgIdListByPersonId(personId);
    }
    
    public Set<String> getRoleIdListByPersonId(String personId) {
        return getDataProvider().getRoleIdListByPersonId(personId);
    }
    
    public List<Role> getRoleListByPersonId(String personId) {
        return getDataProvider().getRoleListByPersonId(personId);
    }
    
    // ==================== Role 相关查询 ====================
    
    public List<Person> getPersonListByRoleId(String roleId) {
        return getDataProvider().getPersonListByRoleId(roleId);
    }
    
    public List<Org> getOrgListByRoleId(String roleId) {
        return getDataProvider().getOrgListByRoleId(roleId);
    }
    
    // ==================== Org 相关查询 ====================
    
    public List<Person> getPersonListByOrgId(String orgId) {
        return getDataProvider().getPersonListByOrgId(orgId);
    }
    
    public List<Role> getRoleListByOrgId(String orgId) {
        return getDataProvider().getRoleListByOrgId(orgId);
    }
    
    public List<Org> getChildrenByOrgId(String orgId) {
        return getDataProvider().getChildrenByOrgId(orgId);
    }
    
    // ==================== 转换方法 ====================
    
    /**
     * 从 Map 数据转换为 Person
     */
    public Person convertFromMap(Map<String, Object> data) {
        if (data == null) return null;
        
        Person person = new Person();
        person.setId((String) data.get("id"));
        person.setAccount((String) data.get("account"));
        person.setName((String) data.get("name"));
        person.setMobile((String) data.get("mobile"));
        person.setEmail((String) data.get("email"));
        person.setNickName((String) data.get("nickName"));
        person.setStatus((String) data.get("status"));
        person.setOrgId((String) data.get("orgId"));
        person.setIndex((Integer) data.get("index"));
        person.setPassword((String) data.get("password"));
        person.setCloudDiskPath((String) data.get("cloudDiskPath"));
        
        return person;
    }
    
    /**
     * 从 Map 数据转换为 Role
     */
    public Role convertRoleFromMap(Map<String, Object> data) {
        if (data == null) return null;
        
        Role role = new Role();
        role.setRoleId((String) data.get("roleId"));
        role.setName((String) data.get("name"));
        role.setRoleName((String) data.get("roleName"));
        role.setType(convertRoleTypeFromString((String) data.get("type")));
        role.setRoleNum((String) data.get("roleNum"));
        role.setSysId((String) data.get("sysId"));
        role.setOrgIdList((List<String>) data.get("orgIdList"));
        role.setPersonIdList((List<String>) data.get("personIdList"));
        
        return role;
    }
    
    /**
     * 从 Map 数据转换为 Org
     */
    public Org convertOrgFromMap(Map<String, Object> data) {
        if (data == null) return null;
        
        Org org = new Org();
        org.setOrgId((String) data.get("orgId"));
        org.setName((String) data.get("name"));
        org.setCode((String) data.get("code"));
        org.setParentId((String) data.get("parentId"));
        org.setStatus((String) data.get("status"));
        org.setIndex((Integer) data.get("index"));
        org.setSysId((String) data.get("sysId"));
        
        return org;
    }
    
    /**
     * 从字符串转换 RoleType
     */
    private RoleType convertRoleTypeFromString(String type) {
        if (type == null) return RoleType.CUSTOM;
        try {
            return RoleType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return RoleType.CUSTOM;
        }
    }
    
    /**
     * 数据提供者接口
     * 由 Engine 层实现
     */
    public interface OrgDataProvider {
        // Person 相关
        Org getOrgById(String orgId);
        List<Org> getOrgListByPersonId(String personId);
        Set<String> getOrgIdListByPersonId(String personId);
        Set<String> getRoleIdListByPersonId(String personId);
        List<Role> getRoleListByPersonId(String personId);
        
        // Role 相关
        List<Person> getPersonListByRoleId(String roleId);
        List<Org> getOrgListByRoleId(String roleId);
        
        // Org 相关
        List<Person> getPersonListByOrgId(String orgId);
        List<Role> getRoleListByOrgId(String orgId);
        List<Org> getChildrenByOrgId(String orgId);
    }
}
