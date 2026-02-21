package net.ooder.common.org.listener;

import net.ooder.common.org.event.PersonEvent;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.vfs.VFSException;

public interface PersonListener<T extends Person> extends java.util.EventListener {

    /**
     * personCreate
     *
     * @param event
     * @throws VFSException
     */
    public void personCreate(PersonEvent<T> event) throws PersonNotFoundException;


    /**
     * personSave
     *
     * @param event
     * @throws VFSException
     */
    public void personSave(PersonEvent<T> event) throws PersonNotFoundException;


    /**
     * 已经被添加
     */
    public void personAdded(PersonEvent<T> event) throws PersonNotFoundException;


    /**
     * 正在被删除
     */
    public void personDelete(PersonEvent<T> event) throws PersonNotFoundException;


    /**
     * 得到系统Code
     *
     * @return
     */
    public String getSystemCode();

}
