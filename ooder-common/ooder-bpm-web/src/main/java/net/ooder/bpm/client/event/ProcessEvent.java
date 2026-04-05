
package net.ooder.bpm.client.event;

import java.util.Map;

import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.event.ProcessEventEnums;
import net.ooder.bpm.enums.right.RightCtx;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhangli
 * @version 2.0
 */
public class ProcessEvent extends BPMEvent {

   
    public ProcessEvent(ProcessInst inst, ProcessEventEnums eventID, WorkflowClientService client, Map<RightCtx, Object> context) {
	super(inst);
	this.id = eventID;
	this.client = client;
	this.context = context;
    }

    /**
     * 取得触发此流程事件的流程实例
     */
    public ProcessInst getProcessInst() {
	return (ProcessInst) getSource();
    }

    @Override
    public ProcessEventEnums getID() {
	return (ProcessEventEnums) id;
    }
}
