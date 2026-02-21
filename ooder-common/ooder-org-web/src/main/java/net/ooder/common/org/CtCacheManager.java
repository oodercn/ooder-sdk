package net.ooder.common.org;

import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManager;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.hsql.HsqlDbCacheManager;
import net.ooder.org.*;
import net.ooder.org.conf.OrgConstants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CtCacheManager implements Serializable {
    private static CtCacheManager cacheManager;

    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtCacheManager.class);

    // ID caches
    public static final String orgCacheName = "ctOrg";
    public static final String orgLevelNameCacheName = "ctOrgLevelName";
    public static final String roleCacheName = "ctRole";
    public static final String orgRoleNameCacheName = "ctOrgRoleName";
    public static final String personAccountCacheName = "ctPersonAccount";
    public static final String personCacheName = "ctPerson";
    public static final String personDutyNameCacheName = "ctPersonDutyName";
    public static final String personGroupNameCacheName = "ctPersonDutyName";
    public static final String personLevelNameCacheName = "ctPersonLevelName";
    public static final String personPositionNameCacheName = "ctPersonDutyName";
    public static final String personRoleNameCacheName = "ctPersonRoleName";
    public static final String idsCacheName = "ctIds";


    public static CtCacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = new CtCacheManager();
        }
        return cacheManager;
    }

    /* TODO here */
    private boolean cacheEnabled = true;

    private Map<String, Cache> cacheMap = new HashMap<String, Cache>();

    // ID caches
    public Cache idsCache;
    public Cache orgCache;

    public Cache<String, Role> roleCache;


    private Cache<String, String> orgLevelNameCache;

    private Cache<String, String> orgRoleNameCache;

    public Cache<String, String> personAccountCache;

    public Cache<String, Person> personCache;

    private Cache<String, String> personDutyNameCache;

    private Cache<String, String> personGroupNameCache;

    private Cache<String, String> personLevelNameCache;

    private Cache<String, String> personPositionNameCache;

    private Cache<String, String> personRoleNameCache;

    boolean isSupportOrgLevel;
    boolean isSupportOrgRole;
    boolean isSupportPersonDuty;
    boolean isSupportPersonGroup;
    boolean isSupportPersonLevel;
    boolean isSupportPersonPosition;
    boolean isSupportPersonRole;

    /**
     * Creates a new cache manager.
     */
    private CtCacheManager() {
        initCache();
    }


    /**
     * Initializes all caches with the correct size and expiration time.
     */
    private void initHsqlCache() {
        cacheEnabled = HsqlDbCacheManager.isCacheEnabled();

        roleCache = HsqlDbCacheManager.getCache(roleCacheName);
        orgCache = HsqlDbCacheManager.getCache(orgCacheName);
        personCache = HsqlDbCacheManager.getCache(personCacheName);
        personAccountCache = HsqlDbCacheManager.getCache(personAccountCacheName);

        orgLevelNameCache = HsqlDbCacheManager.getCache(orgLevelNameCacheName);

        orgRoleNameCache = HsqlDbCacheManager.getCache(orgRoleNameCacheName);

        personDutyNameCache = HsqlDbCacheManager.getCache(personDutyNameCacheName);

        personGroupNameCache = HsqlDbCacheManager.getCache(personGroupNameCacheName);

        personLevelNameCache = HsqlDbCacheManager.getCache(personLevelNameCacheName);
        ;
        personPositionNameCache = HsqlDbCacheManager.getCache(personPositionNameCacheName);

        personRoleNameCache = HsqlDbCacheManager.getCache(personRoleNameCacheName);

        idsCache = HsqlDbCacheManager.getCache(idsCacheName);

        cacheMap = HsqlDbCacheManager.getAllCache();


    }


    /**
     * Initializes all caches with the correct size and expiration time.
     */
    private void initCache() {
        CacheManager cacheManager = CacheManagerFactory.getInstance().getCacheManager(OrgConstants.CONFIG_KEY.getType());
        cacheEnabled = cacheManager.isCacheEnabled();
        roleCache = cacheManager.getCache(roleCacheName);
        orgCache = cacheManager.getCache(orgCacheName);
        personCache = cacheManager.getCache(personCacheName);
        personAccountCache = cacheManager.getCache(personAccountCacheName);

        orgLevelNameCache = cacheManager.getCache(orgLevelNameCacheName);

        orgRoleNameCache = cacheManager.getCache(orgRoleNameCacheName);

        personDutyNameCache = cacheManager.getCache(personDutyNameCacheName);

        personGroupNameCache = cacheManager.getCache(personGroupNameCacheName);

        personLevelNameCache = cacheManager.getCache(personLevelNameCacheName);

        personPositionNameCache = cacheManager.getCache(personPositionNameCacheName);

        personRoleNameCache = cacheManager.getCache(personRoleNameCacheName);

        idsCache = cacheManager.getCache(idsCacheName);

        cacheMap = cacheManager.getAllCache();


    }


    /**
     * @param cacheName
     * @return
     */
    public Cache getCache(String cacheName) {
        Cache cache = cacheMap.get(cacheName);
        if (cache == null) {
            cache = cacheMap.get(OrgConstants.CONFIG_KEY.getType() + "." + cacheName);
        }

        return cache;
    }

    public void clearPersonCache(String personId) {
        this.personCache.remove(personId);
    }


    public void clearRoleCache(String roleId) {
        Role role = roleCache.get(roleId);
        if (role != null) {
            roleCache.remove(roleId);
        }
    }


    public void clearOrgCache(String orgId) {
        try {
            Org org = this.getOrgByID(orgId);
            List<Person> personList = org.getPersonListRecursively();

            for (Person person : personList) {
                this.personCache.remove(person.getID());
            }


            List<Org> orgList = org.getChildrenRecursivelyList();
            for (Org childOrg : orgList) {
                this.orgCache.remove(childOrg.getOrgId());
            }
            this.orgCache.remove(orgId);

        } catch (OrgNotFoundException e) {
            e.printStackTrace();
        }


    }

    /**
     * @param orgId
     * @return
     * @throws OrgNotFoundException
     */
    public Org getOrgByID(String orgId) throws OrgNotFoundException {
        Org org = null;
        if (!cacheEnabled) {
            org = CtOrgFactory.getOrg(orgId);
            if (org == null) {
                throw new OrgNotFoundException("orgId =" + orgId);
            }
        } else { // Cache is enabled.
            org = (Org) orgCache.get(orgId);
            if (org == null) {
                org = CtOrgFactory.getOrg(orgId);
                if (org == null) {
                    throw new OrgNotFoundException("orgId =" + orgId);
                }
                orgCache.put(orgId, org);
            }
        }

        return org;
    }

    /**
     * @param orgLevelId
     * @return
     * @throws RoleNotFoundException
     */
    public Role getOrgLevelByID(String orgLevelId) throws RoleNotFoundException {
        Role orgLevel = null;
        if (!cacheEnabled) {
            orgLevel = CtRoleFactory.getOrgLevel(orgLevelId);
            if (orgLevel == null) {
                throw new RoleNotFoundException();
            }
        } else { // Cache is enabled.


            orgLevel = (Role) roleCache.get(orgLevelId);
            if (orgLevel == null) {
                orgLevel = CtRoleFactory.getOrgLevel(orgLevelId);
                if (orgLevel == null) {
                    throw new RoleNotFoundException("RoleId =" + orgLevelId);
                }
                roleCache.put(orgLevelId, orgLevel);
                String orgLevelName = orgLevel.getName();
                if (orgLevelName != null) {
                    orgLevelNameCache.put(orgLevelName, orgLevelId);
                }
            }
        }

        return orgLevel;
    }

    /**
     * @param orgLevelName
     * @return
     * @throws RoleNotFoundException
     */
    public Role getOrgLevelByName(String orgLevelName, String sysId) throws RoleNotFoundException {
        Role orgLevel = null;
        if (!cacheEnabled) {
            orgLevel = CtRoleFactory.getOrgLevel(orgLevelName, sysId);
            if (orgLevel == null) {
                throw new RoleNotFoundException();
            }
        } else { // Cache is enabled.
            String orgLevelId = (String) orgLevelNameCache.get(orgLevelName + "[" + sysId + "]");
            if (orgLevelId == null) {
                orgLevel = CtRoleFactory.getOrgLevel(orgLevelName, null);
                if (orgLevel == null) {
                    throw new RoleNotFoundException("org level name:" + orgLevelName);
                }
                orgLevelId = orgLevel.getRoleId();
                orgLevelNameCache.put(orgLevelName + "[" + sysId + "]", orgLevelId);
                roleCache.put(orgLevelId, orgLevel);
            } else {
                orgLevel = getOrgLevelByID(orgLevelId);
            }
        }

        return orgLevel;
    }

    /**
     * @param orgRoleId
     * @return
     * @throws RoleNotFoundException
     */
    public Role getOrgRoleByID(String orgRoleId) throws RoleNotFoundException {
        Role orgRole = null;
        if (!cacheEnabled) {
            orgRole = CtRoleFactory.getOrgRole(orgRoleId);
            if (orgRole == null) {
                throw new RoleNotFoundException();
            }
        } else { // Cache is enabled.
            orgRole = (Role) roleCache.get(orgRoleId);
            if (orgRole == null) {
                orgRole = CtRoleFactory.getOrgRole(orgRoleId);
                if (orgRole == null) {
                    throw new RoleNotFoundException("org role ID:" + orgRoleId);
                }
                roleCache.put(orgRoleId, orgRole);
                String orgRoleName = orgRole.getName();
                if (orgRoleName != null) {
                    orgRoleNameCache.put(orgRoleName, orgRoleId);
                }
            }
        }

        return orgRole;
    }

    /**
     * @param orgRoleName
     * @return
     * @throws RoleNotFoundException
     */
    public Role getOrgRoleByName(String orgRoleName, String sysId) throws RoleNotFoundException {
        Role orgRole = null;
        if (!cacheEnabled) {
            orgRole = CtRoleFactory.getOrgRole(orgRoleName, sysId);
            if (orgRole == null) {
                throw new RoleNotFoundException();
            }
        } else { // Cache is enabled.
            String orgRoleId = (String) orgRoleNameCache.get(orgRoleName);
            if (orgRoleId == null) {
                orgRole = CtRoleFactory.getOrgRole(orgRoleName, null);
                if (orgRole == null) {
                    throw new RoleNotFoundException("org role name:" + orgRoleName);
                }
                orgRoleId = orgRole.getRoleId();
                orgRoleNameCache.put(orgRoleName, orgRoleId);
                roleCache.put(orgRoleId, orgRole);
            } else {
                orgRole = getOrgRoleByID(orgRoleId);
            }
        }

        return orgRole;
    }

    /**
     * @param personAccount
     * @return
     * @throws PersonNotFoundException
     */
    public Person getPersonByAccount(String personAccount) throws PersonNotFoundException {
        if (!cacheEnabled) {
            return CtPersonFactory.getPerson(personAccount, null);
        }
        Person person = null;
        // Cache is enabled.
        String personId = (String) personAccountCache.get(personAccount);
        if (personId == null) {
            person = CtPersonFactory.getPerson(personAccount, null);
            if (person != null) {
                personId = person.getID();
                personCache.put(personId, person);
                personAccountCache.put(personAccount, personId);
            }
        } else {
            person = getPersonByID(personId);
        }

        return person;
    }

    /**
     * @param personId
     * @return
     * @throws PersonNotFoundException
     */
    public Person getPersonByID(String personId) throws PersonNotFoundException {
        Person person = null;
        if (personId == null) {
            throw new PersonNotFoundException("person ID:" + personId);
        }
        if (!cacheEnabled) {
            person = CtPersonFactory.getPerson(personId);
            if (person == null) {
                throw new PersonNotFoundException("person ID:" + personId);
            }
        } else { // cache enabled
            person = (Person) personCache.get(personId);
            if (person == null) {
                person = CtPersonFactory.getPerson(personId);
                if (person == null) {
                    throw new PersonNotFoundException("person ID:" + personId);
                }

                personCache.put(personId, person);
                String personAccount = person.getAccount();
                if (personAccount != null) {
                    personAccountCache.put(personAccount, personId);
                }
            }
        }

        return person;
    }

    /**
     * @param personDutyId
     * @return
     * @throws RoleNotFoundException
     */
    public Role getPersonDutyByID(String personDutyId) throws RoleNotFoundException {
        Role personDuty = null;
        if (!cacheEnabled) {
            personDuty = CtRoleFactory.getPersonDuty(personDutyId);
            if (personDuty == null) {
                throw new RoleNotFoundException("person Duty ID:" + personDutyId);
            }
        } else { // Cache is enabled.
            personDuty = (Role) roleCache.get(personDutyId);
            if (personDuty == null) {
                personDuty = CtRoleFactory.getPersonDuty(personDutyId);
                if (personDuty == null) {
                    throw new RoleNotFoundException("person Duty ID:" + personDutyId);
                }
                roleCache.put(personDutyId, personDuty);
                String personDutyName = personDuty.getName();
                if (personDutyName != null) {
                    personDutyNameCache.put(personDutyName, personDutyId);
                }
            }
        }

        return personDuty;
    }

    /**
     * @param personDutyName
     * @return
     * @throws RoleNotFoundException
     */
    public Role getPersonDutyByName(String personDutyName, String sysId) throws RoleNotFoundException {
        Role personDuty = null;
        if (!cacheEnabled) {
            personDuty = CtRoleFactory.getPersonDuty(personDutyName, null);
            if (personDuty == null) {
                throw new RoleNotFoundException("person Duty name:" + personDutyName);
            }
        } else { // Cache is enabled.
            String personDutyId = (String) personDutyNameCache.get(personDutyName + "[" + sysId + "]");
            if (personDutyId == null) {
                personDuty = CtRoleFactory.getPersonDuty(personDutyName, null);
                if (personDuty == null) {
                    throw new RoleNotFoundException("person Duty name:" + personDutyName);
                }
                personDutyId = personDuty.getRoleId();
                personDutyNameCache.put(personDutyName + "[" + sysId + "]", personDutyId);
                roleCache.put(personDutyId, personDuty);
            } else {
                personDuty = getPersonDutyByID(personDutyId);
            }
        }

        return personDuty;
    }

    /**
     * @param personGroupId
     * @return
     * @throws RoleNotFoundException
     */
    public Role getPersonGroupByID(String personGroupId) throws RoleNotFoundException {
        Role personGroup = null;
        if (!cacheEnabled) {
            personGroup = CtRoleFactory.getPersonGroup(personGroupId);
            if (personGroup == null) {
                throw new RoleNotFoundException("person Group ID:" + personGroupId);
            }
        } else { // Cache is enabled.
            personGroup = (Role) roleCache.get(personGroupId);
            if (personGroup == null) {
                personGroup = CtRoleFactory.getPersonGroup(personGroupId);
                if (personGroup == null) {
                    throw new RoleNotFoundException("person Group ID:" + personGroupId);
                }
                roleCache.put(personGroupId, personGroup);
                String personGroupName = personGroup.getName();
                if (personGroupName != null) {
                    personGroupNameCache.put(personGroupName, personGroupId);
                }
            }
        }

        return personGroup;
    }

    /**
     * @param personGroupName
     * @return
     * @throws RoleNotFoundException
     */
    public Role getPersonGroupByName(String personGroupName, String sysId) throws RoleNotFoundException {
        Role personGroup = null;
        if (!cacheEnabled) {
            personGroup = CtRoleFactory.getPersonGroup(personGroupName, null);
            if (personGroup == null) {
                throw new RoleNotFoundException("person Group name:" + personGroupName);
            }
        } else { // Cache is enabled.
            String personGroupId = (String) personGroupNameCache.get(personGroupName);
            if (personGroupId == null) {
                personGroup = CtRoleFactory.getPersonGroup(personGroupName, sysId);
                if (personGroup == null) {
                    throw new RoleNotFoundException("person Group name:" + personGroupName);
                }
                personGroupId = personGroup.getRoleId();
                personGroupNameCache.put(personGroupName, personGroupId);
                roleCache.put(personGroupId, personGroup);
            } else {
                personGroup = getPersonGroupByID(personGroupId);
            }
        }

        return personGroup;
    }

    /**
     * @param personLevelId
     * @return
     * @throws RoleNotFoundException
     */
    public Role getPersonLevelByID(String personLevelId) throws RoleNotFoundException {
        Role personLevel = null;
        if (!cacheEnabled) {
            personLevel = CtRoleFactory.getPersonLevel(personLevelId);
            if (personLevel == null) {
                throw new RoleNotFoundException("person Level ID:" + personLevelId);
            }
        } else { // Cache is enabled.
            personLevel = (Role) roleCache.get(personLevelId);
            if (personLevel == null) {
                personLevel = CtRoleFactory.getPersonLevel(personLevelId);
                if (personLevel == null) {
                    throw new RoleNotFoundException("person Level ID:" + personLevelId);
                }
                roleCache.put(personLevelId, personLevel);
                String personLevelName = personLevel.getName();
                if (personLevelName != null) {
                    personLevelNameCache.put(personLevelName, personLevelId);
                }
            }
        }

        return personLevel;
    }

    /**
     * @param personLevelName
     * @return
     * @throws RoleNotFoundException
     */
    public Role getPersonLevelByName(String personLevelName, String sysId) throws RoleNotFoundException {
        Role personLevel = null;
        if (!cacheEnabled) {
            personLevel = CtRoleFactory.getPersonLevelByName(personLevelName, sysId);
            if (personLevel == null) {
                throw new RoleNotFoundException("person Level name:" + personLevelName);
            }
        } else { // Cache is enabled.
            String personLevelId = (String) personLevelNameCache.get(personLevelName + "[" + sysId + "]");
            if (personLevelId == null) {
                personLevel = CtRoleFactory.getPersonLevelByName(personLevelName, sysId);
                if (personLevel == null) {
                    throw new RoleNotFoundException("person Level name:" + personLevelName);
                }
                personLevelId = personLevel.getRoleId();
                personLevelNameCache.put(personLevelName + "[" + sysId + "]", personLevelId);
                roleCache.put(personLevelId, personLevel);
            } else {
                personLevel = getPersonLevelByID(personLevelId);
            }
        }

        return personLevel;
    }

    /**
     * @param personPositionId
     * @return
     * @throws RoleNotFoundException
     */
    public Role getPersonPositionByID(String personPositionId) throws RoleNotFoundException {
        Role personPosition = null;
        if (!cacheEnabled) {
            personPosition = CtRoleFactory.getPersonPosition(personPositionId);
            if (personPosition == null) {
                throw new RoleNotFoundException("person Position ID:" + personPositionId);
            }
        } else { // Cache is enabled.
            personPosition = (Role) roleCache.get(personPositionId);
            if (personPosition == null) {
                personPosition = CtRoleFactory.getPersonPosition(personPositionId);
                if (personPosition == null) {
                    throw new RoleNotFoundException("person Position ID:" + personPositionId);
                }
                roleCache.put(personPositionId, personPosition);
                String personPositionName = personPosition.getName();
                if (personPositionName != null) {
                    personPositionNameCache.put(personPositionName, personPositionId);
                }
            }
        }

        return personPosition;
    }

    /**
     * @param personPositionName
     * @return
     * @throws RoleNotFoundException
     */
    public Role getPersonPositionByName(String personPositionName, String sysId) throws RoleNotFoundException {
        Role personPosition = null;
        if (!cacheEnabled) {
            personPosition = CtRoleFactory.getPersonPosition(personPositionName, null);
            if (personPosition == null) {
                throw new RoleNotFoundException("person Position name:" + personPositionName);
            }
        } else { // Cache is enabled.
            String personPositionId = (String) personPositionNameCache.get(personPositionName + "[" + sysId + "]");
            if (personPositionId == null) {
                personPosition = CtRoleFactory.getPersonPosition(personPositionName, null);
                if (personPosition == null) {
                    throw new RoleNotFoundException("person Position name:" + personPositionName);
                }
                personPositionId = personPosition.getRoleId();
                personPositionNameCache.put(personPositionName + "[" + sysId + "]", personPositionId);
                roleCache.put(personPositionId, personPosition);
            } else {
                personPosition = getPersonPositionByID(personPositionId);
            }
        }

        return personPosition;
    }

    /**
     * @return
     * @throws RoleNotFoundException
     */
    public Role getRoleByID(String roleId) throws RoleNotFoundException {
        Role role = null;
        if (!cacheEnabled) {
            role = CtRoleFactory.getRole(roleId);
            if (role == null) {
                throw new RoleNotFoundException("person Role ID:" + roleId);
            }
        } else { // Cache is enabled.
            role = (Role) roleCache.get(roleId);
            if (role == null) {
                role = CtRoleFactory.getRole(roleId);
                if (role == null) {
                    throw new RoleNotFoundException("person Role ID:" + roleId);
                }
                roleCache.put(roleId, role);

            }
        }

        return role;
    }

    /**
     * @param personRoleId
     * @return
     * @throws RoleNotFoundException
     */
    public Role getPersonRoleByID(String personRoleId) throws RoleNotFoundException {
        Role personRole = null;
        if (!cacheEnabled) {
            personRole = CtRoleFactory.getPersonRole(personRoleId);
            if (personRole == null) {
                throw new RoleNotFoundException("person Role ID:" + personRoleId);
            }
        } else { // Cache is enabled.
            personRole = (Role) roleCache.get(personRoleId);
            if (personRole == null) {
                personRole = CtRoleFactory.getPersonRole(personRoleId);
                if (personRole == null) {
                    throw new RoleNotFoundException("person Role ID:" + personRoleId);
                }
                roleCache.put(personRoleId, personRole);
                String personRoleName = personRole.getName();
                if (personRoleName != null) {
                    personRoleNameCache.put(personRoleName, personRoleId);
                }
            }
        }

        return personRole;
    }

    /**
     * @param personRoleName
     * @return
     * @throws RoleNotFoundException
     */
    public Role getPersonRoleByName(String personRoleName, String sysId) throws RoleNotFoundException {
        Role personRole = null;
        if (!cacheEnabled) {
            personRole = CtRoleFactory.getPersonRole(personRoleName, sysId);
            if (personRole == null) {
                throw new RoleNotFoundException("person Role name:" + personRoleName);
            }
        } else { // Cache is enabled.
            String personRoleId = (String) personRoleNameCache.get(personRoleName + "[" + sysId + "]");
            if (personRoleId == null) {
                personRole = CtRoleFactory.getPersonRole(personRoleName, sysId);
                if (personRole == null) {
                    throw new RoleNotFoundException("person Role name:" + personRoleName);
                }
                personRoleId = personRole.getRoleId();
                personRoleNameCache.put(personRoleName + "[" + sysId + "]", personRoleId);
                roleCache.put(personRoleId, personRole);
            } else {
                personRole = getPersonRoleByID(personRoleId);
            }
        }

        return personRole;
    }


    /**
     * @return Returns the cacheEnabled.
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     */
    public void invalidate() {
        for (Iterator it = cacheMap.values().iterator(); it.hasNext(); ) {
            Cache cache = (Cache) it.next();
            if (cache != null) {
                cache.clear();
            }
        }

        initCache();
    }
}
