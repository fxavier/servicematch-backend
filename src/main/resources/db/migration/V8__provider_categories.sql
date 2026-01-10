create table provider_categories (
    provider_id uuid not null,
    category_id uuid not null,
    primary key (provider_id, category_id),
    constraint fk_provider_categories_provider
        foreign key (provider_id) references users (id) on delete cascade,
    constraint fk_provider_categories_category
        foreign key (category_id) references catalog_categories (id)
);

create index idx_provider_categories_category on provider_categories (category_id);
