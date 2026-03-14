package net.ooder.agent.client.command.task;

import net.ooder.agent.client.command.PasswordCommand;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import  net.ooder.common.ConfigCode;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.ct.CtMsgDataEngine;
import  net.ooder.server.JDSServer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class DelPassrordCheckCallBackTask implements Callable<PasswordCommand> {

	private PasswordCommand command;

	public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, DelPassrordCheckCallBackTask.class);

	public DelPassrordCheckCallBackTask(PasswordCommand command) {
		this.command = command;

	}

	public PasswordCommand call()  {
		try {
			Device gwDevice = CtIotCacheManager.getInstance().getDeviceByIeee(command.getGatewayieee());
			ConfigCode configCode=JDSServer.getClusterClient().getSystem(command.getSystemCode()).getConfigname();
			CtMsgDataEngine msgEngine = CtMsgDataEngine.getEngine(configCode);

			CommandClient commandClient = msgEngine.getCommandClientByieee(command.getGatewayieee());

			commandClient.sendDeleteLockPasswordCommand(command.getSensorieee(), command.getModeId(), command.getPassId()).get();
			Thread.sleep(20000);
		} catch (HomeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return command;

	}
}
