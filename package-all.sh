#!/bin/bash

# SDK 全工程简化打包脚本
# 使用独立 settings.xml，跳过测试和文档

echo "🚀 开始 SDK 全工程打包..."
echo "📦 使用独立 settings.xml"
echo "📝 跳过测试和文档，保留源码"
echo ""

# 记录开始时间
start_time=$(date +%s)

# 模块列表
modules=(
    "agent-sdk"
    "ooder-annotation"
    "ooder-common"
    "scene-engine"
    "llm-sdk"
)

# 子模块列表
submodules=(
    "skills/skill-ai"
    "skills/skill-business"
    "skills/skill-cache"
    "skills/skill-db"
    "skills/skill-index-web"
    "skills/skill-llm"
    "skills/skill-msg-web"
    "skills/skill-org"
    "skills/skill-queue"
    "skills/skill-storage"
    "skills/skill-vfs"
    "skills/skill-vector"
)

# 打包函数
package_module() {
    local module=$1
    local start=$(date +%s)
    
    echo "📦 正在打包: $module"
    
    if mvn clean install -s settings.xml -pl $module -am > /tmp/mvn-$module.log 2>&1; then
        local end=$(date +%s)
        local duration=$((end - start))
        echo "✅ $module 打包成功 (${duration}s)"
        return 0
    else
        echo "❌ $module 打包失败"
        echo "📋 错误日志:"
        tail -20 /tmp/mvn-$module.log
        return 1
    fi
}

# 打包主模块
echo "📚 打包主模块..."
for module in "${modules[@]}"; do
    if ! package_module "$module"; then
        echo "❌ 打包中断"
        exit 1
    fi
done

# 打包子模块
echo ""
echo "📚 打包子模块..."
for submodule in "${submodules[@]}"; do
    if ! package_module "$submodule"; then
        echo "❌ 打包中断"
        exit 1
    fi
done

# 计算总耗时
end_time=$(date +%s)
total_duration=$((end_time - start_time))

echo ""
echo "🎉 所有模块打包完成！"
echo "⏱️  总耗时: ${total_duration}秒"
echo ""
echo "📁 本地仓库位置: D:/maven/.m2/repository"
echo ""
echo "🔍 验证安装..."
find D:/maven/.m2/repository -name "*.jar" -path "*/net/ooder/*" -newer /tmp/mvn-start.marker | sort

# 清理临时文件
rm -f /tmp/mvn-*.log
rm -f /tmp/mvn-start.marker

echo ""
echo "✨ 打包完成！"
