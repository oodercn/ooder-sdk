package net.ooder.scene.drivers.org;

import java.util.List;

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
