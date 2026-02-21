/**
 * $RCSfile: JDSThreadFactory.java,v $
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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class JDSThreadFactory implements ThreadFactory {
    static final AtomicInteger poolNumber = new AtomicInteger(1);
    final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix;

    public  JDSThreadFactory(String name) {
	SecurityManager s = System.getSecurityManager();
	group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	namePrefix = "CommandExecutors-[" + name + "]" + poolNumber.getAndIncrement() + "-thread-";
    }

    public Thread newThread(Runnable r) {
	Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
	if (t.isDaemon())
	    t.setDaemon(false);
	if (t.getPriority() != Thread.NORM_PRIORITY)
	    t.setPriority(Thread.NORM_PRIORITY);
	return t;
    }
}
