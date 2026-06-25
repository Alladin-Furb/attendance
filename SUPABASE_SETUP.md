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

## 4. Conferir se funcionou

Com o Spring Boot rodando, acesse:

```text
http://localhost:8082
```

Se aparecer erro de rede ou timeout, troque a conexao direta pelo **Session pooler** do Supabase.

## 5. Interface (frontend)

A interface web nao faz parte deste repositorio. Ela vive no repositorio `frontend` (app React) e conversa com este servico de presenca atraves do **api-gateway** (`/api/v1/presencas`, `/api/v1/cursos`).

Para subir a stack completa localmente (gateway + servicos + frontend), use o `docker-compose.yml` da pasta `infra/`.
