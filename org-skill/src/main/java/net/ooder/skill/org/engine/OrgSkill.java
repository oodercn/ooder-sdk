package net.ooder.skill.org.engine;

import net.ooder.skill.org.driver.OrgDriver;

/**
 * Org Skill 主类
 * 整合 Driver/Engine/Web 三层
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class OrgSkill {
    
    private final OrgDriver driver;
    private final OrgEngine engine;
    private final OrgWeb web;
    
    public OrgSkill() {
        this.driver = new OrgDriver();
        this.engine = new OrgEngine(driver);
        this.web = new OrgWeb(engine);
    }
    
    public OrgSkill(OrgDriver driver) {
        this.driver = driver;
        this.engine = new OrgEngine(driver);
        this.web = new OrgWeb(engine);
    }
    
    /**
     * 获取 Driver 层
     */
    public OrgDriver getDriver() {
        return driver;
    }
    
    /**
     * 获取 Engine 层
     */
    public OrgEngine getEngine() {
        return engine;
    }
    
    /**
     * 获取 Web 层
     */
    public OrgWeb getWeb() {
        return web;
    }
    
    /**
     * 获取 Skill 名称
     */
    public String getName() {
        return "org-skill";
    }
    
    /**
     * 获取 Skill 版本
     */
    public String getVersion() {
        return "2.3.0";
    }
    
    /**
     * 获取 Skill 描述
     */
    public String getDescription() {
        return "Organization Management Skill for Ooder SDK";
    }
}
