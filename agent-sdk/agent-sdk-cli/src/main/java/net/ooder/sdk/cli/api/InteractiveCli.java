package net.ooder.sdk.cli.api;

import java.util.List;

/**
 * 交互式CLI接口
 *
 * <p>支持交互式命令行模式</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface InteractiveCli {

    /**
     * 启动交互式模式
     */
    void start();

    /**
     * 停止交互式模式
     */
    void stop();

    /**
     * 读取用户输入
     *
     * @param prompt 提示符
     * @return 用户输入
     */
    String readLine(String prompt);

    /**
     * 读取密码（隐藏输入）
     *
     * @param prompt 提示符
     * @return 密码
     */
    String readPassword(String prompt);

    /**
     * 打印消息
     *
     * @param message 消息
     */
    void print(String message);

    /**
     * 打印消息（带换行）
     *
     * @param message 消息
     */
    void println(String message);

    /**
     * 设置自动补全器
     *
     * @param completer 补全器
     */
    void setCompleter(Completer completer);

    /**
     * 设置历史记录管理器
     *
     * @param historyManager 历史记录管理器
     */
    void setHistoryManager(HistoryManager historyManager);

    /**
     * 是否正在运行
     *
     * @return 是否运行中
     */
    boolean isRunning();

    /**
     * 自动补全器接口
     */
    interface Completer {
        /**
         * 获取补全建议
         *
         * @param buffer 当前输入
         * @param cursor 光标位置
         * @return 建议列表
         */
        List<String> complete(String buffer, int cursor);
    }

    /**
     * 历史记录管理器接口
     */
    interface HistoryManager {
        /**
         * 添加历史记录
         *
         * @param command 命令
         */
        void add(String command);

        /**
         * 获取历史记录
         *
         * @return 历史记录列表
         */
        List<String> getHistory();

        /**
         * 清空历史记录
         */
        void clear();

        /**
         * 保存历史记录
         */
        void save();

        /**
         * 加载历史记录
         */
        void load();
    }
}
