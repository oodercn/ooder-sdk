
package net.ooder.agent.client.iot.udp;
import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.command.filter.CommandFilterChain;
import net.ooder.agent.client.command.filter.command.LocalCommandFilterImpl;
import net.ooder.agent.client.command.filter.command.RemoteCommandFilterImpl;
import net.ooder.agent.client.iot.HomeException;
import  net.ooder.cluster.udp.ClusterCommand;
import  net.ooder.common.JDSException;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.context.JDSUDPContext;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.common.CommandEventEnums;
import  net.ooder.agent.client.home.engine.HomeEventControl;
import  net.ooder.agent.client.home.event.CommandEvent;
import  net.ooder.agent.client.home.udp.SendAppMsg;
import  net.ooder.agent.client.home.udp.UDPData;
import  net.ooder.msg.Msg;
import  net.ooder.msg.SensorMsg;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.OrgManagerFactory;
import  net.ooder.server.udp.AbstractUDPHandle;

import java.util.Map;
import java.util.concurrent.Executors;
public class GHUDPHandle extends AbstractUDPHandle {

    public GHUDPHandle(JDSClientService cleint, JDSSessionHandle sessionHandle, String systemCode) throws JDSException {
        super(cleint, sessionHandle, systemCode);
    }

    private void fireCommandEvent(Command command, CommandEventEnums eventID, Map eventContext) throws HomeException {
        try {
            Map context = this.getClient().getContext().getContext();
            if (eventContext != null) {
                context.putAll(eventContext);
            }
            CommandEvent event = new CommandEvent(command, this.getClient(), eventID, getSystemCode());
            event.setContextMap(context);
            HomeEventControl.getInstance().dispatchEvent(event);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    @Override
    public void receive(String receiveStr) throws JDSException {
        JSONObject jsonobj = JSONObject.parseObject(receiveStr);
        if (jsonobj.containsKey("commandJson")) {
            ClusterCommand clustercommand = JSONObject.parseObject(receiveStr, ClusterCommand.class);
            String commandStr = clustercommand.getCommand();
            if (CommandEnums.fromByName(commandStr) != null) {
                Command command = JSONObject.parseObject(clustercommand.getCommandJson().toString(), CommandEnums.fromByName(commandStr).getCommand());
                JDSSessionHandle handle = clustercommand.getSessionHandle();
                CommandFilterChain chain = new CommandFilterChain();
                chain.addFilter(new LocalCommandFilterImpl());
                chain.addFilter(new RemoteCommandFilterImpl());
                // 开始发送
                fireCommandEvent(command, CommandEventEnums.COMMANDROUTING, null);

                chain.filterObject(command, handle);
                // 结束发送
                fireCommandEvent(command, CommandEventEnums.COMMANDROUTED, null);

            }

        } else {
            UDPData data = (UDPData) JSONObject.parseObject(receiveStr, UDPData.class);
            sendCommand(data);
        }
    }

    @Override
    public boolean send(String msgString) throws JDSException {

        if (this.getConnectInfo() != null) {
            logger.debug("send user:[" + this.getConnectInfo().getLoginName() + "] ");
            logger.debug(msgString);
        }
        Boolean canSend = false;
        if (this.getIp() != null && this.getPort() != null) {
            canSend = this.getUdpServer().send(msgString, this.getIp(), this.getPort());
        }

        return canSend;

    }

    private void sendCommand(UDPData data) throws HomeException {
        if (data != null && data.getSensorinfo() != null) {
            Integer value = Integer.valueOf(data.getSensorinfo().getValue());
            String sensorId = data.getSensorinfo().getSensorId();
            // TaskEngine.scheduleTask(new LightCommand(sensorId,value), new Date());
        }

    }

    @Override
    public void connect(JDSContext context) throws JDSException {
        super.connect(context);
        JDSUDPContext updContext = (JDSUDPContext) context;

        this.setIp(updContext.getIpAddr());
        this.setPort(updContext.getPort());
        JDSUDPContext jdsUDPContext = (JDSUDPContext) context;
        Person eiperson = null;
        try {
            eiperson = OrgManagerFactory.getOrgManager().getPersonByID(this.getClient().getConnectInfo().getUserID());
        } catch (PersonNotFoundException e1) {
            e1.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return this.getIp() + ":" + this.getPort() + "[" + this.getSessionHandle() + "]";
    }

    ;

    @Override
    public void disconnect() throws JDSException {
        super.disconnect();
    }

    @Override
    public boolean repeatMsg(Msg msg, JDSSessionHandle handle) throws JDSException {
        SendAppMsg msgRun = new SendAppMsg((SensorMsg) msg, handle, JDSActionContext.getActionContext());
        Executors.newSingleThreadExecutor().execute(msgRun);
        return true;
    }

}
