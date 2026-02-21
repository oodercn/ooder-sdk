package net.ooder.agent.client.command;

import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import  net.ooder.common.ConfigCode;
import  net.ooder.common.JDSException;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.ct.CtMsgDataEngine;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.server.JDSServer;
import  net.ooder.thread.JDSThreadFactory;
import  net.ooder.thread.ThreadShutdown;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandFactory {

    public static Map<String, Class<? extends Command>> commandMaping = new HashMap<String, Class<? extends Command>>();

    private static CommandFactory instance;


    private static final Map<String, ScheduledExecutorService> threadPoolMap = new HashMap<String, ScheduledExecutorService>();


    private static final Map<String, ScheduledExecutorService> checkTimePoolMap = new HashMap<String, ScheduledExecutorService>();


    private static final Map<String, ScheduledExecutorService> timesPoolMap = new HashMap<String, ScheduledExecutorService>();


    private static final Map<String, ScheduledExecutorService> singlePoolMap = new HashMap<String, ScheduledExecutorService>();


    public final static String COMMANDCONFIG = "command";// 控制命令


    public static final String THREAD_LOCK = "Thread Lock";


//    public static ScheduledExecutorService getCommandTimesExecutors(final String ieee) {
//        ScheduledExecutorService service = timesPoolMap.get(ieee);
//        if (service == null || service.isShutdown()) {
//            service = Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory(ieee));
//            service.schedule(new ThreadShutdown(service), 180, TimeUnit.SECONDS);
//            threadPoolMap.put(ieee, service);
//        }
//
//        return service;
//    }
//
//    public static ScheduledExecutorService getSingleCommandExecutors(final String ieee) {
//        ScheduledExecutorService service = singlePoolMap.get(ieee);
//        if (service == null || service.isShutdown()) {
//            service = Executors.newScheduledThreadPool(2,new JDSThreadFactory("CommandFactory.getSingleCommandExecutors"+ieee));
//            service.schedule(new ThreadShutdown(service), 90, TimeUnit.SECONDS);
//            singlePoolMap.put(ieee, service);
//        }
//
//        return service;
//    }


    public static ScheduledExecutorService getCommandExecutors(final String ieee) {
        ScheduledExecutorService service = threadPoolMap.get(ieee);
        if (service == null || service.isShutdown()) {
            service = Executors.newScheduledThreadPool(10, new JDSThreadFactory("CommandFactory.getCommandExecutors"+ieee));
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

    public CommandClient getCommandClientByieee(String ieee) throws JDSException {
        String sysCode  = CtIotCacheManager.getInstance().getDeviceByIeee(ieee).getSubsyscode();
       ConfigCode configCode= JDSServer.getClusterClient().getSystem(sysCode).getConfigname();
        CtMsgDataEngine msgEngine = (CtMsgDataEngine) HomeServer.getMsgEngine(configCode);
        return msgEngine.getCommandClientByieee(ieee);
    }

    public Map<String, Class<? extends Command>> getCommandMaping() {
        return commandMaping;
    }


    public Map<String, Class<? extends Command>> addClass(String name, Class<? extends Command> clazz) {
        commandMaping.put(name, clazz);
        return commandMaping;
    }


}
