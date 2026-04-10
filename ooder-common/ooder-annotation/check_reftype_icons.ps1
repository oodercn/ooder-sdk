$icons = @(
    "ri-forbid-line",
    "ri-link",
    "ri-arrow-right-line",
    "ri-arrow-left-line",
    "ri-refresh-line",
    "ri-swap-line",
    "ri-search-line"
)

$availableIcons = Get-Content -Path "available_icons.txt"

foreach ($icon in $icons) {
    if ($availableIcons -contains $icon) {
        Write-Host "$icon exists"
    } else {
        Write-Host "$icon NOT found"
    }
}
