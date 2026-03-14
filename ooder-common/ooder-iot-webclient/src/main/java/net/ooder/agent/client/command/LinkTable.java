package net.ooder.agent.client.command;

import java.util.ArrayList;
import java.util.List;

public 	class LinkTable{
    String ieee="0086B0000000000A";
    String parentNodeieee="0086B000000000";
    Integer  lqi=150;
    List<LinkTable> childNode=new ArrayList<LinkTable>();

    public String getIeee() {
        return ieee;
    }

    public void setIeee(String ieee) {
        this.ieee = ieee;
    }

    public String getParentNodeieee() {
        return parentNodeieee;
    }

    public void setParentNodeieee(String parentNodeieee) {
        this.parentNodeieee = parentNodeieee;
    }

    public Integer getLqi() {
        return lqi;
    }

    public void setLqi(Integer lqi) {
        this.lqi = lqi;
    }

    public List<LinkTable> getChildNode() {
        return childNode;
    }

    public void setChildNode(List<LinkTable> childNode) {
        this.childNode = childNode;
    }
}

