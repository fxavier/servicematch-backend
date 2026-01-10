create table customer_profiles (
    user_id uuid primary key,
    display_name varchar(120),
    phone varchar(30),
    created_at ${timestamp_tz} not null,
    updated_at ${timestamp_tz} not null
);

create table provider_profiles (
    user_id uuid primary key,
    display_name varchar(120),
    bio varchar(500),
    created_at ${timestamp_tz} not null,
    updated_at ${timestamp_tz} not null
);
