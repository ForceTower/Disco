create table discipline_offers
(
    id            uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    discipline_id uuid    not null,
    semester_id   uuid    not null,
    created_at    timestamptz default now(),
    constraint discipline_offers_discipline_id_semester_id_unique
        unique (discipline_id, semester_id),
    constraint discipline_offers_discipline_id_foreign
        foreign key (discipline_id) references disciplines (id)
            on delete cascade,
    constraint discipline_offers_semester_id_foreign
        foreign key (semester_id) references semesters (id)
            on delete cascade
);

