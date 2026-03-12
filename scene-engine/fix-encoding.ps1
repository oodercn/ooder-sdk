# 修复 Java 文件中的乱码字符
# 将常见的乱码字符替换为正确的中文字符

$replacements = @{
    "构建�?" = "构建器"
    "策略�?" = "策略。"
    "解�?" = "解释"
    "简洁模�?" = "简洁模板"
    "快速回�?" = "快速回答"
    "结构化模�?" = "结构化模板"
    "引用标�?" = "引用标注"
    "初始化�?" = "初始化器"
    "配�?" = "配置"
    "角�?" = "角色"
    "激�?" = "激活"
    "�?" = ""
    "SDK �?MemberRole" = "SDK 的 MemberRole"
    "�/li>" = "</li>"
    "�/b>" = "</b>"
    "�/p>" = "</p>"
    "请�?" = "请"
    "已提�?" = "已提交"
    "已归�?" = "已归档"
    "邮件汇�?" = "邮件汇总"
    "代码提交汇�?" = "代码提交汇总"
    "草稿状态枚�?" = "草稿状态枚举"
}

Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | ForEach-Object {
    $file = $_.FullName
    $content = Get-Content $file -Raw -Encoding UTF8
    $modified = $false

    foreach ($key in $replacements.Keys) {
        if ($content -contains $key) {
            $content = $content -replace [regex]::Escape($key), $replacements[$key]
            $modified = $true
        }
    }

    if ($modified) {
        Write-Host "Fixed: $file"
        Set-Content $file $content -Encoding UTF8 -NoNewline
    }
}

Write-Host "Done!"
