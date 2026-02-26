package net.ooder.skill.vfs.engine;

import net.ooder.api.core.Versioned;
import net.ooder.infra.driver.api.HealthStatus;
import net.ooder.skill.vfs.api.entity.VfsEntityFacade;
import net.ooder.skill.vfs.driver.VfsDriver;

/**
 * VFS Skill 实现
 */
public class VfsSkill implements Versioned {
    
    private final String skillId = "vfs";
    private final String version = "1.0.0";
    
    private VfsDriver driver;
    private VfsEngine engine;
    private VfsWeb web;
    
    public VfsSkill() {
        initialize();
    }
    
    private void initialize() {
        this.driver = new VfsDriver();
        this.engine = new VfsEngine(driver);
        this.web = new VfsWeb(engine);
        
        // 初始化 Driver
        driver.initialize();
        
        // 注册到 Facade
        VfsEntityFacade.getInstance().setVfsSkill(this);
    }
    
    @Override
    public String getVersion() {
        return version;
    }
    
    @Override
    public void setVersion(String version) {
        // 版本固定，不允许修改
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public VfsDriver getDriver() {
        return driver;
    }
    
    public VfsEngine getEngine() {
        return engine;
    }
    
    public VfsWeb getWeb() {
        return web;
    }
    
    public void pause() {
        // 暂停逻辑
    }
    
    public void resume() {
        // 恢复逻辑
    }
    
    public void destroy() {
        if (driver != null) {
            driver.destroy();
        }
    }
    
    public HealthStatus healthCheck() {
        if (driver != null) {
            return driver.healthCheck();
        }
        return HealthStatus.UNHEALTHY;
    }
}
