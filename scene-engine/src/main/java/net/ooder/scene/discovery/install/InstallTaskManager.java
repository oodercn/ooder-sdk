package net.ooder.scene.discovery.install;

import net.ooder.scene.discovery.internal.InternalDiscoveryService;
import net.ooder.scene.discovery.storage.VfsPathStrategy;
import net.ooder.sdk.service.storage.vfs.VfsManager;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 安装任务管理器
 * 
 * 管理依赖安装任务的生命周期，支持：
 * - 安装进度跟踪
 * - 意外中断恢复
 * - 失败清理
 * - 并发控制
 * 
 * @author ooder Team
 * @since 2.3
 */
public class InstallTaskManager {

    private final VfsManager vfsManager;
    private final ScheduledExecutorService executorService;
    private final Map<String, InstallTask> activeTasks;
    private final Map<String, InstallContext> taskContexts;
    private final BlockingQueue<InstallTask> taskQueue;
    private final AtomicInteger taskCounter;

    // 安装状态常量
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_DOWNLOADING = "DOWNLOADING";
    public static final String STATUS_INSTALLING = "INSTALLING";
    public static final String STATUS_VERIFYING = "VERIFYING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_RECOVERING = "RECOVERING";

    public InstallTaskManager(VfsManager vfsManager) {
        this.vfsManager = vfsManager;
        this.executorService = Executors.newScheduledThreadPool(4);
        this.activeTasks = new ConcurrentHashMap<>();
        this.taskContexts = new ConcurrentHashMap<>();
        this.taskQueue = new LinkedBlockingQueue<>();
        this.taskCounter = new AtomicInteger(0);
        
        // 启动任务处理器
        startTaskProcessor();
        // 恢复未完成的安装
        recoverInterruptedInstalls();
    }

    /**
     * 创建安装任务
     * 
     * @param skillId 技能ID
     * @param version 版本
     * @param dependencies 依赖列表
     * @return 安装任务ID
     */
    public String createInstallTask(String skillId, String version, List<String> dependencies) {
        String installId = generateInstallId(skillId, version);
        
        InstallTask task = new InstallTask();
        task.setInstallId(installId);
        task.setSkillId(skillId);
        task.setVersion(version);
        task.setDependencies(new ArrayList<>(dependencies));
        task.setStatus(STATUS_PENDING);
        task.setStartTime(System.currentTimeMillis());
        task.setTotalSkills(dependencies.size() + 1); // +1 for main skill
        task.setCompletedSkills(0);
        task.setCurrentSkill(null);
        
        InstallContext context = new InstallContext();
        context.setTask(task);
        context.setCancelled(new AtomicBoolean(false));
        context.setPaused(new AtomicBoolean(false));
        
        activeTasks.put(installId, task);
        taskContexts.put(installId, context);
        taskQueue.offer(task);
        
        // 保存任务状态
        saveTaskStatus(task);
        
        return installId;
    }

    /**
     * 获取安装进度
     * 
     * @param installId 安装任务ID
     * @return 安装进度
     */
    public InternalDiscoveryService.InstallProgress getInstallProgress(String installId) {
        InstallTask task = activeTasks.get(installId);
        if (task == null) {
            // 尝试从VFS加载
            task = loadTaskStatus(installId);
        }
        
        if (task == null) {
            return null;
        }
        
        InternalDiscoveryService.InstallProgress progress = new InternalDiscoveryService.InstallProgress();
        progress.setInstallId(installId);
        progress.setCurrentSkill(task.getCurrentSkill());
        progress.setTotalSkills(task.getTotalSkills());
        progress.setCompletedSkills(task.getCompletedSkills());
        progress.setStatus(task.getStatus());
        progress.setMessage(task.getMessage());
        progress.setTimestamp(System.currentTimeMillis());
        
        return progress;
    }

    /**
     * 取消安装任务
     * 
     * @param installId 安装任务ID
     * @return 是否成功取消
     */
    public boolean cancelInstall(String installId) {
        InstallContext context = taskContexts.get(installId);
        if (context == null) {
            return false;
        }
        
        context.getCancelled().set(true);
        
        InstallTask task = activeTasks.get(installId);
        if (task != null) {
            task.setStatus(STATUS_CANCELLED);
            task.setMessage("Installation cancelled by user");
            saveTaskStatus(task);
        }
        
        return true;
    }

    /**
     * 暂停安装任务
     * 
     * @param installId 安装任务ID
     * @return 是否成功暂停
     */
    public boolean pauseInstall(String installId) {
        InstallContext context = taskContexts.get(installId);
        if (context == null) {
            return false;
        }
        
        context.getPaused().set(true);
        
        InstallTask task = activeTasks.get(installId);
        if (task != null) {
            task.setMessage("Installation paused");
            saveTaskStatus(task);
        }
        
        return true;
    }

