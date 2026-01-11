alter table provider_profiles
    add column reputation double precision;

create table provider_reviews (
    id uuid primary key,
    request_id uuid not null,
    provider_id uuid not null,
    requester_id uuid not null,
    rating integer not null,
    comment varchar(1000) not null,
    created_at ${timestamp_tz} not null,
    constraint uk_provider_reviews_request unique (request_id),
    constraint fk_provider_reviews_request
        foreign key (request_id) references service_requests (id) on delete cascade,
    constraint fk_provider_reviews_provider
        foreign key (provider_id) references users (id) on delete cascade,
    constraint fk_provider_reviews_requester
        foreign key (requester_id) references users (id) on delete cascade
);

create index idx_provider_reviews_provider on provider_reviews (provider_id, created_at);
create index idx_provider_reviews_requester on provider_reviews (requester_id, created_at);
