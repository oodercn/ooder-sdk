$mavenRepo = "D:\maven\.m2\repository\net\ooder\skill-hotplug-starter\3.0.2"

if (Test-Path $mavenRepo) {
    Write-Host "Cleaning Maven repository cache for skill-hotplug-starter 3.0.2..."
    Remove-Item "$mavenRepo\*.lastUpdated" -Force -ErrorAction SilentlyContinue
    Remove-Item "$mavenRepo\*.tmp" -Force -ErrorAction SilentlyContinue
    Write-Host "Cache cleaned successfully"
} else {
    Write-Host "Maven repository directory not found: $mavenRepo"
}
