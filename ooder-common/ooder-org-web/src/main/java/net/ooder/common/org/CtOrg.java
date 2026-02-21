package net.ooder.common.org;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.org.*;
import net.ooder.org.conf.OrgConstants;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CtOrg implements Org, Cacheable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected static Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtOrg.class);

    private String brief;
    private String orgId;
    private String city;
    private Integer index = 0;
    private String leaderId;
    private String leaderName;


    private String name;
    private String parentId;
    private int tier;

    private List<String> personIds;

    private List<String> roleIds;

    private List<String> childIds;

    private List<String> recursiveChildIdLis;

    private List<String> recursivePersonIdLis;

    public CtOrg() {

    }

    public CtOrg(Org org) {

        this.brief = org.getBrief();
        this.tier = org.getTier();
        this.childIds = org.getChildIdList();
        this.roleIds = org.getChildIdList();
        this.leaderId = org.getLeaderId();

        this.orgId = org.getOrgId();
        this.city = org.getCity();
        this.index = org.getIndex();
        this.name = org.getName();
        this.parentId = org.getParentId();

        this.personIds = org.getPersonIdList();

        if (childIds == null) {
            childIds = new ArrayList<String>();
        }

        if (roleIds == null) {
            roleIds = new ArrayList<String>();
        }

        if (personIds == null) {
            personIds = new ArrayList<String>();
        }

    }

    public String getOrgId() {
        return orgId;
    }

    public String getName() {
        return name;
    }

    public String getBrief() {
        return brief;
    }

    public Integer getTier() {
        return tier;
    }

    public void setLeader(String leaderId) {
        this.leaderId = leaderId;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLeaderName() {
        return this.getLeader() != null ? this.getLeader().getName() : null;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public void setPersonIds(List<String> personIds) {
        this.personIds = personIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    public void setChildIds(List<String> childIds) {
        this.childIds = childIds;
    }


    @JSONField(serialize = false)
    public int getCachedSize() {
        return 100;
    }

    @JSONField(serialize = false)
    public Org getParent() {
        try {
            return CtCacheManager.getInstance().getOrgByID(parentId);
        } catch (OrgNotFoundException ex) {
            return null;
        }
    }


    public String toString() {
        return name;
        // StringBuffer sb = new StringBuffer();
        // sb.append("orgId: ").append(ID).append("\r\n");
        // sb.append("orgName: ").append(name).append("\r\n");
        // sb.append("parent: ").append(parentId).append("\r\n");
        // if (childIds != null) {
        // for (int i = 0; i < childIds.length; i++) {
        // if (i == 0)
        // sb.append("children: ").append("\r\n");
        // sb.append(childIds[i] + " ").append("\r\n");
        // }
        // }
        // return sb.toString();
    }

    /**
     * @return Returns the leaderId.
     */
    public String getLeaderId() {
        return leaderId;
    }

    @Override
    public Integer getIndex() {
        return this.index;
    }

    @Override
    public String getCity() {
        return this.city;
    }

    /**
     * @return Returns the parentId.
     */
    public String getParentId() {
        return parentId;
    }

    @Override
    public List<String> getChildIdList() {
        return childIds;
    }

    @Override
    public List<String> getPersonIdList() {
        return personIds;
    }

    @Override

    @JSONField(serialize = false)
    public List<Person> getPersonList() {

        List<Person> personList = new ArrayList<Person>();
        CtCacheManager cacheManager = CtCacheManager.getInstance();
        for (String personId : personIds) {
            try {
                personList.add(cacheManager.getPersonByID(personId));
            } catch (PersonNotFoundException ex) {
                log.error(ex.getMessage());
            }
        }
        return personList;
    }

    @Override

    @JSONField(serialize = false)
    public List<Role> getRoleList() {

        List<Role> roleList = new ArrayList<Role>();
        CtCacheManager cacheManager = CtCacheManager.getInstance();
        for (String roleId : roleIds) {
            try {
                roleList.add(cacheManager.getOrgRoleByID(roleId));
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
                roleList.add(cacheManager.getOrgRoleByID(roleId));
            } catch (RoleNotFoundException ex) {
            }
        }
        return roleList;
    }

    @Override
    public List<String> getRoleIdList() {
        return roleIds;
    }

    @Override

    @JSONField(serialize = false)
    public List<Org> getChildrenList() {

        List<Org> childList = new ArrayList<Org>();
        CtCacheManager cacheManager = CtCacheManager.getInstance();
        for (String childId : childIds) {
            try {
                if (childId != null && !childId.equals(this.getOrgId())) {
                    childList.add(cacheManager.getOrgByID(childId));
                }

            } catch (OrgNotFoundException ex) {
            }
        }
        return childList;
    }


    @JSONField(serialize = false)
    public Person getLeader() {
        Person leader = null;
        try {
            if (leaderId != null) {
                leader = CtCacheManager.getInstance().getPersonByID(leaderId);
            }
        } catch (PersonNotFoundException ex) {
        }
        return leader;
    }


    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object != null && object instanceof Org) {
            return orgId.equals(((Org) object).getOrgId());
        } else {
            return false;
        }
    }

    @Override
    @JSONField(serialize = false)
    public List<Person> getPersonListRecursively() {

        List<Person> personList = new ArrayList<Person>();

        if (recursivePersonIdLis == null) {
            Set<String> personSet = new LinkedHashSet<String>();
            List<String> personIds = this.getPersonIdList();
            // 加上本机构的人员

            personSet.addAll(personIds);

            // 加上子机构的人员
            List<Org> orgs = this.getChildrenRecursivelyList();
            for (Org org : orgs) {
                personIds = org.getPersonIdList();
                personSet.addAll(personIds);
            }
            recursivePersonIdLis = new ArrayList<String>(personSet);
        }


        for (int i = 0, n = recursivePersonIdLis.size(); i < n; i++) {
            try {
                personList.add(CtCacheManager.getInstance().getPersonByID(recursivePersonIdLis.get(i)));
            } catch (PersonNotFoundException e) {
                e.printStackTrace();
            }

        }
        return personList;


    }

    @Override
    @JSONField(serialize = false)
    public List<Org> getChildrenRecursivelyList() {
        List<Org> childList = new ArrayList<Org>();
        if (recursiveChildIdLis == null) {
            recursiveChildIdLis = new ArrayList<String>();
            List<Org> orgs = this.getChildrenList();
            for (Org org : orgs) {
                recursiveChildIdLis.add(org.getOrgId());
                List<Org> temp = org.getChildrenRecursivelyList();
                for (Org corg : temp) {
                    recursiveChildIdLis.add(corg.getOrgId());
                }
            }
        }

        for (int i = 0, n = recursiveChildIdLis.size(); i < n; i++) {
            try {
                childList.add(CtCacheManager.getInstance().getOrgByID((String) recursiveChildIdLis.get(i)));
            } catch (OrgNotFoundException ex) {
            }
        }
        return childList;


    }

    public List<String> getPersonIds() {
        return personIds;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public List<String> getChildIds() {
        return childIds;
    }

    @Override
    public int compareTo(Org o) {
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
}
