create table sync_registry
(
    id uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    student_id uuid not null constraint sync_registry_fk1 references student(id),
    executor text not null,
    completed bool not null default false,
    success bool default null,
    error integer default null,
    message text default null,
    start_at timestamptz not null,
    finished_at timestamptz default null
);