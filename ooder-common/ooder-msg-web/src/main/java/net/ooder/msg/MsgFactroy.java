/**
 * $RCSfile: MsgFactroy.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.msg;

import net.ooder.common.JDSException;
import net.ooder.msg.ct.CtMsg;
import net.ooder.msg.ct.CtMsgClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MsgFactroy {

    static MsgFactroy instance;

    Map<String, MsgClient> msgClientMap = new HashMap<String, MsgClient>();


    public static final String THREAD_LOCK = "Thread Lock";


    public static MsgFactroy getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new MsgFactroy();
                }
            }
        }

        return instance;
    }


    public <V extends Msg> MsgClient getClient(String personId, Class<V> clazz) {

        MsgClient client = msgClientMap.get(personId + "." + clazz.getName());
        if (client == null) {
            client = new <V>CtMsgClient(personId, clazz);
            msgClientMap.put(personId + "." + clazz.getName(), client);
        }

        return client;
    }

    public static void main(String[] args) throws IOException {
        String personId="18618287247";
        MsgClient<CtMsg> client = MsgFactroy.getInstance().getClient("18618287247", CtMsg.class);
        try {
            CtMsg msg= client.creatMsg();
            //接收人ID
            msg.setReceiver("1111111");
            //消息内容
            msg.setBody("消息内容");
            //发送
            client.updateMsg(msg);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        //更新消息
        String msgId="ddddddddddd";
        CtMsg msg=client.getMsgById(msgId);
        //更新状态到集群
        client.updateMsg(msg);

        //////////////////////////////

        //获取多有接收到的消息
        try {
            //获取条数
           Integer size=  client.getAllReceiveMsg().getSize();
           //获取集合
            List<CtMsg> msga=client.getAllReceiveMsg().get();
            //获取 所有已发送的例子
            List<CtMsg> sendmsgs=    client.getAllSendMsg().get();
        } catch (JDSException e) {
            e.printStackTrace();
        }




        try {
            //client.getAllSendMsg();
            //ParameterizedType[] interfacesTypes = (ParameterizedType[]) client.getClass().getGenericInterfaces();
            Type clazz = client.getClass().getGenericSuperclass();
            clazz.getTypeName();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}


