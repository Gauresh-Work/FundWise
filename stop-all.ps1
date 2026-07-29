# Stops only the processes listening on the FundWise ports.
# Run from this folder: .\stop-all.ps1

$ports = 8761, 8080, 8081, 8082, 8083, 8084, 8085

foreach ($port in $ports) {
    $processIds = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue |
        Select-Object -ExpandProperty OwningProcess -Unique

    foreach ($processId in $processIds) {
        Write-Host "Stopping process $processId on port $port..."
        Stop-Process -Id $processId -Force
    }
}

Write-Host "FundWise services stopped."
