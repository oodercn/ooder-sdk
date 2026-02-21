package net.ooder.scene.drivers.org;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JsonPerson implements Person {

    private String id;
    private String account;
    private String name;
    private String password;
    private String mobile;
    private String email;
    private String nickName;
    private String status = "active";
    private String orgId;
    private Integer index = 0;
    private String cloudDiskPath;
    
    private transient JsonOrgDataSource dataSource;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName != null ? nickName : name;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getCloudDiskPath() {
        return cloudDiskPath;
    }

    public void setCloudDiskPath(String cloudDiskPath) {
        this.cloudDiskPath = cloudDiskPath;
    }

    public void setDataSource(JsonOrgDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Org getOrg() {
        if (dataSource != null && orgId != null) {
            return dataSource.getOrgByID(orgId);
        }
        return null;
    }

    public List<Org> getOrgList() {
        List<Org> result = new ArrayList<Org>();
        Org org = getOrg();
        if (org != null) {
            result.add(org);
        }
        return result;
    }

    public Set<String> getOrgIdList() {
        Set<String> result = new HashSet<String>();
        if (orgId != null) {
            result.add(orgId);
        }
        return result;
    }

    public List<Role> getRoleList() {
        if (dataSource != null) {
            return dataSource.getRolesByPersonId(id);
        }
        return new ArrayList<Role>();
    }

    public Set<String> getRoleIdList() {
        if (dataSource != null) {
            return dataSource.getRoleIdSetByPersonId(id);
        }
        return new HashSet<String>();
    }

    public int compareTo(Person other) {
        if (this.name == null || other.getName() == null) {
            return 0;
        }
        return this.name.compareTo(other.getName());
    }
}
