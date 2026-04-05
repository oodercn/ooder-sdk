
package net.ooder.bpm.client.event;

import java.util.List;
import java.util.Map;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.event.ActivityEventEnums;
import net.ooder.bpm.enums.right.RightCtx;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhangli
 * @version 2.0
 */
public class ActivityEvent extends BPMEvent {

    public ActivityEvent(List<ActivityInst> insts, ActivityEventEnums eventID, WorkflowClientService client, Map<RightCtx, Object> context) {
        super(insts);
        this.id = eventID;
        this.client = client;
        this.context = context;
    }

    @Override
    public ActivityEventEnums getID() {
        return (ActivityEventEnums) id;
    }

    public void setEventID(ActivityEventEnums eventID) {
        this.id = eventID;
    }

    /**
     * 取得触发此流程事件的一个或多个活动实例
     */
    public List<ActivityInst> getActivityInsts() {
        return (List<ActivityInst>) getSource();
    }
}
