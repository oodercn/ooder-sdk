/**
 * $RCSfile: EIServerEvent.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:02 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: EIServerEvent.java,v $
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
package net.ooder.engine.event;

import net.ooder.common.JDSEvent;
import net.ooder.common.ServerEventEnums;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 核心服务器事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 2.0
 */
public class EIServerEvent extends JDSEvent {

    public EIServerEvent(Object obj, ServerEventEnums eventID) {
        super(obj, null);
        id = eventID;

    }

    @Override
    public ServerEventEnums getID() {
        return (ServerEventEnums) id;
    }

    public EIServerEvent(ServerEventEnums eventID) {
        super("ServerEvent", null);
        id = eventID;
    }

}
