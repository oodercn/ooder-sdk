package net.ooder.agent.client.command;

import java.util.List;


public class CommandReportStatus {
	
	public String commandId;
	
	public String command;
	
	public String errDes;

	public Integer requestStatus;
	
	public Integer status;
	
	public List<String> sensorieees;

	public Integer code;
	
	String  modeId;

	public String getModeId() {
		return modeId;
	}

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}


	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public Integer getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(Integer requestStatus) {
		this.requestStatus = requestStatus;
	}

	public List<String> getSensorieees() {
		return sensorieees;
	}

	public void setSensorieees(List<String> sensorieees) {
		this.sensorieees = sensorieees;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getErrDes() {
		return errDes;
	}

	public void setErrDes(String errDes) {
		this.errDes = errDes;
	}



}
