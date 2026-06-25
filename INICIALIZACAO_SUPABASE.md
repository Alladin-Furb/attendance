# Inicializacao do Projeto com Supabase

Este guia sobe a aplicacao usando o Supabase como banco de dados PostgreSQL.

## Pre-requisitos

Antes de iniciar, confirme que existem estes itens:

- Java 17 instalado em:
  `C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot`
- Maven extraido em:
  `C:\Users\gabri\tools\maven\apache-maven-3.9.16`
- Node.js instalado.
- Arquivo `.env.supabase` criado na raiz do projeto.

O arquivo `.env.supabase` deve ter este formato:

```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://db.xjrepdfpwokwsgfztwku.supabase.co:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=SUA_SENHA_REAL_DO_BANCO
SPRING_DATASOURCE_MAX_POOL_SIZE=3
SPRING_DATASOURCE_MIN_IDLE=1
```

Nao commite esse arquivo, porque ele contem a senha real do banco.

## Terminal 1: Spring Boot

Na raiz do projeto:

```powershell
cd "C:\Users\gabri\Desktop\Faculdade\6 Semestre\Sistemas Distribuidos\Presença-service\attendance"
.\run-spring-supabase.ps1
```

Esse comando sobe o Spring Boot em:

```text
http://localhost:8082
```

Observacao: o RabbitMQ fica desativado temporariamente nesse script para permitir testar o Supabase sem precisar de broker local.

## Terminal 2: Backend da Interface

Em outro terminal:

```powershell
cd "C:\Users\gabri\Desktop\Faculdade\6 Semestre\Sistemas Distribuidos\Presença-service\attendance\backend-calendar"
.\run-supabase.ps1
```

Esse comando sobe o backend da interface em:

```text
http://localhost:3001
```

Ele tambem usa o Supabase como banco.

Para teste local, o script habilita login simples. Na tela, voce pode entrar com qualquer email e senha.

## Terminal 3: Frontend

Em outro terminal, na raiz do projeto:

```powershell
cd "C:\Users\gabri\Desktop\Faculdade\6 Semestre\Sistemas Distribuidos\Presença-service\attendance"
node frontend-server.js
```

Ou:

```powershell
.\run-frontend.ps1
```

Esse comando sobe a interface em:

```text
http://localhost:3000/index.html
```

Tela de monitoramento:

```text
http://localhost:3000/monitoramento.html
```

## Teste pela Interface

1. Abra:
   `http://localhost:3000/index.html`
2. Faca login com qualquer email/senha, por exemplo:
   `teste@local.com` / `123456`
3. Selecione a localizacao no mapa.
4. Confirme a presenca.
5. No Supabase, confira a tabela:
   `"Confirmacao_Presenca_Diaria"`

## Teste rapido via PowerShell

Listar alunos no Spring:

```powershell
(Invoke-WebRequest -UseBasicParsing http://localhost:8082/api/v1/alunos).Content
```

Health check do backend da interface:

```powershell
(Invoke-WebRequest -UseBasicParsing http://localhost:3001/api/health).Content
```

## Portas usadas

```text
8082 - Spring Boot / presenca-service
3001 - backend-calendar
3000 - frontend estatico
```

## Problemas comuns

### `mvn nao e reconhecido`

Use o script `run-spring-supabase.ps1`, porque ele configura o Maven local automaticamente.

### `JAVA_HOME environment variable is not defined correctly`

Confirme se o Java esta neste caminho:

```text
C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
```

Se estiver em outro caminho, atualize `run-spring-supabase.ps1`.

### Erro de RabbitMQ em `localhost:5672`

Para teste com Supabase, use:

```powershell
.\run-spring-supabase.ps1
```

Esse script desativa os listeners do RabbitMQ temporariamente.

### Python nao encontrado

Nao precisa mais usar Python. Use:

```powershell
node frontend-server.js
```

### Porta ocupada

Confira qual processo esta usando a porta:

```powershell
Get-NetTCPConnection -LocalPort 3000
Get-NetTCPConnection -LocalPort 3001
Get-NetTCPConnection -LocalPort 8082
```

## Quando for usar RabbitMQ na nuvem

Quando voce tiver uma URL AMQP real, configure algo como:

```powershell
$env:RABBITMQ_ENABLED="true"
$env:RABBITMQ_URL="amqps://usuario:SENHA@host/vhost"
```

Nao use URL MQTT para este projeto sem adaptar o codigo, porque o projeto atual usa RabbitMQ via AMQP.
