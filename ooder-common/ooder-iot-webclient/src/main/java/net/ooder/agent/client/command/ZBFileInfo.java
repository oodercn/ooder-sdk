package net.ooder.agent.client.command;

public class ZBFileInfo {
    String name;
    Integer size;
    String mode;
    String node;
    String mode_ep_page;

    public String getMode() {
	return mode;
    }

    public void setMode(String mode) {
	this.mode = mode;
    }

    public String getMode_ep_page() {
	return mode_ep_page;
    }

    public void setMode_ep_page(String mode_ep_page) {
	this.mode_ep_page = mode_ep_page;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getNode() {
	return node;
    }

    public void setNode(String node) {
	this.node = node;
    }

    public Integer getSize() {
	return size;
    }

    public void setSize(Integer size) {
	this.size = size;
    }

}
