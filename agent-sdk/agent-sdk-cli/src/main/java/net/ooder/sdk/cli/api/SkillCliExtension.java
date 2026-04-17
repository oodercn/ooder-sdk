package net.ooder.sdk.cli.api;

import java.util.Map;

/**
 * Skill CLI 扩展接口 (统一接口)
 *
 * <p>与 Skills 框架兼容的 CLI 扩展接口定义。</p>
 * <p>此接口作为 Skill 团队开发 CLI 扩展的标准接口。</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface SkillCliExtension {

    /**
     * 获取 Skill ID
     *
     * @return Skill 唯一标识
     */
    String getSkillId();

    /**
     * 获取命令名称
     *
     * @return 命令名称
     */
    String getCommand();

    /**
     * 获取命令描述
     *
     * @return 命令描述
     */
    String getDescription();

    /**
     * 获取命令用法说明
     *
     * @return 用法说明
     */
    default String getUsage() {
        return getCommand();
    }

    /**
     * 执行命令
     *
     * @param args 命令参数
     * @param context 场景上下文（包含 sceneId, sessionId 等信息）
     * @return 执行结果
     */
    CliResult execute(String[] args, Map<String, Object> context);

    /**
     * 获取命令分类
     *
     * @return 命令分类
     */
    default String getCategory() {
        return "skill";
    }

    /**
     * 获取命令别名
     *
     * @return 别名数组
     */
    default String[] getAliases() {
        return new String[0];
    }

    /**
     * 验证参数
     *
     * @param args 参数
     * @return 验证结果
     */
    default boolean validate(String[] args) {
        return true;
    }

    /**
     * 是否支持交互模式
     *
     * @return 是否支持
     */
    default boolean isInteractive() {
        return false;
    }

    /**
     * 初始化扩展
     */
    default void initialize() {
        // 默认空实现
    }

    /**
     * 销毁扩展
     */
    default void destroy() {
        // 默认空实现
    }

    /**
     * CLI 执行结果
     */
    interface CliResult {
        /**
         * 获取退出码
         * @return 退出码，0表示成功
         */
        int getExitCode();

        /**
         * 获取输出消息
         * @return 输出消息
         */
        String getMessage();

        /**
         * 获取结构化数据
         * @return 数据对象
         */
        Object getData();

        /**
         * 是否成功
         * @return 是否成功
         */
        boolean isSuccess();

        /**
         * 创建成功结果
         */
        static CliResult success(String message) {
            return new CliResult() {
                @Override
                public int getExitCode() { return 0; }
                @Override
                public String getMessage() { return message; }
                @Override
                public Object getData() { return null; }
                @Override
                public boolean isSuccess() { return true; }
            };
        }

        /**
         * 创建成功结果（带数据）
         */
        static CliResult success(String message, Object data) {
            return new CliResult() {
                @Override
                public int getExitCode() { return 0; }
                @Override
                public String getMessage() { return message; }
                @Override
                public Object getData() { return data; }
                @Override
                public boolean isSuccess() { return true; }
            };
        }

        /**
         * 创建失败结果
         */
        static CliResult error(String message) {
            return new CliResult() {
                @Override
                public int getExitCode() { return 1; }
                @Override
                public String getMessage() { return message; }
                @Override
                public Object getData() { return null; }
                @Override
                public boolean isSuccess() { return false; }
            };
        }

        /**
         * 创建失败结果（带退出码）
         */
        static CliResult error(int exitCode, String message) {
            return new CliResult() {
                @Override
                public int getExitCode() { return exitCode; }
                @Override
                public String getMessage() { return message; }
                @Override
                public Object getData() { return null; }
                @Override
                public boolean isSuccess() { return false; }
            };
        }
    }
}
