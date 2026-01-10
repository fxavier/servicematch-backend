create table provider_zones (
    user_id uuid not null,
    zone_id uuid not null,
    center_lat double precision not null,
    center_lng double precision not null,
    radius_km double precision not null,
    primary key (user_id, zone_id),
    constraint fk_provider_zones_profile
        foreign key (user_id) references provider_profiles (user_id) on delete cascade
);
