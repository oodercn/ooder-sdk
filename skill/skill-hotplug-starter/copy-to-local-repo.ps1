$sourceDir = "e:\github\ooder-sdk\ooder-common\skill-hotplug-starter\target"
$targetDir = "D:\maven\.m2\repository\net\ooder\skill-hotplug-starter\3.0.2"

# Create target directory if not exists
if (!(Test-Path $targetDir)) {
    New-Item -ItemType Directory -Path $targetDir -Force
}

# Copy files
$files = @(
    "skill-hotplug-starter-3.0.2.jar",
    "skill-hotplug-starter-3.0.2.pom",
    "skill-hotplug-starter-3.0.2-sources.jar",
    "skill-hotplug-starter-3.0.2-javadoc.jar"
)

foreach ($file in $files) {
    $sourceFile = Join-Path $sourceDir $file
    if (Test-Path $sourceFile) {
        Copy-Item $sourceFile $targetDir -Force
        Write-Host "Copied: $file"
    } else {
        Write-Host "Not found: $file"
    }
}

Write-Host ""
Write-Host "Files copied to: $targetDir"
