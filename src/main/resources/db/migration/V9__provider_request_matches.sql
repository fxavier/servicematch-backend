create table provider_request_matches (
    id uuid primary key,
    provider_id uuid not null,
    request_id uuid not null,
    category_id uuid not null,
    description varchar(1000) not null,
    location_lat double precision not null,
    location_lng double precision not null,
    matched_at ${timestamp_tz} not null,
    constraint uk_provider_request_matches unique (provider_id, request_id),
    constraint fk_provider_matches_provider
        foreign key (provider_id) references users (id) on delete cascade,
    constraint fk_provider_matches_request
        foreign key (request_id) references service_requests (id) on delete cascade
);

create index idx_provider_matches_provider on provider_request_matches (provider_id);
