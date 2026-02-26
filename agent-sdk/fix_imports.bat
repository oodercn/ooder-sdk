@echo off
chcp 65001 >nul
echo 修复 skills-framework 中的 import 语句...
echo.

set BASE_DIR=e:\github\ooder-sdk\agent-sdk\skills-framework\src\main\java\net\ooder\skills

REM 修复 net.ooder.common.enums -> net.ooder.skills.common.enums
echo [1/4] 修复 common.enums import...
for /r %BASE_DIR% %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'net\.ooder\.common\.enums', 'net.ooder.skills.common.enums' | Set-Content '%%f'"
)

REM 修复 net.ooder.infra.exception -> net.ooder.skills.exception
echo [2/4] 修复 infra.exception import...
for /r %BASE_DIR% %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'net\.ooder\.infra\.exception', 'net.ooder.skills.exception' | Set-Content '%%f'"
)

REM 修复 net.ooder.api.scene.store -> net.ooder.skills.store
echo [3/4] 修复 api.scene.store import...
for /r %BASE_DIR% %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'net\.ooder\.api\.scene\.store', 'net.ooder.skills.store' | Set-Content '%%f'"
)

REM 修复 package net.ooder.sdk.api.skill -> net.ooder.skills.api
echo [4/4] 修复 package 声明...
for /r %BASE_DIR% %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'package net\.ooder\.sdk\.api\.skill;', 'package net.ooder.skills.api;' | Set-Content '%%f'"
    powershell -Command "(Get-Content '%%f') -replace 'package net\.ooder\.sdk\.core\.skill;', 'package net.ooder.skills.core;' | Set-Content '%%f'"
    powershell -Command "(Get-Content '%%f') -replace 'package net\.ooder\.sdk\.skill;', 'package net.ooder.skills.md;' | Set-Content '%%f'"
)

echo.
echo 修复完成！
pause
