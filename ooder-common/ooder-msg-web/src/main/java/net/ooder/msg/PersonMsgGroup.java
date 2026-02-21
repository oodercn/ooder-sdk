/**
 * $RCSfile: PersonMsgGroup.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.msg;

import net.ooder.common.JDSException;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title: ooder组织机构中间件
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003-2008
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 2.0
 */
public interface PersonMsgGroup{


	public List<RMsg> getHisMsgList() throws Exception;
	
	public List<RMsg> getHisMsgList(Date startTime, Date endTime) throws JDSException;

	public List<RMsg> getHisMsgList(String activityInstId, Date startTime, Date endTime) throws JDSException;

	public List<RMsg> getProcessHisMsgList(String processInstId, Date startTime, Date endTime) throws JDSException;

	public List<RMsg> getActivityInstIdHisMsgList(String activityInstId) throws JDSException ;

	public List<RMsg> getActivityInstIdHisMsgList(String activityInstId, String event) throws JDSException ;

	public List<RMsg> getReceiveMsgList();


	public RMsg createSendMsg()throws PersonMsgNotFoundException;

	public List<RMsg> massSendMsg(RMsg msg, List<String> toPersonId);

	public List<RMsg> massSendMsg(RMsg msg, List<String> toPersonId, boolean update);
	
	
	public void sendMsg();
	
	

}

