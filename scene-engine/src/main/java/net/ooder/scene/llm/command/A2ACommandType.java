package net.ooder.scene.llm.command;

/**
 * A2A 命令类型枚举
 * 
 * <p>定义 Agent 间通信的所有命令类型。</p>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public enum A2ACommandType {

    LLM_ACTIVATE("llm.activate", "激活 LLM 沙箱"),
    LLM_DEACTIVATE("llm.deactivate", "停用 LLM 沙箱"),
    LLM_CHAT("llm.chat", "LLM 对话命令"),
    LLM_COMPLETE("llm.complete", "LLM 补全命令"),
    LLM_EMBED("llm.embed", "LLM 向量化命令"),
    LLM_FUNCTION("llm.function", "LLM 函数调用命令"),
    
    TASK_SUBMIT("task.submit", "提交任务"),
    TASK_CANCEL("task.cancel", "取消任务"),
    TASK_STATUS("task.status", "查询任务状态"),
    TASK_RESULT("task.result", "获取任务结果"),
    
    ENV_SET("env.set", "设置环境变量"),
    ENV_GET("env.get", "获取环境变量"),
    ENV_CLEAR("env.clear", "清除环境变量"),
    CONTEXT_UPDATE("context.update", "更新上下文"),
    
    KB_SEARCH("kb.search", "知识库检索"),
    KB_INDEX("kb.index", "知识库索引"),
    KB_DELETE("kb.delete", "知识库删除"),
    
    TOOL_INVOKE("tool.invoke", "工具调用"),
    TOOL_REGISTER("tool.register", "工具注册"),
    TOOL_UNREGISTER("tool.unregister", "工具注销"),
    
    SCENE_ENTER("scene.enter", "进入场景"),
    SCENE_EXIT("scene.exit", "退出场景"),
    SCENE_TRANSFER("scene.transfer", "场景数据传输"),
    
    SESSION_CREATE("session.create", "创建会话"),
    SESSION_DESTROY("session.destroy", "销毁会话"),
    SESSION_SYNC("session.sync", "同步会话"),
    
    LLM_HANDSHAKE("llm.handshake", "LLM 握手"),
    LLM_CONTEXT_SHARE("llm.context.share", "LLM 上下文共享"),
    LLM_CONTEXT_REQUEST("llm.context.request", "LLM 上下文请求"),
    LLM_DATA_EXCHANGE("llm.data.exchange", "LLM 数据交换"),
    LLM_TASK_DELEGATE("llm.task.delegate", "LLM 任务委托"),
    LLM_RESULT_RETURN("llm.result.return", "LLM 结果返回");

    private final String code;
    private final String description;

    A2ACommandType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    public static A2ACommandType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (A2ACommandType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
