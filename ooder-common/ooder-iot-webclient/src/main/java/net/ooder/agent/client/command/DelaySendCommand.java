package net.ooder.agent.client.command;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.common.CommandEventEnums;
import  net.ooder.msg.*;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.server.OrgManagerFactory;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.thread.JDSThreadFactory;
import net.sf.cglib.beans.BeanMap;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DelaySendCommand<T extends Command> implements Callable<T> {

    private final T command;
    private final String personId;
    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, DelaySendCommand.class);
    private static ScheduledExecutorService commandpool = Executors.newScheduledThreadPool(150, new JDSThreadFactory("DelaySendCommand.commandpool"));
    private MinServerActionContextImpl autoruncontext;

    public DelaySendCommand(String personId, T command, String gwIeee) {
        JDSContext context = JDSActionContext.getActionContext();
        this.command = command;
        this.personId = personId;

        this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
        autoruncontext.setParamMap(context.getContext());
        if (context.getSessionId() != null) {
            autoruncontext.setSessionId(context.getSessionId());
            autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
        }
        autoruncontext.setSessionMap(context.getSession());

    }

    public T call() throws Exception {
        JDSActionContext.setContext(autoruncontext);
        String gwAccount = CtIotCacheManager.getInstance().getDeviceByIeee(command.getGatewayieee()).getBindingaccount();

        Person gwPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(gwAccount);
        List<String> personIds = new ArrayList<String>();
        personIds.add(gwPerson.getID());

        MsgClient<CommandMsg> msgMsgClient = MsgFactroy.getInstance().getClient(personId, CommandMsg.class);

        CommandMsg msg = createCommandMsg(command, gwAccount);

        if (msg instanceof PasswordCommandMsg) {
            msgMsgClient = MsgFactroy.getInstance().getClient(personId, PasswordCommandMsg.class);
        }
        msgMsgClient.sendMassMsg(msg, personIds);

        String msgId = msg.getId();

        MsgClient<CommandMsg> finalMsgMsgClient = msgMsgClient;

        commandpool.schedule(new Runnable() {
            @Override
            public void run() {
                CommandMsg newmsg = finalMsgMsgClient.getMsgById(msgId);
                if (newmsg.getResultCode() == null || newmsg.getResultCode().equals(CommandEventEnums.COMMANDINIT)) {
                    newmsg.setResultCode(CommandEventEnums.COMMANDSENDTIME);
                    finalMsgMsgClient.updateMsg(newmsg);
                } else {
                    //补充记录状态
                    finalMsgMsgClient.updateMsg(newmsg);
                }
            }
        }, 10000, TimeUnit.MILLISECONDS);

        command.setCommandId(msgId);
        return command;

    }

    public CommandMsg createCommandMsg(T command, String toPersonAccount) throws JDSException {
        CommandMsg msg = null;
        try {
            Person toPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(toPersonAccount);
            MsgClient<CommandMsg> client = MsgFactroy.getInstance().getClient(personId, CommandMsg.class);
            msg = client.creatMsg2Person(toPerson.getID());
            msg.setType(MsgType.COMMAND.getType());
            msg.setGatewayId(command.getGatewayieee());
            msg.setEvent(command.getCommand().getType());
            msg.setResultCode(CommandEventEnums.COMMANDINIT);
            Map map = BeanMap.create(command);
            Iterator keyit = map.keySet().iterator();
            Map valueMap = new HashMap();
            while (keyit.hasNext()) {
                String key = (String) keyit.next();
                Object value = map.get(key);
                if ((value != null) && (!value.equals(""))) {
                    valueMap.put(key, value);
                }
            }
            msg.setBody(JSONObject.toJSON(valueMap).toString());

            if (command instanceof SensorCommand) {
                SensorCommand gatewayCommand = (SensorCommand) command;
                msg.setSensorId(gatewayCommand.getSensorieee());
            }
            MsgFactroy.getInstance().getClient(personId, CommandMsg.class).updateMsg(msg);


        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }

        return msg;
    }

}
