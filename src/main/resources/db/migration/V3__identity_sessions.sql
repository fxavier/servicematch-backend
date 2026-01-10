create table sessions (
    id uuid primary key,
    user_id uuid not null,
    refresh_token_hash varchar(64) not null,
    created_at ${timestamp_tz} not null,
    expires_at ${timestamp_tz} not null,
    revoked_at ${timestamp_tz},
    replaced_by uuid,
    constraint fk_sessions_user
        foreign key (user_id) references users (id) on delete cascade,
    constraint uk_sessions_refresh_token_hash unique (refresh_token_hash)
);
