package net.ooder.scene.drivers.org;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ooder.scene.core.PageRequest;
import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrgSkillImpl implements OrgSkill {

    private static final Logger logger = LoggerFactory.getLogger(OrgSkillImpl.class);

    private static final String SKILL_ID = "skill-org";
    private static final String SKILL_NAME = "Organization Skill";
    private static final String SKILL_VERSION = "0.7.3";

    private OrgCapabilities capabilities;
    private OrgManager remoteOrgManager;
    private JsonOrgDataSource jsonDataSource;
    private TokenStore tokenStore;
    
    private boolean initialized = false;

    public OrgSkillImpl() {
        this.capabilities = OrgCapabilities.forJson();
        this.jsonDataSource = new JsonOrgDataSource();
        this.tokenStore = new TokenStore();
    }

    public void initialize(OrgCapabilities capabilities) {
        this.capabilities = capabilities;
        this.jsonDataSource = new JsonOrgDataSource();
        this.jsonDataSource.initialize();
        this.initialized = true;
        
        logger.info("OrgSkill initialized with provider: {}", capabilities.getProviderType());
        if (capabilities.requiresFallback()) {
            logger.info("Fallback required for: {}", capabilities.getUnsupportedCapabilities());
        }
    }

    public void initialize(OrgCapabilities capabilities, OrgManager remoteOrgManager) {
        this.capabilities = capabilities;
        this.remoteOrgManager = remoteOrgManager;
        this.jsonDataSource = new JsonOrgDataSource();
        this.jsonDataSource.initialize();
        this.initialized = true;
        
        logger.info("OrgSkill initialized with remote provider: {}", capabilities.getProviderType());
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public String getSkillId() {
        return SKILL_ID;
    }

    @Override
    public String getSkillName() {
        return SKILL_NAME;
    }

    @Override
    public String getSkillVersion() {
        return SKILL_VERSION;
    }

    @Override
    public List<String> getCapabilities() {
        List<String> caps = new ArrayList<String>();
        caps.add("user.login");
        caps.add("user.logout");
        caps.add("user.register");
        caps.add("user.info");
        caps.add("user.list");
        caps.add("token.validate");
        caps.add("token.refresh");
        caps.add("org.tree");
        caps.add("org.info");
        caps.add("org.users");
        caps.add("role.list");
        caps.add("role.query");
        return caps;
    }

    public boolean isSupportOrgLevel() {
        return capabilities.isSupportOrgLevel();
    }

    public boolean isSupportOrgRole() {
        return capabilities.isSupportOrgRole();
    }

    public boolean isSupportPersonDuty() {
        return capabilities.isSupportPersonDuty();
    }

    public boolean isSupportPersonGroup() {
        return capabilities.isSupportPersonGroup();
    }

    public boolean isSupportPersonLevel() {
        return capabilities.isSupportPersonLevel();
    }

    public boolean isSupportPersonPosition() {
        return capabilities.isSupportPersonPosition();
    }

    public boolean isSupportPersonRole() {
        return capabilities.isSupportPersonRole();
    }

    @Override
    public UserInfo login(String username, String password, String clientIp) {
        logger.info("User login attempt: {} from {}", username, clientIp);
        
        boolean verified = false;
        Person person = null;
        
        if (capabilities.isSupportUserAuth() && remoteOrgManager != null) {
            try {
                verified = remoteOrgManager.verifyPerson(username, password);
                if (verified) {
                    person = remoteOrgManager.getPersonByAccount(username);
                }
            } catch (Exception e) {
                logger.warn("Remote auth failed, fallback to local: {}", e.getMessage());
                verified = jsonDataSource.verifyPerson(username, password);
                if (verified) {
                    person = jsonDataSource.getPersonByAccount(username);
                }
            }
        } else {
            verified = jsonDataSource.verifyPerson(username, password);
            if (verified) {
                person = jsonDataSource.getPersonByAccount(username);
            }
        }
        
        if (!verified || person == null) {
            logger.warn("Login failed for user: {}", username);
            return null;
        }
        
        UserInfo userInfo = convertToUserInfo(person);
        enrichUserInfo(userInfo, person);
        
        String token = tokenStore.createToken(userInfo.getUserId(), username);
        userInfo.setToken(token);
        
        logger.info("User logged in successfully: {}", username);
        return userInfo;
    }

    @Override
    public boolean logout(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        tokenStore.removeToken(token);
        logger.info("User logged out");
        return true;
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return tokenStore.validateToken(token);
    }

    @Override
    public UserInfo refreshToken(String refreshToken) {
        String userId = tokenStore.getUserIdByRefreshToken(refreshToken);
        if (userId == null) {
            return null;
        }
        
        Person person = getPersonByID(userId);
        if (person == null) {
            return null;
        }
        
        UserInfo userInfo = convertToUserInfo(person);
        enrichUserInfo(userInfo, person);
        
        String newToken = tokenStore.refreshToken(refreshToken);
        userInfo.setToken(newToken);
        
        return userInfo;
    }

    @Override
    public UserInfo getUser(String userId) {
        Person person = getPersonByID(userId);
        if (person == null) {
            return null;
        }
        UserInfo userInfo = convertToUserInfo(person);
        enrichUserInfo(userInfo, person);
        return userInfo;
    }

    @Override
    public UserInfo getUserByAccount(String account) {
        Person person = getPersonByAccount(account);
        if (person == null) {
            return null;
        }
        UserInfo userInfo = convertToUserInfo(person);
        enrichUserInfo(userInfo, person);
        return userInfo;
    }

    @Override
    public UserInfo registerUser(UserInfo userInfo) {
        String userId;
        
        if (capabilities.isSupportOrgAdmin() && remoteOrgManager != null) {
            try {
                userId = remoteOrgManager.registerPerson(
                    userInfo.getUsername(),
                    userInfo.getPhone(),
                    userInfo.getNickname()
                );
            } catch (Exception e) {
                logger.warn("Remote register failed, fallback to local: {}", e.getMessage());
                userId = jsonDataSource.registerPerson(
                    userInfo.getUsername(),
                    userInfo.getPhone(),
                    userInfo.getNickname()
                );
            }
        } else {
            userId = jsonDataSource.registerPerson(
                userInfo.getUsername(),
                userInfo.getPhone(),
                userInfo.getNickname()
            );
        }
        
        if (userId == null) {
            return null;
        }
        
        Person person = getPersonByID(userId);
        return convertToUserInfo(person);
    }

    @Override
    public UserInfo updateUser(String userId, UserInfo userInfo) {
        logger.warn("Update user not fully implemented for userId: {}", userId);
        return getUser(userId);
    }

    @Override
    public boolean deleteUser(String userId) {
        logger.warn("Delete user not fully implemented for userId: {}", userId);
        return false;
    }

    @Override
    public PageResult<UserInfo> listUsers(PageRequest request) {
        List<Person> persons = getPersons();
        
        int total = persons.size();
        int fromIndex = request.getOffset();
        int toIndex = Math.min(fromIndex + request.getPageSize(), total);
        
        List<UserInfo> items = new ArrayList<UserInfo>();
        for (int i = fromIndex; i < toIndex && i < total; i++) {
            UserInfo info = convertToUserInfo(persons.get(i));
            enrichUserInfo(info, persons.get(i));
            items.add(info);
        }
        
        return PageResult.of(items, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public List<String> getUserRoles(String userId) {
        List<Role> roles = getPersonRoles(userId);
        List<String> roleNames = new ArrayList<String>();
        for (Role role : roles) {
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    @Override
    public List<OrgInfo> getOrgTree() {
        List<Org> topOrgs = getTopOrgs();
        List<OrgInfo> result = new ArrayList<OrgInfo>();
        
        for (Org org : topOrgs) {
            result.add(convertToOrgInfo(org, true));
        }
        
        return result;
    }

    @Override
    public OrgInfo getOrg(String orgId) {
        Org org = getOrgByID(orgId);
        if (org == null) {
            return null;
        }
        return convertToOrgInfo(org, false);
    }

    @Override
    public List<UserInfo> getOrgUsers(String orgId) {
        List<Person> persons = getPersonsByOrgID(orgId);
        List<UserInfo> result = new ArrayList<UserInfo>();
        
        for (Person person : persons) {
            UserInfo info = convertToUserInfo(person);
            enrichUserInfo(info, person);
            result.add(info);
        }
        
        return result;
    }

    @Override
    public Object invoke(String capability, Map<String, Object> params) {
        logger.info("Invoking capability: {}", capability);
        
        try {
            switch (capability) {
                case "user.login":
                    return login(
                        (String) params.get("username"),
                        (String) params.get("password"),
                        (String) params.getOrDefault("clientIp", "unknown")
                    );
                    
                case "user.logout":
                    return logout((String) params.get("token"));
                    
                case "user.register":
                    UserInfo newUser = new UserInfo();
                    newUser.setUsername((String) params.get("username"));
                    newUser.setPhone((String) params.get("phone"));
                    newUser.setNickname((String) params.get("nickname"));
                    return registerUser(newUser);
                    
                case "user.info":
                    String userId = (String) params.get("userId");
                    String token = (String) params.get("token");
                    if (userId != null) {
                        return getUser(userId);
                    } else if (token != null) {
                        String uid = tokenStore.getUserId(token);
                        return getUser(uid);
                    }
                    return null;
                    
                case "user.list":
                    PageRequest request = new PageRequest(
                        (Integer) params.getOrDefault("pageNum", 1),
                        (Integer) params.getOrDefault("pageSize", 20)
                    );
                    return listUsers(request);
                    
                case "token.validate":
                    return validateToken((String) params.get("token"));
                    
                case "token.refresh":
                    return refreshToken((String) params.get("refreshToken"));
                    
                case "org.tree":
                    return getOrgTree();
                    
                case "org.info":
                    return getOrg((String) params.get("orgId"));
                    
                case "org.users":
                    return getOrgUsers((String) params.get("orgId"));
                    
                case "role.list":
                    return getUserRoles((String) params.get("userId"));
                    
                case "role.query":
                    return queryRole(params);
                    
                default:
                    logger.warn("Unknown capability: {}", capability);
                    return null;
            }
        } catch (Exception e) {
            logger.error("Failed to invoke capability: {}", capability, e);
            return null;
        }
    }

    private Person getPersonByID(String personId) {
        Person person = null;
        
        if (capabilities.isSupportPersonQuery() && remoteOrgManager != null) {
            try {
                person = remoteOrgManager.getPersonByID(personId);
            } catch (Exception e) {
                logger.warn("Remote getPersonByID failed, fallback to local: {}", e.getMessage());
            }
        }
        
        if (person == null) {
            person = jsonDataSource.getPersonByID(personId);
        }
        
        return person;
    }

    private Person getPersonByAccount(String account) {
        Person person = null;
        
        if (capabilities.isSupportPersonQuery() && remoteOrgManager != null) {
            try {
                person = remoteOrgManager.getPersonByAccount(account);
            } catch (Exception e) {
                logger.warn("Remote getPersonByAccount failed, fallback to local: {}", e.getMessage());
            }
        }
        
        if (person == null) {
            person = jsonDataSource.getPersonByAccount(account);
        }
        
        return person;
    }

    private List<Person> getPersons() {
        if (capabilities.isSupportPersonQuery() && remoteOrgManager != null) {
            try {
                return remoteOrgManager.getPersons();
            } catch (Exception e) {
                logger.warn("Remote getPersons failed, fallback to local: {}", e.getMessage());
            }
        }
        return jsonDataSource.getPersons();
    }

    private List<Person> getPersonsByOrgID(String orgId) {
        if (capabilities.isSupportOrgQuery() && remoteOrgManager != null) {
            try {
                return remoteOrgManager.getPersonsByOrgID(orgId);
            } catch (Exception e) {
                logger.warn("Remote getPersonsByOrgID failed, fallback to local: {}", e.getMessage());
            }
        }
        return jsonDataSource.getPersonsByOrgID(orgId);
    }

    private Org getOrgByID(String orgId) {
        Org org = null;
        
        if (capabilities.isSupportOrgQuery() && remoteOrgManager != null) {
            try {
                org = remoteOrgManager.getOrgByID(orgId);
            } catch (Exception e) {
                logger.warn("Remote getOrgByID failed, fallback to local: {}", e.getMessage());
            }
        }
        
        if (org == null) {
            org = jsonDataSource.getOrgByID(orgId);
        }
        
        return org;
    }

    private List<Org> getTopOrgs() {
        if (capabilities.isSupportOrgQuery() && remoteOrgManager != null) {
            try {
                return remoteOrgManager.getTopOrgs();
            } catch (Exception e) {
                logger.warn("Remote getTopOrgs failed, fallback to local: {}", e.getMessage());
            }
        }
        return jsonDataSource.getTopOrgs();
    }

    private List<Role> getPersonRoles(String personId) {
        if (capabilities.isSupportPersonRole() && remoteOrgManager != null) {
            try {
                Person person = remoteOrgManager.getPersonByID(personId);
                if (person != null) {
                    return person.getRoleList();
                }
            } catch (Exception e) {
                logger.warn("Remote getPersonRoles failed, fallback to local: {}", e.getMessage());
            }
        }
        return jsonDataSource.getRolesByPersonId(personId);
    }

    private Role getRoleByID(String roleId) {
        if (capabilities.isSupportPersonRole() && remoteOrgManager != null) {
            try {
                return remoteOrgManager.getRoleByID(roleId);
            } catch (Exception e) {
                logger.warn("Remote getRoleByID failed, fallback to local: {}", e.getMessage());
            }
        }
        return jsonDataSource.getRoleByID(roleId);
    }

    private Object queryRole(Map<String, Object> params) {
        String roleId = (String) params.get("roleId");
        String roleName = (String) params.get("roleName");
        String typeStr = (String) params.get("type");
        
        if (roleId != null) {
            return getRoleByID(roleId);
        }
        
        if (roleName != null && typeStr != null) {
            RoleType type = RoleType.valueOf(typeStr);
            return getRoleByName(type, roleName);
        }
        
        return null;
    }

    private Role getRoleByName(RoleType type, String roleName) {
        if (capabilities.isSupportPersonRole() && remoteOrgManager != null) {
            try {
                return remoteOrgManager.getRoleByName(type, roleName, null);
            } catch (Exception e) {
                logger.warn("Remote getRoleByName failed, fallback to local: {}", e.getMessage());
            }
        }
        
        for (Role role : jsonDataSource.getRolesByPersonId(null)) {
            if (role.getType() == type && roleName.equals(role.getName())) {
                return role;
            }
        }
        return null;
    }

    private void enrichUserInfo(UserInfo userInfo, Person person) {
        if (!capabilities.isSupportPersonRole()) {
            List<Role> roles = jsonDataSource.getRolesByPersonId(person.getID());
            List<String> roleNames = new ArrayList<String>();
            Set<String> roleIds = new HashSet<String>();
            
            for (Role role : roles) {
                roleNames.add(role.getName());
                roleIds.add(role.getRoleId());
            }
            
            userInfo.setRoles(roleNames);
            userInfo.setRoleIds(roleIds);
        }
    }

    private UserInfo convertToUserInfo(Person person) {
        if (person == null) {
            return null;
        }
        
        UserInfo info = new UserInfo();
        info.setUserId(person.getID());
        info.setUsername(person.getAccount());
        info.setNickname(person.getName());
        info.setEmail(person.getEmail());
        info.setPhone(person.getMobile());
        info.setStatus(person.getStatus());
        
        return info;
    }

    private OrgInfo convertToOrgInfo(Org org, boolean includeChildren) {
        if (org == null) {
            return null;
        }
        
        OrgInfo info = new OrgInfo();
        info.setOrgId(org.getOrgId());
        info.setName(org.getName());
        info.setBrief(org.getBrief());
        info.setParentId(org.getParentId());
        info.setLeaderId(org.getLeaderId());
        info.setTier(org.getTier());
        info.setIndex(org.getIndex());
        
        List<Person> persons = org.getPersonList();
        info.setMemberCount(persons != null ? persons.size() : 0);
        
        if (includeChildren) {
            List<Org> children = org.getChildrenList();
            if (children != null && !children.isEmpty()) {
                List<OrgInfo> childInfos = new ArrayList<OrgInfo>();
                for (Org child : children) {
                    childInfos.add(convertToOrgInfo(child, true));
                }
                info.setChildren(childInfos);
            }
        }
        
        return info;
    }
}
