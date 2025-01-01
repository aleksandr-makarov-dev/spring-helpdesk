create table if not exists public.users
(
    id                  varchar(36) primary key default gen_random_uuid(),
    username            varchar(254) not null unique,
    email               varchar(320) not null unique,
    password_hash       varchar(72)  not null,
    created_at          timestamp    not null   default current_timestamp,
    email_confirmed_at  timestamp,
    lockout_expires_at  timestamp,
    access_failed_count int
);

create table if not exists public.roles
(
    id   varchar(36) primary key default gen_random_uuid(),
    name varchar(72) not null unique
);

create table if not exists public.users_roles
(
    id      varchar(36) primary key default gen_random_uuid(),
    user_id varchar(36) not null references users (id),
    role_id varchar(36) not null references roles (id),
    constraint uk_user_id_role_id unique (user_id, role_id)
)