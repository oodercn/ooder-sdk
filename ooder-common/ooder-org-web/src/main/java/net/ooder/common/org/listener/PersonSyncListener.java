package net.ooder.common.org.listener;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.EsbFlowType;
import net.ooder.common.org.CtCacheManager;
import net.ooder.common.org.event.PersonEvent;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;

@EsbBeanAnnotation(id = "PersonSyncListener", name = "同步组织机用户数据", expressionArr = "PersonSyncListener()", desc = "同步组织机用户数据", flowType = EsbFlowType.listener)

public class PersonSyncListener<T extends Person> implements PersonListener<T> {


    @Override
    public void personCreate(PersonEvent<T> event) throws PersonNotFoundException {

    }

    @Override
    public void personSave(PersonEvent<T> event) throws PersonNotFoundException {
        CtCacheManager.getInstance().clearPersonCache(event.getSource().getID());
    }

    @Override
    public void personAdded(PersonEvent<T> event) throws PersonNotFoundException {
        CtCacheManager.getInstance().clearPersonCache(event.getSource().getID());
        CtCacheManager.getInstance().clearOrgCache(event.getSource().getOrgId());
    }

    @Override
    public void personDelete(PersonEvent<T> event) throws PersonNotFoundException {
        CtCacheManager.getInstance().clearPersonCache(event.getSource().getID());
        CtCacheManager.getInstance().clearOrgCache(event.getSource().getOrgId());
    }

    @Override
    public String getSystemCode() {
        return null;
    }
}
