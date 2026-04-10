$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:M2_HOME = "D:\maven\apache-maven-3.9.10"
$env:MAVEN_HOME = "D:\maven\apache-maven-3.9.10"

Write-Host "JAVA_HOME: $env:JAVA_HOME"
Write-Host "M2_HOME: $env:M2_HOME"
Write-Host ""

Set-Location "e:\github\ooder-sdk\ooder-common\skill-hotplug-starter"

$mavenCmd = "D:\maven\apache-maven-3.9.10\bin\mvn.cmd"
Write-Host "Running: $mavenCmd clean compile -Dmaven.test.skip=true"
Write-Host ""

& $mavenCmd clean compile -Dmaven.test.skip=true
