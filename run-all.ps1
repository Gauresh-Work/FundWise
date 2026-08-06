# Starts FundWise in dependency order and waits until every port is ready.
# Run from this folder: .\run-all.ps1

$servicePorts = [ordered]@{
    "discovery-server"    = 8761
    "investor-service"    = 8081
    "scheme-service"      = 8082
    "folio-service"       = 8083
    "transaction-service" = 8084
    "statement-service"   = 8085
    "auth-service"        = 8086
    "api-gateway"         = 8080
}

$logDirectory = Join-Path $PSScriptRoot "logs"
New-Item -ItemType Directory -Path $logDirectory -Force | Out-Null
$mavenExecutable = (Get-Command "mvn.cmd" -ErrorAction Stop).Source

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
    Start-Process -FilePath $mavenExecutable `
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

function Test-FundWiseAuthRoute {
    try {
        $response = Invoke-WebRequest `
            -Uri "http://127.0.0.1:8080/auth/login" `
            -Method Post `
            -ContentType "application/json" `
            -Body "{}" `
            -UseBasicParsing `
            -TimeoutSec 3
        $status = [int]$response.StatusCode
    } catch {
        if ($null -eq $_.Exception.Response) { return $false }
        $status = [int]$_.Exception.Response.StatusCode
    }
    return $status -ne 502 -and $status -ne 503
}

function Wait-FundWiseAuthRoute([int]$TimeoutSeconds = 60) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if (Test-FundWiseAuthRoute) {
            Write-Host "  Authentication route is ready." -ForegroundColor Green
            return $true
        }
        Start-Sleep -Milliseconds 750
    }
    Write-Host "  Authentication route did not become ready. Check .\logs\api-gateway.out.log" -ForegroundColor Red
    return $false
}

# Eureka must be ready before clients register.
Start-FundWiseService "discovery-server" $servicePorts["discovery-server"]
if (-not (Wait-FundWisePort "discovery-server" $servicePorts["discovery-server"])) { exit 1 }

# Business services can start together after discovery is available.
$businessServices = @("investor-service", "scheme-service", "folio-service", "transaction-service", "statement-service", "auth-service")
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
if (-not (Wait-FundWiseAuthRoute)) { exit 1 }

Write-Host ""
Write-Host "FundWise is ready: http://localhost:8080" -ForegroundColor Green
Write-Host "Eureka registry:   http://localhost:8761" -ForegroundColor Green
