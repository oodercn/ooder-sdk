package net.ooder.common.org.listener;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.EsbFlowType;
import net.ooder.common.org.CtCacheManager;
import net.ooder.common.org.event.RoleEvent;
import net.ooder.org.Role;
import net.ooder.org.RoleNotFoundException;

@EsbBeanAnnotation(id = "RoleSyncListener", name = "同步组织机用户数据", expressionArr = "RoleSyncListener()", desc = "同步组织机用户数据", flowType = EsbFlowType.listener)

public class RoleSyncListener<T extends Role> implements RoleListener<T> {


    @Override
    public void roleCreate(RoleEvent<T> event) throws RoleNotFoundException {

    }

    @Override
    public void roleSave(RoleEvent<T> event) throws RoleNotFoundException {
        CtCacheManager.getInstance().clearRoleCache(event.getSource().getRoleId());
    }

    @Override
    public void roleAdded(RoleEvent<T> event) throws RoleNotFoundException {
        CtCacheManager.getInstance().clearRoleCache(event.getSource().getRoleId());
    }

    @Override
    public void addPerson(RoleEvent<T> event) throws RoleNotFoundException {
        CtCacheManager.getInstance().clearRoleCache(event.getSource().getRoleId());
    }

    @Override
    public void removePerson(RoleEvent<T> event) throws RoleNotFoundException {
        CtCacheManager.getInstance().clearRoleCache(event.getSource().getRoleId());
    }

    @Override
    public void removeOrg(RoleEvent<T> event) throws RoleNotFoundException {
        CtCacheManager.getInstance().clearRoleCache(event.getSource().getRoleId());
    }

    @Override
    public void addOrg(RoleEvent<T> event) throws RoleNotFoundException {
        CtCacheManager.getInstance().clearRoleCache(event.getSource().getRoleId());
    }

    @Override
    public void roleDelete(RoleEvent<T> event) throws RoleNotFoundException {
        CtCacheManager.getInstance().clearRoleCache(event.getSource().getRoleId());
    }

    @Override
    public void roleRename(RoleEvent<T> event) throws RoleNotFoundException {
        CtCacheManager.getInstance().clearRoleCache(event.getSource().getRoleId());
    }

    @Override
    public String getSystemCode() {
        return null;
    }
}
