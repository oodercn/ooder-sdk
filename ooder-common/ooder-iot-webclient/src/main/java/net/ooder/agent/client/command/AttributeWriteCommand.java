package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

import java.util.ArrayList;
import java.util.List;

@EsbBeanAnnotation(id = "AttributeWrite",dataType=ContextType.Command, name = "Zigbee属性读写", expressionArr = "AttributeWriteCommand()", desc = "Zigbee属性读写")
public class AttributeWriteCommand extends SensorCommand implements ADCommand{
    @Override
    public String getFactory() {
        return "jds";
    }
    List<HAAttribute> attributes = new ArrayList<HAAttribute>();



    public AttributeWriteCommand() {
	super(CommandEnums.AttributeWrite);
        HAAttribute attribute=new HAAttribute();
        attribute.setAttributename("ONOFF");
        attribute.setLength(2);
        attribute.setValue("1");
        attributes.add(attribute);
    }

    public List<HAAttribute> getAttributes() {
	return attributes;
    }

    public void setAttributes(List<HAAttribute> attributes) {
	this.attributes = attributes;
    }
}
