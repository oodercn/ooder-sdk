/**
 * $RCSfile: ThreadShutdown.java,v $
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
package net.ooder.thread;

import java.util.concurrent.ScheduledExecutorService;


public class ThreadShutdown implements Runnable{
	private ScheduledExecutorService service;
	public ThreadShutdown(ScheduledExecutorService service){
		this.service=service;			
	}
	public void run() {
		service.shutdown();			
	}
}