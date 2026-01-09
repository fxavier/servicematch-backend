create table users (
    id uuid primary key,
    email varchar(320) not null,
    password_hash varchar(255) not null,
    account_status varchar(30) not null,
    constraint uk_users_email unique (email)
);

create table user_roles (
    user_id uuid not null,
    role varchar(50) not null,
    primary key (user_id, role),
    constraint fk_user_roles_user
        foreign key (user_id) references users (id) on delete cascade
);
