package net.ooder.scene.skills.org;

import java.util.List;

/**
 * Org 组织接口
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface Org extends Comparable<Org> {
    
    String getOrgId();
    
    String getName();
    
    String getBrief();
    
    String getCity();
    
    String getParentId();
    
    String getLeaderId();
    
    Integer getTier();
    
    Integer getIndex();
    
    List<Role> getRoleList();
    
    List<String> getRoleIdList();
    
    Person getLeader();
    
    List<Person> getPersonList();
    
    List<Person> getPersonListRecursively();
    
    Org getParent();
    
    List<Org> getChildrenList();
    
    List<Org> getChildrenRecursivelyList();
    
    List<String> getChildIdList();
    
    List<String> getPersonIdList();
}
