create table if not exists messages(
    id uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    platform_id bigint NOT NULL,
    student_id uuid not null constraint message_user_idx references student(id) on delete cascade,
    content text not null,
    timestamp bigint NOT NULL,
    sender_profile bigint NOT NULL,
    sender_name TEXT,
    notified boolean NOT NULL default false,
    discipline TEXT,
    code_discipline TEXT,
    html boolean NOT NULL default false,
    date timestamptz,
    attachment_name TEXT,
    attachment_link TEXT,
    created_at timestamptz NOT NULL default now()
);

create unique index message_user_platform_id_idx on messages(platform_id, student_id);
