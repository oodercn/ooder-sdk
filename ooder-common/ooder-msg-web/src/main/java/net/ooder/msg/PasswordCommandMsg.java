/**
 * $RCSfile: PasswordCommandMsg.java,v $
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

public interface PasswordCommandMsg  extends CommandMsg {


    public void setModeId(String modeId);

    public String getModeId();

    public String getPassId();

    public void setPassId(String passId);
}


