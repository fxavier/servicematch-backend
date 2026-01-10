create table service_requests (
    id uuid primary key,
    requester_id uuid not null,
    category_id uuid not null,
    description varchar(1000) not null,
    status varchar(20) not null,
    location_lat double precision not null,
    location_lng double precision not null,
    created_at ${timestamp_tz} not null,
    updated_at ${timestamp_tz} not null,
    constraint fk_service_requests_user
        foreign key (requester_id) references users (id) on delete cascade,
    constraint fk_service_requests_category
        foreign key (category_id) references catalog_categories (id)
);

create index idx_service_requests_requester on service_requests (requester_id);
create index idx_service_requests_category on service_requests (category_id);
