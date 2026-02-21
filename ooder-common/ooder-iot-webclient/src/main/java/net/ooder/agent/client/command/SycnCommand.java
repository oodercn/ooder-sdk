package net.ooder.agent.client.command;

import  net.ooder.agent.client.enums.CommandEnums;

public class SycnCommand extends Command {

    String value="SycnCommand";

    public SycnCommand(CommandEnums command) {
	super(command);
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

}
