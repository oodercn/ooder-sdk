package net.ooder.agent.client.command.task;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import  net.ooder.common.JDSException;
import  net.ooder.common.util.StringUtility;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.agent.client.home.ct.CtMsgDataEngine;
import  net.ooder.msg.Msg;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.server.JDSServer;
import  net.ooder.server.OrgManagerFactory;
import  net.ooder.common.ConfigCode;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestTimesCommand implements Callable<Msg> {
    private String commandStr;
    private Integer num = 1;
    private Integer times;
    private String systemCode;
    private static ScheduledExecutorService commandpool = Executors.newScheduledThreadPool(20);
    private String gatewayieee;
    private Integer time = 0;

    public TestTimesCommand(String commandStr, String gatewayieee, String systemCode, Integer num, Integer times) {
        this.num = num;
        this.time = 0;
        this.times = times;
        this.commandStr = commandStr;
        this.systemCode = systemCode;
        this.gatewayieee = gatewayieee;

    }


    public Msg call() {
        Device device = null;

        Msg msg = null;
        try {

            String commandexe = StringUtility.replace(commandStr, "{num}", time.toString());

            JSONObject jsonobj = JSONObject.parseObject(commandexe);

            Command command = (Command) JSONObject.parseObject(commandexe, CommandEnums.fromByName(jsonobj.getString("command")).getCommand());

            command.setGatewayieee(gatewayieee);
            device = CtIotCacheManager.getInstance().getDeviceByIeee(gatewayieee);
            ConfigCode configCode=JDSServer.getClusterClient().getSystem(systemCode).getConfigname();
            CtMsgDataEngine msgEngine = CtMsgDataEngine.getEngine(configCode);


            Person currPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(device.getBindingaccount());

            msgEngine.sendCommand(command, Integer.valueOf(0));
        } catch (HomeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PersonNotFoundException e) {
            OrgManagerFactory.getOrgManager().registerPerson(device.getBindingaccount(), device.getSerialno(), device.getSubsyscode());
        } catch (JDSException e) {
            e.printStackTrace();
        }

        if (time < num) {
            time = time + 1;
            commandpool.schedule(this, times * 2000, TimeUnit.MILLISECONDS);

        }

        return msg;
    }

}
