/**
 * $RCSfile: ClusterExeCMDControl.java,v $
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
package net.ooder.cluster.udp;

import net.ooder.common.MsgStatus;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.jds.core.esb.util.ActionContext;

public class ClusterExeCMDControl implements Runnable {

    private final ClusterCommand command;

    ClusterExeCMDControl(ClusterCommand command) {
        this.command = command;

    }

    @Override
    public void run() {
        ActionContext.getContext().getContextMap().put("command", command);
        Object obj = EsbUtil.parExpression(command.getExpression());

        if (obj != null && Boolean.valueOf(obj.toString())) {
            UDPClient.getInstance().updateEventStatus(command.getToken(), MsgStatus.UPDATE);
        } else {
            UDPClient.getInstance().updateEventStatus(command.getToken(), MsgStatus.ERROR);
        }
    }
}
