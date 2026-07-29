# Starts all FundWise applications in the background.
# Run from this folder: .\run-all.ps1

$services = @(
    "discovery-server",
    "investor-service",
    "scheme-service",
    "folio-service",
    "transaction-service",
    "statement-service",
    "api-gateway"
)

$logDirectory = Join-Path $PSScriptRoot "logs"
New-Item -ItemType Directory -Path $logDirectory -Force | Out-Null

foreach ($service in $services) {
    Write-Host "Starting $service..."
    $outputLog = Join-Path $logDirectory "$service.out.log"
    $errorLog = Join-Path $logDirectory "$service.err.log"
    Start-Process -FilePath "mvn" `
        -ArgumentList "'-Djava.version=17' spring-boot:run" `
        -WorkingDirectory (Join-Path $PSScriptRoot $service) `
        -RedirectStandardOutput $outputLog `
        -RedirectStandardError $errorLog `
        -WindowStyle Hidden
}

Write-Host "All applications are starting. Logs are in .\logs."
Write-Host "Example live log: Get-Content .\logs\investor-service.out.log -Wait"
