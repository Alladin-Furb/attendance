$ErrorActionPreference = "Stop"

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
$env:MAVEN_HOME = Join-Path $env:USERPROFILE "tools\maven\apache-maven-3.9.16"
$env:Path = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:Path"

Get-Content .env.supabase | ForEach-Object {
    if ($_ -and $_ -notmatch "^\s*#") {
        $name, $value = $_ -split "=", 2
        Set-Item -Path "Env:$name" -Value $value
    }
}

$env:SPRING_RABBITMQ_LISTENER_SIMPLE_AUTO_STARTUP = "false"

& "$env:MAVEN_HOME\bin\mvn.cmd" spring-boot:run
