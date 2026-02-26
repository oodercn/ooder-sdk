@echo off
chcp 65001
echo ==========================================
echo    百度文心 LLM Driver 测试
echo ==========================================
echo.

cd /d "%~dp0"

echo [1/3] 编译项目...
mvn clean compile test-compile -q

if errorlevel 1 (
    echo 编译失败！
    pause
    exit /b 1
)

echo [2/3] 运行测试...
mvn exec:java -Dexec.mainClass="net.ooder.sdk.drivers.llm.BaiduWenxinTest" -Dexec.classpathScope=test

echo.
echo [3/3] 测试完成
echo ==========================================
pause
