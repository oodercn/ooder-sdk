package net.ooder.scene.core.secure;

import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.ReturnTypeEnums;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.context.JDSContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.scene.core.*;
import net.ooder.scene.engine.EngineStatus;
import net.ooder.scene.provider.model.config.SystemConfig;
import net.ooder.scene.skill.model.RichSkill;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.server.JDSClientService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * SecureSceneEngineProxy - 安全的SceneEngine代理
 *
 * <p><b>【强制】必须通过JDSServer获取，不支持直接创建。</b></p>
 *
 * <p>继承JDSClientService，封装SceneEngine，提供安全的场景引擎访问。</p>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li><b>强制JDSServer获取</b>：必须通过JDSServer.getJDSClientService()获取代理</li>
 *   <li><b>强制登录</b>：必须先登录JDSServer才能获取代理</li>
 *   <li><b>会话绑定</b>：每个代理与特定sessionHandle绑定</li>
 *   <li><b>安全验证</b>：每次操作前验证session有效性</li>
 *   <li><b>统一入口</b>：封装SceneEngine、SceneClient、AdminClient所有功能</li>
 * </ul>
 *
 * <p><b>唯一使用方式（强制）：</b></p>
 * <pre>
 * // 1. 登录JDSServer获取sessionHandle
 * JDSSessionHandle sessionHandle = JDSServer.getInstance().connect(clientService);
 *
 * // 2. 【强制】从JDSServer获取代理（不支持直接new创建）
 * ConfigCode sceneCode = ConfigCode.fromType("scene");
 * SecureSceneEngineProxy proxy = (SecureSceneEngineProxy) JDSServer.getInstance()
 *     .getJDSClientService(sessionHandle, sceneCode);
 *
 * // 3. 使用代理操作SceneEngine（自动验证session）
 * SceneClient client = proxy.login(username, password);
 * List<RichSkill> skills = client.searchSkills(query);
 * </pre>
 *
 * <p><b>配置要求：</b></p>
 * <pre>
 * // 在application.xml中配置scene引擎使用SecureSceneEngineProxy
 * &lt;Application code="scene"&gt;
 *     &lt;JdsService implementation="net.ooder.scene.core.secure.SecureSceneEngineProxy"/&gt;
 * &lt;/Application&gt;
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 * @see JDSClientService
 * @see SceneEngine
 * @see JDSServer#getJDSClientService(JDSSessionHandle, ConfigCode)
 */
public class SecureSceneEngineProxy implements JDSClientService {

    // ============================================ JDSClientService 基础字段
    private JDSSessionHandle sessionHandle;
    private ConfigCode configCode;
    private ConnectInfo connectInfo;
    private ConnectionHandle connectionHandle;

    // ============================================ SceneEngine 委托对象
    private SceneEngine sceneEngine;
    private SceneClient sceneClient;
    private AdminClient adminClient;

    // ============================================ 安全验证
    private final SecureSceneEngineValidator validator;

    /**
     * 构造函数（供JDSServer反射创建使用）
     *
     * <p>符合JDSServer.newJDSClientService()的反射创建要求。</p>
     * <p><b>强制使用JDSServer方式获取代理，不提供其他方式。</b></p>
     *
     * @param connectInfo 连接信息（包含登录名、密码等）
     * @param configCode  配置代码
     */
    public SecureSceneEngineProxy(ConnectInfo connectInfo, ConfigCode configCode) {
        this.connectInfo = connectInfo;
        this.configCode = configCode;
        // 从SceneEngineHolder获取全局SceneEngine实例
        this.sceneEngine = SceneEngineHolder.getInstance().getSceneEngine();
        if (this.sceneEngine == null) {
            throw new IllegalStateException("SceneEngine not initialized. Please ensure SceneEngineAutoConfiguration is loaded.");
        }
        // sessionHandle将在connect方法中设置
        this.validator = new SecureSceneEngineValidator(null, sceneEngine);
    }

    // ============================================ JDSClientService 接口实现

    @Override
    @MethodChinaName(cname = "取得系统", display = false)
    public ConfigCode getConfigCode() {
        return this.configCode;
    }

    @Override
    public String getSystemCode() {
        return this.configCode != null ? this.configCode.getType() : null;
    }

