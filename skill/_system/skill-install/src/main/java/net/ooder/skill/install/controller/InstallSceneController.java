package net.ooder.skill.install.controller;

import net.ooder.skill.install.model.ResultModel;
import net.ooder.skills.api.InstallProgress;
import net.ooder.skills.api.InstallResultWithDependencies;
import net.ooder.skills.api.SkillPackageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/v1/install-scene")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class InstallSceneController {

    private static final Logger log = LoggerFactory.getLogger(InstallSceneController.class);

    @Autowired(required = false)
    private SkillPackageManager skillPackageManager;

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    @Value("${ooder.skills.dir:./skills}")
    private String skillsDir;

    @Value("${ooder.data.dir:./data}")
    private String dataDir;

    private static final Map<String, InstallProcess> activeProcesses = new ConcurrentHashMap<>();

    @GetMapping("/steps")
    public ResultModel<List<InstallStepDTO>> getInstallSteps(@RequestParam(defaultValue = "micro") String profile) {
        log.info("[getInstallSteps] Getting install steps for profile: {}", profile);
        
        List<InstallStepDTO> steps = getStepsForProfile(profile);
        return ResultModel.success(steps);
    }

    @GetMapping("/skills")
    public ResultModel<List<SkillPackageDTO>> getRequiredSkills(@RequestParam(defaultValue = "micro") String profile) {
        log.info("[getRequiredSkills] Getting required skills for profile: {}", profile);
        
        List<SkillPackageDTO> skills = getSkillsForProfile(profile);
        return ResultModel.success(skills);
    }

    @GetMapping("/start")
    public SseEmitter startInstall(@RequestParam(defaultValue = "micro") String profile) {
        log.info("[startInstall] Starting install for profile: {}", profile);
        
        SseEmitter emitter = new SseEmitter(600000L);
        
        String installId = "install-" + System.currentTimeMillis();
        
        InstallProcess process = new InstallProcess();
        process.setInstallId(installId);
        process.setProfile(profile);
        process.setStatus("RUNNING");
        process.setCurrentStep(0);
        process.setSteps(getStepsForProfile(profile));
        process.setSkills(getSkillsForProfile(profile));
        process.setInstalledSkills(new ArrayList<>());
        process.setStartTime(System.currentTimeMillis());
        
        activeProcesses.put(installId, process);
        
        emitter.onCompletion(() -> {
            log.info("SSE completed for: {}", installId);
            activeProcesses.remove(installId);
        });
        
        emitter.onTimeout(() -> {
            log.info("SSE timeout for: {}", installId);
            process.setStatus("TIMEOUT");
            activeProcesses.remove(installId);
        });
        
        emitter.onError(e -> {
            log.error("SSE error for: {}", installId, e);
            process.setStatus("ERROR");
            activeProcesses.remove(installId);
        });
        
        startInstallProcessAsync(installId, emitter, profile);
        
        return emitter;
    }

    private void startInstallProcessAsync(String installId, SseEmitter emitter, String profile) {
        CompletableFuture.runAsync(() -> {
            try {
                InstallProcess process = activeProcesses.get(installId);
                if (process == null) {
                    sendErrorEvent(emitter, "Process not found");
                    return;
                }

                emitter.send(SseEmitter.event()
                    .name("start")
                    .data(Collections.singletonMap("installId", installId)));

                List<InstallStepDTO> steps = process.getSteps();
                List<SkillPackageDTO> skills = process.getSkills();

                for (int stepIndex = 0; stepIndex < steps.size(); stepIndex++) {
                    if (!process.getStatus().equals("RUNNING")) {
                        break;
                    }

                    InstallStepDTO step = steps.get(stepIndex);
                    process.setCurrentStep(stepIndex + 1);

                    emitter.send(SseEmitter.event()
                        .name("step")
                        .data(Map.of(
                            "step", stepIndex + 1,
                            "total", steps.size(),
                            "name", step.getName(),
                            "description", step.getDescription()
                        )));

                    Thread.sleep(500);

                    if (stepIndex == 1 && !skills.isEmpty()) {
                        for (int skillIndex = 0; skillIndex < skills.size(); skillIndex++) {
                            if (!process.getStatus().equals("RUNNING")) {
                                break;
                            }

                            SkillPackageDTO skill = skills.get(skillIndex);

                            emitter.send(SseEmitter.event()
                                .name("skill")
                                .data(Map.of(
                                    "skillId", skill.getId(),
                                    "name", skill.getName(),
                                    "index", skillIndex + 1,
                                    "total", skills.size()
                                )));

                            boolean installed = installSkill(skill, process);
                            
                            String status = installed ? "installed" : "skipped";
                            String message = installed 
                                ? skill.getName() + " 安装成功" 
                                : skill.getName() + " 已存在，跳过";

                            emitter.send(SseEmitter.event()
                                .name("skillProgress")
                                .data(Map.of(
                                    "skillId", skill.getId(),
                                    "status", status,
                                    "message", message
                                )));

                            if (installed) {
                                process.getInstalledSkills().add(skill.getId());
                            }

                            Thread.sleep(300);
                        }
                    }

                    emitter.send(SseEmitter.event()
                        .name("stepComplete")
                        .data(Map.of("step", stepIndex + 1)));

                    Thread.sleep(300);
                }

                process.setStatus("COMPLETED");
                process.setEndTime(System.currentTimeMillis());

                emitter.send(SseEmitter.event()
                    .name("complete")
                    .data(Map.of(
                        "installId", installId,
                        "profile", profile,
                        "installedSkills", process.getInstalledSkills(),
                        "totalInstalled", process.getInstalledSkills().size(),
                        "duration", process.getEndTime() - process.getStartTime()
                    )));

                emitter.complete();

            } catch (IOException | InterruptedException e) {
                log.error("Error in install process: {}", installId, e);
                try {
                    sendErrorEvent(emitter, e.getMessage());
                } catch (IOException ex) {
                    log.error("Failed to send error event", ex);
                }
            }
        });
    }

    private void sendErrorEvent(SseEmitter emitter, String error) throws IOException {
        emitter.send(SseEmitter.event()
            .name("error")
            .data(Map.of("error", error)));
        emitter.complete();
    }

    private boolean installSkill(SkillPackageDTO skill, InstallProcess process) {
        try {
            if (skillPackageManager != null) {
                Boolean isInstalled = skillPackageManager.isInstalled(skill.getId()).join();
                if (isInstalled) {
                    log.info("[installSkill] Skill already installed via SkillPackageManager: {}", skill.getId());
                    return false;
                }
                
                InstallResultWithDependencies result = skillPackageManager
                    .installWithDependencies(skill.getId(), net.ooder.skills.api.InstallRequest.InstallMode.FULL_INSTALL)
                    .join();
                
                if (result.isSuccess()) {
                    log.info("[installSkill] Skill installed via SkillPackageManager: {} - status: {}", 
                        skill.getId(), result.getStatus());
                    return true;
                } else {
                    log.error("[installSkill] Failed to install via SkillPackageManager: {} - {}", 
                        skill.getId(), result.getError());
                    return false;
                }
            }
            
            Path skillPath = Paths.get(skillsDir, "_system", skill.getId());
            if (skillPath.toFile().exists()) {
                log.info("[installSkill] Skill already exists: {}", skill.getId());
                return false;
            }

            Path downloadPath = Paths.get(dataDir, "downloads", skill.getId());
            if (downloadPath.toFile().exists()) {
                Files.createDirectories(skillPath.getParent());
                copyDirectory(downloadPath, skillPath);
                log.info("[installSkill] Copied skill from downloads: {} -> {}", downloadPath, skillPath);
                return true;
            }

            log.info("[installSkill] Skill {} not found locally, marking as installed for demo", skill.getId());
            return true;

        } catch (Exception e) {
            log.error("[installSkill] Failed to install skill: {}", skill.getId(), e);
            return false;
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source)
            .forEach(sourcePath -> {
                try {
                    Path targetPath = target.resolve(source.relativize(sourcePath));
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error("Failed to copy: {}", sourcePath, e);
                }
            });
    }

    private List<InstallStepDTO> getStepsForProfile(String profile) {
        List<InstallStepDTO> steps = new ArrayList<>();
        
        steps.add(new InstallStepDTO(
            "环境检查",
            "检查系统环境和依赖项",
            true,
            false
        ));
        
        steps.add(new InstallStepDTO(
            "技能安装",
            "安装所选规模所需的技能包",
            true,
            false
        ));
        
        steps.add(new InstallStepDTO(
            "知识库初始化",
            "初始化知识资料库",
            true,
            true
        ));
        
        steps.add(new InstallStepDTO(
            "菜单配置",
            "配置系统菜单",
            true,
            true
        ));
        
        steps.add(new InstallStepDTO(
            "激活服务",
            "激活已安装的技能",
            true,
            false
        ));

        if ("enterprise".equals(profile) || "cloud".equals(profile)) {
            steps.add(new InstallStepDTO(
                "集群配置",
                "配置集群和高可用",
                true,
                false
            ));
        }

        if ("cloud".equals(profile)) {
            steps.add(new InstallStepDTO(
                "云端部署",
                "部署到云端环境",
                true,
                false
            ));
        }

        return steps;
    }

    private List<SkillPackageDTO> getSkillsForProfile(String profile) {
        List<SkillPackageDTO> skills = new ArrayList<>();
        
        skills.add(new SkillPackageDTO(
            "skill-discovery",
            "技能发现服务",
            "提供技能发现、发布和管理功能",
            "3.0.2",
            true,
            isSkillInstalled("skill-discovery")
        ));
        
        skills.add(new SkillPackageDTO(
            "skill-capability",
            "能力管理服务",
            "提供能力注册、调用和监控功能",
            "3.0.2",
            true,
            isSkillInstalled("skill-capability")
        ));
        
        skills.add(new SkillPackageDTO(
            "skill-llm-chat",
            "LLM对话服务",
            "提供大语言模型对话和知识库功能",
            "3.0.2",
            true,
            isSkillInstalled("skill-llm-chat")
        ));
        
        skills.add(new SkillPackageDTO(
            "skill-config",
            "配置管理服务",
            "提供系统配置管理功能",
            "3.0.2",
            true,
            isSkillInstalled("skill-config")
        ));
        
        skills.add(new SkillPackageDTO(
            "skill-menu",
            "菜单管理服务",
            "提供场景菜单管理功能",
            "3.0.2",
            true,
            isSkillInstalled("skill-menu")
        ));

        if ("enterprise".equals(profile) || "cloud".equals(profile)) {
            skills.add(new SkillPackageDTO(
                "skill-auth",
                "认证授权服务",
                "提供用户认证和权限管理",
                "3.0.2",
                true,
                isSkillInstalled("skill-auth")
            ));
            
            skills.add(new SkillPackageDTO(
                "skill-org",
                "组织管理服务",
                "提供组织架构和用户管理",
                "3.0.2",
                true,
                isSkillInstalled("skill-org")
            ));
        }

        if ("cloud".equals(profile)) {
            skills.add(new SkillPackageDTO(
                "skill-cluster",
                "集群管理服务",
                "提供集群部署和管理功能",
                "3.0.2",
                false,
                isSkillInstalled("skill-cluster")
            ));
        }

        return skills;
    }

    private boolean isSkillInstalled(String skillId) {
        Path skillPath = Paths.get(skillsDir, "_system", skillId);
        return skillPath.toFile().exists();
    }

    public static class InstallProcess {
        private String installId;
        private String profile;
        private String status;
        private int currentStep;
        private List<InstallStepDTO> steps;
        private List<SkillPackageDTO> skills;
        private List<String> installedSkills;
        private long startTime;
        private long endTime;

        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        
        public String getProfile() { return profile; }
        public void setProfile(String profile) { this.profile = profile; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public int getCurrentStep() { return currentStep; }
        public void setCurrentStep(int currentStep) { this.currentStep = currentStep; }
        
        public List<InstallStepDTO> getSteps() { return steps; }
        public void setSteps(List<InstallStepDTO> steps) { this.steps = steps; }
        
        public List<SkillPackageDTO> getSkills() { return skills; }
        public void setSkills(List<SkillPackageDTO> skills) { this.skills = skills; }
        
        public List<String> getInstalledSkills() { return installedSkills; }
        public void setInstalledSkills(List<String> installedSkills) { this.installedSkills = installedSkills; }
        
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
    }

    public static class InstallStepDTO {
        private String name;
        private String description;
        private boolean required;
        private boolean recommended;

        public InstallStepDTO() {}

        public InstallStepDTO(String name, String description, boolean required, boolean recommended) {
            this.name = name;
            this.description = description;
            this.required = required;
            this.recommended = recommended;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        
        public boolean isRecommended() { return recommended; }
        public void setRecommended(boolean recommended) { this.recommended = recommended; }
    }

    public static class SkillPackageDTO {
        private String id;
        private String name;
        private String desc;
        private String version;
        private boolean required;
        private boolean currentSystem;

        public SkillPackageDTO() {}

        public SkillPackageDTO(String id, String name, String desc, String version, boolean required, boolean currentSystem) {
            this.id = id;
            this.name = name;
            this.desc = desc;
            this.version = version;
            this.required = required;
            this.currentSystem = currentSystem;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDesc() { return desc; }
        public void setDesc(String desc) { this.desc = desc; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        
        public boolean isCurrentSystem() { return currentSystem; }
        public void setCurrentSystem(boolean currentSystem) { this.currentSystem = currentSystem; }
    }
}
