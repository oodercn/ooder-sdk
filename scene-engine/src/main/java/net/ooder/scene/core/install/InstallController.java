package net.ooder.scene.core.install;

import net.ooder.scene.core.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 安装控制器
 *
 * <p>提供安装相关的API</p>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
@RestController
@RequestMapping("/api/v1/install")
public class InstallController {

    private final InstallService installService;

    @Autowired
    public InstallController(InstallService installService) {
        this.installService = installService;
    }

    /**
     * 获取安装状态
     *
     * @param installId 安装ID
     * @return 安装信息
     */
    @GetMapping("/status")
    public Result<InstallInfo> getInstallStatus(@RequestParam String installId) {
        InstallInfo info = installService.getInstallStatus(installId);
        if (info == null) {
            return Result.error("安装任务不存在", 404);
        }
        return Result.success(info);
    }

    /**
     * 开始安装
     *
     * @param config 安装配置
     * @return 安装ID
     */
    @PostMapping("/start")
    public Result<String> startInstall(@RequestBody Map<String, Object> config) {
        String installId = installService.startInstall(config).join();
        return Result.success(installId);
    }

    /**
     * 获取安装进度
     *
     * @param installId 安装ID
     * @return 进度百分比
     */
    @GetMapping("/progress")
    public Result<Integer> getInstallProgress(@RequestParam String installId) {
        int progress = installService.getInstallProgress(installId);
        return Result.success(progress);
    }

    /**
     * 完成安装
     *
     * @param installId 安装ID
     * @return 是否成功
     */
    @PostMapping("/complete")
    public Result<Boolean> completeInstall(@RequestParam String installId) {
        Boolean success = installService.completeInstall(installId).join();
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("安装任务不存在", 404);
        }
    }
}
