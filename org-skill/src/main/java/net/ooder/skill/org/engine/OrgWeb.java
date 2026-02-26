package net.ooder.skill.org.engine;

import net.ooder.skill.org.api.entity.Person;
import net.ooder.skill.org.api.entity.Role;
import net.ooder.skill.org.api.entity.Org;

import java.util.List;

/**
 * Org Web 层
 * UI/交互层 - 组织管理界面
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class OrgWeb {
    
    private final OrgEngine engine;
    
    public OrgWeb(OrgEngine engine) {
        this.engine = engine;
    }
    
    /**
     * 渲染人员信息
     * @param personId 人员ID
     * @return HTML/JSON 表示
     */
    public String renderPerson(String personId) {
        Person person = engine.getPerson(personId);
        if (person == null) {
            return "{\"error\": \"Person not found\"}";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"id\": \"").append(person.getId()).append("\",\n");
        sb.append("  \"name\": \"").append(person.getName()).append("\",\n");
        sb.append("  \"account\": \"").append(person.getAccount()).append("\",\n");
        sb.append("  \"email\": \"").append(person.getEmail()).append("\",\n");
        sb.append("  \"mobile\": \"").append(person.getMobile()).append("\",\n");
        sb.append("  \"status\": \"").append(person.getStatus()).append("\"\n");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 渲染角色信息
     * @param roleId 角色ID
     * @return HTML/JSON 表示
     */
    public String renderRole(String roleId) {
        Role role = engine.getRole(roleId);
        if (role == null) {
            return "{\"error\": \"Role not found\"}";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"roleId\": \"").append(role.getRoleId()).append("\",\n");
        sb.append("  \"name\": \"").append(role.getName()).append("\",\n");
        sb.append("  \"type\": \"").append(role.getType()).append("\"\n");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 渲染组织信息
     * @param orgId 组织ID
     * @return HTML/JSON 表示
     */
    public String renderOrg(String orgId) {
        Org org = engine.getOrg(orgId);
        if (org == null) {
            return "{\"error\": \"Organization not found\"}";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"orgId\": \"").append(org.getOrgId()).append("\",\n");
        sb.append("  \"name\": \"").append(org.getName()).append("\",\n");
        sb.append("  \"code\": \"").append(org.getCode()).append("\",\n");
        sb.append("  \"status\": \"").append(org.getStatus()).append("\"\n");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 渲染组织人员列表
     * @param orgId 组织ID
     * @return HTML/JSON 表示
     */
    public String renderPersonListByOrg(String orgId) {
        List<Person> persons = engine.getPersonListByOrg(orgId);
        
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < persons.size(); i++) {
            Person person = persons.get(i);
            sb.append("  {\n");
            sb.append("    \"id\": \"").append(person.getId()).append("\",\n");
            sb.append("    \"name\": \"").append(person.getName()).append("\",\n");
            sb.append("    \"account\": \"").append(person.getAccount()).append("\"\n");
            sb.append("  }");
            if (i < persons.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * 渲染人员角色列表
     * @param personId 人员ID
     * @return HTML/JSON 表示
     */
    public String renderRoleListByPerson(String personId) {
        List<Role> roles = engine.getRoleListByPerson(personId);
        
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            sb.append("  {\n");
            sb.append("    \"roleId\": \"").append(role.getRoleId()).append("\",\n");
            sb.append("    \"name\": \"").append(role.getName()).append("\",\n");
            sb.append("    \"type\": \"").append(role.getType()).append("\"\n");
            sb.append("  }");
            if (i < roles.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
