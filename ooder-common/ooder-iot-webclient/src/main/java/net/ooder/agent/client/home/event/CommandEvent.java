package   net.ooder.agent.client.home.event;

import net.ooder.agent.client.command.Command;
import  net.ooder.common.CommandEventEnums;
import  net.ooder.server.JDSClientService;

/**
 * <p>
 * Title: HOME管理系统
 * </p>
 * <p>
 * Description: 核心网关传感器事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 1.0
 */
@SuppressWarnings("all")
public class CommandEvent<T extends Command> extends HomeEvent<T> {

    public static final String CONTEXT_SENSOR = "GatewayEvent.CONTEXT_SENSORS";

    public static final String CONTEXT_COMMAND = "GatewayEvent.CONTEXT_COMMAND";


    private Integer errCode;

    /**
     * GetwayEvent
     * 
     * @param path
     * @param eventID
     */
    public CommandEvent(T command, JDSClientService client, CommandEventEnums eventID, String sysCode) {
	super(command, null);
	id = eventID;
	this.systemCode = sysCode;
	this.setClientService(client);

    }

    @Override
    public CommandEventEnums getID() {
	return (CommandEventEnums) id;

    }


    public Integer getErrCode() {
	return errCode;
    }

    public void setErrCode(Integer errCode) {
	this.errCode = errCode;
    }

}
