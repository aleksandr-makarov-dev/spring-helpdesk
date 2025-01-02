create table if not exists public.refresh_tokens
(
    id         uuid primary key      default gen_random_uuid(),
    token      varchar(512) not null unique,
    created_at timestamp    not null default current_timestamp,
    expires_at timestamp    not null,
    revoked_at timestamp,
    user_id    uuid         not null references users (id)
)