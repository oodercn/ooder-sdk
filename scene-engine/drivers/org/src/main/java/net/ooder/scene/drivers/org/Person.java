package net.ooder.scene.drivers.org;

import java.util.List;
import java.util.Set;

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
