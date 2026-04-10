@echo off
echo Copying skill-hotplug-starter 3.0.2 to local Maven repository...
echo.

set SOURCE=e:\github\ooder-sdk\ooder-common\skill-hotplug-starter\target
set TARGET=D:\maven\.m2\repository\net\ooder\skill-hotplug-starter\3.0.2

if not exist "%TARGET%" mkdir "%TARGET%"

copy "%SOURCE%\skill-hotplug-starter-3.0.2.jar" "%TARGET%\" /Y
copy "%SOURCE%\skill-hotplug-starter-3.0.2.pom" "%TARGET%\" /Y
copy "%SOURCE%\skill-hotplug-starter-3.0.2-sources.jar" "%TARGET%\" /Y
copy "%SOURCE%\skill-hotplug-starter-3.0.2-javadoc.jar" "%TARGET%\" /Y

echo.
echo Done! Files copied to: %TARGET%
echo.
pause
