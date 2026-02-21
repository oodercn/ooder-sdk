package net.ooder.scene.drivers.org;

import java.util.List;

public interface OrgManager {
    
    void init(Object configCode);
    
    boolean verifyPerson(String account, String password);
    
    Person getPersonByID(String personId);
    
    Person getPersonByAccount(String account);
    
    Person getPersonByMobile(String mobile);
    
    Person getPersonByEmail(String email);
    
    Org getOrgByID(String orgId);
    
    Role getRoleByID(String roleId);
    
    Role getRoleByName(RoleType type, String roleName, String sysId);
    
    List<Org> getOrgs();
    
    List<Org> getOrgs(String sysId);
    
    List<Person> getPersons();
    
    List<Person> getPersonsByOrgID(String orgId);
    
    List<Org> getTopOrgs();
    
    List<Org> getTopOrgs(String sysId);
    
    List<Role> getAllRoles();
    
    List<Role> getAllRoles(String sysId);
    
    List<Role> getPersonRoles(String sysId);
    
    List<Role> getOrgRoles(String sysId);
    
    List<Role> getOrgLevels(String sysId);
    
    List<Role> getPersonDuties(String sysId);
    
    List<Role> getPersonGroups(String sysId);
    
    List<Role> getPersonLevels(String sysId);
    
    List<Role> getPersonPositions(String sysId);
    
    Role getOrgLevelByID(String orgLevelId);
    
    Role getOrgLevelByName(String orgLevelName, String sysId);
    
    Role getPersonRoleByID(String personRoleId);
    
    Role getPersonRoleByName(String personRoleName, String sysId);
    
    Role getPersonDutyByID(String personDutyId);
    
    Role getPersonDutyByName(String personDutyName, String sysId);
    
    Role getPersonGroupByID(String personGroupId);
    
    Role getPersonGroupByName(String personGroupName, String sysId);
    
    Role getPersonLevelByID(String personLevelId);
    
    Role getPersonLevelByName(String personLevelName, String sysId);
    
    Role getPersonPositionByID(String personPositionId);
    
    Role getPersonPositionByName(String personPositionName, String sysId);
    
    List<Org> getOrgsByOrgLevelID(String levelId);
    
    List<Org> getOrgsByOrgLevelName(String levelName, String sysId);
    
    List<Org> getOrgsByRoleID(String roleId);
    
    List<Org> getOrgsByRoleName(String roleName, String sysId);
    
    List<Person> getPersonsByPersonRoleID(String personRoleId);
    
    List<Person> getPersonsByPersonRoleName(String personRoleName, String sysId);
    
    List<Person> getPersonsByPersonDutyID(String personDutyId);
    
    List<Person> getPersonsByPersonDutyName(String personDutyName, String sysId);
    
    List<Person> getPersonsByPersonGroupID(String personGroupId);
    
    List<Person> getPersonsByPersonGroupName(String personGroupName, String sysId);
    
    List<Person> getPersonsByPersonLevelID(String personLevelId);
    
    List<Person> getPersonsByPersonLevelName(String personLevelName, String sysId);
    
    List<Person> getPersonsByPersonPositionID(String personPositionId);
    
    List<Person> getPersonsByPersonPositionName(String personPositionName, String sysId);
    
    boolean isSupportOrgLevel();
    
    boolean isSupportOrgRole();
    
    boolean isSupportPersonRole();
    
    boolean isSupportPersonDuty();
    
    boolean isSupportPersonGroup();
    
    boolean isSupportPersonLevel();
    
    boolean isSupportPersonPosition();
    
    String registerPerson(String account, String password, String name);
    
    void reloadAll();
}
