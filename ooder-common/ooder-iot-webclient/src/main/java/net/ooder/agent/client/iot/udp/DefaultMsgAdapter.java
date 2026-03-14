package net.ooder.agent.client.iot.udp;

import net.ooder.agent.client.command.filter.SendMsgChain;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgAdapter;
import  net.ooder.web.RemoteConnectionManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@EsbBeanAnnotation(id = "MsgAdapter",
        name = "消息分发适配器",
        expressionArr = "DefaultMsgAdapter()",
        desc = "UDP发送适配器")
public class DefaultMsgAdapter implements MsgAdapter {


    public DefaultMsgAdapter() {


    }


    public void submit(Msg msg) {
        ExecutorService commandpool = RemoteConnectionManager.getConntctionService("SendCommand");
        SendMsgChain msgRun = new SendMsgChain(msg);
        try {
            commandpool.submit(msgRun).get();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void stop(Msg msg) {
        RemoteConnectionManager.getConntctionService("SendCommand").shutdownNow();
    }

}