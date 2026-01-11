create table notifications_log (
    id uuid primary key,
    recipient_id uuid not null,
    event_type varchar(60) not null,
    title varchar(160) not null,
    body varchar(1000) not null,
    payload text,
    status varchar(20) not null,
    created_at ${timestamp_tz} not null,
    dispatched_at ${timestamp_tz},
    failure_reason varchar(500),
    constraint fk_notifications_recipient
        foreign key (recipient_id) references users (id) on delete cascade
);

create index idx_notifications_recipient on notifications_log (recipient_id, created_at);
create index idx_notifications_status on notifications_log (status);
