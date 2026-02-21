/**
 * $RCSfile: CommandFactory.java,v $
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
package net.ooder.msg.mqtt.command;

import net.ooder.thread.JDSThreadFactory;
import net.ooder.thread.ThreadShutdown;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandFactory {

    public static Map<String, Class<? extends MQTTCommand>> commandMaping = new HashMap<String, Class<? extends MQTTCommand>>();

    private static CommandFactory instance;


    private static final Map<String, ScheduledExecutorService> threadPoolMap = new HashMap<String, ScheduledExecutorService>();


    private static final Map<String, ScheduledExecutorService> checkTimePoolMap = new HashMap<String, ScheduledExecutorService>();


    private static final Map<String, ScheduledExecutorService> timesPoolMap = new HashMap<String, ScheduledExecutorService>();


    private static final Map<String, ScheduledExecutorService> singlePoolMap = new HashMap<String, ScheduledExecutorService>();


    public final static String COMMANDCONFIG = "command";// 控制命令


    public static final String THREAD_LOCK = "Thread Lock";


    public static ScheduledExecutorService getCommandExecutors(final String ieee) {
        ScheduledExecutorService service = threadPoolMap.get(ieee);
        if (service == null || service.isShutdown()) {
            service = Executors.newScheduledThreadPool(10, new JDSThreadFactory("CommandFactory.getCommandExecutors" + ieee));
            service.schedule(new ThreadShutdown(service), 180, TimeUnit.SECONDS);
            threadPoolMap.put(ieee, service);
        }

        return service;
    }

    /**
     * 取得JDSServer服务器的单例实现
     *
     * @return
     * @throws JDSException
     */
    public static CommandFactory getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new CommandFactory();
                }
            }
        }

        return instance;
    }

    CommandFactory() {

    }


    public Map<String, Class<? extends MQTTCommand>> getCommandMaping() {
        return commandMaping;
    }


    public Map<String, Class<? extends MQTTCommand>> addClass(String name, Class<? extends MQTTCommand> clazz) {
        commandMaping.put(name, clazz);
        return commandMaping;
    }


}


