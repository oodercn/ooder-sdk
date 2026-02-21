package net.ooder.scene.skills.org;

import java.util.List;
import java.util.Set;

/**
 * Person 人员接口
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface Person extends Comparable<Person> {
    
    String getID();
    
    String getAccount();
    
    String getName();
    
    String getMobile();
    
    String getEmail();
    
    String getNickName();
    
    String getStatus();
    
    String getOrgId();
    
    Integer getIndex();
    
    String getPassword();
    
    String getCloudDiskPath();
    
    Org getOrg();
    
    List<Org> getOrgList();
    
    Set<String> getOrgIdList();
    
    Set<String> getRoleIdList();
    
    List<Role> getRoleList();
}
