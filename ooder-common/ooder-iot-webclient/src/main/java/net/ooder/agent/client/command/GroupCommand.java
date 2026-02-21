package net.ooder.agent.client.command;

import java.io.Serializable;

public interface GroupCommand extends Serializable {

    public String getGroupid() ;

    public void setGroupid(String groupid);

    public String getGroupname();
    
    public void setGroupname(String groupname) ;

}
