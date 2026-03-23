package net.ooder.scene.event;

public enum SceneEventType {
    
    LOGIN_SUCCESS("security.login.success", "登录成功"),
    LOGIN_FAILED("security.login.failed", "登录失败"),
    LOGOUT("security.logout", "登出"),
    TOKEN_GENERATED("security.token.generated", "Token生成"),
    TOKEN_REVOKED("security.token.revoked", "Token撤销"),
    TOKEN_EXPIRED("security.token.expired", "Token过期"),
    TOKEN_VALIDATION_FAILED("security.token.validation.failed", "Token验证失败"),
    OPERATION_DENIED("security.operation.denied", "操作被拒绝"),
    PERMISSION_GRANTED("security.permission.granted", "权限授予"),
    PERMISSION_REVOKED("security.permission.revoked", "权限撤销"),
    
    SESSION_CREATED("session.created", "会话创建"),
    SESSION_DESTROYED("session.destroyed", "会话销毁"),
    SESSION_EXPIRED("session.expired", "会话过期"),
    SESSION_REFRESHED("session.refreshed", "会话刷新"),
    SESSION_TOUCHED("session.touched", "会话活跃"),
    SESSION_VALIDATION_FAILED("session.validation.failed", "会话验证失败"),
    USER_SESSIONS_CLEARED("session.user.cleared", "用户会话清理"),
    
    SKILL_INSTALLED("skill.installed", "技能安装"),
    SKILL_INSTALL_FAILED("skill.install.failed", "技能安装失败"),
    SKILL_UNINSTALLED("skill.uninstalled", "技能卸载"),
    SKILL_UNINSTALL_FAILED("skill.uninstall.failed", "技能卸载失败"),
    SKILL_STARTED("skill.started", "技能启动"),
    SKILL_STOPPED("skill.stopped", "技能停止"),
    SKILL_RESTARTED("skill.restarted", "技能重启"),
    SKILL_EXECUTION_ERROR("skill.execution.error", "技能执行错误"),
    SKILL_SHARED("skill.shared", "技能分享"),
    SKILL_SHARE_ACCEPTED("skill.share.accepted", "技能分享接受"),
    SKILL_SHARE_REJECTED("skill.share.rejected", "技能分享拒绝"),
    SKILL_SHARE_CANCELLED("skill.share.cancelled", "技能分享取消"),
    
    CAPABILITY_REGISTERED("capability.registered", "能力注册"),
    CAPABILITY_UNREGISTERED("capability.unregistered", "能力注销"),
    CAPABILITY_INVOKED("capability.invoked", "能力调用"),
    CAPABILITY_INVOCATION_FAILED("capability.invocation.failed", "能力调用失败"),
    CAPABILITY_SYNC_COMPLETED("capability.sync.completed", "能力同步完成"),
    
    CONFIG_SET("config.set", "配置设置"),
    CONFIG_DELETED("config.deleted", "配置删除"),
    CONFIG_ROLLBACK("config.rollback", "配置回滚"),
    CONFIG_IMPORTED("config.imported", "配置导入"),
    CONFIG_EXPORTED("config.exported", "配置导出"),
    CONFIG_BATCH_SET("config.batch.set", "批量配置设置"),
    SECURITY_CONFIG_CHANGED("config.security.changed", "安全配置变更"),
    
    ENGINE_INITIALIZED("engine.initialized", "引擎初始化"),
    ENGINE_STARTED("engine.started", "引擎启动"),
    ENGINE_STOPPED("engine.stopped", "引擎停止"),
    ENGINE_DESTROYED("engine.destroyed", "引擎销毁"),
    ENGINE_CONFIG_UPDATED("engine.config.updated", "引擎配置更新"),
    ENGINE_HEALTH_CHECK("engine.health.check", "引擎健康检查"),
    ENGINE_REGISTERED("engine.registered", "引擎注册"),
    ENGINE_UNREGISTERED("engine.unregistered", "引擎注销"),
    
