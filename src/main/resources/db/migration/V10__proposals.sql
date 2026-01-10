create table proposals (
    id uuid primary key,
    request_id uuid not null,
    provider_id uuid not null,
    message varchar(1000),
    status varchar(20) not null,
    created_at ${timestamp_tz} not null,
    updated_at ${timestamp_tz} not null,
    constraint uk_proposals_request_provider unique (request_id, provider_id),
    constraint fk_proposals_request
        foreign key (request_id) references service_requests (id) on delete cascade,
    constraint fk_proposals_provider
        foreign key (provider_id) references users (id) on delete cascade
);

create index idx_proposals_request on proposals (request_id);
