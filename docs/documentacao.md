# Documentacao - ServiceMatch Backend

## Visao geral
O ServiceMatch Backend e uma API monolitica modular (Spring Modulith) para um marketplace de servicos. Ele conecta clientes que publicam solicitacoes de servico com prestadores que recebem um feed geolocalizado, enviam propostas, conversam e recebem avaliacao. O sistema usa Postgres para persistencia, RabbitMQ para eventos de integracao e um gateway de notificacoes (stub) para push.

## Principais capacidades
- Autenticacao com JWT e refresh tokens com rotacao.
- Cadastro e gestao de perfis de cliente e prestador.
- Catalogo de categorias de servico (admin).
- Criacao de solicitacoes com geolocalizacao e status.
- Matching por categoria e raio geospacial para gerar feed de prestadores.
- Propostas de prestadores para solicitacoes publicadas.
- Conversas entre cliente e prestador apos aceitacao da proposta.
- Notificacoes de eventos chave e log de entrega.
- Avaliacoes de prestadores e calculo de reputacao.

## Arquitetura geral
- Estilo: monolito modular com DDD e camadas (domain, application, infra).
- Modulos definidos com Spring Modulith e dependencias controladas por `@ApplicationModule`.
- Eventos internos via Spring `ApplicationEventPublisher`.
- Evento de integracao via RabbitMQ (ex: `user.registered.v1`).

### Diagrama de contexto
```mermaid
flowchart LR
  Client[Cliente] --> API[ServiceMatch Backend]
  Provider[Prestador] --> API
  Admin[Admin] --> API

  API --> DB[(Postgres)]
  API --> MQ[(RabbitMQ)]
  API --> Push[Gateway Push]
```

### Diagrama de containers
```mermaid
flowchart LR
  API[ServiceMatch Backend]
  DB[(Postgres)]
  MQ[(RabbitMQ)]
  Push[Gateway Push]

  API <-->|JDBC| DB
  API <-->|AMQP| MQ
  API -->|HTTP/SDK| Push
```

## Modulos (Spring Modulith)
- `identityaccess`: autenticacao, usuarios, sessoes e JWT. Depende de `common`.
- `profiles`: perfis de cliente e prestador, zonas de atendimento e reputacao.
- `servicecatalog`: categorias de servico.
- `servicerequests`: solicitacoes de servico e eventos de publicacao. Depende de `servicecatalog::api`.
- `geomatching`: matching de prestadores por categoria e zona. Depende de `servicerequests::events`.
- `proposals`: propostas e aceitacao. Depende de `servicerequests::api`.
- `messaging`: conversas e mensagens. Depende de `proposals::events`.
- `notifications`: notificacoes para eventos de solicitacao e proposta. Depende de `servicerequests::api`, `servicerequests::events`, `proposals::events`.
- `ratingsreviews`: avaliacoes e reputacao. Depende de `servicerequests::api`, `proposals::api`, `profiles::api`.
- `common`: utilitarios e contratos compartilhados (modulo aberto).

### Diagrama de dependencias entre modulos
```mermaid
flowchart LR
  common[common]
  identity[identityaccess]
  profiles[profiles]
  catalog[servicecatalog]
  requests[servicerequests]
  matching[geomatching]
  proposals[proposals]
  messaging[messaging]
  notifications[notifications]
  ratings[ratingsreviews]

  identity --> common
  requests --> catalog
  proposals --> requests
  matching --> requests
  messaging --> proposals
  notifications --> requests
  notifications --> proposals
  ratings --> requests
  ratings --> proposals
  ratings --> profiles
```

## Estrutura em camadas
- `domain`: entidades, value objects e eventos de dominio.
- `application`: casos de uso, DTOs e regras de orquestracao.
- `infra`: web (controllers), persistencia (repositories), config e adapters externos.

