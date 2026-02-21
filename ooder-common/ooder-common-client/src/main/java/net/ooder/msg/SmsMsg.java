/**
 * $RCSfile: SmsMsg.java,v $
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

public interface SmsMsg extends Msg{

    public  Integer getTimes();

    public  void setTimes(Integer times);

    public String getEvent();

    public void setEvent(String event);

    public void getPhone();

    public void setPhone();

}