    SCENE_AGENT_CREATED("scene.agent.created", "场景Agent创建"),
    SCENE_AGENT_STARTED("scene.agent.started", "场景Agent启动"),
    SCENE_AGENT_PAUSED("scene.agent.paused", "场景Agent暂停"),
    SCENE_AGENT_RESUMED("scene.agent.resumed", "场景Agent恢复"),
    SCENE_AGENT_STOPPED("scene.agent.stopped", "场景Agent停止"),
    SCENE_STATE_SAVED("scene.state.saved", "场景状态保存"),
    SCENE_STATE_LOADED("scene.state.loaded", "场景状态加载"),
    SCENE_ACTIVATED("scene.activated", "场景激活"),
    SCENE_DEACTIVATED("scene.deactivated", "场景停用"),
    SCENE_GROUP_JOINED("scene.group.joined", "加入场景组"),
    SCENE_GROUP_LEFT("scene.group.left", "离开场景组"),
    SCENE_GROUP_CREATED("scene.group.created", "创建场景组"),
    SCENE_GROUP_DESTROYED("scene.group.destroyed", "销毁场景组"),
    SCENE_GROUP_ACTIVATED("scene.group.activated", "激活场景组"),
    SCENE_GROUP_SUSPENDED("scene.group.suspended", "暂停场景组"),
    SCENE_GROUP_ARCHIVED("scene.group.archived", "归档场景组"),
    SCENE_GROUP_RESTORED("scene.group.restored", "恢复场景组"),
    SCENE_GROUP_PARTICIPANT_ADDED("scene.group.participant.added", "添加参与者"),
    SCENE_GROUP_PARTICIPANT_REMOVED("scene.group.participant.removed", "移除参与者"),
    SCENE_GROUP_PARTICIPANT_ROLE_CHANGED("scene.group.participant.role.changed", "参与者角色变更"),
    SCENE_GROUP_CAPABILITY_BOUND("scene.group.capability.bound", "能力绑定"),
    SCENE_GROUP_CAPABILITY_UNBOUND("scene.group.capability.unbound", "能力解绑"),
    SCENE_GROUP_KNOWLEDGE_BOUND("scene.group.knowledge.bound", "知识库绑定"),
    SCENE_GROUP_KNOWLEDGE_UNBOUND("scene.group.knowledge.unbound", "知识库解绑"),
    SCENE_GROUP_SNAPSHOT_CREATED("scene.group.snapshot.created", "快照创建"),
    SCENE_GROUP_SNAPSHOT_RESTORED("scene.group.snapshot.restored", "快照恢复"),
    
    USER_CREATED("user.created", "用户创建"),
    USER_UPDATED("user.updated", "用户更新"),
    USER_DELETED("user.deleted", "用户删除"),
    USER_ENABLED("user.enabled", "用户启用"),
    USER_DISABLED("user.disabled", "用户禁用"),
    USER_PERMISSIONS_CHANGED("user.permissions.changed", "用户权限变更"),
    
    ORG_COMPANY_CREATED("org.company.created", "创建公司"),
    ORG_COMPANY_UPDATED("org.company.updated", "更新公司"),
    ORG_COMPANY_DELETED("org.company.deleted", "删除公司"),
    ORG_DEPARTMENT_CREATED("org.department.created", "创建部门"),
    ORG_DEPARTMENT_UPDATED("org.department.updated", "更新部门"),
    ORG_DEPARTMENT_DELETED("org.department.deleted", "删除部门"),
    ORG_USER_CREATED("org.user.created", "创建组织用户"),
    ORG_USER_UPDATED("org.user.updated", "更新组织用户"),
    ORG_USER_DELETED("org.user.deleted", "删除组织用户"),
    
    WORKFLOW_REGISTERED("workflow.registered", "工作流注册"),
    WORKFLOW_UNREGISTERED("workflow.unregistered", "工作流注销"),
    WORKFLOW_EXECUTED("workflow.executed", "工作流执行"),
    WORKFLOW_COMPLETED("workflow.completed", "工作流完成"),
    WORKFLOW_FAILED("workflow.failed", "工作流失败"),
    WORKFLOW_PAUSED("workflow.paused", "工作流暂停"),
    WORKFLOW_RESUMED("workflow.resumed", "工作流恢复"),
    WORKFLOW_CANCELLED("workflow.cancelled", "工作流取消"),
    
    PEER_DISCOVERED("peer.discovered", "节点发现"),
    PEER_OFFLINE("peer.offline", "节点离线"),
    PEER_STATUS_CHANGED("peer.status.changed", "节点状态变更"),
    PEER_DISCOVERY_COMPLETED("peer.discovery.completed", "节点发现完成"),
    MCP_DISCOVERED("peer.mcp.discovered", "MCP节点发现"),
    
    HEARTBEAT_STARTED("heartbeat.started", "心跳启动"),
    HEARTBEAT_STOPPED("heartbeat.stopped", "心跳停止"),
    HEARTBEAT_TIMEOUT("heartbeat.timeout", "心跳超时"),
    HEARTBEAT_RECOVERED("heartbeat.recovered", "心跳恢复"),
    
