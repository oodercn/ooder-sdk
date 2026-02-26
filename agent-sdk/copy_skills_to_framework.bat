@echo off
chcp 65001 >nul
echo ==========================================
echo Skills Framework 文件复制脚本
echo ==========================================
echo.

set SOURCE_DIR=e:\github\ooder-sdk\agent-sdk\src\main\java\net\ooder\sdk
set TARGET_DIR=e:\github\ooder-sdk\skills-framework\src\main\java\net\ooder\skills

echo 源目录: %SOURCE_DIR%
echo 目标目录: %TARGET_DIR%
echo.

REM 创建目标目录结构
echo [1/4] 创建目标目录结构...
mkdir "%TARGET_DIR%\api" 2>nul
mkdir "%TARGET_DIR%\api\impl" 2>nul
mkdir "%TARGET_DIR%\core" 2>nul
mkdir "%TARGET_DIR%\core\impl" 2>nul
mkdir "%TARGET_DIR%\core\discovery" 2>nul
mkdir "%TARGET_DIR%\core\installer" 2>nul
mkdir "%TARGET_DIR%\core\lifecycle" 2>nul
mkdir "%TARGET_DIR%\core\lifecycle\impl" 2>nul
mkdir "%TARGET_DIR%\core\capability" 2>nul
mkdir "%TARGET_DIR%\core\dependency" 2>nul
mkdir "%TARGET_DIR%\core\collaboration" 2>nul
mkdir "%TARGET_DIR%\core\loader" 2>nul
mkdir "%TARGET_DIR%\md" 2>nul
mkdir "%TARGET_DIR%\md\impl" 2>nul
mkdir "%TARGET_DIR%\runtime" 2>nul
echo 目录结构创建完成
echo.

REM 复制 api/skill/ 文件
echo [2/4] 复制 api/skill/ 文件...
xcopy /E /I /Y "%SOURCE_DIR%\api\skill\*" "%TARGET_DIR%\api\"
if %errorlevel% neq 0 (
    echo 复制 api/skill/ 失败
    pause
    exit /b 1
)
echo api/skill/ 复制完成
echo.

REM 复制 core/skill/ 文件
echo [3/4] 复制 core/skill/ 文件...
xcopy /E /I /Y "%SOURCE_DIR%\core\skill\*" "%TARGET_DIR%\core\"
if %errorlevel% neq 0 (
    echo 复制 core/skill/ 失败
    pause
    exit /b 1
)
echo core/skill/ 复制完成
echo.

REM 复制 skill/ 文件
echo [4/4] 复制 skill/ 文件...
xcopy /E /I /Y "%SOURCE_DIR%\skill\*" "%TARGET_DIR%\md\"
if %errorlevel% neq 0 (
    echo 复制 skill/ 失败
    pause
    exit /b 1
)
echo skill/ 复制完成
echo.

echo ==========================================
echo 文件复制完成！
echo ==========================================
echo.
echo 接下来请执行：
echo 1. 在 IDEA 中打开 skills-framework 工程
echo 2. 修改所有文件的包名：
echo    - net.ooder.sdk.api.skill.* ^-^> net.ooder.skills.api.*
echo    - net.ooder.sdk.core.skill.* ^-^> net.ooder.skills.core.*
echo    - net.ooder.sdk.skill.* ^-^> net.ooder.skills.md.*
echo 3. 编译验证
echo.
pause
