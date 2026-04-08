package net.ooder.skill.hotplug.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Skill 发现过滤器
 * 用于过滤非技能目录和无效的技能包
 */
public class SkillDiscoveryFilter {

    private static final Logger logger = LoggerFactory.getLogger(SkillDiscoveryFilter.class);

    // 默认排除的目录模式
    private static final List<String> DEFAULT_EXCLUDE_PATTERNS = Arrays.asList(
        "docs/", "doc/", "documentation/",
        "tools/", "tool/", "scripts/", "script/",
        ".github/", ".git/", ".idea/", ".vscode/",
        "test/", "tests/", "example/", "examples/",
        "demo/", "demos/", "sample/", "samples/",
        "backup/", "bak/", "old/", "temp/", "tmp/",
        "out/", "target/", "build/", "dist/",
        "node_modules/", "vendor/"
    );

    // 必需的文件
    private static final String REQUIRED_FILE = "skill.yaml";

    /**
     * 检查路径是否为有效的技能目录
     *
     * @param path 目录路径
     * @return 是否有效
     */
    public static boolean isValidSkillDirectory(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }

        String normalizedPath = path.toLowerCase();

        // 1. 检查是否匹配排除模式
        for (String pattern : DEFAULT_EXCLUDE_PATTERNS) {
            if (normalizedPath.startsWith(pattern) ||
                normalizedPath.contains("/" + pattern) ||
                normalizedPath.contains("\\" + pattern)) {
                logger.debug("Path excluded by pattern '{}': {}", pattern, path);
                return false;
            }
        }

        // 2. 检查是否以 -skill 结尾（技能目录命名约定）
        // 可选：如果需要严格的命名检查，可以取消下面的注释
        // if (!normalizedPath.contains("-skill") && !normalizedPath.contains("skill-")) {
        //     logger.debug("Path does not match skill naming convention: {}", path);
        //     return false;
        // }

        return true;
    }

    /**
     * 检查文件是否为有效的 skill.yaml
     *
     * @param content 文件内容
     * @return 是否有效
     */
    public static boolean isValidSkillYaml(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        // 检查必需字段
        return content.contains("metadata:") &&
               (content.contains("id:") || content.contains("skillId:"));
    }

    /**
     * 获取默认排除模式列表
     *
     * @return 排除模式列表
     */
    public static List<String> getDefaultExcludePatterns() {
        return DEFAULT_EXCLUDE_PATTERNS;
    }

    /**
     * 创建自定义过滤器
     *
     * @param additionalExcludes 额外的排除模式
     * @return 过滤结果
     */
    public static boolean filter(String path, List<String> additionalExcludes) {
        // 先检查默认排除模式
        if (!isValidSkillDirectory(path)) {
            return false;
        }

        // 再检查额外排除模式
        if (additionalExcludes != null) {
            String normalizedPath = path.toLowerCase();
            for (String pattern : additionalExcludes) {
                if (normalizedPath.contains(pattern.toLowerCase())) {
                    logger.debug("Path excluded by custom pattern '{}': {}", pattern, path);
                    return false;
                }
            }
        }

        return true;
    }
}
