
package net.ooder.bpm.client.event;

import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.enums.event.BPDEventEnums;

import java.util.Map;

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
public class BPDEvent extends BPMEvent {

    public BPDEvent(ProcessDefVersion version, BPDEventEnums eventID,  Map context) {
        super(version);
        this.id = eventID;
        this.client = client;
        this.context = context;
    }

    @Override
    public BPDEventEnums getID() {
        return (BPDEventEnums) id;
    }

    public void setEventID(BPDEventEnums eventID) {
        this.id = eventID;
    }

}
