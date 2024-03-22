create table classes
(
    id               uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    offer_id         uuid         not null,
    sequence         text         not null,
    platform_id      bigint       not null unique,
    credits_override integer      null,
    created_at       timestamptz  not null default now(),
    constraint classes_offer_id_sequence_type_id_unique
        unique (offer_id, sequence),
    constraint classes_offer_id_foreign
        foreign key (offer_id) references discipline_offers (id)
            on delete cascade
);

create table teachers
(
    id               uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    name             text           not null,
    platform_id      bigint         null        unique,
    created_at       timestamptz    not null    default now()
);

create table class_teacher
(
    id              uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    class_id        uuid        not null,
    teacher_id      uuid        not null,

    constraint class_teacher_class_id_teacher_id_unique
        unique (class_id, teacher_id),
    constraint class_teacher_teacher_id_foreign
        foreign key (teacher_id) references teachers (id)
            on delete cascade
);

create table student_classes
(
    id                  uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    student_id          uuid                not null,
    class_id            uuid                not null,
    final_grade         decimal             null,
    final_grade_raw     varchar(80)         null,
    partial_grade       decimal             null,
    partial_grade_raw   varchar(80)         null,
    created_at          timestamptz         not null default now(),
    constraint student_classes_student_id_class_id_unique
        unique (student_id, class_id),
    constraint student_classes_class_id_foreign
        foreign key (class_id) references classes (id)
            on delete cascade,
    constraint student_classes_student_id_foreign
        foreign key (student_id) references student (id)
            on delete cascade
);

create table grades
(
    id                  uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    name                text           not null,
    resumed_name        text           null,
    student_class_id    uuid           not null,
    grouping_name       text           null,
    date                timestamptz    null,
    grade               decimal        null,
    grade_raw           varchar(80)    null,
    platform_id         text           not null,
    notification_state  smallint       not null,
    created_at          timestamptz    not null default now(),
    updated_at          timestamptz    not null default now(),
    constraint grades_student_class_id_platform_id_unique
        unique (student_class_id, platform_id),
    constraint grades_student_class_id_foreign
        foreign key (student_class_id) references student_classes (id)
            on delete cascade
);
