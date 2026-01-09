# servicematch-backend

## Setup local

1) Copie `.env.example` para `.env` e ajuste se precisar.
2) Suba o Postgres:

```sh
docker compose up -d
```

3) Acesse o RabbitMQ Management: `http://localhost:15672` (user/pass do `.env`).

4) Rode a aplicação com o profile `dev`:

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Migrations

As migrations do Flyway ficam em `src/main/resources/db/migration`.
