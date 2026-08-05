# Starts FundWise in dependency order and waits until every port is ready.
# Run from this folder: .\run-all.ps1

$servicePorts = [ordered]@{
    "discovery-server"    = 8761
    "investor-service"    = 8081
    "scheme-service"      = 8082
    "folio-service"       = 8083
    "transaction-service" = 8084
    "statement-service"   = 8085
    "api-gateway"         = 8080
}

$logDirectory = Join-Path $PSScriptRoot "logs"
New-Item -ItemType Directory -Path $logDirectory -Force | Out-Null

function Test-FundWisePort([int]$Port) {
    $client = [System.Net.Sockets.TcpClient]::new()
    try {
        $task = $client.ConnectAsync("127.0.0.1", $Port)
        return $task.Wait(350) -and $client.Connected
    } catch {
        return $false
    } finally {
        $client.Dispose()
    }
}

function Start-FundWiseService([string]$Service, [int]$Port) {
    if (Test-FundWisePort $Port) {
        Write-Host "$Service is already running on port $Port." -ForegroundColor Yellow
        return
    }

    Write-Host "Starting $Service..."
    $outputLog = Join-Path $logDirectory "$Service.out.log"
    $errorLog = Join-Path $logDirectory "$Service.err.log"
    Start-Process -FilePath "mvn" `
        -ArgumentList "spring-boot:run" `
        -WorkingDirectory (Join-Path $PSScriptRoot $Service) `
        -RedirectStandardOutput $outputLog `
        -RedirectStandardError $errorLog `
        -WindowStyle Hidden
}

function Wait-FundWisePort([string]$Service, [int]$Port, [int]$TimeoutSeconds = 120) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if (Test-FundWisePort $Port) {
            Write-Host "  $Service is ready on port $Port." -ForegroundColor Green
            return $true
        }
        Start-Sleep -Milliseconds 750
    }
    Write-Host "  $Service did not become ready. Check .\logs\$Service.out.log" -ForegroundColor Red
    return $false
}

# Eureka must be ready before clients register.
Start-FundWiseService "discovery-server" $servicePorts["discovery-server"]
if (-not (Wait-FundWisePort "discovery-server" $servicePorts["discovery-server"])) { exit 1 }

# Business services can start together after discovery is available.
$businessServices = @("investor-service", "scheme-service", "folio-service", "transaction-service", "statement-service")
foreach ($service in $businessServices) {
    Start-FundWiseService $service $servicePorts[$service]
}

$businessReady = $true
foreach ($service in $businessServices) {
    if (-not (Wait-FundWisePort $service $servicePorts[$service])) { $businessReady = $false }
}
if (-not $businessReady) { exit 1 }

# Start the UI gateway only after every routed service is available.
Start-FundWiseService "api-gateway" $servicePorts["api-gateway"]
if (-not (Wait-FundWisePort "api-gateway" $servicePorts["api-gateway"])) { exit 1 }

Write-Host ""
Write-Host "FundWise is ready: http://localhost:8080" -ForegroundColor Green
Write-Host "Eureka registry:   http://localhost:8761" -ForegroundColor Green
