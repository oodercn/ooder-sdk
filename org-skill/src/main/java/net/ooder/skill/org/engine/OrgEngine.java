package net.ooder.skill.org.engine;

import net.ooder.skill.org.api.entity.*;
import net.ooder.skill.org.driver.OrgDriver;

import java.util.*;

/**
 * Org Engine 层
 * 业务逻辑实现 - 组织管理
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class OrgEngine {
    
    private final OrgDriver driver;
    
    public OrgEngine(OrgDriver driver) {
        this.driver = driver;
    }
    
    /**
     * 获取人员信息
     * @param personId 人员ID
     * @return 人员信息
     */
    public Person getPerson(String personId) {
        Map<String, Object> params = new HashMap<>();
        params.put("personId", personId);
        Object result = driver.invoke("org.getPersonById", params);
        return result instanceof Person ? (Person) result : null;
    }
    
    /**
     * 获取角色信息
     * @param roleId 角色ID
     * @return 角色信息
     */
    public Role getRole(String roleId) {
        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);
        Object result = driver.invoke("org.getRoleById", params);
        return result instanceof Role ? (Role) result : null;
    }
    
    /**
     * 获取组织信息
     * @param orgId 组织ID
     * @return 组织信息
     */
    public Org getOrg(String orgId) {
        Map<String, Object> params = new HashMap<>();
        params.put("orgId", orgId);
        Object result = driver.invoke("org.getOrgById", params);
        return result instanceof Org ? (Org) result : null;
    }
    
    /**
     * 获取组织下的人员列表
     * @param orgId 组织ID
     * @return 人员列表
     */
    public List<Person> getPersonListByOrg(String orgId) {
        Map<String, Object> params = new HashMap<>();
        params.put("orgId", orgId);
        Object result = driver.invoke("org.getPersonListByOrgId", params);
        if (result instanceof List) {
            return (List<Person>) result;
        }
        return new ArrayList<>();
    }
    
    /**
     * 获取人员的角色列表
     * @param personId 人员ID
     * @return 角色列表
     */
    public List<Role> getRoleListByPerson(String personId) {
        Map<String, Object> params = new HashMap<>();
        params.put("personId", personId);
        Object result = driver.invoke("org.getRoleListByPersonId", params);
        if (result instanceof List) {
            return (List<Role>) result;
        }
        return new ArrayList<>();
    }
    
    /**
     * 执行能力
     * @param capabilityId 能力ID
     * @param params 参数
     * @return 结果
     */
    public Object execute(String capabilityId, Map<String, Object> params) {
        switch (capabilityId) {
            case "org.getPerson":
                return getPerson((String) params.get("personId"));
            case "org.getRole":
                return getRole((String) params.get("roleId"));
            case "org.getOrg":
                return getOrg((String) params.get("orgId"));
            case "org.getPersonListByOrg":
                return getPersonListByOrg((String) params.get("orgId"));
            case "org.getRoleListByPerson":
                return getRoleListByPerson((String) params.get("personId"));
            default:
                throw new UnsupportedOperationException("Unknown capability: " + capabilityId);
        }
    }
}
