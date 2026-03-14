package net.ooder.agent.client.home.udp;

import com.alibaba.fastjson.JSONObject;
import  net.ooder.config.ResultModel;
import  net.ooder.msg.Msg;
import  net.ooder.server.JDSServer;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import java.nio.charset.Charset;

public class SendRemoteAppMsg implements Runnable{
	private Msg msg;

	private Integer times = 0;
	
	private static String msgUrl="/api/sys/SendAppMsg";
		

	public Msg getMsg() {
		return msg;
	}

	public void setMsg(Msg msg) {
		this.msg = msg;

	}

	public SendRemoteAppMsg(Msg msg) {

		this.msg = msg;

	}

	public void send() {
		times++;
		boolean isSuccess = false;
		
		String serverUrl ="";
		if (JDSServer.getClusterClient().getSystem("console")!=null){
			 serverUrl =JDSServer.getClusterClient().getServerNodeById("console").getUrl()+ msgUrl;
		}else{
			 serverUrl =JDSServer.getClusterClient().getServerNodeById("service").getUrl()+ msgUrl;
		}
		
		Form form = Form.form();
		form.add("msgjson", JSONObject.toJSON(msg).toString());
		form.add("SYSID", msg.getSystemCode());
		
		Request request = Request.Post(serverUrl).bodyForm(form.build(),
				Charset.forName("utf-8"));

		String returnjson = "";
		try {
			returnjson = request.execute().returnContent().asString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!returnjson.equals("")) {
			ResultModel model = JSONObject.parseObject(returnjson,ResultModel.class);
			if (model.getRequestStatus() == 0) {
				isSuccess = true;

			}
		}
	
			//msg.setSubSystemId(msg.getSubSystemId());
			//OrgManagerFactory.getRoManager().updateMsg(msg);
	


	}

	public void run() {
		//send();
//		try {
//			//DbManager.getInstance().beginTransaction();
//			send();
//			DbManager.getInstance().endTransaction(true);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	

	}
}
