/**
 * $RCSfile: CMDControl.java,v $
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

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.engine.event.Listener;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.jds.core.esb.util.CompoundRoot;
import net.ooder.msg.Msg;
import net.ooder.server.JDSServer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;



public class CMDControl implements Callable<JDSServer> {

	private static Map<String,Msg> msgMap=new HashMap<String,Msg>();

	private Msg msg;
	
	private static final Log logger = LogFactory.getLog(
			JDSConstants.CONFIG_KEY, CMDControl.class);
	
	
	
	 static ReadWriteLock lock = new ReentrantReadWriteLock();  

		public CMDControl(Msg msg ){
			 this.msg=msg;	 
		 }
		
		

		public JDSServer call() throws Exception {
			JDSServer server=JDSServer.getInstance();
			if (!msgMap.containsKey(msg.getId())){
				try{
					ClusterClient client=JDSServer.getClusterClient();
					if (lock.readLock().tryLock(30, TimeUnit.SECONDS)){
						CompoundRoot root=ActionContext.getContext().getValueStack().getRoot();
						if (!root.contains(client)){
							root.push(client);
						}
						client.updateTaskStatus(msg.getId(),"READED");
					}else{
						client.reboot();
					}
				
					
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					lock.readLock().unlock();
					msgMap.put(msg.getId(), msg);
				}
				if (msg!=null){
					//logger.info("end expression "+msg.getEvent());
				}
				
				
			}
	
			return server;
		}


}