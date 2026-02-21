package net.ooder.common.org;

import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.annotation.RoleType;
import net.ooder.common.cache.Cache;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.org.service.OrgWebManager;
import net.ooder.config.UserBean;
import net.ooder.context.JDSActionContext;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.msg.PersonPrivateGroup;
import net.ooder.org.*;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.server.SubSystem;
import net.ooder.server.service.SysWebManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CtOrgManager implements OrgManager, Serializable {
    protected static Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtOrgManager.class);

    protected static OrgWebManager orgWebManager = null;

    private static Boolean isSupportOrgLevel;

    private static Boolean isSupportOrgRole;

    private static Boolean isSupportPersonDuty;

    private static Boolean isSupportPersonGroup;

    private static Boolean isSupportPersonLevel;

    private static Boolean isSupportPersonPosition;

    private static Boolean isSupportPersonRole;

    private static boolean orgWebManagerInvalid;

    private static CtCacheManager cacheManager;

    ConfigCode configCode;
    public SubSystem subSystem;

    public CtOrgManager() {
        cacheManager = CtCacheManager.getInstance();
        checkWSOrgManager();
    }

    SysWebManager getSysWebManager() {
        return  EsbUtil.parExpression(SysWebManager.class);
    }

    public void clearOrgCache(String orgId) {
        if (orgId != null) {
            cacheManager.clearOrgCache(orgId);
        }

    }

    public void clearPersonCache(String personId) {
        if (personId != null) {
            cacheManager.clearPersonCache(personId);
        }

    }

    public void clearRoleCache(String roleId) {

        if (roleId != null) {
            cacheManager.clearRoleCache(roleId);
        }

    }

    @Override
    public void init(ConfigCode configCode) {
        log.info("start init " + configCode);
        if (configCode == null) {
            configCode = UserBean.getInstance().getConfigName();
        }
        this.configCode = configCode;

    }

    public static void invalidateWSOrgMananger() {
        orgWebManagerInvalid = true;
        if (cacheManager == null) {
            cacheManager = CtCacheManager.getInstance();
        }

        if (cacheManager.isCacheEnabled()) {
            cacheManager.invalidate();
        }
    }

    private void checkWSOrgManager() {
        if (orgWebManagerInvalid || orgWebManager == null) {

            orgWebManager = (OrgWebManager) EsbUtil.parExpression(OrgWebManager.class);

        }
    }

    public boolean verifyPerson(String account, String password) throws PersonNotFoundException {
        return true;
    }

    public List<Role> getOrgLevels(String sysId) {

        checkWSOrgManager();
        List<Role> orgLevels = new ArrayList<Role>();
        try {
            orgLevels = CtRoleFactory.toClient(orgWebManager.getOrgLevels(sysId).get());
        } catch (JDSException e) {
            e.printStackTrace();
        }

        if (orgLevels != null) {
            Cache cache = cacheManager.getCache(CtCacheManager.roleCacheName);
            Cache nameCache = cacheManager.getCache(CtCacheManager.orgLevelNameCacheName);
            cache.clear();
            nameCache.clear();
            for (Role orgLevel : orgLevels) {
                cache.put(orgLevel.getRoleId(), orgLevel);
                nameCache.put(orgLevel.getName(), orgLevel.getRoleId());
            }
        }
        return orgLevels;
    }


    @Override
    public void reloadAll() {
        try {
            cacheManager.invalidate();
            this.orgWebManager.reLoadAll().get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    public List<Role> getOrgLevelsByNum(String orgLevelNum) {
        return this.getOrgLevelsByNum(orgLevelNum, JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public List<Role> getOrgLevelsByNum(String orgLevelNum, String sysId) {
        if (!isSupportOrgLevel() || orgLevelNum == null || orgLevelNum.equals("")) {
            return new ArrayList<Role>();
        }

        checkWSOrgManager();
        List<Role> orgLevels = new ArrayList<Role>();
        try {
            orgLevels = CtRoleFactory.toClient(orgWebManager.getOrgLevelsByNum(orgLevelNum, sysId).get());
        } catch (JDSException e) {
            e.printStackTrace();
        }

        if (orgLevels != null) {
            Cache cache = cacheManager.getCache(CtCacheManager.roleCacheName);
            Cache nameCache = cacheManager.getCache(CtCacheManager.orgLevelNameCacheName);
            for (Role orgLevel : orgLevels) {
                cache.put(orgLevel.getRoleId(), orgLevel);
                nameCache.put(orgLevel.getName() + "[" + sysId + "]", orgLevel.getRoleId());
            }
        } else {
            orgLevels = new ArrayList<Role>();
        }
        return orgLevels;
    }

    @Override
    public Person registerPerson(String accountName, String enName, String systemCode) {
        Person person = null;
        try {
            person = OrgManagerFactory.getOrgManager().getPersonByAccount(accountName);
        } catch (PersonNotFoundException e) {
            try {
                person = orgWebManager.registerPerson(accountName, enName, systemCode).get();
            } catch (JDSException ee) {
                ee.printStackTrace();
            }
        }
        return person;
    }


    public List<Role> getOrgRoles(String sysId) {
        try {
            if (!isSupportOrgRole()) {
                return new ArrayList<Role>();
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }

        checkWSOrgManager();
        List<Role> orgRoles = new ArrayList<Role>();

        try {
            orgRoles = CtRoleFactory.toClient(orgWebManager.getOrgRoles(sysId).get());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (orgRoles != null) {
            Cache cache = cacheManager.getCache(CtCacheManager.roleCacheName);
            Cache nameCache = cacheManager.getCache(CtCacheManager.orgRoleNameCacheName);
            cache.clear();
            nameCache.clear();
            for (Role orgRole : orgRoles) {

                cache.put(orgRole.getRoleId(), orgRole);
                nameCache.put(orgRole.getName(), orgRole.getRoleId());
            }
        }
        return orgRoles;
    }

    public List<Org> getTopOrgs(String sysId) {
        checkWSOrgManager();
        SubSystem subSystem = JDSServer.getClusterClient().getSystem(sysId);
        List<Org> orgs = new ArrayList<>();
        if (subSystem.getOrgId() != null && !subSystem.getOrgId().equals("")) {
            try {
                Org org = this.getOrgByID(subSystem.getOrgId());
                orgs.add(org);
            } catch (OrgNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                orgs = CtOrgFactory.toClient(orgWebManager.getTopOrgs(sysId).get());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return orgs;
    }

    public List<Org> getTopOrgs() {
        checkWSOrgManager();
        return this.getTopOrgs(JDSActionContext.getActionContext().getSystemCode());
    }

    public List<Org> getOrgs(String sysId) {
        checkWSOrgManager();
        List<Org> orgList = new ArrayList<Org>();
        List<String> orgIds = getOrgIds(sysId);
        for (String orgId : orgIds) {
            try {
                orgList.add(getOrgByID(orgId));
            } catch (Exception e) {
            }
        }

        return orgList;
    }

    public List<Org> getOrgsByOrgLevelID(String levelId) {
        List<Org> orgList = new ArrayList<Org>();
        try {
            Role orgLevel = cacheManager.getOrgLevelByID(levelId);
            orgList = orgLevel.getOrgList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return orgList;
    }

    @Override
    public List<Org> getOrgsByOrgLevelName(String levelName) {
        return null;
    }

    public List<Org> getOrgsByOrgLevelName(String levelName, String sysId) {
        List<Org> orgList = new ArrayList<Org>();
        try {
            Role orgLevel = cacheManager.getOrgLevelByName(levelName, sysId);
            orgList = orgLevel.getOrgList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return orgList;
    }

    public List<Role> getPersonDuties(String sysId) {

        checkWSOrgManager();
        List<Role> personDutys = new ArrayList<Role>();

        try {

            personDutys = CtRoleFactory.toClient(orgWebManager.getPersonDuties(sysId).get());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (personDutys != null) {
            Cache cache = cacheManager.getCache(CtCacheManager.roleCacheName);
            Cache nameCache = cacheManager.getCache(CtCacheManager.personDutyNameCacheName);
            cache.clear();
            nameCache.clear();
            for (Role personDuty : personDutys) {

                cache.put(personDuty.getRoleId(), personDuty);
                nameCache.put(personDuty.getName(), personDuty.getRoleId());
            }
        }
        return personDutys;
    }

    public List<Role> getPersonDutiesByNum(String personDutyNum, String sysId) {

        checkWSOrgManager();
        List<Role> personDutys = new ArrayList<Role>();

        try {
            personDutys = CtRoleFactory.toClient(orgWebManager.getPersonDutiesByNum(personDutyNum, sysId).get());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return personDutys;
    }

    public List<Role> getPersonGroups(String sysId) {

        checkWSOrgManager();
        List<Role> personGroups = new ArrayList<Role>();
        try {
            personGroups = CtRoleFactory.toClient(orgWebManager.getPersonGroups(sysId).get());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (personGroups != null) {
            Cache cache = cacheManager.getCache(CtCacheManager.roleCacheName);
            Cache nameCache = cacheManager.getCache(CtCacheManager.personGroupNameCacheName);
            cache.clear();
            nameCache.clear();
            for (Role personGroup : personGroups) {

                cache.put(personGroup.getRoleId(), personGroup);
                nameCache.put(personGroup.getName(), personGroup.getRoleId());
            }
        }

        return personGroups;
    }

    public List<Role> getPersonLevels(String sysId) {

        checkWSOrgManager();
        List<Role> personLevels = new ArrayList<Role>();
        try {
            personLevels = CtRoleFactory.toClient(orgWebManager.getPersonLevels(sysId).get());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (personLevels != null) {
            Cache cache = cacheManager.getCache(CtCacheManager.roleCacheName);
            Cache nameCache = cacheManager.getCache(CtCacheManager.personLevelNameCacheName);
            cache.clear();
            nameCache.clear();
            for (Role personLevel : personLevels) {

                cache.put(personLevel.getRoleId(), personLevel);
                nameCache.put(personLevel.getName(), personLevel.getRoleId());
            }
        }

        return personLevels;
    }

    public List<Role> getPersonLevelsByNum(String personLevelNum, String sysId) throws RoleNotFoundException {

        checkWSOrgManager();
        List<Role> personLevels = new ArrayList<Role>();
        try {
            personLevels = CtRoleFactory.toClient(orgWebManager.getPersonLevelsByNum(personLevelNum, sysId).get());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return personLevels;
    }

    public List<Role> getPersonPositions(String sysId) {

        checkWSOrgManager();
        List<Role> personPositions = new ArrayList<Role>();
        try {
            personPositions = CtRoleFactory.toClient(orgWebManager.getPersonPositions(sysId).get());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (personPositions != null) {
            Cache cache = cacheManager.getCache(CtCacheManager.roleCacheName);
            Cache nameCache = cacheManager.getCache(CtCacheManager.personPositionNameCacheName);

            nameCache.clear();
            for (Role personPosition : personPositions) {

                cache.put(personPosition.getRoleId(), personPosition);
                nameCache.put(personPosition.getName(), personPosition.getRoleId());
            }
        }

        return personPositions;
    }


    public List<Role> getAllRoles(String sysId) {
        checkWSOrgManager();
        List<Role> roles = new ArrayList<Role>();
        try {
            roles = CtRoleFactory.toClient(orgWebManager.getAllRoles(sysId).get());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (roles != null) {
            Cache cache = cacheManager.getCache(CtCacheManager.roleCacheName);
            Cache nameCache = cacheManager.getCache(CtCacheManager.personRoleNameCacheName);
            nameCache.clear();
            for (Role personRole : roles) {
                cache.put(personRole.getRoleId(), personRole);
                nameCache.put(personRole.getName(), personRole.getRoleId());
            }
        }
        return roles;
    }


    public List<Role> getPersonRoles(String sysId) {

        checkWSOrgManager();
        List<Role> personRoles = new ArrayList<Role>();
        try {

            personRoles = CtRoleFactory.toClient(orgWebManager.getPersonRoles(sysId).get());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (personRoles != null) {
            Cache cache = cacheManager.getCache(CtCacheManager.roleCacheName);
            Cache nameCache = cacheManager.getCache(CtCacheManager.personRoleNameCacheName);
            nameCache.clear();
            for (Role personRole : personRoles) {
                cache.put(personRole.getRoleId(), personRole);
                nameCache.put(personRole.getName(), personRole.getRoleId());
            }
        }
        return personRoles;
    }


    public List<Person> getPersons(String sysId) {
        checkWSOrgManager();
        List<Person> persons = null;

        try {
            persons = CtPersonFactory.toClient(orgWebManager.getPersons(sysId).get());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (persons != null) {
            Cache cache = cacheManager.getCache(CtCacheManager.personCacheName);
            Cache accountCache = cacheManager.getCache(CtCacheManager.personAccountCacheName);
            cache.setStopPutWhenFull(true);
            accountCache.setStopPutWhenFull(true);
            for (Person person : persons) {
                cache.put(person.getID(), person);
                accountCache.put(person.getAccount(), person.getID());
            }
            cache.setStopPutWhenFull(false);
            accountCache.setStopPutWhenFull(false);
        }
        return persons;
    }

    public List<Person> getPersonsByOrgID(String orgId) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = cacheManager.getOrgByID(orgId).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    /**
     * @see net.ooder.org.OrgManager#getPersonsByPersonDutyID(String)
     */
    public List<Person> getPersonsByPersonDutyID(String personDutyId) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonDutyByID(personDutyId).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    /**
     * @see net.ooder.org.OrgManager#getPersonsByPersonDutyName(String)
     */
    public List<Person> getPersonsByPersonDutyName(String personDutyName) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonDutyByName(personDutyName).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    @Override
    public List<Person> getPersonsByPersonDutyName(String personDutyName, String sysId) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonDutyByName(personDutyName, sysId).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    /**
     * @see net.ooder.org.OrgManager#getPersonsByPersonGroupID(String)
     */
    public List<Person> getPersonsByPersonGroupID(String personGroupId) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonGroupByID(personGroupId).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    /**
     * @see net.ooder.org.OrgManager#getPersonsByPersonGroupName(String)
     */
    public List<Person> getPersonsByPersonGroupName(String personGroupName) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonGroupByName(personGroupName).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    @Override
    public List<Person> getPersonsByPersonGroupName(String personGroupName, String sysId) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonGroupByName(personGroupName, sysId).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    /**
     * @see net.ooder.org.OrgManager#getPersonsByPersonLevelID(String)
     */
    public List<Person> getPersonsByPersonLevelID(String personLevelId) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonLevelByID(personLevelId).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    /**
     * @see net.ooder.org.OrgManager#getPersonsByPersonLevelName(String)
     */
    public List<Person> getPersonsByPersonLevelName(String personLevelName) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonLevelByName(personLevelName).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    @Override
    public List<Person> getPersonsByPersonLevelName(String personLevelName, String sysId) {
        return null;
    }

    /**
     * @see net.ooder.org.OrgManager#getPersonsByPersonPositionID(String)
     */
    public List<Person> getPersonsByPersonPositionID(String personPositionId) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonPositionByID(personPositionId).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    /**
     * @see net.ooder.org.OrgManager#getPersonsByPersonPositionName(String)
     */
    public List<Person> getPersonsByPersonPositionName(String personPositionName) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonPositionByName(personPositionName).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    @Override
    public List<Person> getPersonsByPersonPositionName(String personPositionName, String sysId) {
        return null;
    }

    /**
     * @see net.ooder.org.OrgManager#getPersonsByPersonRoleID(String)
     */
    public List<Person> getPersonsByPersonRoleID(String personRoleId) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonRoleByID(personRoleId).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    /**
     * @see net.ooder.org.OrgManager#getPersonsByPersonRoleName(String)
     */
    public List<Person> getPersonsByPersonRoleName(String personRoleName) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonRoleByName(personRoleName).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    @Override
    public List<Person> getPersonsByPersonRoleName(String personRoleName, String sysId) {
        List<Person> personList = new ArrayList<Person>();
        try {
            personList = getPersonRoleByName(personRoleName, sysId).getPersonList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return personList;
    }

    @Override
    public boolean isSupportOrgLevel() {
        return false;
    }

    public boolean isSupportOrgLevel(String sysId) {
        if (isSupportOrgLevel == null) {

            checkWSOrgManager();
            try {
                isSupportOrgLevel = new Boolean(orgWebManager.isSupportOrgLevel(sysId).get());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (isSupportOrgLevel != null) {
            return isSupportOrgLevel.booleanValue();
        }
        return false;
    }

    public boolean isSupportOrgRole(String sysId) {
        if (isSupportOrgRole == null) {

            checkWSOrgManager();
            try {
                isSupportOrgRole = new Boolean(orgWebManager.isSupportOrgRole(sysId).get());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (isSupportOrgRole != null) {
            return isSupportOrgRole.booleanValue();
        }
        return false;
    }

    public boolean isSupportPersonDuty(String sysId) {
        if (isSupportPersonDuty == null) {

            checkWSOrgManager();
            try {
                isSupportPersonDuty = new Boolean(orgWebManager.isSupportPersonDuty(sysId).get());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (isSupportPersonDuty != null) {
            return isSupportPersonDuty.booleanValue();
        }
        return false;
    }

    public boolean isSupportPersonGroup(String sysId) {
        if (isSupportPersonGroup == null) {

            checkWSOrgManager();
            try {
                isSupportPersonGroup = new Boolean(orgWebManager.isSupportPersonGroup(sysId).get());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (isSupportPersonGroup != null) {
            return isSupportPersonGroup.booleanValue();
        }
        return false;
    }

    public boolean isSupportPersonLevel(String sysId) {
        if (isSupportPersonLevel == null) {

            checkWSOrgManager();
            try {
                isSupportPersonLevel = new Boolean(orgWebManager.isSupportPersonLevel(sysId).get());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (isSupportPersonLevel != null) {
            return isSupportPersonLevel.booleanValue();
        }
        return false;
    }

    public boolean isSupportPersonPosition(String sysId) {
        if (isSupportPersonPosition == null) {

            checkWSOrgManager();
            try {
                isSupportPersonPosition = new Boolean(orgWebManager.isSupportPersonPosition(sysId).get());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (isSupportPersonPosition != null) {
            return isSupportPersonPosition.booleanValue();
        }
        return false;
    }

    public boolean isSupportPersonRole(String sysId) {
        if (isSupportPersonRole == null) {

            checkWSOrgManager();
            try {
                isSupportPersonRole = new Boolean(orgWebManager.isSupportPersonRole(sysId).get());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (isSupportPersonRole != null) {
            return isSupportPersonRole.booleanValue();
        }
        return false;
    }

    public Org getOrgByID(String orgId) throws OrgNotFoundException {
        return cacheManager.getOrgByID(orgId);
    }

    public Role getOrgLevelByID(String orgLevelId) throws RoleNotFoundException {
        if (!isSupportOrgLevel()) {
            return null;
        }
        return cacheManager.getOrgLevelByID(orgLevelId);
    }

    @Override
    public Role getOrgLevelByName(String orgLevelName) throws RoleNotFoundException {
        return this.getOrgLevelByName(orgLevelName, JDSActionContext.getActionContext().getSystemCode());
    }

    public Role getOrgLevelByName(String orgLevelName, String sysId) throws RoleNotFoundException {
        if (!isSupportOrgLevel()) {
            return null;
        }
        return cacheManager.getOrgLevelByName(orgLevelName, sysId);
    }

    public Role getOrgRoleByID(String orgRoleId) {
        try {
            if (!isSupportOrgRole()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }

        try {
            return cacheManager.getOrgRoleByID(orgRoleId);
        } catch (RoleNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public List<Role> getOrgLevels() {

        return this.getOrgLevels(JDSActionContext.getActionContext().getSystemCode());
    }

    public Role getOrgRoleByName(String orgRoleName, String sysId) {
        try {
            if (!isSupportOrgRole()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        try {
            return cacheManager.getOrgRoleByName(orgRoleName, sysId);
        } catch (RoleNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Person getPersonByAccount(String personAccount) throws PersonNotFoundException {
        return cacheManager.getPersonByAccount(personAccount);
    }

    public Person getPersonByID(String personId) throws PersonNotFoundException {
        return cacheManager.getPersonByID(personId);
    }

    public Role getPersonDutyByID(String personDutyId) throws RoleNotFoundException {
        try {
            if (!isSupportPersonDuty()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return cacheManager.getPersonDutyByID(personDutyId);
    }

    @Override
    public Role getPersonDutyByName(String personDutyName) throws RoleNotFoundException {
        return this.getPersonDutyByName(personDutyName, JDSActionContext.getActionContext().getSystemCode());
    }

    public Role getPersonDutyByName(String personDutyName, String sysId) throws RoleNotFoundException {
        try {
            if (!isSupportPersonDuty()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return (Role) cacheManager.getPersonDutyByName(personDutyName, sysId);
    }

    public Role getPersonGroupByID(String personGroupId) throws RoleNotFoundException {
        try {
            if (!isSupportPersonGroup()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return cacheManager.getPersonGroupByID(personGroupId);
    }

    @Override
    public Role getPersonGroupByName(String personGroupName) throws RoleNotFoundException {
        return this.getPersonGroupByName(personGroupName, JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public List<Role> getPersonGroups() {
        return this.getPersonGroups(JDSActionContext.getActionContext().getSystemCode());
    }

    public Role getPersonGroupByName(String personGroupName, String sysId) throws RoleNotFoundException {
        try {
            if (!isSupportPersonGroup()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return cacheManager.getPersonGroupByName(personGroupName, sysId);
    }

    public Role getPersonLevelByID(String personLevelId) throws RoleNotFoundException {
        try {
            if (!isSupportPersonLevel()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return cacheManager.getPersonLevelByID(personLevelId);
    }

    @Override
    public Role getPersonLevelByName(String personLevelName) throws RoleNotFoundException {
        return this.getPersonLevelByName(personLevelName, JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public List<Role> getPersonLevels() {
        return this.getPersonLevels(JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public List<Role> getPersonLevelsByNum(String personLevelNum) throws RoleNotFoundException {
        return this.getPersonLevelsByNum(personLevelNum, JDSActionContext.getActionContext().getSystemCode());
    }

    public Role getPersonLevelByName(String personLevelName, String sysId) throws RoleNotFoundException {
        try {
            if (!isSupportPersonLevel()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return (Role) cacheManager.getPersonLevelByName(personLevelName, sysId);
    }

    public Role getPersonPositionByID(String personPositionId) throws RoleNotFoundException {
        try {
            if (!isSupportPersonPosition()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return cacheManager.getPersonPositionByID(personPositionId);
    }

    @Override
    public Role getPersonPositionByName(String personPositionName) throws RoleNotFoundException {
        return this.getPersonPositionByName(personPositionName, JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public List<Role> getPersonPositions() {
        return this.getPersonPositions(JDSActionContext.getActionContext().getSystemCode());
    }

    public Role getPersonPositionByName(String personPositionName, String sysId) throws RoleNotFoundException {
        try {
            if (!isSupportPersonPosition()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return cacheManager.getPersonPositionByName(personPositionName, sysId);
    }

    public Role getPersonRoleByID(String personRoleId) throws RoleNotFoundException {
        try {
            if (!isSupportPersonRole()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return cacheManager.getPersonRoleByID(personRoleId);
    }

    public Role getPersonRoleByName(String personRoleName) throws RoleNotFoundException {
        try {
            if (!isSupportPersonRole()) {
                return null;
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return cacheManager.getPersonRoleByName(personRoleName, JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public Role getPersonRoleByName(String personRoleName, String sysId) throws RoleNotFoundException {
        return cacheManager.getPersonRoleByName(personRoleName, sysId);
    }

    @Override
    public List<Role> getPersonRoles() {
        return this.getPersonRoles(JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public List<Role> getAllRoles() {
        return this.getAllRoles(JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public List<Person> getPersons() {
        return this.getPersons(JDSActionContext.getActionContext().getSystemCode());
    }


    @Override
    public Role getRoleByID(String roleId) throws RoleNotFoundException {

        Role role = cacheManager.getRoleByID(roleId);

        return role;


    }


    @Override
    public List<Role> getOrgRoles() {
        return this.getOrgRoles(JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public List<Org> getOrgs() {
        return this.getOrgs(JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public List<String> getOrgIds() {
        return this.getOrgIds(JDSActionContext.getActionContext().getSystemCode());
    }

    public Role getRoleByName(RoleType type, String roleName) throws RoleNotFoundException {
        return this.getRoleByName(type, roleName, JDSActionContext.getActionContext().getSystemCode());
    }

    public Role getRoleByName(RoleType type, String roleName, String sysId) throws RoleNotFoundException {
        Role role = null;
        switch (type) {
            case Role:
                role = cacheManager.getOrgRoleByName(roleName, sysId);
            case Duty:
                role = cacheManager.getPersonDutyByName(roleName, sysId);
            case Group:
                role = cacheManager.getPersonGroupByName(roleName, sysId);
            case Position:
                role = cacheManager.getPersonPositionByName(roleName, sysId);
            default:
                role = cacheManager.getPersonLevelByName(roleName, sysId);
        }

        return role;

    }

    @Override
    public List<Org> getOrgsByRoleID(String roleId) {
        List<Org> orgList = new ArrayList<Org>();
        try {
            Role orgRole = cacheManager.getOrgRoleByID(roleId);
            orgList = orgRole.getOrgList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return orgList;
    }

    @Override
    public List<Org> getOrgsByRoleName(String roleName) {
        List<Org> orgList = new ArrayList<Org>();
        try {
            Role orgRole = cacheManager.getOrgRoleByName(roleName, JDSActionContext.getActionContext().getSystemCode());
            orgList = orgRole.getOrgList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return orgList;
    }

    public List<Org> getOrgsByRoleName(String roleName, String sysId) {
        List<Org> orgList = new ArrayList<Org>();
        try {
            Role orgRole = cacheManager.getOrgRoleByName(roleName, sysId);
            orgList = orgRole.getOrgList();
        } catch (Exception ex) {
            log.error("", ex);

        }
        return orgList;
    }

    @Override
    public Person getPersonByMobile(String mobilenum) throws PersonNotFoundException {
        try {
            return orgWebManager.getPersonByMobile(mobilenum).get();
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            throw new PersonNotFoundException();
        }

    }

    @Override
    public Person getPersonByEmail(String email) throws PersonNotFoundException {

        try {
            return orgWebManager.getPersonByEmail(email).get();
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            throw new PersonNotFoundException();
        }
    }

    @Override
    public List<Role> getPersonDuties() {
        return this.getPersonDuties(JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public List<Role> getPersonDutiesByNum(String personDutyNum) {
        return this.getPersonDutiesByNum(personDutyNum, JDSActionContext.getActionContext().getSystemCode());
    }

    @Override
    public boolean isSupportRole() throws JDSException {
        return orgWebManager.isSupportRole(JDSActionContext.getActionContext().getSystemCode()).get();

    }

    @Override
    public boolean isSupportPersonDuty() throws JDSException {
        return orgWebManager.isSupportPersonDuty(JDSActionContext.getActionContext().getSystemCode()).get();
    }

    @Override
    public boolean isSupportPersonGroup() throws JDSException {
        return orgWebManager.isSupportPersonGroup(JDSActionContext.getActionContext().getSystemCode()).get();
    }

    @Override
    public boolean isSupportPersonLevel() throws JDSException {
        return orgWebManager.isSupportPersonLevel(JDSActionContext.getActionContext().getSystemCode()).get();
    }

    @Override
    public boolean isSupportPersonPosition() throws JDSException {
        return orgWebManager.isSupportPersonPosition(JDSActionContext.getActionContext().getSystemCode()).get();
    }

    @Override
    public boolean isSupportPersonRole() throws JDSException {
        return orgWebManager.isSupportPersonRole(JDSActionContext.getActionContext().getSystemCode()).get();
    }

    @Override
    public PersonPrivateGroup getPrivateGroupById(String personGroupId) throws JDSException {
        // TODO Auto-generated method stub
        return orgWebManager.getPrivateGroupById(personGroupId).get();
    }

    @Override
    public boolean isSupportOrgRole() throws JDSException {
        return orgWebManager.isSupportOrgRole(JDSActionContext.getActionContext().getSystemCode()).get();
    }

    @Override
    public List<Org> getOrgsByOrgRoleName(String roleName) {
        return this.getOrgsByOrgRoleName(roleName, JDSActionContext.getActionContext().getSystemCode());
    }


    public List<Org> getOrgsByOrgRoleName(String roleName, String sysId) {
        try {
            return orgWebManager.getOrgsByOrgRoleName(roleName, sysId).get();
        } catch (JDSException e) {
            return new ArrayList<Org>();
        }
    }

    @Override
    public List<Org> getOrgsByOrgRoleID(String roleId) {
        try {
            return orgWebManager.getOrgsByOrgRoleID(roleId).get();
        } catch (JDSException e) {
            return new ArrayList<Org>();
        }
    }

    @Override
    public Role getOrgRoleByName(String orgRoleName) {
        return this.getOrgRoleByName(orgRoleName, JDSActionContext.getActionContext().getSystemCode());
    }


    public List<String> getOrgIds(String sysId) {
        List<String> orgIds = new ArrayList<String>();
        Cache idsCache = cacheManager.getCache(CtCacheManager.idsCacheName);
        orgIds = (List<String>) idsCache.get("orgIds[" + sysId + "]");
        if (orgIds != null) {
            return orgIds;
        }

        checkWSOrgManager();

        try {
            orgIds = orgWebManager.getOrgIds(sysId).get();
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // put into cache.
        idsCache.put("orgIds[" + sysId + "]", orgIds);

        return orgIds;
    }

}