## Persistencia e modelo de dados
- Banco: Postgres com migrations Flyway em `src/main/resources/db/migration`.
- Tabelas principais: `users`, `sessions`, `service_requests`, `proposals`, `conversations`, `provider_reviews`, `notifications_log`.

### Diagrama ER (simplificado)
```mermaid
erDiagram
  users ||--o{ user_roles : has
  users ||--o{ sessions : has
  users ||--o| customer_profiles : profile
  users ||--o| provider_profiles : profile
  provider_profiles ||--o{ provider_zones : zones

  catalog_categories ||--o{ catalog_categories : parent
  users ||--o{ service_requests : creates
  catalog_categories ||--o{ service_requests : categorized

  users ||--o{ provider_categories : offers
  catalog_categories ||--o{ provider_categories : in

  users ||--o{ provider_request_matches : matches
  service_requests ||--o{ provider_request_matches : matches

  service_requests ||--o{ proposals : receives
  users ||--o{ proposals : submits

  service_requests ||--|| conversations : has
  proposals ||--|| conversations : accepted
  conversations ||--o{ conversation_messages : messages
  users ||--o{ conversation_messages : sends

  service_requests ||--o| provider_reviews : review
  users ||--o{ provider_reviews : as_participant

  users ||--o{ notifications_log : receives
```

## Eventos e mensageria
### Eventos internos (ApplicationEvent)
- `UserRegistered`: publicado no registro de usuario.
- `RequestPublished`: publicado ao criar solicitacao com status `PUBLISHED`.
- `ProposalSubmitted`: publicado ao enviar proposta.
- `ProposalAccepted`: publicado ao aceitar proposta.
- `ProvidersMatched`: publicado apos matching geografico.

### Evento de integracao (RabbitMQ)
- `user.registered.v1`: publicado por `identityaccess` e consumido por `profiles` para bootstrap de perfis.

## Fluxos principais (sequencias)
### Cadastro e bootstrap de perfis
```mermaid
sequenceDiagram
  actor Client as Cliente
  participant Auth as AuthController
  participant AuthSvc as AuthService
  participant RegSvc as UserRegistrationService
  participant Repo as UserRepository
  participant Pub as UserRegisteredEventHandler
  participant MQ as RabbitMQ
  participant Listener as UserRegisteredListener
  participant ProfSvc as ProfilesBootstrapService

  Client->>Auth: POST /auth/register
  Auth->>AuthSvc: register()
  AuthSvc->>RegSvc: register(email, hash, roles)
  RegSvc->>Repo: save(user)
  RegSvc-->>Pub: publish UserRegistered
  Pub->>MQ: publish user.registered.v1
  MQ-->>Listener: UserRegisteredMessage
  Listener->>ProfSvc: bootstrap(userId, roles)
```

### Publicacao de solicitacao e matching
```mermaid
sequenceDiagram
  actor Client as Cliente
  participant Req as ServiceRequestsController
  participant ReqSvc as ServiceRequestService
  participant Repo as ServiceRequestRepository
  participant Notif as NotificationEventListener
  participant NotifSvc as NotificationsService
  participant MatchH as RequestPublishedEventHandler
  participant MatchSvc as GeoMatchingService
  participant MatchRepo as ProviderRequestMatchRepository

  Client->>Req: POST /service-requests
  Req->>ReqSvc: create(requesterId, request)
  ReqSvc->>Repo: save(serviceRequest)
  ReqSvc-->>Notif: publish RequestPublished
  ReqSvc-->>MatchH: publish RequestPublished
  Notif->>NotifSvc: notify(requesterId, REQUEST_PUBLISHED)
  MatchH->>MatchSvc: handleRequestPublished(event)
  MatchSvc->>MatchRepo: save(matches)
```

