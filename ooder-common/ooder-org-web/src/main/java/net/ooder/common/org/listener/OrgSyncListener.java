package net.ooder.common.org.listener;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.EsbFlowType;
import net.ooder.common.org.CtCacheManager;
import net.ooder.common.org.event.OrgEvent;
import net.ooder.org.Org;
import net.ooder.org.OrgNotFoundException;

@EsbBeanAnnotation(id = "OrgSyncListener", name = "同步组织机构数据", expressionArr = "OrgSyncListener()", desc = "同步组织机构数据", flowType = EsbFlowType.listener)

public class OrgSyncListener<T extends Org> implements OrgListener<T> {

    @Override
    public void orgCreate(OrgEvent<T> event) throws OrgNotFoundException {

    }

    @Override
    public void orgSave(OrgEvent<T> event) throws OrgNotFoundException {
        CtCacheManager.getInstance().clearOrgCache(event.getSource().getOrgId());
    }

    @Override
    public void personAdded(OrgEvent<T> event) throws OrgNotFoundException {
        CtCacheManager.getInstance().clearOrgCache(event.getSource().getOrgId());
    }

    @Override
    public void orgAdded(OrgEvent<T> event) throws OrgNotFoundException {
        CtCacheManager.getInstance().clearOrgCache(event.getSource().getParent().getOrgId());
    }

    @Override
    public void orgDelete(OrgEvent<T> event) throws OrgNotFoundException {
        CtCacheManager.getInstance().clearOrgCache(event.getSource().getOrgId());
    }

    @Override
    public void orgRename(OrgEvent<T> event) throws OrgNotFoundException {
        CtCacheManager.getInstance().clearOrgCache(event.getSource().getOrgId());
    }


    @Override
    public String getSystemCode() {
        return null;
    }
}
