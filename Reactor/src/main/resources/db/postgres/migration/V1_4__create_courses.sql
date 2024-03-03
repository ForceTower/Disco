create table if not exists course(
    id uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    name varchar(255) not null,
    image_url varchar(255) default null
);

create table if not exists student_course(
    id uuid primary key unique default gen_random_uuid(),
    student_id uuid not null constraint student_course_student_fk1 references student(id),
    course_id uuid not null constraint student_course_course_fk2 references course(id)
);

create unique index student_course_keys_idx on student_course(student_id,course_id);