
package net.ooder.bpm.client.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.RightEngine;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.event.RightEventEnums;

import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 权限事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhangli
 * @version 2.0
 */
public class RightEvent extends BPMEvent {

    private RightEngine rightEngine;

    private ActivityInst inst;

    public RightEvent(ActivityInst inst, RightEventEnums eventID, WorkflowClientService client, Map context) {
	super(inst);

	id = eventID;
	this.client = client;
	this.inst = inst;

	this.rightEngine = (RightEngine) EsbUtil.parExpression("$RightEngine");

	this.context = context;
    }

    @Override
    public RightEventEnums getID() {
	return (RightEventEnums) id;
    }

    /**
     * 取得触发此权限事件的一个或多个活动实例
     */
    public ActivityInst getActivityInst() {
	return (ActivityInst) getSource();
    }

    /**
     * 获取当前办理人
     * 
     * @return
     */
    public Person getCurrPerformer() {

	Person person = null;
	try {
	    person = client.getOrgManager().getPersonByID(client.getConnectInfo().getUserID());
	} catch (PersonNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return person;
    }

    /**
     * 获取发送人
     * 
     * @return
     */
    public List<Person> getSponsor() {
	List<Person> sponsors = new ArrayList<Person>();
	try {
	    sponsors = (List<Person>) rightEngine.getActivityInstRightAttribute(inst.getActivityInstId(), ActivityInstRightAtt.SPONSOR, context);
	} catch (BPMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return sponsors;
    }

    /**
     * 获取当前活动办理人
     * 
     * @return
     */
    public List<Person> getPerformer() {
	List<Person> performers = new ArrayList<Person>();
	try {
	    performers = (List<Person>) rightEngine.getActivityInstRightAttribute(inst.getActivityInstId(), ActivityInstRightAtt.PERFORMER, context);
	} catch (BPMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return performers;
    }

    /**
     * 获取当前活动阅办人
     * 
     * @return
     */
    public List<Person> getReader() {
	List<Person> readers = new ArrayList<Person>();
	try {
	    readers = (List<Person>) rightEngine.getActivityInstRightAttribute(inst.getActivityInstId(), ActivityInstRightAtt.READER, context);
	} catch (BPMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return readers;
    }

}
