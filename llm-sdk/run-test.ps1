Write-Host "==========================================="
Write-Host "    百度文心 LLM Driver 测试"
Write-Host "==========================================="
Write-Host

# 切换到项目目录
Set-Location "$PSScriptRoot"

# 编译项目
Write-Host "[1/3] 编译项目..."
try {
    mvn clean compile test-compile -q
    Write-Host "编译成功!"
} catch {
    Write-Host "编译失败: $($_.Exception.Message)" -ForegroundColor Red
    Read-Host "按 Enter 键退出..."
    exit 1
}

# 运行测试
Write-Host "[2/3] 运行测试..."
try {
    # 使用参数数组来避免解析问题
    $cmd = "mvn"
    $args = @(
        "exec:java",
        "-Dexec.mainClass=net.ooder.sdk.drivers.llm.BaiduWenxinTest",
        "-Dexec.classpathScope=test"
    )
    & $cmd @args
} catch {
    Write-Host "测试失败: $($_.Exception.Message)" -ForegroundColor Red
    Read-Host "按 Enter 键退出..."
    exit 1
}

Write-Host
Write-Host "[3/3] 测试完成"
Write-Host "==========================================="
Read-Host "按 Enter 键退出..."
