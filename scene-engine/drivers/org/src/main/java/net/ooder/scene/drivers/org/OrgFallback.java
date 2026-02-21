package net.ooder.scene.drivers.org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class OrgFallback {
    
    private static final Logger logger = LoggerFactory.getLogger(OrgFallback.class);
    
    private String storagePath;
    private JSONObject orgData;
    private Map<String, FallbackPerson> personMap = new HashMap<String, FallbackPerson>();
    private Map<String, FallbackOrg> orgMap = new HashMap<String, FallbackOrg>();
    private Map<String, FallbackRole> roleMap = new HashMap<String, FallbackRole>();
    
    private Map<String, List<String>> personRoleMap = new HashMap<String, List<String>>();
    private Map<String, List<String>> orgPersonMap = new HashMap<String, List<String>>();
    private Map<String, String> personOrgMap = new HashMap<String, String>();
    
    public OrgFallback() {
        this.storagePath = "data/org.json";
    }
    
    public OrgFallback(String storagePath) {
        this.storagePath = storagePath;
    }
    
    public void initialize() {
        logger.info("Initializing ORG Fallback with storage: {}", storagePath);
        loadFromJson();
    }
    
    public void shutdown() {
        saveToJson();
        logger.info("ORG Fallback shutdown completed");
    }
    
    private void loadFromJson() {
        File file = new File(storagePath);
        if (!file.exists()) {
            createDefaultData();
            return;
        }
        
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            
            String jsonStr = new String(data, "UTF-8");
            orgData = JSON.parseObject(jsonStr);
            parseOrgData();
            logger.info("Loaded {} persons, {} orgs, {} roles", 
                personMap.size(), orgMap.size(), roleMap.size());
        } catch (IOException e) {
            logger.warn("Failed to load org data, creating default: {}", e.getMessage());
            createDefaultData();
        }
    }
    
    private void createDefaultData() {
        orgData = new JSONObject();
        orgData.put("persons", new JSONArray());
        orgData.put("orgs", new JSONArray());
        orgData.put("roles", new JSONArray());
        
        FallbackRole adminRole = new FallbackRole();
        adminRole.setRoleId("role-admin");
        adminRole.setName("System Administrator");
        roleMap.put(adminRole.getRoleId(), adminRole);
        
        FallbackOrg defaultOrg = new FallbackOrg();
        defaultOrg.setOrgId("org-default");
        defaultOrg.setName("Default Organization");
        defaultOrg.setTier(1);
        orgMap.put(defaultOrg.getOrgId(), defaultOrg);
        
        FallbackPerson admin = new FallbackPerson();
        admin.setID("person-admin");
        admin.setAccount("admin");
        admin.setName("Administrator");
        admin.setPassword("admin123");
        admin.setOrgId("org-default");
        personMap.put(admin.getID(), admin);
        
        List<String> adminRoles = new ArrayList<String>();
        adminRoles.add("role-admin");
        personRoleMap.put(admin.getID(), adminRoles);
        
        List<String> orgPersons = new ArrayList<String>();
        orgPersons.add(admin.getID());
        orgPersonMap.put(defaultOrg.getOrgId(), orgPersons);
        personOrgMap.put(admin.getID(), defaultOrg.getOrgId());
        
        saveToJson();
        logger.info("Created default org data with admin user");
    }
    
    private void parseOrgData() {
        JSONArray persons = orgData.getJSONArray("persons");
        if (persons != null) {
            for (int i = 0; i < persons.size(); i++) {
                JSONObject p = persons.getJSONObject(i);
                FallbackPerson person = new FallbackPerson();
                person.setID(p.getString("personId"));
                person.setAccount(p.getString("account"));
                person.setName(p.getString("name"));
                person.setPassword(p.getString("password"));
                person.setMobile(p.getString("mobile"));
                person.setEmail(p.getString("email"));
                person.setStatus(p.getString("status"));
                person.setOrgId(p.getString("orgId"));
                personMap.put(person.getID(), person);
                
                String orgId = p.getString("orgId");
                if (orgId != null) {
                    personOrgMap.put(person.getID(), orgId);
                    if (!orgPersonMap.containsKey(orgId)) {
                        orgPersonMap.put(orgId, new ArrayList<String>());
                    }
                    orgPersonMap.get(orgId).add(person.getID());
                }
                
                JSONArray roles = p.getJSONArray("roles");
                if (roles != null) {
                    List<String> roleIds = new ArrayList<String>();
                    for (int j = 0; j < roles.size(); j++) {
                        roleIds.add(roles.getString(j));
                    }
                    personRoleMap.put(person.getID(), roleIds);
                }
            }
        }
        
        JSONArray orgs = orgData.getJSONArray("orgs");
        if (orgs != null) {
            for (int i = 0; i < orgs.size(); i++) {
                JSONObject o = orgs.getJSONObject(i);
                FallbackOrg org = new FallbackOrg();
                org.setOrgId(o.getString("orgId"));
                org.setName(o.getString("name"));
                org.setParentId(o.getString("parentId"));
                org.setTier(o.getInteger("tier"));
                org.setLeaderId(o.getString("leaderId"));
                orgMap.put(org.getOrgId(), org);
            }
        }
        
        JSONArray roles = orgData.getJSONArray("roles");
        if (roles != null) {
            for (int i = 0; i < roles.size(); i++) {
                JSONObject r = roles.getJSONObject(i);
                FallbackRole role = new FallbackRole();
                role.setRoleId(r.getString("roleId"));
                role.setName(r.getString("name"));
                roleMap.put(role.getRoleId(), role);
            }
        }
    }
    
    public void saveToJson() {
        try {
            JSONObject data = new JSONObject();
            
            JSONArray persons = new JSONArray();
            for (FallbackPerson person : personMap.values()) {
                JSONObject p = new JSONObject();
                p.put("personId", person.getID());
                p.put("account", person.getAccount());
                p.put("name", person.getName());
                p.put("password", person.getPassword());
                p.put("mobile", person.getMobile());
                p.put("email", person.getEmail());
                p.put("status", person.getStatus());
                p.put("orgId", person.getOrgId());
                
                List<String> roles = personRoleMap.get(person.getID());
                if (roles != null) {
                    p.put("roles", roles);
                }
                persons.add(p);
            }
            data.put("persons", persons);
            
            JSONArray orgs = new JSONArray();
            for (FallbackOrg org : orgMap.values()) {
                JSONObject o = new JSONObject();
                o.put("orgId", org.getOrgId());
                o.put("name", org.getName());
                o.put("parentId", org.getParentId());
                o.put("tier", org.getTier());
                o.put("leaderId", org.getLeaderId());
                orgs.add(o);
            }
            data.put("orgs", orgs);
            
            JSONArray roles = new JSONArray();
            for (FallbackRole role : roleMap.values()) {
                JSONObject r = new JSONObject();
                r.put("roleId", role.getRoleId());
                r.put("name", role.getName());
                roles.add(r);
            }
            data.put("roles", roles);
            
            File file = new File(storagePath);
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);
            writer.write(data.toJSONString());
            writer.close();
        } catch (IOException e) {
            logger.error("Failed to save org data", e);
        }
    }
    
    public Person getPersonByID(String personId) {
        return personMap.get(personId);
    }
    
    public Person getPersonByAccount(String account) {
        for (FallbackPerson person : personMap.values()) {
            if (account.equals(person.getAccount())) {
                return person;
            }
        }
        return null;
    }
    
    public List<Person> getPersons() {
        return new ArrayList<Person>(personMap.values());
    }
    
    public List<Person> getPersonsByOrgID(String orgId) {
        List<Person> result = new ArrayList<Person>();
        List<String> personIds = orgPersonMap.get(orgId);
        if (personIds != null) {
            for (String pid : personIds) {
                Person p = personMap.get(pid);
                if (p != null) {
                    result.add(p);
                }
            }
        }
        return result;
    }
    
    public Org getOrgByID(String orgId) {
        return orgMap.get(orgId);
    }
    
    public List<Org> getTopOrgs() {
        List<Org> result = new ArrayList<Org>();
        for (FallbackOrg org : orgMap.values()) {
            if (org.getParentId() == null || org.getParentId().isEmpty()) {
                result.add(org);
            }
        }
        return result;
    }
    
    public Role getRoleByID(String roleId) {
        return roleMap.get(roleId);
    }
    
    public List<Role> getRolesByPersonId(String personId) {
        List<Role> result = new ArrayList<Role>();
        List<String> roleIds = personRoleMap.get(personId);
        if (roleIds != null) {
            for (String rid : roleIds) {
                Role r = roleMap.get(rid);
                if (r != null) {
                    result.add(r);
                }
            }
        }
        return result;
    }
    
    public boolean verifyPerson(String account, String password) {
        FallbackPerson person = (FallbackPerson) getPersonByAccount(account);
        if (person == null) {
            return false;
        }
        return password.equals(person.getPassword());
    }
    
    public String registerPerson(String account, String password, String name) {
        if (getPersonByAccount(account) != null) {
            return null;
        }
        
        FallbackPerson person = new FallbackPerson();
        person.setID("person-" + System.currentTimeMillis());
        person.setAccount(account);
        person.setPassword(password);
        person.setName(name);
        person.setStatus("active");
        
        personMap.put(person.getID(), person);
        saveToJson();
        
        return person.getID();
    }
    
    static class FallbackPerson implements Person {
        private String ID;
        private String account;
        private String name;
        private String password;
        private String mobile;
        private String email;
        private String status;
        private String orgId;
        
        public String getID() { return ID; }
        public void setID(String ID) { this.ID = ID; }
        public String getAccount() { return account; }
        public void setAccount(String account) { this.account = account; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getOrgId() { return orgId; }
        public void setOrgId(String orgId) { this.orgId = orgId; }
        public String getNickName() { return name; }
        public Integer getIndex() { return 0; }
        public String getCloudDiskPath() { return null; }
        public Org getOrg() { return null; }
        public List<Org> getOrgList() { return new ArrayList<Org>(); }
        public java.util.Set<String> getOrgIdList() { return new java.util.HashSet<String>(); }
        public java.util.Set<String> getRoleIdList() { return new java.util.HashSet<String>(); }
        public List<Role> getRoleList() { return new ArrayList<Role>(); }
        public int compareTo(Person other) { return 0; }
    }
    
    static class FallbackOrg implements Org {
        private String orgId;
        private String name;
        private String parentId;
        private Integer tier;
        private String leaderId;
        
        public String getOrgId() { return orgId; }
        public void setOrgId(String orgId) { this.orgId = orgId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getParentId() { return parentId; }
        public void setParentId(String parentId) { this.parentId = parentId; }
        public Integer getTier() { return tier; }
        public void setTier(Integer tier) { this.tier = tier; }
        public String getLeaderId() { return leaderId; }
        public void setLeaderId(String leaderId) { this.leaderId = leaderId; }
        public String getBrief() { return null; }
        public String getCity() { return null; }
        public Integer getIndex() { return 0; }
        public List<Role> getRoleList() { return new ArrayList<Role>(); }
        public List<String> getRoleIdList() { return new ArrayList<String>(); }
        public Person getLeader() { return null; }
        public List<Person> getPersonList() { return new ArrayList<Person>(); }
        public List<Person> getPersonListRecursively() { return new ArrayList<Person>(); }
        public Org getParent() { return null; }
        public List<Org> getChildrenList() { return new ArrayList<Org>(); }
        public List<Org> getChildrenRecursivelyList() { return new ArrayList<Org>(); }
        public List<String> getChildIdList() { return new ArrayList<String>(); }
        public List<String> getPersonIdList() { return new ArrayList<String>(); }
        public int compareTo(Org other) { return 0; }
    }
    
    static class FallbackRole implements Role {
        private String roleId;
        private String name;
        
        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRoleName() { return name; }
        public RoleType getType() { return RoleType.PERSON_ROLE; }
        public void setType(RoleType type) { }
        public String getRoleNum() { return null; }
        public void setRoleNum(String num) { }
        public String getSysId() { return null; }
        public void setSysId(String sysId) { }
        public List<Person> getPersonList() { return new ArrayList<Person>(); }
        public List<Org> getOrgList() { return new ArrayList<Org>(); }
        public List<String> getOrgIdList() { return new ArrayList<String>(); }
        public void setOrgIdList(List<String> orgIds) { }
        public List<String> getPersonIdList() { return new ArrayList<String>(); }
        public void setPersonIdList(List<String> personIds) { }
        public int compareTo(Org other) { return 0; }
    }
}
