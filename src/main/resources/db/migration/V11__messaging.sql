create table conversations (
    request_id uuid primary key,
    proposal_id uuid not null,
    requester_id uuid not null,
    provider_id uuid not null,
    created_at ${timestamp_tz} not null,
    updated_at ${timestamp_tz} not null,
    constraint fk_conversations_request
        foreign key (request_id) references service_requests (id) on delete cascade,
    constraint fk_conversations_proposal
        foreign key (proposal_id) references proposals (id) on delete cascade,
    constraint fk_conversations_requester
        foreign key (requester_id) references users (id) on delete cascade,
    constraint fk_conversations_provider
        foreign key (provider_id) references users (id) on delete cascade
);

create index idx_conversations_proposal on conversations (proposal_id);
create index idx_conversations_participants on conversations (requester_id, provider_id);

create table conversation_messages (
    id uuid primary key,
    conversation_id uuid not null,
    sender_id uuid not null,
    body varchar(2000) not null,
    sent_at ${timestamp_tz} not null,
    constraint fk_messages_conversation
        foreign key (conversation_id) references conversations (request_id) on delete cascade,
    constraint fk_messages_sender
        foreign key (sender_id) references users (id) on delete cascade
);

create index idx_messages_conversation on conversation_messages (conversation_id, sent_at);
create index idx_messages_sender on conversation_messages (sender_id);
