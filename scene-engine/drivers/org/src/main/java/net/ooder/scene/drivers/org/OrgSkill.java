package net.ooder.scene.drivers.org;

import net.ooder.scene.core.PageRequest;
import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.UserInfo;

import java.util.List;
import java.util.Map;

public interface OrgSkill {

    String getSkillId();

    String getSkillName();

    String getSkillVersion();

    List<String> getCapabilities();

    UserInfo login(String username, String password, String clientIp);

    boolean logout(String token);

    boolean validateToken(String token);

    UserInfo refreshToken(String refreshToken);

    UserInfo getUser(String userId);

    UserInfo getUserByAccount(String account);

    UserInfo registerUser(UserInfo userInfo);

    UserInfo updateUser(String userId, UserInfo userInfo);

    boolean deleteUser(String userId);

    PageResult<UserInfo> listUsers(PageRequest request);

    List<String> getUserRoles(String userId);

    List<OrgInfo> getOrgTree();

    OrgInfo getOrg(String orgId);

    List<UserInfo> getOrgUsers(String orgId);

    Object invoke(String capability, Map<String, Object> params);
}
