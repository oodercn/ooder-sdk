package net.ooder.scene.drivers.org;

import java.util.ArrayList;
import java.util.List;

public class JsonRole implements Role {

    private String roleId;
    private String name;
    private RoleType type = RoleType.PERSON_ROLE;
    private String roleNum;
    private String sysId;
    
    private List<String> orgIdList = new ArrayList<String>();
    private List<String> personIdList = new ArrayList<String>();
    
    private transient JsonOrgDataSource dataSource;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleName() {
        return name;
    }

    public RoleType getType() {
        return type;
    }

    public void setType(RoleType type) {
        this.type = type;
    }

    public String getRoleNum() {
        return roleNum;
    }

    public void setRoleNum(String roleNum) {
        this.roleNum = roleNum;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public void setDataSource(JsonOrgDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Person> getPersonList() {
        List<Person> result = new ArrayList<Person>();
        if (dataSource != null) {
            for (String personId : personIdList) {
                Person p = dataSource.getPersonByID(personId);
                if (p != null) {
                    result.add(p);
                }
            }
        }
        return result;
    }

    public List<Org> getOrgList() {
        List<Org> result = new ArrayList<Org>();
        if (dataSource != null) {
            for (String orgId : orgIdList) {
                Org o = dataSource.getOrgByID(orgId);
                if (o != null) {
                    result.add(o);
                }
            }
        }
        return result;
    }

    public List<String> getOrgIdList() {
        return orgIdList;
    }

    public void setOrgIdList(List<String> orgIdList) {
        this.orgIdList = orgIdList != null ? orgIdList : new ArrayList<String>();
    }

    public List<String> getPersonIdList() {
        return personIdList;
    }

    public void setPersonIdList(List<String> personIdList) {
        this.personIdList = personIdList != null ? personIdList : new ArrayList<String>();
    }

    public int compareTo(Org other) {
        if (this.name == null || other.getName() == null) {
            return 0;
        }
        return this.name.compareTo(other.getName());
    }
}
