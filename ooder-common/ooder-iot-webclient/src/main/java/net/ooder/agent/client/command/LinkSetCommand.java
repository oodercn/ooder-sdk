package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "LinkSet",dataType=ContextType.Command, name = "链路设定", expressionArr = "LinkSetCommand()", desc = "链路设定")

public class LinkSetCommand extends Command implements NetCommand{

	public LinkSetCommand() {
		super(CommandEnums.LinkSet);
	}

	public LinkTable linkTable;

	public LinkTable getLinkTable() {
		return linkTable;
	}

	public void setLinkTable(LinkTable linkTable) {
		this.linkTable = linkTable;
	}



}
