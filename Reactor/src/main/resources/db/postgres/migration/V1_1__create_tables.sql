CREATE TABLE IF NOT EXISTS role(
    id uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    name varchar(255) NOT NULL UNIQUE,
    basic boolean not null default false
);

INSERT INTO role(name, basic) values ('basic', true);

CREATE TABLE IF NOT EXISTS users(
    id uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    username varchar(255) NOT NULL UNIQUE,
    name varchar(255) NOT NULL,
    email varchar(255) DEFAULT NULL UNIQUE,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS user_roles(
    id uuid primary key unique default gen_random_uuid(),
    user_id uuid not null constraint user_role_fk1 references users(id),
    role_id uuid not null constraint user_role_fk2 references role(id)
);

CREATE UNIQUE INDEX user_role_unique_idx ON user_roles(user_id, role_id);

CREATE TABLE IF NOT EXISTS student (
    id uuid PRIMARY KEY UNIQUE DEFAULT gen_random_uuid(),
    name varchar(255) NOT NULL,
    platform_id bigint NOT NULL UNIQUE,
    user_id uuid NOT NULL CONSTRAINT student_user_foreign_key1 REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS passkey(
    id uuid primary key unique default gen_random_uuid(),
    key_id varchar(255) not null unique,
    key_type varchar(255) not null,
    public_key_cose text not null,
    user_id uuid not null constraint passkey_user_foreign_key1 references users(id)
);

