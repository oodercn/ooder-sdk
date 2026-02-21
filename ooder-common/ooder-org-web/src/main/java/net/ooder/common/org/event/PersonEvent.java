package net.ooder.common.org.event;

import net.ooder.common.JDSEvent;
import net.ooder.org.Person;
import net.ooder.org.enums.PersonEventEnums;

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
public class PersonEvent<T> extends JDSEvent<T> {

    private String sysCode;

    public PersonEvent(Person person, PersonEventEnums eventID, String sysCode) {
        super((T) person, null);
        id = eventID;
        this.sysCode = sysCode;
    }

    public String getSysCode() {
        return sysCode;
    }

    @Override
    public PersonEventEnums getID() {

        return (PersonEventEnums) id;
    }
}
