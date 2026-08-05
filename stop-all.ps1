# Stops only the processes listening on the FundWise ports.
# Run from this folder: .\stop-all.ps1

$ports = @("8761", "8080", "8081", "8082", "8083", "8084", "8085")
$processIds = @()

# netstat works consistently even when Get-NetTCPConnection is restricted.
foreach ($line in (netstat -ano | Select-String "LISTENING")) {
    $parts = $line.ToString().Trim() -split "\s+"
    $localPort = ($parts[1] -split ":")[-1]
    if ($ports -contains $localPort) {
        $processIds += [int]$parts[-1]
    }
}

foreach ($processId in ($processIds | Select-Object -Unique)) {
    Write-Host "Stopping FundWise process $processId..."
    Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
}

Write-Host "FundWise services stopped."
