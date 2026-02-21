package net.ooder.common.org;

import net.ooder.annotation.Operator;
import net.ooder.common.JDSException;
import net.ooder.config.ListResultModel;
import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManager;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5;
import net.ooder.common.org.service.OrgAdminService;
import net.ooder.context.JDSActionContext;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.*;
import net.ooder.org.conf.OrgConstants;
import net.ooder.org.query.OrgCondition;
import net.ooder.org.query.OrgConditionKey;
import net.ooder.server.OrgManagerFactory;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;


public class CtOrgAdminManager {

    private static CtOrgAdminManager instance;
    public static final String THREAD_LOCK = "Thread Lock";
    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtCacheManager.class);
    public static final String OrgRootId = "00000000-0000-0000-0000-000000000000";
    private Cache<String, Org> orgCache;
    private Cache<String, Person> personCache;
    private Cache<String, String> personAccountCache;
    public static final String orgCacheName = "CtAdminOrg";
    public static final String orgPersonName = "CtAdminPerson";
    public static final String orgPersonAccontName = "CtAccountPerson";

    public static CtOrgAdminManager getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new CtOrgAdminManager();
                }
            }
        }
        return instance;
    }


    CtOrgAdminManager() {
        CacheManager cacheManager = CacheManagerFactory.getInstance().getCacheManager(OrgConstants.CONFIG_KEY.getType());
        orgCache = cacheManager.getCache(orgCacheName);
        personCache = cacheManager.getCache(orgPersonName);
        personAccountCache = cacheManager.getCache(orgPersonAccontName);
    }


    public Org getOrgById(String orgId) throws JDSException {
        Org org = null;
        if (orgId != null) {
            org = orgCache.get(orgId);
            if (org == null) {
                OrgCondition condition = new OrgCondition(OrgConditionKey.ORG_ORGID, Operator.EQUALS, orgId);
                List<Org> orgs = CtOrgAdminManager.getInstance().findOrgs(condition).get();
                if (orgs.size() > 0) {
                    Org eiorg = orgs.get(0);
                    org = new CtOrg(eiorg);
                    orgCache.put(orgId, org);
                }
            }
        }

        return org;
    }

    public Person getPersonById(String personId) throws JDSException {
        Person person = null;
        if (personId != null) {
            person = personCache.get(personId);
            if (person == null) {
                OrgCondition condition = new OrgCondition(OrgConditionKey.PERSON_PERSONID, Operator.EQUALS, personId);
                List<Person> persons = this.findPersons(condition).get();
                if (persons != null && persons.size() > 0) {
                    Person eiperson = persons.get(0);
                    person = new CtPerson(eiperson);
                    personCache.put(personId, person);
                    personAccountCache.put(person.getAccount(), personId);
                }
            }
        }
        return person;
    }

    public Person getPersonByAccount(String account) throws JDSException {
        Person person = null;
        if (account != null) {
            String personId = personAccountCache.get(account);
            if (personId != null) {
                person = this.getPersonById(personId);
            }
            if (person == null) {
                OrgCondition condition = new OrgCondition(OrgConditionKey.PERSON_USERID, Operator.EQUALS, account);
                List<Person> persons = this.findPersons(condition).get();
                if (persons != null && persons.size() > 0) {
                    Person eiperson = persons.get(0);
                    person = new CtPerson(eiperson);
                    personCache.put(personId, person);
                    personAccountCache.put(person.getAccount(), personId);
                }
            }
        }
        return person;
    }


    public Org createTopOrg(String sysId, String name) throws JDSException {
        CtOrg org = new CtOrg();
        org.setParentId(OrgRootId);
        org.setOrgId(UUID.randomUUID().toString());
        org.setName(name);
        this.saveOrg(org);
        Person person = createPerson(org.getOrgId(), org.getOrgId(), "admin");
        org.setLeaderId(person.getID());
        this.saveOrg(org);
        return org;

    }

    public Person createPerson(String orgId, String account, String name) throws JDSException {

        CtPerson person = new CtPerson(UUID.randomUUID().toString());
        person.setAccount(account);
        person.setName(name);
        person.setOrgId(orgId);
        person.setAccount(account);
        person.setPassword(MD5.getHashString("admin"));
        this.savePerson(person);
        return person;
    }

    public ListResultModel<List<Org>> findOrgs(OrgCondition condition) throws JDSException {
        return this.getOrgAdminService().findOrgs(condition);
    }


    public ListResultModel<List<Person>> findPersons(@RequestBody OrgCondition condition) throws JDSException {
        if (condition == null) {
            condition = new OrgCondition(OrgConditionKey.PERSON_NAME, Operator.LIKE, "%%");
        }
        return this.getOrgAdminService().findPersons(condition);
    }


    public ListResultModel<List<Role>> findRoles(@RequestBody OrgCondition condition) throws JDSException {
        return this.getOrgAdminService().findRoles(condition);
    }


    public void savePerson(@RequestBody CtPerson person) {
        this.getOrgAdminService().savePerson(person);
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        manager.clearPersonCache(person.getID());
    }


    public void addPerson2Org(String personId, String orgId) {
        this.getOrgAdminService().addPerson2Org(personId, orgId);
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        manager.clearOrgCache(orgId);
        manager.clearPersonCache(personId);

    }


    public void saveOrg(@RequestBody CtOrg org) throws JDSException {
        if (org.getOrgId() == null || org.getOrgId().equals("")) {
            org.setOrgId(UUID.randomUUID().toString());
        }
        this.getOrgAdminService().saveOrg(org);
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        manager.clearOrgCache(org.getParentId());
    }


    public void saveRole(CtRole role) {
        if (role.getSysId() == null || role.getSysId().equals("")) {
            role.setSysId(JDSActionContext.getActionContext().getSystemCode());
        }
        this.getOrgAdminService().saveRole(role);
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        manager.clearRoleCache(role.getSysId());

    }

    public void addPerson2Role(String personId, Role role) throws PersonNotFoundException {
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        Person person = manager.getPersonByID(personId);
        this.getOrgAdminService().addPerson2Role(personId, role.getRoleId());
        manager.clearRoleCache(role.getRoleId());
        manager.clearPersonCache(personId);

    }


    public void addRole2Person(String roleId, Person person) throws RoleNotFoundException {
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        Role role = manager.getRoleByID(roleId);
        this.getOrgAdminService().addPerson2Role(person.getID(), roleId);
        manager.clearRoleCache(roleId);
        manager.clearPersonCache(person.getID());

    }

    public void addOrg2Role(String orgId, Role role) throws OrgNotFoundException {
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        Org org = manager.getOrgByID(orgId);
        this.getOrgAdminService().addOrg2Role(orgId, role.getRoleId());
        manager.clearRoleCache(role.getRoleId());
        manager.clearOrgCache(orgId);

    }


    public void addRole2Org(String roleId, Org org) throws RoleNotFoundException {
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        Role role = manager.getRoleByID(roleId);
        this.getOrgAdminService().addOrg2Role(org.getOrgId(), roleId);
        manager.clearRoleCache(roleId);
        manager.clearOrgCache(org.getOrgId());

    }

    public void removeRole2Person(String roleId, Person person) throws RoleNotFoundException {
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        Role role = manager.getRoleByID(roleId);
        if (person != null && role != null) {
            this.getOrgAdminService().removePerson2Role(person.getID(), roleId);
            manager.clearRoleCache(roleId);
            manager.clearPersonCache(person.getID());

        }

    }


    public void removePerson2Role(String personId, Role role) throws PersonNotFoundException, RoleNotFoundException {
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        Person person = manager.getPersonByID(personId);
        if (person != null && role != null) {
            this.getOrgAdminService().removePerson2Role(personId, role.getRoleId());
            manager.clearRoleCache(role.getRoleId());
            manager.clearPersonCache(personId);
        }

    }

    public void removeOrg2Role(String orgId, Role role) throws RoleNotFoundException, OrgNotFoundException {
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        Org org = manager.getOrgByID(orgId);
        if (org != null && role != null) {
            this.getOrgAdminService().removeOrg2Role(orgId, role.getRoleId());
            manager.clearRoleCache(role.getRoleId());
            manager.clearOrgCache(orgId);
        }

    }


    public void removeRole2Org(String roleId, Org org) throws PersonNotFoundException, RoleNotFoundException {
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();

        Role role = manager.getRoleByID(roleId);
        if (org != null && role != null) {
            this.getOrgAdminService().removeOrg2Role(org.getOrgId(), roleId);
            manager.clearRoleCache(roleId);
            manager.clearOrgCache(org.getOrgId());

        }

    }


    public void removePerson2Org(String personId, String orgId) {
        this.getOrgAdminService().removePerson2Org(personId, orgId);
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        manager.clearOrgCache(orgId);
        manager.clearPersonCache(personId);

    }


    public void delOrg(String orgId) {
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        try {
            Org org = manager.getOrgByID(orgId);
            if (org != null) {
                List<Org> orgs = org.getChildrenRecursivelyList();
                List<Person> personListRecursively = org.getPersonListRecursively();
                for (Person person : personListRecursively) {
                    if (person != null) {
                        this.getOrgAdminService().delPerson(person.getID());
                    }
                }
                for (Org childOrg : orgs) {
                    if (childOrg != null) {
                        this.getOrgAdminService().delOrg(childOrg.getOrgId());
                    }
                }

                this.getOrgAdminService().delOrg(orgId);
                manager.clearOrgCache(org.getParentId());
            }


        } catch (OrgNotFoundException e) {
            e.printStackTrace();
        }


    }


    public void delRole(String roleId) {
        this.getOrgAdminService().delRole(roleId);
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        manager.clearRoleCache(roleId);

    }


    public void delPerson(String personId) {
        CtOrgManager manager = (CtOrgManager) OrgManagerFactory.getOrgManager();
        try {
            Person person = manager.getPersonByID(personId);
            if (person != null) {
                this.getOrgAdminService().delPerson(personId);
                manager.clearPersonCache(personId);
                manager.clearOrgCache(person.getOrgId());
            }

        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }


    }

    OrgAdminService getOrgAdminService() {
        return (OrgAdminService) EsbUtil.parExpression(OrgAdminService.class);
    }

}
