package net.ooder.agent.client.command;

import  net.ooder.common.JDSCommand;

import java.io.Serializable;

public interface SceneCommand extends JDSCommand {


    public String getSceneid();

    public void setSceneid(String sceneid);

    public String getScenename();

    public void setScenename(String Scenename);

    ;

}
