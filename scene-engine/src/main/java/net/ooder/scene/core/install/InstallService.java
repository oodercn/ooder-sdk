package net.ooder.scene.core.install;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 安装服务
 *
 * @author Ooder Team
 * @since 2.3.1
 */
@Service
public class InstallService {

    private final Map<String, InstallInfo> installTasks = new ConcurrentHashMap<>();

    /**
     * 开始安装
     *
     * @param config 安装配置
     * @return 安装ID
     */
    public CompletableFuture<String> startInstall(Map<String, Object> config) {
        return CompletableFuture.supplyAsync(() -> {
            String installId = UUID.randomUUID().toString();

            InstallInfo info = new InstallInfo();
            info.setInstallId(installId);
            info.setStatus(InstallStatus.IN_PROGRESS);
            info.setProgress(0);
            info.setCurrentStep("初始化");
            info.setMessage("开始安装...");
            info.setStartTime(System.currentTimeMillis());
            info.setConfig(config);

            installTasks.put(installId, info);

            // 模拟异步安装过程
            simulateInstallProcess(installId);

            return installId;
        });
    }

    /**
     * 获取安装状态
     *
     * @param installId 安装ID
     * @return 安装信息
     */
    public InstallInfo getInstallStatus(String installId) {
        return installTasks.get(installId);
    }

    /**
     * 获取安装进度
     *
     * @param installId 安装ID
     * @return 进度百分比
     */
    public int getInstallProgress(String installId) {
        InstallInfo info = installTasks.get(installId);
        return info != null ? info.getProgress() : 0;
    }

    /**
     * 完成安装
     *
     * @param installId 安装ID
     * @return 是否成功
     */
    public CompletableFuture<Boolean> completeInstall(String installId) {
        return CompletableFuture.supplyAsync(() -> {
            InstallInfo info = installTasks.get(installId);
            if (info == null) {
                return false;
            }

            info.setStatus(InstallStatus.COMPLETED);
            info.setProgress(100);
            info.setCurrentStep("完成");
            info.setMessage("安装完成");
            info.setEndTime(System.currentTimeMillis());

            return true;
        });
    }

    /**
     * 模拟安装过程
     */
    private void simulateInstallProcess(String installId) {
        new Thread(() -> {
            try {
                InstallInfo info = installTasks.get(installId);
                if (info == null) return;

                String[] steps = {"检查环境", "下载依赖", "初始化数据库", "配置系统", "启动服务"};
                int[] progressPoints = {10, 30, 50, 70, 90};

                for (int i = 0; i < steps.length; i++) {
                    Thread.sleep(1000); // 模拟每步耗时
                    info.setCurrentStep(steps[i]);
                    info.setProgress(progressPoints[i]);
                    info.setMessage("正在执行: " + steps[i]);
                }

                // 等待完成调用
                while (info.getStatus() == InstallStatus.IN_PROGRESS) {
                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
