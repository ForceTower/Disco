create table user_settings
(
    id uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    user_id uuid unique not null constraint user_settings_fk1 references users(id),
    grade_spoiler smallint not null default 3,
    initial_sync_completed bool not null default false
);
