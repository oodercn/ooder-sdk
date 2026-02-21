package net.ooder.common.org.listener;

import net.ooder.common.org.event.RoleEvent;
import net.ooder.org.Role;
import net.ooder.org.RoleNotFoundException;
import net.ooder.vfs.VFSException;

public interface RoleListener<T extends Role> extends java.util.EventListener {

    /**
     * roleCreate
     *
     * @param event
     * @throws VFSException
     */
    public void roleCreate(RoleEvent<T> event) throws RoleNotFoundException;


    /**
     * roleSave
     *
     * @param event
     * @throws VFSException
     */
    public void roleSave(RoleEvent<T> event) throws RoleNotFoundException;


    /**
     * 已经被添加
     */
    public void roleAdded(RoleEvent<T> event) throws RoleNotFoundException;


    /**
     * 已经被添加
     */
    public void addPerson(RoleEvent<T> event) throws RoleNotFoundException;


    public void removePerson(RoleEvent<T> event) throws RoleNotFoundException;


    public void removeOrg(RoleEvent<T> event) throws RoleNotFoundException;


    /**
     * 已经被添加
     */
    public void addOrg(RoleEvent<T> event) throws RoleNotFoundException;


    /**
     * 正在被删除
     */
    public void roleDelete(RoleEvent<T> event) throws RoleNotFoundException;


    /**
     * 文件夹从命名
     */
    public void roleRename(RoleEvent<T> event) throws RoleNotFoundException;


    /**
     * 得到系统Code
     *
     * @return
     */
    public String getSystemCode();

}
