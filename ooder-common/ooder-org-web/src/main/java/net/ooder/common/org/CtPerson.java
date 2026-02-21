package net.ooder.common.org;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.annotation.RoleType;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.org.*;
import net.ooder.org.conf.OrgConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CtPerson implements Person, Cacheable, Serializable {
    protected static Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtPerson.class);

    private String iD;

    private String account;

    private Integer accountType;

    private Integer index = 0;

    private String name;

    private String orgId;

    private String status;

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Set<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<String> roleIds) {
        this.roleIds = roleIds;
    }

    public Set<String> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(Set<String> orgIds) {
        this.orgIds = orgIds;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String mobile;

    private String email;

    private String nickName;

    private Set<String> roleIds = new LinkedHashSet<>();


    private Set<String> orgIds = new LinkedHashSet<String>();

    private String password;

    private String path;

    public CtPerson(String id) {
        this.iD = id;

    }

    public CtPerson() {

    }

    public CtPerson(Person wsPerson) {

        this.account = wsPerson.getAccount();
        this.email = wsPerson.getEmail();
        this.iD = wsPerson.getID();
        this.orgIds = wsPerson.getOrgIdList();
        this.orgId = wsPerson.getOrgId();


        this.name = wsPerson.getName();
        this.password = wsPerson.getPassword();
        this.mobile = wsPerson.getMobile();
        this.nickName = wsPerson.getNickName();

        roleIds = wsPerson.getRoleIdList();
    }

    @JSONField(serialize = false)
    public Org getOrg() {
        Org org = null;
        CtCacheManager cacheManager = CtCacheManager.getInstance();
        if (this.getOrgId() != null) {
            try {
                org = cacheManager.getOrgByID(orgId);
            } catch (OrgNotFoundException e) {
                e.printStackTrace();
            }
        }
        return org;
    }

    @JSONField(serialize = false)
    public List<Org> getOrgList() {

        List orgList = new ArrayList();
        CtCacheManager cacheManager = CtCacheManager.getInstance();
        for (String orgId : orgIds) {
            try {
                orgList.add(cacheManager.getOrgByID(orgId));
            } catch (OrgNotFoundException ex) {
            }
        }
        return orgList;
    }


    @Override
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @JSONField(serialize = false)

    @Override
    public List<Role> getRoleList() {

        List<Role> roleList = new ArrayList<Role>();
        CtCacheManager cacheManager = CtCacheManager.getInstance();
        for (String roleId : roleIds) {
            try {
                Role role = cacheManager.getPersonRoleByID(roleId);
                if (role.getType().equals(RoleType.Role)) {
                    roleList.add(role);
                }

            } catch (RoleNotFoundException ex) {
            }
        }
        return roleList;
    }


    @JSONField(serialize = false)
    public List<Role> getAllRoleList() {

        List<Role> roleList = new ArrayList<Role>();
        CtCacheManager cacheManager = CtCacheManager.getInstance();
        for (String roleId : roleIds) {
            try {
                Role role = cacheManager.getPersonRoleByID(roleId);
                roleList.add(role);
            } catch (RoleNotFoundException ex) {
            }
        }
        return roleList;
    }


    @JSONField(serialize = false)
    public List<Role> getDutieList() {

        List<Role> dutiesList = new ArrayList<Role>();
        CtCacheManager cacheManager = CtCacheManager.getInstance();
        for (String roleId : roleIds) {
            try {
                Role role = cacheManager.getPersonRoleByID(roleId);
                if (role.getType().equals(RoleType.Duty)) {
                    dutiesList.add(role);
                }

            } catch (RoleNotFoundException ex) {
            }
        }
        return dutiesList;
    }


    @JSONField(serialize = false)

    public List<Role> getGroupList() {

        List<Role> groupList = new ArrayList<Role>();
        CtCacheManager cacheManager = CtCacheManager.getInstance();
        for (String groupId : roleIds) {
            Role role = null;
            try {
                role = cacheManager.getPersonRoleByID(groupId);
                if (role.getType().equals(RoleType.Group)) {
                    groupList.add(role);
                }
            } catch (RoleNotFoundException e) {
                e.printStackTrace();
            }

        }
        return groupList;
    }

    @JSONField(serialize = false)

    public List<Role> getPersonLevelList() {

        List<Role> groupList = new ArrayList<Role>();
        CtCacheManager cacheManager = CtCacheManager.getInstance();
        for (String groupId : roleIds) {
            Role role = null;
            try {
                role = cacheManager.getPersonRoleByID(groupId);
                if (role.getType().equals(RoleType.PersonLevel)) {
                    groupList.add(role);
                }
            } catch (RoleNotFoundException e) {
                e.printStackTrace();
            }

        }
        return groupList;
    }


    @JSONField(serialize = false)

    public List<Role> getPositionList() {

        List<Role> positionList = new ArrayList<Role>();

        CtCacheManager cacheManager = CtCacheManager.getInstance();
        for (String positionId : roleIds) {
            Role role = null;
            try {
                role = cacheManager.getPersonRoleByID(positionId);
                if (role.getType().equals(RoleType.Position)) {
                    positionList.add(role);
                }
            } catch (RoleNotFoundException e) {
                e.printStackTrace();
            }
        }

        return positionList;
    }

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAccoutType() {
        return this.accountType;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public String getNickName() {
        return this.nickName;
    }

    @Override
    public String getCloudDiskPath() {
        return null;
    }

    @Override
    @JSONField(serialize = false)
    public Set<String> getRoleIdList() {
        return this.roleIds;
    }

    public int getCachedSize() {
        int size = 1000;
        return size;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object != null && object instanceof Person) {
            return iD.equals(((Person) object).getID());
        } else {
            return false;
        }
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public int compareTo(Person o) {
        if (index == null && o.getIndex() == null) {
            return 0;
        } else if (index == null) {
            return -1;
        } else if (o.getIndex() == null) {
            return 1;
        } else {
            return o.getIndex() - index;
        }

    }

    @Override
    @JSONField(serialize = false)
    public Set<String> getOrgIdList() {
        return this.orgIds;
    }

}
