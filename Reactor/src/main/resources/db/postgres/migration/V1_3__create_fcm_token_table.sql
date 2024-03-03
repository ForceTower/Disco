create table if not exists user_messaging_token(
    id uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    token text not null,
    user_id uuid not null constraint user_messaging_token_fk1 references users(id)
);

create index if not exists user_messaging_token_idx on user_messaging_token(token);