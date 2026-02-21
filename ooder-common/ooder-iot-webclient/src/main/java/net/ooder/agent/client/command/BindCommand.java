package net.ooder.agent.client.command;

import  net.ooder.agent.client.enums.CommandEnums;

public class BindCommand extends Command {


    String sourcedev = "sourcedev";
    String destdev = "sensorieee";


    String clusterid;

    public BindCommand(CommandEnums command) {
        super(command);
    }


    public String getClusterid() {
        return clusterid;
    }

    public void setClusterid(String clusterid) {
        this.clusterid = clusterid;
    }

    public String getDestdev() {
        return destdev;
    }

    public void setDestdev(String destdev) {
        this.destdev = destdev;
    }

    public String getSourcedev() {
        return sourcedev;
    }

    public void setSourcedev(String sourcedev) {
        this.sourcedev = sourcedev;
    }
}