    SERVICE_STARTED("service.started", "服务启动"),
    SERVICE_STOPPED("service.stopped", "服务停止"),
    SERVICE_RESTARTED("service.restarted", "服务重启"),
    SERVICE_REGISTERED("service.registered", "服务注册"),
    SERVICE_UNREGISTERED("service.unregistered", "服务注销"),
    SERVICE_DISCOVERED("service.discovered", "服务发现"),
    HEALTH_CHECK_EXECUTED("health.check.executed", "健康检查执行"),
    HEALTH_CHECK_SCHEDULED("health.check.scheduled", "健康检查调度"),
    COMPONENT_HEALTH_WARNING("health.component.warning", "组件健康警告"),
    
    VOLUME_CREATED("volume.created", "卷创建"),
    VOLUME_DELETED("volume.deleted", "卷删除"),
    VOLUME_MOUNTED("volume.mounted", "卷挂载"),
    VOLUME_UNMOUNTED("volume.unmounted", "卷卸载"),
    
    NETWORK_SETTING_UPDATED("network.setting.updated", "网络设置更新"),
    STATIC_IP_ADDED("network.static.ip.added", "静态IP添加"),
    IP_DELETED("network.ip.deleted", "IP删除"),
    IP_BLACKLISTED("network.ip.blacklisted", "IP加入黑名单"),
    IP_UNBLACKLISTED("network.ip.unblacklisted", "IP移出黑名单"),
    
    AUTOSCALE_POLICY_CREATED("autoscale.policy.created", "伸缩策略创建"),
    AUTOSCALE_POLICY_UPDATED("autoscale.policy.updated", "伸缩策略更新"),
    AUTOSCALE_POLICY_DELETED("autoscale.policy.deleted", "伸缩策略删除"),
    AUTOSCALE_POLICY_ENABLED("autoscale.policy.enabled", "伸缩策略启用"),
    AUTOSCALE_POLICY_DISABLED("autoscale.policy.disabled", "伸缩策略禁用"),
    
    SYSTEM_COMMAND_EXECUTED("system.command.executed", "系统命令执行"),
    
    AUDIT_LOG_EXPORTED("audit.log.exported", "审计日志导出"),
    AUDIT_LOG_CLEANED("audit.log.cleaned", "审计日志清理"),
    
    DISCOVERY_PROVIDER_REGISTERED("discovery.provider.registered", "发现提供者注册"),
    DISCOVERY_PROVIDER_UNREGISTERED("discovery.provider.unregistered", "发现提供者注销"),
    DISCOVERY_SYNC_FAILED("discovery.sync.failed", "发现同步失败"),
    
    SKILL_DISCOVERED("skill.discovered", "技能发现"),
    SCENE_DISCOVERED("scene.discovered", "场景发现"),
    CAPABILITY_DISCOVERED("capability.discovered", "能力发现"),
    DISCOVERY_COMPLETED("discovery.completed", "发现完成"),
    DISCOVERY_FAILED("discovery.failed", "发现失败"),
    
    ASSET_REGISTERED("asset.registered", "资产注册"),
    ASSET_UPDATED("asset.updated", "资产更新"),
    ASSET_DECOMMISSIONED("asset.decommissioned", "资产停用"),
    ASSET_OWNERSHIP_TRANSFERRED("asset.ownership.transferred", "资产所有权转移"),
    ASSET_STATUS_CHANGED("asset.status.changed", "资产状态变更"),
    
    KB_PERMISSION_GRANTED("kb.permission.granted", "知识库权限授予"),
    KB_PERMISSION_REVOKED("kb.permission.revoked", "知识库权限撤销"),
    KB_OWNERSHIP_TRANSFERRED("kb.ownership.transferred", "知识库所有权转移"),
    
    KNOWLEDGE_BASE_CREATED("kb.created", "知识库创建"),
    KNOWLEDGE_BASE_UPDATED("kb.updated", "知识库更新"),
    KNOWLEDGE_BASE_DELETED("kb.deleted", "知识库删除"),
    KNOWLEDGE_BASE_DOCUMENT_ADDED("kb.document.added", "知识库文档添加"),
    KNOWLEDGE_BASE_DOCUMENT_DELETED("kb.document.deleted", "知识库文档删除"),
    KNOWLEDGE_BASE_INDEX_REBUILT("kb.index.rebuilt", "知识库索引重建"),
    
    SHARE_CREATED("share.created", "分享创建"),
    SHARE_CANCELED("share.canceled", "分享取消"),
    SHARE_UPDATED("share.updated", "分享更新"),
    SHARE_ACCESSED("share.accessed", "分享访问");
    
    private final String code;
    private final String description;
    
    SceneEventType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
}
