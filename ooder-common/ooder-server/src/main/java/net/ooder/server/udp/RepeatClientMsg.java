package net.ooder.server.udp;

import net.ooder.common.JDSException;
import net.ooder.server.JDSUDPServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RepeatClientMsg implements Runnable {

    private String ucontent;

    public RepeatClientMsg(String ucontent) {

	this.ucontent = ucontent;
    }

    @Override
    public void run() {

	String[] uctontentArr = split(ucontent, "||||");
	String tip = uctontentArr[0];
	Integer tport = Integer.valueOf(uctontentArr[1]);
	String tcontent = uctontentArr[2];

	DatagramPacket usendPacket;
	try {
	    usendPacket = new DatagramPacket(tcontent.getBytes(), tcontent.getBytes().length, InetAddress.getByName(tip), tport);
		JDSUDPServer.getInstance().getPushMsgSocket().send(usendPacket);
	} catch (JDSException | IOException e) {
	    // TODO Auto-generated catch block
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
