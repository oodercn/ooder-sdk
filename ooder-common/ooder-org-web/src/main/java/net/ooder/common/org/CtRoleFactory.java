package net.ooder.common.org;

import net.ooder.common.org.service.OrgWebManager;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Role;
import net.ooder.org.RoleNotFoundException;
import net.ooder.annotation.RoleType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CtRoleFactory {

    private static Role loadFromServer(String value, boolean loadByName, RoleType type) throws RoleNotFoundException, InterruptedException, ExecutionException {

        synchronized (value + type.name()) {
            OrgWebManager orgWebManager = (OrgWebManager) EsbUtil.parExpression(OrgWebManager.class);

            Role role = orgWebManager.getRoleByID(value).get();
            if (role instanceof CtRole) {
                return role;
            }
            return toClient(role);
        }

    }

    private static Role loadFromServer(String value) throws RoleNotFoundException, InterruptedException, ExecutionException {

        synchronized (value) {
            OrgWebManager orgWebManager = (OrgWebManager) EsbUtil.parExpression(OrgWebManager.class);
            Role role = orgWebManager.getRoleByID(value).get();
            if (role instanceof CtRole) {
                return role;
            }
            return toClient(role);
        }

    }

    /**
     * @param role
     * @return
     */
    public static Role toClient(Role role) {
        if (role == null) {
            return null;
        }
        if (role instanceof CtRole) {
            return role;
        }
        CtRole personRole = new CtRole(role.getType());
        personRole.setName(role.getName());
        personRole.setRoleId(role.getRoleId());
        personRole.setPersonIds(role.getPersonIdList());
        personRole.setOrgIds(role.getOrgIdList());

        return personRole;
    }

    public static List<Role> toClient(List<Role> wsRoles) {
        List<Role> ctRoles = new ArrayList<Role>();

        if (wsRoles == null) {
            return new ArrayList<Role>();
        }

        for (Role role : wsRoles) {
            ctRoles.add(toClient(role));
        }
        return ctRoles;
    }

    /**
     * @param personRoleId
     * @return
     */
    public static Role getRole(String personRoleId, RoleType type) throws RoleNotFoundException {
        Role personRole = null;

        try {
            personRole = loadFromServer(personRoleId, false, type);
        } catch (Exception e) {
            throw new RoleNotFoundException("remote exception occured when load personRole from server!");

        }
        return personRole;
    }

    /**
     * @param roleId
     * @return
     */
    public static Role getRole(String roleId) throws RoleNotFoundException {
        Role personRole = null;

        try {
            personRole = loadFromServer(roleId);
        } catch (Exception e) {
            throw new RoleNotFoundException("remote exception occured when load personRole from server!");

        }
        return personRole;
    }


    public static Role getRole(String personRoleName, String sysId, RoleType type) throws RoleNotFoundException {
        Role personRole = null;
        try {
            OrgWebManager orgWebManager = (OrgWebManager) EsbUtil.parExpression(OrgWebManager.class);

            personRole = orgWebManager.getRoleByName(type, personRoleName, sysId).get();

        } catch (Exception e) {
            throw new RoleNotFoundException("remote exception occured when load personRole from server!");
        }
        return toClient(personRole);
    }

    public static Role getOrgRole(String orgRoleId) throws RoleNotFoundException {
        return getRole(orgRoleId, RoleType.OrgRole);
    }

    public static Role getOrgRole(String orgRoleName, String sysId) throws RoleNotFoundException {
        return getRole(orgRoleName, sysId, RoleType.OrgRole);
    }

    public static Role getOrgLevel(String orgLevelId) throws RoleNotFoundException {
        return getRole(orgLevelId, RoleType.OrgLevel);
    }

    public static Role getOrgLevel(String orgLevelName, String sysId) throws RoleNotFoundException {
        return getRole(orgLevelName, sysId, RoleType.OrgLevel);
    }

    public static Role getPersonDuty(String personDutyName, String sysId) throws RoleNotFoundException {
        return getRole(personDutyName, sysId, RoleType.Duty);
    }

    public static Role getPersonGroup(String personGroupName, String sysId) throws RoleNotFoundException {
        return getRole(personGroupName, sysId, RoleType.Group);
    }


    public static Role getPersonLevelByName(String personLevelName, String sysId) throws RoleNotFoundException {
        return getRole(personLevelName, sysId, RoleType.PersonLevel);
    }


    public static Role getPersonLevel(String personLevelId) throws RoleNotFoundException {
        return getRole(personLevelId, RoleType.PersonLevel);
    }

    public static Role getPersonPosition(String personPositionId) throws RoleNotFoundException {
        return getRole(personPositionId, RoleType.Position);
    }

    public static Role getPersonPosition(String personPositionName, String sysId) throws RoleNotFoundException {
        return getRole(personPositionName, sysId, RoleType.Position);
    }

    public static Role getPersonDuty(String personDutyId) throws RoleNotFoundException {
        return getRole(personDutyId, RoleType.Duty);
    }

    public static Role getPersonRole(String personRoleId) throws RoleNotFoundException {
        return getRole(personRoleId, RoleType.Role);
    }

    public static Role getPersonGroup(String personGroupId) throws RoleNotFoundException {
        return getRole(personGroupId, RoleType.Group);
    }

    public static Role getPersonRole(String personRoleName, String sysId) throws RoleNotFoundException {
        return getRole(personRoleName, sysId, RoleType.Role);
    }
}
