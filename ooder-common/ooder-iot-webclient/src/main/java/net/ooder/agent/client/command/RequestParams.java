package net.ooder.agent.client.command;

import java.util.ArrayList;
import java.util.List;

public class RequestParams {
	String path;
	List<ZBFileInfo> file=new ArrayList<ZBFileInfo>();
	public List<ZBFileInfo> getFile() {
		return file;
	}
	public void setFile(List<ZBFileInfo> file) {
		this.file = file;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

}
