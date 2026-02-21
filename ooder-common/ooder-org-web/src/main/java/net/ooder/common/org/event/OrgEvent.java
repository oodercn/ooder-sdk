package net.ooder.common.org.event;

import net.ooder.common.JDSEvent;
import net.ooder.org.Org;
import net.ooder.org.enums.OrgEventEnums;

/**
 * <p>
 * Title: VFS管理系统
 * </p>
 * <p>
 * Description: 核心文件事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 2.0
 */
public class OrgEvent<T extends Org> extends JDSEvent<T> {

    public OrgEvent(Org org, OrgEventEnums eventID, String sysCode) {
        super((T) org, null);
        id = eventID;
        this.systemCode = sysCode;
    }


    @Override
    public OrgEventEnums getID() {

        return (OrgEventEnums) id;
    }
}