    @Override
    @MethodChinaName(cname = "取得SessionHandle", display = false)
    public JDSSessionHandle getSessionHandle() {
        return this.sessionHandle;
    }

    @Override
    @MethodChinaName(cname = "登陆", returnStr = "connect($connInfo)", display = false)
    public void connect(ConnectInfo connInfo) throws JDSException {
        // 验证session有效性
        validator.validateSession();

        this.connectInfo = connInfo;

        // 通过SceneEngine登录获取SceneClient
        if (connInfo != null && connInfo.getLoginName() != null) {
            this.sceneClient = sceneEngine.login(connInfo.getLoginName(), connInfo.getPassword());
        }
    }

    @Override
    @MethodChinaName(cname = "注销", returnStr = "disconnect()", display = false)
    public ReturnType disconnect() throws JDSException {
        // 验证session有效性
        validator.validateSession();

        if (sessionHandle != null) {
            sceneEngine.logout(sessionHandle.getSessionID());
        }

        this.sceneClient = null;
        this.adminClient = null;

        return new ReturnType(ReturnTypeEnums.MAINCODE_SUCCESS, "Disconnected successfully");
    }

    @Override
    public void setContext(JDSContext context) {
        // SceneEngine不需要额外的context设置
        // 如果需要，可以在子类中重写
    }

    @Override
    public JDSContext getContext() {
        // SceneEngine不需要额外的context
        return null;
    }

    @Override
    @MethodChinaName(cname = "取得登录人信息")
    public ConnectInfo getConnectInfo() {
        return this.connectInfo;
    }

    @Override
    @MethodChinaName(cname = "获取长联接控制器")
    public ConnectionHandle getConnectionHandle() {
        return this.connectionHandle;
    }

    @Override
    @MethodChinaName(cname = "设定长联接控制器")
    public void setConnectionHandle(ConnectionHandle handle) {
        this.connectionHandle = handle;
    }

    // ============================================ SceneEngine 代理方法

    /**
     * 用户登录（用户名密码方式）
     *
     * @param username 用户名
     * @param password 密码
     * @return SceneClient 用户客户端实现
     */
    public SceneClient login(String username, String password) {
        // 验证session有效性
        validator.validateSession();

        this.sceneClient = sceneEngine.login(username, password);
        return this.sceneClient;
    }

    /**
     * 用户登录（Token方式）
     *
     * @param token 访问令牌
     * @return SceneClient 用户客户端实现
     */
    public SceneClient login(String token) {
        // 验证session有效性
        validator.validateSession();

        this.sceneClient = sceneEngine.login(token);
        return this.sceneClient;
    }

    /**
     * 管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @return AdminClient 管理客户端实现
     */
    public AdminClient adminLogin(String username, String password) {
        // 验证session有效性
        validator.validateSession();

        this.adminClient = sceneEngine.adminLogin(username, password);
        return this.adminClient;
    }

    /**
     * 获取引擎状态
     *
     * @return EngineStatus 引擎状态
     */
    public EngineStatus getEngineStatus() {
        // 验证session有效性
        validator.validateSession();

        return sceneEngine.getStatus();
    }

    // ============================================ SceneClient 代理方法

    /**
     * 查找技能
     *
     * @param skillId 技能ID
     * @return RichSkill 技能信息
     */
    public RichSkill findSkill(String skillId) {
        validator.validateSessionAndClient(sceneClient);
        return sceneClient.findSkill(skillId);
    }

    /**
     * 搜索技能
     *
     * @param query 查询条件
     * @return List<RichSkill> 技能列表
     */
    public List<RichSkill> searchSkills(SkillQuery query) {
        validator.validateSessionAndClient(sceneClient);
        return sceneClient.searchSkills(query);
    }

    /**
     * 获取已安装技能列表
     *
     * @return List<InstalledSkillInfo> 已安装技能列表
     */
    public List<InstalledSkillInfo> listMySkills() {
        validator.validateSessionAndClient(sceneClient);
        return sceneClient.listMySkills();
    }

    /**
     * 安装技能
     *
     * @param skillId 技能ID
     * @return SkillInstallResult 安装结果
     */
    public SkillInstallResult installSkill(String skillId) {
        validator.validateSessionAndClient(sceneClient);
        return sceneClient.installSkill(skillId);
    }

