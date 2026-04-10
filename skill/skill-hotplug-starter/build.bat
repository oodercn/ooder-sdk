@echo off
set JAVA_HOME=E:\Program Files\jds21
set M2_HOME=D:\maven\apache-maven-3.9.10
set MAVEN_HOME=D:\maven\apache-maven-3.9.10
set PATH=%JAVA_HOME%\bin;%M2_HOME%\bin;%PATH%

echo JAVA_HOME: %JAVA_HOME%
echo M2_HOME: %M2_HOME%
echo.
echo Running Maven clean compile...
echo.

call "%M2_HOME%\bin\mvn.cmd" clean compile -Dmaven.test.skip=true
