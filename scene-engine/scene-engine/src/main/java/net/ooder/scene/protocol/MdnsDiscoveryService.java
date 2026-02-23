package net.ooder.scene.protocol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MdnsDiscoveryService {
    private ExecutorService executor;
    private boolean running;
    private OoderServiceRegistrar registrar;

    public void start() {
        executor = Executors.newSingleThreadExecutor();
        running = true;
        registrar = new OoderServiceRegistrar();

        // 启动mDNS服务
        executor.submit(this::runService);
    }

    public void stop() {
        running = false;
        if (registrar != null) {
            registrar.unregisterServices();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }

    public void registerService(String serviceName, int port) {
        if (registrar != null) {
            registrar.registerService(serviceName, port);
        }
    }

    public void unregisterService(String serviceName) {
        if (registrar != null) {
            registrar.unregisterService(serviceName);
        }
    }

    private void runService() {
        while (running) {
            // 这里可以添加mDNS服务的运行逻辑
            // 暂时简单实现
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
