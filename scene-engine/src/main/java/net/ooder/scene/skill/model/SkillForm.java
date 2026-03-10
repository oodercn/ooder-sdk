package net.ooder.scene.skill.model;

/**
 * 技能形态
 * 
 * <p>定义技能的基本结构形态，类比文件系统中的文件和文件夹</p>
 * 
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>技能是唯一核心实体，场景是技能的形态之一</li>
 *   <li>形态在开发时声明，运行时只读</li>
 *   <li>形态决定技能的组织方式和生命周期</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0
 */
public enum SkillForm {
    
    /**
     * 场景技能 - 容器型
     * 
     * <p>类比：文件系统中的文件夹</p>
     * 
     * <p>特点：</p>
     * <ul>
     *   <li>可包含内部能力和子技能</li>
     *   <li>有组织结构，可嵌套</li>
     *   <li>通过场景类型（AUTO/TRIGGER/HYBRID）决定运行模式</li>
     *   <li>用户通过场景入口访问内部能力</li>
     * </ul>
     */
    SCENE("场景技能", "folder", true),
    
    /**
     * 独立技能 - 原子型
     * 
     * <p>类比：文件系统中的文件</p>
     * 
     * <p>特点：</p>
     * <ul>
     *   <li>单一能力单元，不可再分</li>
     *   <li>直接对外提供服务</li>
     *   <li>可被场景技能引用和组合</li>
     *   <li>通过Agent组网参与协作</li>
     * </ul>
     */
    STANDALONE("独立技能", "file", false);
    
    private final String name;
    private final String fileSystemAnalog;
    private final boolean isContainer;
    
    SkillForm(String name, String fileSystemAnalog, boolean isContainer) {
        this.name = name;
        this.fileSystemAnalog = fileSystemAnalog;
        this.isContainer = isContainer;
    }
    
    public String getName() {
        return name;
    }
    
    public String getFileSystemAnalog() {
        return fileSystemAnalog;
    }
    
    /**
     * 是否为容器型（可包含其他技能）
     */
    public boolean isContainer() {
        return isContainer;
    }
    
    /**
     * 是否为场景技能
     */
    public boolean isScene() {
        return this == SCENE;
    }
    
    /**
     * 是否为独立技能
     */
    public boolean isStandalone() {
        return this == STANDALONE;
    }
}
