/**
 * $RCSfile: UDPControl.java,v $
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
import net.ooder.jds.core.User;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UDPControl implements Runnable {

	public static final ReadWriteLock lock = new ReentrantReadWriteLock(true);

	private String msgId;

	private User user;

	private String status;

	public UDPControl(User user, String msgId, String status) {
		this.msgId = msgId;
		this.user = user;
		this.status = status;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		if (msgId != null) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(UDPClient.SESSIONID, user.getSessionId());
			map.put(UDPClient.SYSTEMCODE, user.getSystemCode());
			map.put("msgId", msgId);
			map.put("status", status);
			String mpStr = JSONObject.toJSONString(map);
			URL url = null;

			try {
				lock.writeLock().lock();
				url = new URL("http://" + user.getUdpIP());
				DatagramPacket hp = new DatagramPacket(URLEncoder.encode(mpStr)
						.getBytes(), URLEncoder.encode(mpStr).length(),
						InetAddress.getByName(url.getHost()), user.getUdpPort());
				UDPClient.getInstance().getClientSocket().send(hp);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.writeLock().unlock();
			}

		}
	}

}