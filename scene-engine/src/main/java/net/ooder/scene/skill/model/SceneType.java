package net.ooder.scene.skill.model;

/**
 * 场景类型
 * 
 * <p>定义场景技能的运行模式，类比文件系统中的文件夹类型</p>
 * 
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>场景类型在开发时声明，运行时只读</li>
 *   <li>类型决定场景的自驱能力和触发方式</li>
 *   <li>消除模糊地带，必须明确选择</li>
 * </ul>
 * 
 * <h3>与旧模型映射：</h3>
 * <ul>
 *   <li>AUTO = 原ABS + 原ASS（所有自驱场景）</li>
 *   <li>TRIGGER = 原TBS（触发场景）</li>
 *   <li>HYBRID = 新增（既可主动也可被动）</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0
 */
public enum SceneType {
    
    /**
     * 自主场景
     * 
     * <p>类比：文件系统中的源码包（Source Folder）</p>
     * <p>特点：自包含、可自编译运行</p>
     * 
     * <p>运行模式：</p>
     * <ul>
     *   <li>自驱动运行，无需外部触发</li>
     *   <li>可设置定时任务或事件监听</li>
     *   <li>生命周期由自身控制</li>
     * </ul>
     * 
     * <p>适用场景：</p>
     * <ul>
     *   <li>智能助手（主动推荐）</li>
     *   <li>监控告警（主动发现）</li>
     *   <li>自动化流程（定时执行）</li>
     * </ul>
     */
    AUTO("自主场景", "source-folder", true, false),
    
    /**
     * 触发场景
     * 
     * <p>类比：文件系统中的资源文件夹（Resource Folder）</p>
     * <p>特点：需外部引用，被动响应</p>
     * 
     * <p>运行模式：</p>
     * <ul>
     *   <li>等待外部触发（API调用、用户指令、事件）</li>
     *   <li>无自驱能力，被动响应</li>
     *   <li>生命周期由调用方控制</li>
     * </ul>
     * 
     * <p>适用场景：</p>
     * <ul>
     *   <li>审批流程（人工触发）</li>
     *   <li>报表生成（按需生成）</li>
     *   <li>工具服务（API调用）</li>
     * </ul>
     */
    TRIGGER("触发场景", "resource-folder", false, true),
    
    /**
     * 混合场景
     * 
     * <p>类比：文件系统中的普通文件夹（Regular Folder）</p>
     * <p>特点：既可主动也可被动，灵活切换</p>
     * 
     * <p>运行模式：</p>
     * <ul>
     *   <li>默认自主运行</li>
     *   <li>可随时接受外部触发</li>
     *   <li>根据配置动态调整行为</li>
     * </ul>
     * 
     * <p>适用场景：</p>
     * <ul>
     *   <li>复杂业务系统（既有定时任务又需人工干预）</li>
     *   <li>智能客服（主动推送+被动应答）</li>
     *   <li>工作流引擎（自动流转+人工审批）</li>
     * </ul>
     */
    HYBRID("混合场景", "regular-folder", true, true);
    
    private final String name;
    private final String folderAnalog;
    private final boolean canSelfDrive;
    private final boolean canBeTriggered;
    
    SceneType(String name, String folderAnalog, boolean canSelfDrive, boolean canBeTriggered) {
        this.name = name;
        this.folderAnalog = folderAnalog;
        this.canSelfDrive = canSelfDrive;
        this.canBeTriggered = canBeTriggered;
    }
    
    public String getName() {
        return name;
    }
    
    public String getFolderAnalog() {
        return folderAnalog;
    }
    
    /**
     * 是否可自驱动
     */
    public boolean canSelfDrive() {
        return canSelfDrive;
    }
    
    /**
     * 是否可被触发
     */
    public boolean canBeTriggered() {
        return canBeTriggered;
    }
    
    /**
     * 是否为纯自主场景（只能主动，不能被动）
     */
    public boolean isPureAuto() {
        return canSelfDrive && !canBeTriggered;
    }
    
    /**
     * 是否为纯触发场景（只能被动，不能主动）
     */
    public boolean isPureTrigger() {
        return !canSelfDrive && canBeTriggered;
    }
    
    /**
     * 是否为混合场景（既可主动也可被动）
     */
    public boolean isHybrid() {
        return canSelfDrive && canBeTriggered;
    }
    
    /**
     * 从旧分类代码转换
     * 
     * @param legacyCode 旧分类代码（ABS/ASS/TBS）
     * @return 对应的场景类型
     */
    public static SceneType fromLegacyCode(String legacyCode) {
        if (legacyCode == null) {
            return null;
        }
        String code = legacyCode.toUpperCase();
        if ("ABS".equals(code) || "ASS".equals(code)) {
            return AUTO;
        } else if ("TBS".equals(code)) {
            return TRIGGER;
        }
        return null;
    }
}
