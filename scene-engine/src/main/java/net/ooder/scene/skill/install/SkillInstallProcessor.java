package net.ooder.scene.skill.install;

import net.ooder.scene.skill.prompt.model.PromptDocument;

import java.util.List;
import java.util.Map;

/**
 * 技能安装处理器接口
 *
 * <p>负责技能安装时的配置构建和入库流程：</p>
 * <ul>
 *   <li>解析 skill.yaml 和相关配置文件</li>
 *   <li>自动构建技能运行时配置</li>
 *   <li>将配置文档入库到知识库（用于 RAG 检索）</li>
 *   <li>记录配置变更历史</li>
 *   <li>支持配置回滚</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface SkillInstallProcessor {

    /**
     * 执行技能安装
     *
     * @param request 安装请求
     * @return 安装结果
     */
    InstallResult install(InstallRequest request);

    /**
     * 构建并存储配置
     *
     * @param skillId 技能ID
     * @param config 技能配置
     */
    void buildAndStoreConfig(String skillId, SkillRuntimeConfig config);

    /**
     * 索引提示语文档
     *
     * @param skillId 技能ID
     * @param promptFiles 提示词文件列表
     */
    void indexPromptDocuments(String skillId, List<String> promptFiles);

    /**
     * 注册能力
     *
     * @param skillId 技能ID
     * @param capabilities 能力定义列表
     */
    void registerCapabilities(String skillId, List<CapabilityDef> capabilities);

    /**
     * 获取配置历史
     *
     * @param skillId 技能ID
     * @return 配置历史
     */
    ConfigHistory getConfigHistory(String skillId);

    /**
     * 回滚配置
     *
     * @param skillId 技能ID
     * @param version 目标版本号
     * @return 是否成功
     */
    boolean rollbackConfig(String skillId, int version);

    /**
     * 卸载技能
     *
     * @param skillId 技能ID
     * @return 卸载结果
     */
    UninstallResult uninstall(String skillId);

    /**
     * 检查技能是否已安装
     *
     * @param skillId 技能ID
     * @return 是否已安装
     */
    boolean isInstalled(String skillId);

    /**
     * 获取技能安装信息
     *
     * @param skillId 技能ID
     * @return 安装信息
     */
    InstallInfo getInstallInfo(String skillId);
}
