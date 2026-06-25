$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$supabaseEnv = Join-Path $root ".env.supabase"

if (-not (Test-Path $supabaseEnv)) {
    throw "Arquivo .env.supabase nao encontrado em $supabaseEnv"
}

Get-Content $supabaseEnv | ForEach-Object {
    if ($_ -and $_ -notmatch "^\s*#") {
        $name, $value = $_ -split "=", 2
        Set-Item -Path "Env:$name" -Value $value
    }
}

$env:PORT = "3001"
$env:DB_HOST = "db.xjrepdfpwokwsgfztwku.supabase.co"
$env:DB_PORT = "5432"
$env:DB_NAME = "postgres"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = $env:SPRING_DATASOURCE_PASSWORD
$env:DB_SSL = "true"
$env:RABBITMQ_ENABLED = "false"
$env:LOCAL_AUTH_ENABLED = "true"
$env:JWT_SECRET = "local-supabase-test-secret-change-me"
$env:LOCAL_AUTH_PROFILE_ID = "1"

node calendar-server.js
