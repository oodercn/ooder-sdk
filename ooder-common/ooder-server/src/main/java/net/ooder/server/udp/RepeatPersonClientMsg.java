package net.ooder.server.udp;

import net.ooder.common.JDSException;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.server.JDSServer;
import net.ooder.server.JDSUDPServer;
import net.ooder.server.OrgManagerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class RepeatPersonClientMsg implements Runnable {

    private String subsystemCode;
    private String content;

    public RepeatPersonClientMsg(String content, String subsystemCode) {
        this.subsystemCode = subsystemCode;
        this.content = content;
    }

    @Override
    public void run() {
        String[] uctontentArr = split(content, "||&&&&");
        String personAccount = uctontentArr[0];
        String msgjson = uctontentArr[1];
        Person toPerson;
        try {
            toPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(personAccount);
            ConnectInfo connection = new ConnectInfo(toPerson.getID(), toPerson.getAccount(), toPerson.getPassword());
            Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connection);
            List<String> ipList = new ArrayList<String>();
            for (JDSSessionHandle handle : sessionHandleList) {
                if (handle.getIp() != null && handle.getPort() != null && handle.getPort() != 0) {
                    if (!ipList.contains(handle.getIp() + ":" + handle.getPort())) {
                        DatagramPacket usendPacket = new DatagramPacket(msgjson.getBytes(), msgjson.getBytes().length, InetAddress.getByName(handle.getIp()), handle.getPort());
                        JDSUDPServer.getInstance().getPushMsgSocket().send(usendPacket);
                        ipList.add(handle.getIp() + ":" + handle.getPort());
                    }
                }
            }
        } catch (PersonNotFoundException | JDSException | IOException e) {
            e.printStackTrace();
        }

    }

    public static String[] split(String sourceString, String delim) {
        if (sourceString == null || delim == null)
            return new String[0];
        StringTokenizer st = new StringTokenizer(sourceString, delim);
        List stringList = new ArrayList();
        for (; st.hasMoreTokens(); stringList.add(st.nextToken()))
            ;
        return (String[]) (stringList.toArray(new String[stringList.size()]));
    }
}
