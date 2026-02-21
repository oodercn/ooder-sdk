package net.ooder.scene.drivers.org;

import java.util.List;

public interface Role extends Comparable<Org> {
    
    String getRoleId();
    
    void setRoleId(String roleId);
    
    String getName();
    
    void setName(String name);
    
    String getRoleName();
    
    RoleType getType();
    
    void setType(RoleType type);
    
    String getRoleNum();
    
    void setRoleNum(String num);
    
    String getSysId();
    
    void setSysId(String sysId);
    
    List<Person> getPersonList();
    
    List<Org> getOrgList();
    
    List<String> getOrgIdList();
    
    void setOrgIdList(List<String> orgIds);
    
    List<String> getPersonIdList();
    
    void setPersonIdList(List<String> personIds);
}
