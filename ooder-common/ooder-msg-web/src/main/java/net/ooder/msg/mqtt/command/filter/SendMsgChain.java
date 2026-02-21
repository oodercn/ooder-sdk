/**
 * $RCSfile: SendMsgChain.java,v $
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
package net.ooder.msg.mqtt.command.filter;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.Msg;
import net.ooder.msg.MsgFactroy;
import net.ooder.msg.filter.MsgFilterChain;
import net.ooder.org.Person;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;

public class SendMsgChain implements Callable<Boolean> {

    private Msg msg;
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, SendMsgChain.class);

    public SendMsgChain(Msg msg) {
        this.msg = msg;
    }

    public Boolean call() throws Exception {
        Boolean isSuccess = false;

        try {
            String receiver = msg.getReceiver();
            Person person = OrgManagerFactory.getOrgManager().getPersonByID(receiver);
            ConnectInfo connectInfo = new ConnectInfo(person.getID(), person.getAccount(), person.getPassword());
            Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connectInfo);
            for (JDSSessionHandle sessionHandle : sessionHandleList) {
                MsgFilterChain chain = new MsgFilterChain();
                chain.addFilter(new MsgFilterImpl());
                chain.addFilter(new CommandFilterImpl());
                isSuccess = chain.filterObject(msg, sessionHandle);
            }
            MsgFactroy.getInstance().getClient(null, Msg.class).updateMsg(msg);


        } catch (Exception e) {

            e.printStackTrace();
        }

        return isSuccess;
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\wenzhang\\Desktop\\smart.tujia.com_3.6.8.bin");
        try {
            System.out.println(MD5.getHashString(file).toString());
            System.out.println(file.length());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


