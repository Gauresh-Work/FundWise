# PowerShell's local execution policy may block this file. The batch launcher has
# no policy dependency and avoids the PATH/Path conflict in Windows PowerShell 5.1.
& cmd.exe /d /c (Join-Path $PSScriptRoot "run-all.cmd")
exit $LASTEXITCODE
