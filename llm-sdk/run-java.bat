@echo off
chcp 65001
echo ==========================================
echo    百度文心 LLM 测试
echo ==========================================
echo.

cd /d "%~dp0"

set CLASSPATH=".;target\classes;%USERPROFILE%\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar;%USERPROFILE%\.m2\repository\com\alibaba\fastjson\1.2.83\fastjson-1.2.83.jar"

echo 运行测试...
java -cp "%CLASSPATH%" net.ooder.sdk.drivers.llm.SimpleTest

echo.
echo 测试完成
echo ==========================================
pause
