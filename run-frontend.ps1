$ErrorActionPreference = "Stop"

$frontendPath = Join-Path $PSScriptRoot "frontend"
$port = 3000

Write-Host "Frontend: http://localhost:$port"
Write-Host "API de confirmacao: http://localhost:3001"

node frontend-server.js
