# Configurando o Supabase

Este projeto ja esta preparado para usar PostgreSQL em producao. O Supabase entra como o banco PostgreSQL gerenciado.

## 1. Criar o projeto no Supabase

1. Acesse o painel do Supabase.
2. Crie um novo projeto.
3. Guarde a senha do banco com cuidado.
4. Espere o projeto ficar ativo.

## 2. Pegar a string de conexao

No projeto do Supabase, abra **Connect** e copie uma conexao PostgreSQL.

Para testar localmente, comece com a conexao direta. Se o ambiente nao conseguir acessar por IPv6, use **Session pooler**.

Formato normal:

```text
postgresql://postgres:SENHA@HOST:5432/postgres?sslmode=require
```

Para o Spring Boot, use o mesmo host, mas com prefixo JDBC:

```text
jdbc:postgresql://HOST:5432/postgres?sslmode=require
```

## 3. Configurar o Spring Boot

No PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://db.xjrepdfpwokwsgfztwku.supabase.co:5432/postgres?sslmode=require"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="SUA_SENHA"
$env:SPRING_DATASOURCE_MAX_POOL_SIZE="3"
$env:SPRING_DATASOURCE_MIN_IDLE="1"
mvn spring-boot:run
```

O perfil `prod` usa PostgreSQL e o Hibernate esta configurado com `ddl-auto=update`, entao as tabelas do servico de presenca devem ser criadas/atualizadas automaticamente.

## 4. Configurar o backend-calendar

Crie `backend-calendar/.env` baseado em `backend-calendar/.env.example`:

```env
PORT=3001
DATABASE_URL=postgresql://postgres:SUA_SENHA@db.xjrepdfpwokwsgfztwku.supabase.co:5432/postgres?sslmode=require
```

Depois teste:

```powershell
cd backend-calendar
npm run db:test
npm start
```

## 5. Criar tabelas auxiliares do calendario

O `backend-calendar` cria algumas tabelas ao iniciar. Para criar tambem a tabela `calendario_2026`, rode o arquivo:

```text
database/schema.sql
```

Voce pode colar o conteudo no SQL Editor do Supabase e executar.

## 6. Conferir se funcionou

Spring Boot:

```text
http://localhost:8082
```

Backend calendario:

```text
http://localhost:3001/api/health
```

Se aparecer erro de rede ou timeout, troque a conexao direta pelo **Session pooler** do Supabase.

## 7. Rodar a interface usando Supabase

Em um terminal, suba o backend da interface:

```powershell
cd backend-calendar
.\run-supabase.ps1
```

Esse script habilita um login local apenas para teste da interface. Voce pode entrar com qualquer email/senha enquanto estiver rodando localmente.

Em outro terminal, suba o frontend estatico:

```powershell
.\run-frontend.ps1
```

Acesse:

```text
http://localhost:3000/index.html
http://localhost:3000/monitoramento.html
```