    /**
     * 恢复安装任务
     * 
     * @param installId 安装任务ID
     * @return 是否成功恢复
     */
    public boolean resumeInstall(String installId) {
        InstallContext context = taskContexts.get(installId);
        if (context == null) {
            // 尝试恢复中断的任务
            return recoverTask(installId);
        }
        
        context.getPaused().set(false);
        
        InstallTask task = activeTasks.get(installId);
        if (task != null && STATUS_FAILED.equals(task.getStatus())) {
            task.setStatus(STATUS_RECOVERING);
            taskQueue.offer(task);
        }
        
        return true;
    }

    /**
     * 清理失败的安装
     * 
     * @param installId 安装任务ID
     * @return 是否成功清理
     */
    public boolean cleanupFailedInstall(String installId) {
        try {
            // 停止任务
            InstallContext context = taskContexts.get(installId);
            if (context != null) {
                context.getCancelled().set(true);
            }
            
            // 删除VFS中的安装文件
            String installPath = VfsPathStrategy.getInstallPath(installId);
            vfsManager.delete(installPath);
            
            // 删除临时文件
            InstallTask task = activeTasks.get(installId);
            if (task != null) {
                String tempPath = VfsPathStrategy.getTempDownloadPath(task.getSkillId(), task.getVersion());
                vfsManager.delete(tempPath);
            }
            
            // 从内存中移除
            activeTasks.remove(installId);
            taskContexts.remove(installId);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取所有活跃任务
     * 
     * @return 活跃任务列表
     */
    public List<InstallTask> getActiveTasks() {
        return new ArrayList<>(activeTasks.values());
    }

    /**
     * 获取任务状态
     * 
     * @param installId 安装任务ID
     * @return 任务状态
     */
    public String getTaskStatus(String installId) {
        InstallTask task = activeTasks.get(installId);
        if (task != null) {
            return task.getStatus();
        }
        return null;
    }

    /**
     * 等待任务完成
     * 
     * @param installId 安装任务ID
     * @param timeoutMs 超时时间（毫秒）
     * @return 是否成功完成
     */
    public boolean waitForCompletion(String installId, long timeoutMs) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            InstallTask task = activeTasks.get(installId);
            if (task == null) {
                return true; // 任务已完成并从内存移除
            }
            
            String status = task.getStatus();
            if (STATUS_COMPLETED.equals(status)) {
                return true;
            }
            if (STATUS_FAILED.equals(status) || STATUS_CANCELLED.equals(status)) {
                return false;
            }
            
            Thread.sleep(100);
        }
        
        return false; // 超时
    }