### Proposta e aceitacao
```mermaid
sequenceDiagram
  actor Provider as Prestador
  actor Client as Cliente
  participant Prop as ProposalsController
  participant PropSvc as ProposalService
  participant ReqSvc as ServiceRequestService
  participant Notif as NotificationEventListener
  participant MsgH as ProposalAcceptedEventHandler
  participant MsgSvc as ConversationService

  Provider->>Prop: POST /proposals
  Prop->>PropSvc: submit(providerId, request)
  PropSvc->>ReqSvc: assertRequestAcceptsProposals()
  PropSvc-->>Notif: publish ProposalSubmitted

  Client->>Prop: POST /proposals/{id}/accept
  Prop->>PropSvc: accept(requesterId, proposalId)
  PropSvc->>ReqSvc: bookRequest()
  PropSvc-->>Notif: publish ProposalAccepted
  PropSvc-->>MsgH: publish ProposalAccepted
  MsgH->>MsgSvc: handleProposalAccepted()
```

### Avaliacao e reputacao
```mermaid
sequenceDiagram
  actor Client as Cliente
  participant Rev as ProviderReviewsController
  participant RevSvc as RatingsReviewsService
  participant ReqSvc as ServiceRequestService
  participant PropSvc as ProposalService
  participant ProfSvc as ProfilesService

  Client->>Rev: POST /service-requests/{id}/reviews
  Rev->>RevSvc: submit(requesterId, requestId)
  RevSvc->>ReqSvc: assertRequestCompleted()
  RevSvc->>PropSvc: findAcceptedProviderId()
  RevSvc->>ProfSvc: updateProviderReputation()
```

## API (alto nivel)
- Publico:
  - `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout`
  - `GET /catalog/categories`
- Protegido (JWT + roles):
  - `GET /profiles/me`, `PATCH /profiles/me`
  - `POST /profiles/me/zones`, `PATCH /profiles/me/zones/{zoneId}`, `DELETE /profiles/me/zones/{zoneId}`
  - `POST /service-requests`
  - `GET /provider/feed` ou `GET /matching/feed`
  - `POST /proposals`, `POST /proposals/{proposalId}/accept`
  - `POST /conversations/{conversationId}/messages`, `GET /conversations/{conversationId}/messages`
  - `POST /service-requests/{requestId}/reviews`
  - `GET /rbac/client`, `GET /rbac/provider`, `GET /rbac/admin`

## Seguranca
- JWT com roles no claim `roles` e prefixo `ROLE_`.
- Hash de senha com BCrypt.
- Refresh tokens com hash SHA-256 em `sessions` e rotacao por `SessionService`.
- Endpoints anonimos limitados a `auth`, `actuator` e `swagger`.

## Configuracao e ambientes
- `application.yml` define datasource, RabbitMQ e seguranca.
- Profiles: `dev`, `test`, `prod` com overrides em arquivos dedicados.
- Docker Compose provem Postgres e RabbitMQ (management em `:15672`).

## Observabilidade
- Actuator habilitado: `health` e `info`.
- OpenAPI/Swagger configurado via `OpenApiConfig`.

## Consideracoes de system design
- Consistencia: escrita principal em Postgres; eventos internos usam o mesmo banco e transacao local.
- Eventual consistency: efeitos secundarios (notificacoes, matching) reagem a eventos de dominio.
- Idempotencia: criacao de conversas trata concorrencia com verificacao de existencia e trata violacao de chave unica.
- Escalabilidade: app stateless, pode escalar horizontalmente; Postgres e RabbitMQ sao pontos centrais.
- Melhorias possiveis:
  - Outbox pattern para garantir publicacao de eventos.
  - Retries e DLQ para notificacoes e listeners RabbitMQ.
  - Observabilidade com tracing e metrics detalhadas.

## Pontos de extensao
- Substituir `StubPushGateway` por um provider real (FCM, SNS, etc).
- Enriquecer matching (ranking por reputacao, tempo de resposta, etc).
- Expor endpoints de listagem de solicitacoes, propostas e conversas.
- Expandir politicas de RBAC para escopos mais finos.
