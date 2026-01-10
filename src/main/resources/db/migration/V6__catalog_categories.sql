create table catalog_categories (
    id uuid primary key,
    name varchar(120) not null,
    parent_id uuid,
    path varchar(500),
    created_at ${timestamp_tz} not null,
    updated_at ${timestamp_tz} not null,
    constraint fk_catalog_categories_parent
        foreign key (parent_id) references catalog_categories (id) on delete set null
);

create index idx_catalog_categories_parent on catalog_categories (parent_id);
