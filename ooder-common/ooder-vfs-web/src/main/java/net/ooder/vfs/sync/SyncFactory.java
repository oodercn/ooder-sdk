package net.ooder.vfs.sync;

import  net.ooder.config.JDSConfig;
import  net.ooder.thread.JDSThreadFactory;
import  net.ooder.thread.ThreadShutdown;
import  net.ooder.vfs.sync.restor.RestorCMD;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SyncFactory {

    static SyncFactory factory;


    static String vfsRootPath = "/root/cediskroot/";
    static String localRootPath = JDSConfig.Config.rootServerHome().getAbsolutePath();
    private int maxTaskSize = 50;

    public static final String THREAD_LOCK = "VFS LOCK Thread Lock";

    private Map<Path, SyncLocal> localMap = new HashMap<Path, SyncLocal>();

    private static Map<Path, ScheduledExecutorService> serviceMap = new HashMap<Path, ScheduledExecutorService>();
    private static Map<String, ScheduledExecutorService> serverMap = new HashMap<String, ScheduledExecutorService>();
    private static Map<Path, ScheduledExecutorService> uploadServiceMap = new HashMap<Path, ScheduledExecutorService>();

    public static ScheduledExecutorService getLocalService(Path path) {
        ScheduledExecutorService service = serviceMap.get(path);
        String threadName = "getLocalService[" + path.toString() + "]";
        synchronized (path) {
            if (service == null || service.isShutdown()) {
                service = Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory(threadName));
                serviceMap.put(path, service);
            } else {
                service.shutdownNow();
                service = Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory(threadName));
                serviceMap.put(path, service);
            }
        }
        return service;
    }

    public static ScheduledExecutorService getServerService(String path) {
        ScheduledExecutorService service = serverMap.get(path);
        String threadName = "getLocalService[" + path.toString() + "]";
        synchronized (path) {
            if (service == null || service.isShutdown()) {
                service = Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory(threadName));
                serverMap.put(path, service);
            } else {
                service.shutdownNow();
                service = Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory(threadName));
                serverMap.put(path, service);
            }
        }
        return service;
    }

    public String getVfsRootPath() {
        return vfsRootPath;
    }

    public void setVfsRootPath(String vfsRootPath) {
        this.vfsRootPath = vfsRootPath;
    }

    public String getLocalRootPath() {
        return localRootPath;
    }


    public static ExecutorService getUPLoadService(Path path, Long delayTime, int maxTaskSize) {
        ScheduledExecutorService service = uploadServiceMap.get(path);
        if (service == null || service.isShutdown()) {
            service = Executors.newScheduledThreadPool(maxTaskSize + 1, new JDSThreadFactory("getUPLoadService[" + path.toString() + "]"));
            service.schedule(new ThreadShutdown(service), delayTime, TimeUnit.MILLISECONDS);
            uploadServiceMap.put(path, service);
        }
        return service;
    }


    public static SyncFactory getInstance() {
        synchronized (THREAD_LOCK) {
            if (factory == null) {
                factory = new SyncFactory();
            }
        }
        return factory;

    }

    SyncLocal getSyncLocal(Path path, String vfsPath) {
        SyncLocal sync = localMap.get(path);
        if (sync == null) {
            synchronized (THREAD_LOCK) {
                sync = new SyncLocal(path, vfsPath);
                localMap.put(path, sync);

            }
        }
        return sync;

    }

    public void push(Path path, String vfsPath) throws IOException, ExecutionException, InterruptedException {
        push(path, vfsPath, maxTaskSize);
    }

    public void importAll(Path path, int maxTaskSize) throws IOException, ExecutionException, InterruptedException {
        SyncLocal sync = localMap.get(path);
        if (sync == null) {
            synchronized (THREAD_LOCK) {
                sync = new SyncLocal(path, 0, maxTaskSize);
                localMap.put(path, sync);

            }
        }
        sync.importAll();
    }

    public void restor(Path path, int maxTaskSize) throws IOException, ExecutionException, InterruptedException {

        RestorCMD restorDB = new RestorCMD(path, maxTaskSize);
        restorDB.restore();
    }

    public void push(Path path, String vfsPath, Integer maxTaskSize) throws IOException, ExecutionException, InterruptedException {
        SyncLocal sync = localMap.get(path);
        if (sync == null) {
            synchronized (THREAD_LOCK) {
                sync = new SyncLocal(path, vfsPath, maxTaskSize);
                localMap.put(path, sync);

            }
        }

        sync.push().get();


    }

    public void pull(Path path, String vfsPath, Integer maxTaskSize) throws IOException {
        SyncLocal sync = localMap.get(path);
        if (sync == null) {
            synchronized (THREAD_LOCK) {
                sync = new SyncLocal(path, vfsPath, maxTaskSize);
                localMap.put(path, sync);

            }
        }

        sync.downLoad();


    }

    public void pull(Path path, String vfsPath) throws IOException {
        pull(path, vfsPath, maxTaskSize);


    }


    SyncLocal getSyncLocal(Path path, Long delayTime, int maxTaskSize) {
        SyncLocal sync = localMap.get(path);
        if (sync == null) {
            synchronized (THREAD_LOCK) {
                sync = new SyncLocal(path, delayTime, maxTaskSize);
                localMap.put(path, sync);

            }
        }
        return sync;

    }

    void addListener(Path path, Long delayTime, int maxTaskSize) {

        try {
            WatchService watcher = path.getFileSystem().newWatchService();
            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                WatchKey watckKey = watcher.take();

                List<WatchEvent<?>> events = watckKey.pollEvents();
                for (WatchEvent<?> event : events) {
                    Path cpath = Paths.get(path.toFile().getAbsolutePath() + "/" + event.context().toString());

                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        SyncLocal local = SyncFactory.getInstance().getSyncLocal(cpath, delayTime, maxTaskSize);
                        local.push();
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {

                        SyncLocal local = SyncFactory.getInstance().getSyncLocal(cpath, delayTime, maxTaskSize);
                        local.push();
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        SyncLocal local = SyncFactory.getInstance().getSyncLocal(cpath, delayTime, maxTaskSize);
                        local.push();
                    }
                }
                // 重设WatchKey
                boolean valid = watckKey.reset();
                // 如果重设失败，退出监听
                if (!valid) {
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

}
