package net.ooder.agent.client.command.task;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.CommandFactory;
import net.ooder.agent.client.command.PasswordCommand;
import net.ooder.agent.client.iot.AppLockPassword;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import  net.ooder.annotation.JoinOperator;
import  net.ooder.annotation.Operator;
import  net.ooder.common.Condition;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.common.CommandEventEnums;
import  net.ooder.msg.CommandMsg;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgFactroy;
import  net.ooder.org.Person;
import  net.ooder.org.query.MsgConditionKey;
import  net.ooder.server.OrgManagerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class LockCheckCallBack implements Callable<Msg>{

	private String ieee;
	private String systemCode;
	
		

	public LockCheckCallBack(String ieee,String systemCode){
	    
	   this.ieee=ieee;	
	   this.systemCode=systemCode;
	}

	public Msg call() throws Exception {
		
		Device device=CtIotCacheManager.getInstance().getDeviceByIeee(ieee);
		
		Person person = OrgManagerFactory.getOrgManager().getPersonByAccount(device.getBindingaccount());
		ZNode znode = CtIotCacheManager.getInstance().findZNodeByIeee(ieee, person.getID());
		
		Condition con = new Condition(MsgConditionKey.MSG_PROCESSINSTID, Operator.EQUALS, ieee);
		
		Condition eventcon = new Condition(MsgConditionKey.MSG_EVENT, Operator.EQUALS, CommandEnums.AddPassword);
		
		con.addCondition(eventcon, JoinOperator.JOIN_AND);
		
	
		List<CommandMsg> realmsgs = (List<CommandMsg>) MsgFactroy.getInstance().getClient(null,CommandMsg.class).getMsgList(con).get();
		
		
		for (CommandMsg msg : realmsgs) {
				AppLockPassword password = new AppLockPassword();
				String commandStr = msg.getBody();
				PasswordCommand command = (PasswordCommand) JSONObject.parseObject(commandStr, PasswordCommand.class);
				password.setStartTime(command.getStartTime() * 1000);
				password.setEndTime(command.getEndTime() * 1000);
				password.setPassword(command.getPassVal1());
				password.setModeId(command.getModeId());
				password.setPassId(command.getPassId());
				password.setGwserialno(command.getGatewayieee());
				password.setStatus(msg.getResultCode());
				password.setPasswordType(command.getPassType());
				password.setMsgId(msg.getId());	
				if (password.getStatus()==null  || password.getStatus().equals(CommandEventEnums.COMMANDTIMEOUT)){
					AddPasswordCommandTask scommand=new AddPasswordCommandTask(password,systemCode);
					CommandFactory.getCommandExecutors(command.getSensorieee()).schedule(scommand,0,TimeUnit.SECONDS);
				}
		}
		
		
		
		return null;
		
	}
}