    /**
     * 卸载技能
     *
     * @param skillId 技能ID
     * @return SkillUninstallResult 卸载结果
     */
    public SkillUninstallResult uninstallSkill(String skillId) {
        validator.validateSessionAndClient(sceneClient);
        return sceneClient.uninstallSkill(skillId);
    }

    /**
     * 获取安装进度
     *
     * @param installId 安装ID
     * @return SkillInstallProgress 安装进度
     */
    public SkillInstallProgress getInstallProgress(String installId) {
        validator.validateSessionAndClient(sceneClient);
        return sceneClient.getInstallProgress(installId);
    }

    /**
     * 调用能力
     *
     * @param skillId    技能ID
     * @param capability 能力名称
     * @param params     参数
     * @return Object 调用结果
     */
    public Object invokeCapability(String skillId, String capability, Map<String, Object> params) {
        validator.validateSessionAndClient(sceneClient);
        return sceneClient.invokeCapability(skillId, capability, params);
    }

    /**
     * 启动场景组心跳
     *
     * @param groupId 场景组ID
     * @return CompletableFuture<HeartbeatResult> 心跳结果
     */
    public CompletableFuture<HeartbeatResult> startHeartbeat(String groupId) {
        validator.validateSessionAndClient(sceneClient);
        return sceneClient.startHeartbeat(groupId);
    }

    /**
     * 停止场景组心跳
     *
     * @param groupId 场景组ID
     */
    public void stopHeartbeat(String groupId) {
        validator.validateSessionAndClient(sceneClient);
        sceneClient.stopHeartbeat(groupId);
    }

    // ============================================ AdminClient 代理方法

    /**
     * 获取所有技能列表（管理员）
     *
     * @param request 分页请求
     * @return PageResult<RichSkill> 技能列表
     */
    public PageResult<RichSkill> listAllSkills(PageRequest request) {
        validator.validateSessionAndAdminClient(adminClient);
        return adminClient.listAllSkills(request);
    }

    /**
     * 审批技能
     *
     * @param skillId 技能ID
     */
    public void approveSkill(String skillId) {
        validator.validateSessionAndAdminClient(adminClient);
        adminClient.approveSkill(skillId);
    }

    /**
     * 拒绝技能
     *
     * @param skillId 技能ID
     * @param reason  原因
     */
    public void rejectSkill(String skillId, String reason) {
        validator.validateSessionAndAdminClient(adminClient);
        adminClient.rejectSkill(skillId, reason);
    }

    /**
     * 删除技能
     *
     * @param skillId 技能ID
     */
    public void deleteSkill(String skillId) {
        validator.validateSessionAndAdminClient(adminClient);
        adminClient.deleteSkill(skillId);
    }

    /**
     * 获取系统统计信息
     *
     * @return SystemStats 系统统计
     */
    public SystemStats getSystemStats() {
        validator.validateSessionAndAdminClient(adminClient);
        return adminClient.getSystemStats();
    }

    /**
     * 获取系统配置
     *
     * @return SystemConfig 系统配置
     */
    public SystemConfig getSystemConfig() {
        validator.validateSessionAndAdminClient(adminClient);
        return adminClient.getSystemConfig();
    }

    // ============================================ 内部方法

    /**
     * 获取SceneClient（用于高级自定义操作）
     *
     * @return SceneClient
     */
    public SceneClient getSceneClient() {
        validator.validateSession();
        return this.sceneClient;
    }

    /**
     * 获取AdminClient（用于高级自定义操作）
     *
     * @return AdminClient
     */
    public AdminClient getAdminClient() {
        validator.validateSession();
        return this.adminClient;
    }

    /**
     * 获取原始SceneEngine（用于高级自定义操作）
     *
     * @return SceneEngine
     */
    public SceneEngine getSceneEngine() {
        validator.validateSession();
        return this.sceneEngine;
    }

    /**
     * 检查是否已登录SceneClient
     *
     * @return true 已登录
     */
    public boolean isSceneClientLoggedIn() {
        return this.sceneClient != null;
    }

    /**
     * 检查是否已登录AdminClient
     *
     * @return true 已登录
     */
    public boolean isAdminClientLoggedIn() {
        return this.adminClient != null;
    }
}