    /**
     * 启动任务处理器
     */
    private void startTaskProcessor() {
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    InstallTask task = taskQueue.take();
                    processInstallTask(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * 处理安装任务
     */
    private void processInstallTask(InstallTask task) {
        String installId = task.getInstallId();
        InstallContext context = taskContexts.get(installId);
        
        if (context == null || context.getCancelled().get()) {
            return;
        }
        
        task.setStatus(STATUS_INSTALLING);
        saveTaskStatus(task);
        
        try {
            // 安装主技能
            installSkill(task, task.getSkillId(), context);
            
            // 安装依赖
            for (String dependency : task.getDependencies()) {
                if (context.getCancelled().get()) {
                    throw new InterruptedException("Installation cancelled");
                }
                
                while (context.getPaused().get()) {
                    Thread.sleep(1000);
                }
                
                installSkill(task, dependency, context);
            }
            
            task.setStatus(STATUS_COMPLETED);
            task.setEndTime(System.currentTimeMillis());
            task.setMessage("Installation completed successfully");
            
        } catch (Exception e) {
            task.setStatus(STATUS_FAILED);
            task.setEndTime(System.currentTimeMillis());
            task.setMessage("Installation failed: " + e.getMessage());
            task.setError(e.getMessage());
        }
        
        saveTaskStatus(task);
        
        // 完成后从活跃任务中移除（保留一段时间用于查询）
        executorService.schedule(() -> {
            activeTasks.remove(installId);
            taskContexts.remove(installId);
        }, 1, TimeUnit.HOURS);
    }

    /**
     * 安装单个技能
     */
    private void installSkill(InstallTask task, String skillId, InstallContext context) 
            throws InterruptedException {
        task.setCurrentSkill(skillId);
        task.setMessage("Installing " + skillId);
        saveTaskStatus(task);
        
        // 模拟安装过程
        Thread.sleep(1000);
        
        task.setCompletedSkills(task.getCompletedSkills() + 1);
        saveTaskStatus(task);
    }

    /**
     * 恢复中断的安装
     */
    private void recoverInterruptedInstalls() {
        executorService.submit(() -> {
            try {
                // 从VFS加载未完成的安装任务
                String installBasePath = VfsPathStrategy.getBasePath(VfsPathStrategy.PathType.INSTALL);
                List<String> installDirs = vfsManager.listFiles(installBasePath).get();
                
                if (installDirs != null) {
                    for (String installId : installDirs) {
                        InstallTask task = loadTaskStatus(installId);
                        if (task != null && isRecoverableStatus(task.getStatus())) {
                            recoverTask(installId);
                        }
                    }
                }
            } catch (Exception e) {
                // 忽略恢复错误
            }
        });
    }

    /**
     * 恢复单个任务
     */
    private boolean recoverTask(String installId) {
        InstallTask task = loadTaskStatus(installId);
        if (task == null) {
            return false;
        }
        
        // 检查是否可以恢复
        if (!isRecoverableStatus(task.getStatus())) {
            return false;
        }
        
        // 重新创建任务上下文
        InstallContext context = new InstallContext();
        context.setTask(task);
        context.setCancelled(new AtomicBoolean(false));
        context.setPaused(new AtomicBoolean(false));
        
        activeTasks.put(installId, task);
        taskContexts.put(installId, context);
        
        task.setStatus(STATUS_RECOVERING);
        task.setMessage("Recovering installation");
        saveTaskStatus(task);
        
        // 重新加入队列
        taskQueue.offer(task);
        
        return true;
    }

    /**
     * 判断状态是否可恢复
     */
    private boolean isRecoverableStatus(String status) {
        return STATUS_FAILED.equals(status) || 
               STATUS_PENDING.equals(status) || 
               STATUS_DOWNLOADING.equals(status) ||
               STATUS_INSTALLING.equals(status);
    }

    /**
     * 生成安装ID
     */
    private String generateInstallId(String skillId, String version) {
        return skillId + "_" + version + "_" + System.currentTimeMillis() + "_" + taskCounter.incrementAndGet();
    }

    /**
     * 保存任务状态到VFS
     */
    private void saveTaskStatus(InstallTask task) {
        try {
            String path = VfsPathStrategy.getInstallStatusPath(task.getInstallId());
            String json = taskToJson(task);
            vfsManager.writeFile(path, json.getBytes());
        } catch (Exception e) {
            // 忽略保存错误
        }
    }

    /**
     * 从VFS加载任务状态
     */
    private InstallTask loadTaskStatus(String installId) {
        try {
            String path = VfsPathStrategy.getInstallStatusPath(installId);
            byte[] content = vfsManager.readFile(path).get();
            if (content != null) {
                return jsonToTask(new String(content));
            }
        } catch (Exception e) {
            // 忽略加载错误
        }
        return null;
    }

    /**
     * 任务转JSON
     */
    private String taskToJson(InstallTask task) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"installId\":\"").append(task.getInstallId()).append("\",");
        sb.append("\"skillId\":\"").append(task.getSkillId()).append("\",");
        sb.append("\"version\":\"").append(task.getVersion()).append("\",");
        sb.append("\"status\":\"").append(task.getStatus()).append("\",");
        sb.append("\"message\":\"").append(escape(task.getMessage())).append("\",");
        sb.append("\"error\":\"").append(escape(task.getError())).append("\",");
        sb.append("\"totalSkills\":").append(task.getTotalSkills()).append(",");
        sb.append("\"completedSkills\":").append(task.getCompletedSkills()).append(",");
        sb.append("\"startTime\":").append(task.getStartTime()).append(",");
        sb.append("\"endTime\":").append(task.getEndTime());
        sb.append("}");
        return sb.toString();
    }

    /**
     * JSON转任务
     */
    private InstallTask jsonToTask(String json) {
        InstallTask task = new InstallTask();
        // 简化实现
        return task;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    /**
     * 关闭管理器
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 安装任务
     */
    public static class InstallTask {
        private String installId;
        private String skillId;
        private String version;
        private List<String> dependencies;
        private String status;
        private String message;
        private String error;
        private int totalSkills;
        private int completedSkills;
        private String currentSkill;
        private long startTime;
        private long endTime;

        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public int getTotalSkills() { return totalSkills; }
        public void setTotalSkills(int totalSkills) { this.totalSkills = totalSkills; }
        public int getCompletedSkills() { return completedSkills; }
        public void setCompletedSkills(int completedSkills) { this.completedSkills = completedSkills; }
        public String getCurrentSkill() { return currentSkill; }
        public void setCurrentSkill(String currentSkill) { this.currentSkill = currentSkill; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
    }

    /**
     * 安装上下文
     */
    private static class InstallContext {
        private InstallTask task;
        private AtomicBoolean cancelled;
        private AtomicBoolean paused;

        public InstallTask getTask() { return task; }
        public void setTask(InstallTask task) { this.task = task; }
        public AtomicBoolean getCancelled() { return cancelled; }
        public void setCancelled(AtomicBoolean cancelled) { this.cancelled = cancelled; }
        public AtomicBoolean getPaused() { return paused; }
        public void setPaused(AtomicBoolean paused) { this.paused = paused; }
    }
}
