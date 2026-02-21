package net.ooder.scene.drivers.org;

import java.util.ArrayList;
import java.util.List;

public class JsonOrg implements Org {

    private String orgId;
    private String name;
    private String brief;
    private String city;
    private String parentId;
    private String leaderId;
    private Integer tier = 1;
    private Integer index = 0;
    
    private transient JsonOrgDataSource dataSource;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setDataSource(JsonOrgDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Role> getRoleList() {
        return new ArrayList<Role>();
    }

    public List<String> getRoleIdList() {
        return new ArrayList<String>();
    }

    public Person getLeader() {
        if (dataSource != null && leaderId != null) {
            return dataSource.getPersonByID(leaderId);
        }
        return null;
    }

    public List<Person> getPersonList() {
        if (dataSource != null) {
            return dataSource.getPersonsByOrgID(orgId);
        }
        return new ArrayList<Person>();
    }

    public List<Person> getPersonListRecursively() {
        List<Person> result = new ArrayList<Person>();
        result.addAll(getPersonList());
        
        for (Org child : getChildrenList()) {
            result.addAll(child.getPersonListRecursively());
        }
        return result;
    }

    public Org getParent() {
        if (dataSource != null && parentId != null) {
            return dataSource.getOrgByID(parentId);
        }
        return null;
    }

    public List<Org> getChildrenList() {
        List<Org> result = new ArrayList<Org>();
        if (dataSource != null) {
            for (Org org : dataSource.getOrgs()) {
                if (orgId.equals(org.getParentId())) {
                    result.add(org);
                }
            }
        }
        return result;
    }

    public List<Org> getChildrenRecursivelyList() {
        List<Org> result = new ArrayList<Org>();
        for (Org child : getChildrenList()) {
            result.add(child);
            result.addAll(child.getChildrenRecursivelyList());
        }
        return result;
    }

    public List<String> getChildIdList() {
        List<String> result = new ArrayList<String>();
        for (Org child : getChildrenList()) {
            result.add(child.getOrgId());
        }
        return result;
    }

    public List<String> getPersonIdList() {
        List<String> result = new ArrayList<String>();
        for (Person p : getPersonList()) {
            result.add(p.getID());
        }
        return result;
    }

    public int compareTo(Org other) {
        if (this.name == null || other.getName() == null) {
            return 0;
        }
        return this.name.compareTo(other.getName());
    }
}
