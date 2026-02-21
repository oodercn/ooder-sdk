package net.ooder.common.org.event;

import net.ooder.common.JDSEvent;
import net.ooder.org.Role;
import net.ooder.org.enums.RoleEventEnums;

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
public class RoleEvent<T extends Role> extends JDSEvent<T> {

	private String sysCode;

	public RoleEvent(Role role, RoleEventEnums eventID, String sysCode) {
		super((T) role,null);
		id = eventID;
		this.sysCode=sysCode;
	}
	
	public String getSysCode() {
		return sysCode;
	}



	@Override
	public RoleEventEnums getID() {

	return (RoleEventEnums) id;
	}
}
