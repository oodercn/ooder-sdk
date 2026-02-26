Write-Host "==========================================="
Write-Host "    百度文心 LLM Driver 测试"
Write-Host "==========================================="
Write-Host

# 编译项目
Write-Host "[1/3] 编译项目..."
mvn clean compile test-compile -q

# 运行测试
Write-Host "[2/3] 运行测试..."
mvn exec:java -Dexec.mainClass=net.ooder.sdk.drivers.llm.BaiduWenxinTest -Dexec.classpathScope=test

Write-Host
Write-Host "[3/3] 测试完成"
Write-Host "==========================================="
