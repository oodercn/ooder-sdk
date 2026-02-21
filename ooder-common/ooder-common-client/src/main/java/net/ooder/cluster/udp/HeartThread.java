/**
 * $RCSfile: HeartThread.java,v $
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
package net.ooder.cluster.udp;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.ooder.config.JDSConfig;
import net.ooder.jds.core.User;
import net.ooder.server.JDSServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class HeartThread implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(HeartThread.class);
    private DatagramSocket ds;

    private User user;

    Long lastHeartTime;
    private final static SerializeConfig config = new SerializeConfig();

    static {
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }


    public void setLastTime(final Long lastHeartTime) {
        this.lastHeartTime = lastHeartTime;
    }

    ;

    public HeartThread(final DatagramSocket ds, final User user) {
        this.ds = ds;
        this.user = user;
        lastHeartTime = System.currentTimeMillis();
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    @Override
    public void run() {

        try {
            final URL url = new URL("http://" + user.getUdpIP());


            while (ds != null && !ds.isClosed()) {
                try {

                    Thread.sleep(5 * 1000);
                    Long interval = System.currentTimeMillis() - lastHeartTime;
                    if (interval < 15 * 1000 && UDPClient.getInstance().getUser() != null) {
                        final Map<String, String> map = new HashMap<String, String>();

                        HeardInfo heardInfo = new HeardInfo();
                        heardInfo.setSessionId(user.getSessionId());
                        heardInfo.setSystemCode(user.getSystemCode());
                        heardInfo.setUserid(user.getId());

                        final String hertStr = JSONObject.toJSON(heardInfo).toString();
                        final DatagramPacket hp = new DatagramPacket(URLEncoder.encode(hertStr).getBytes(), URLEncoder.encode(hertStr).length(), InetAddress.getByName(url.getHost()), user.getUdpPort());
                        //logger.info("heard start "+url.getHost()+":"+user.getUdpPort());
                        // logger.info("heard start "+hertStr);
                        if (ds != null && !ds.isClosed()) {
                            ds.send(hp);
                        }

                    } else {

                        Boolean self = new Boolean(JDSConfig.getValue("udpServer.self"));
                        if (self != null && self) {
                            // 是udp自身心跳超时，则退出当前应用，触发脚本重新登陆
                            //logger.info("udp can not estabilsh connect then exit application");
                            //System.exit(0);
                        } else {
                            new Thread() {
                                @Override
                                public void run() {
                                    JDSServer.getClusterClient().reboot();
                                }
                            }.start();
                            ds = null;
                        }
                    }

                } catch (final Exception e) {
                    ds.close();
                    // e.printStackTrace();
                }

            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    public DatagramSocket getDs() {
        return ds;
    }

    public void setDs(final DatagramSocket ds) {
        this.ds = ds;
    }

    public static void main(final String[] args) {

        final DatagramSocket clientSocket;
        try {
            clientSocket = new DatagramSocket();
            final String hertStr = "testudpdata";

            final DatagramPacket hp = new DatagramPacket(URLEncoder.encode(hertStr).getBytes(), URLEncoder.encode(hertStr).length(), InetAddress.getByName("www.itjds.net"), 8090);
            clientSocket.send(hp);

        } catch (

                final SocketException e)

        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
