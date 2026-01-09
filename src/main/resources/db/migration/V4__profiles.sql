create table customer_profiles (
    user_id uuid primary key,
    display_name varchar(120),
    phone varchar(30),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table provider_profiles (
    user_id uuid primary key,
    display_name varchar(120),
    bio varchar(500),
    created_at timestamptz not null,
    updated_at timestamptz not null
);
