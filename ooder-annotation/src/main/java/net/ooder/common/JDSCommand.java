package net.ooder.common;

import java.io.Serializable;

public interface JDSCommand extends Serializable {
	
	static final long serialVersionUID = 112141964444887262L;

	public String getCommandId();

	public void setCommandId(String commandId);

	public void setSystemCode(String systemCode);
	
	public String getSystemCode();
}
