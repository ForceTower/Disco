CREATE TABLE semesters (
   id uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
   name text NOT NULL,
   codename text NOT NULL,
   platform_id bigint NOT NULL,
   start timestamptz,
   finish timestamptz,
   start_class timestamptz DEFAULT NULL,
   end_class timestamptz default NULL
);

create unique index semesters_platform_id ON semesters(platform_id);
create index semesters_name ON semesters(name);
create index semesters_codename ON semesters(codename);

create table departments (
    id         uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    name       text         not null,
    code       text         not null unique,
    phone      text         null,
    site       text         null,
    email      text         null
);

create table disciplines(
    id            uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    code          text     not null,
    name          text     not null,
    program       text     null,
    credits       integer  not null,
    department_id uuid     null,
    full_code     text     null,
    constraint disciplines_code_department_id_unique
        unique (code, department_id),
    constraint disciplines_department_id_foreign
        foreign key (department_id) references departments (id)
            on update cascade on delete cascade
);
