package net.ooder.agent.client.iot.udp;

import net.ooder.agent.client.command.filter.SendMsgChain;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EsbBeanAnnotation(id = "GHRemoteMsgAdapter", name = "消息分发适配器", expressionArr = "GHRemoteMsgAdapter(\"systemCode\",size)", desc = "UDP发送适配器")
public class GHRemoteMsgAdapter implements MsgAdapter {

    private static ScheduledExecutorService commandpool = Executors.newScheduledThreadPool(100);
    ;
    private String systemCode;
    static Map<String, ScheduledExecutorService> remoteServiceMap = new HashMap<String, ScheduledExecutorService>();

    static synchronized ScheduledExecutorService getRemoteService(String personId) {
        ScheduledExecutorService service = remoteServiceMap.get(personId);
        if (service == null || service.isShutdown()) {
            service = Executors.newSingleThreadScheduledExecutor();
            remoteServiceMap.put(personId, service);
        }
        return service;
    }

    public GHRemoteMsgAdapter(String systemCode, Integer _threadPoolSize) {
        this.systemCode = systemCode;
    }

    public void submit(Msg msg) {
        SendMsgChain msgRun = new SendMsgChain(msg);
        try {
            getRemoteService(msg.getReceiver()).submit(msgRun).get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void stop(Msg msg) {
        commandpool.shutdownNow();
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    }

}