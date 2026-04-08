$mvnPath = (Get-Command mvn -ErrorAction SilentlyContinue).Source
if ($mvnPath) {
    Write-Host "Maven found at: $mvnPath"
    $mavenHome = Split-Path (Split-Path $mvnPath -Parent) -Parent
    Write-Host "Maven home: $mavenHome"
} else {
    Write-Host "Maven not found in PATH"
    
    $commonPaths = @(
        "C:\Program Files\Maven\apache-maven-*",
        "C:\Program Files (x86)\Maven\apache-maven-*",
        "C:\apache-maven-*",
        "D:\apache-maven-*",
        "D:\maven\apache-maven-*"
    )
    
    foreach ($pathPattern in $commonPaths) {
        $paths = Get-Item $pathPattern -ErrorAction SilentlyContinue
        if ($paths) {
            $mavenHome = $paths[0].FullName
            Write-Host "Maven found at: $mavenHome"
            break
        }
    }
}

if (-not $mavenHome) {
    Write-Host "Maven home not found. Please install Maven or set M2_HOME environment variable."
    exit 1
}
