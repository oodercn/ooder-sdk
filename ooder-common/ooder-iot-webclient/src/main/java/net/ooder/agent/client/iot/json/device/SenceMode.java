package net.ooder.agent.client.iot.json.device;

import java.util.ArrayList;
import java.util.List;

public class SenceMode {
      Integer count;
      List<Mode> mode=new ArrayList<Mode>();
      
      
      class Mode{
    	  ModeInfo mode;

		public ModeInfo getMode() {
			return mode;
		}

		public void setMode(ModeInfo mode) {
			this.mode = mode;
		}
      }
       class ModeInfo{
    	  String modeid;
    	  String modename;
		public String getModeid() {
			return modeid;
		}
		public void setModeid(String modeid) {
			this.modeid = modeid;
		}
		public String getModename() {
			return modename;
		}
		public void setModename(String modename) {
			this.modename = modename;
		}
		
      }
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public List<Mode> getMode() {
		return mode;
	}
	public void setMode(List<Mode> mode) {
		this.mode = mode;
	}
}
