package net.ooder.common.org;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.annotation.RoleType;
import net.ooder.common.cache.Cacheable;
import net.ooder.org.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CtRole implements Role, Cacheable, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private RoleType type = RoleType.Role;

    public CtRole() {


    }

    public CtRole(RoleType type) {
        this.type = type;
    }

    private String roleId;
    private String name;
    private String sysId;

    private String roleNum;
    private List<String> personIds = new ArrayList<String>();
    private List<String> orgIds = new ArrayList<String>();

    public int getCachedSize() {
        return 0;
    }

    /**
     * @param personIds The personIds to set.
     */
    public void setPersonIds(List<String> personIds) {
        this.personIds = personIds;
    }

    public void setOrgIds(List<String> orgIds) {
        this.orgIds = orgIds;
    }

    public String getName() {
        return name;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String getSysId() {
        return sysId;
    }

    @Override
    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    @Override
    public RoleType getType() {

        return this.type;
    }

    @Override
    public String getRoleNum() {
        return this.roleNum;
    }

    @Override
    public List<String> getOrgIdList() {
        return this.orgIds;
    }

    @Override
    public List<String> getPersonIdList() {
        return this.personIds;
    }

    @Override
    public List<Person> getPersonList() {
        List<Person> personList = new ArrayList<Person>();
        if (personIds != null) {
            CtCacheManager cacheManager = CtCacheManager.getInstance();
            for (String personId : personIds) {
                try {
                    personList.add(cacheManager.getPersonByID(personId));
                } catch (PersonNotFoundException ex) {
                }
            }
        }

        return personList;
    }

    @Override
    @JSONField(serialize = false)
    public List<Org> getOrgList() {
        List<Org> orgList = new ArrayList<Org>();
        if (orgIds != null) {
            CtCacheManager cacheManager = CtCacheManager.getInstance();
            for (String orgId : orgIds) {
                try {
                    orgList.add(cacheManager.getOrgByID(orgId));
                } catch (OrgNotFoundException ex) {
                }
            }
        }

        return orgList;
    }

    @Override
    public void setType(RoleType type) {
        this.type = type;
    }

    @Override
    public void setRoleNum(String num) {
        this.roleNum = num;
    }

    @Override
    public void setOrgIdList(List<String> orgIds) {
        this.orgIds = orgIds;
    }

    @Override
    public void setPersonIdList(List<String> personIds) {
        this.personIds = personIds;
    }

    public List<String> getPersonIds() {
        return personIds;
    }

    public List<String> getOrgIds() {
        return orgIds;
    }


    @Override
    public int compareTo(Org o) {
        return 0;
    }
}
