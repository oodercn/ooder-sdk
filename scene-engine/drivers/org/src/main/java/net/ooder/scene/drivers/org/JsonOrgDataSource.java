package net.ooder.scene.drivers.org;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonOrgDataSource {

    private String storagePath;
    private JSONObject orgData;
    private Map<String, JsonPerson> personMap = new HashMap<String, JsonPerson>();
    private Map<String, JsonOrg> orgMap = new HashMap<String, JsonOrg>();
    private Map<String, JsonRole> roleMap = new HashMap<String, JsonRole>();
    
    private Map<String, List<String>> personRoleMap = new HashMap<String, List<String>>();
    private Map<String, List<String>> orgPersonMap = new HashMap<String, List<String>>();
    private Map<String, String> personOrgMap = new HashMap<String, String>();

    public JsonOrgDataSource() {
        this.storagePath = "data/org.json";
    }

    public JsonOrgDataSource(String storagePath) {
        this.storagePath = storagePath;
    }

    public void initialize() {
        loadFromJson();
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
        } catch (IOException e) {
            createDefaultData();
        }
    }

    private void createDefaultData() {
        orgData = new JSONObject();
        orgData.put("persons", new JSONArray());
        orgData.put("orgs", new JSONArray());
        orgData.put("roles", new JSONArray());
        
        JsonRole adminRole = new JsonRole();
        adminRole.setRoleId("role-admin");
        adminRole.setName("System Administrator");
        adminRole.setType(RoleType.PERSON_ROLE);
        roleMap.put(adminRole.getRoleId(), adminRole);
        
        JsonOrg defaultOrg = new JsonOrg();
        defaultOrg.setOrgId("org-default");
        defaultOrg.setName("Default Organization");
        defaultOrg.setTier(1);
        orgMap.put(defaultOrg.getOrgId(), defaultOrg);
        
        JsonPerson admin = new JsonPerson();
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
    }

    private void parseOrgData() {
        JSONArray persons = orgData.getJSONArray("persons");
        if (persons != null) {
            for (int i = 0; i < persons.size(); i++) {
                JSONObject p = persons.getJSONObject(i);
                JsonPerson person = new JsonPerson();
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
                JsonOrg org = new JsonOrg();
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
                JsonRole role = new JsonRole();
                role.setRoleId(r.getString("roleId"));
                role.setName(r.getString("name"));
                String typeStr = r.getString("type");
                if ("ORG_LEVEL".equals(typeStr)) {
                    role.setType(RoleType.ORG_LEVEL);
                } else if ("ORG_ROLE".equals(typeStr)) {
                    role.setType(RoleType.ORG_ROLE);
                } else if ("PERSON_DUTY".equals(typeStr)) {
                    role.setType(RoleType.PERSON_DUTY);
                } else if ("PERSON_GROUP".equals(typeStr)) {
                    role.setType(RoleType.PERSON_GROUP);
                } else if ("PERSON_LEVEL".equals(typeStr)) {
                    role.setType(RoleType.PERSON_LEVEL);
                } else if ("PERSON_POSITION".equals(typeStr)) {
                    role.setType(RoleType.PERSON_POSITION);
                } else {
                    role.setType(RoleType.PERSON_ROLE);
                }
                roleMap.put(role.getRoleId(), role);
            }
        }
    }

    public void saveToJson() {
        try {
            JSONObject data = new JSONObject();
            
            JSONArray persons = new JSONArray();
            for (JsonPerson person : personMap.values()) {
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
            for (JsonOrg org : orgMap.values()) {
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
            for (JsonRole role : roleMap.values()) {
                JSONObject r = new JSONObject();
                r.put("roleId", role.getRoleId());
                r.put("name", role.getName());
                r.put("type", role.getType().name());
                roles.add(r);
            }
            data.put("roles", roles);

            File file = new File(storagePath);
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);
            writer.write(data.toJSONString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Person getPersonByID(String personId) {
        return personMap.get(personId);
    }

    public Person getPersonByAccount(String account) {
        for (JsonPerson person : personMap.values()) {
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

    public List<Org> getOrgs() {
        return new ArrayList<Org>(orgMap.values());
    }

    public List<Org> getTopOrgs() {
        List<Org> result = new ArrayList<Org>();
        for (JsonOrg org : orgMap.values()) {
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

    public List<String> getRoleIdsByPersonId(String personId) {
        return personRoleMap.get(personId);
    }

    public Set<String> getRoleIdSetByPersonId(String personId) {
        List<String> roleIds = personRoleMap.get(personId);
        if (roleIds != null) {
            return new HashSet<String>(roleIds);
        }
        return new HashSet<String>();
    }

    public boolean verifyPerson(String account, String password) {
        JsonPerson person = (JsonPerson) getPersonByAccount(account);
        if (person == null) {
            return false;
        }
        return password.equals(person.getPassword());
    }

    public String registerPerson(String account, String password, String name) {
        if (getPersonByAccount(account) != null) {
            return null;
        }
        
        JsonPerson person = new JsonPerson();
        person.setID("person-" + System.currentTimeMillis());
        person.setAccount(account);
        person.setPassword(password);
        person.setName(name);
        person.setStatus("active");
        
        personMap.put(person.getID(), person);
        saveToJson();
        
        return person.getID();
    }

    public void addRoleToPerson(String personId, String roleId) {
        if (!personRoleMap.containsKey(personId)) {
            personRoleMap.put(personId, new ArrayList<String>());
        }
        if (!personRoleMap.get(personId).contains(roleId)) {
            personRoleMap.get(personId).add(roleId);
        }
    }

    public void setPersonOrg(String personId, String orgId) {
        String oldOrgId = personOrgMap.get(personId);
        if (oldOrgId != null && orgPersonMap.containsKey(oldOrgId)) {
            orgPersonMap.get(oldOrgId).remove(personId);
        }
        
        personOrgMap.put(personId, orgId);
        if (!orgPersonMap.containsKey(orgId)) {
            orgPersonMap.put(orgId, new ArrayList<String>());
        }
        if (!orgPersonMap.get(orgId).contains(personId)) {
            orgPersonMap.get(orgId).add(personId);
        }
        
        JsonPerson person = personMap.get(personId);
        if (person != null) {
            person.setOrgId(orgId);
        }
    }

    public void reloadAll() {
        personMap.clear();
        orgMap.clear();
        roleMap.clear();
        personRoleMap.clear();
        orgPersonMap.clear();
        personOrgMap.clear();
        loadFromJson();
    }
}
