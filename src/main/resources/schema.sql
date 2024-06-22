-- create table  IF NOT EXISTS users
-- (
--     id              integer generated by default as identity
--         primary key,
--     login           varchar(30)                                     not null
--         unique,
--     password        varchar(80)                                     not null,
--     name            varchar(30),
--     email           varchar(50)                                     not null
--         unique,
--     is_in_ban       boolean                                         not null,
--     surname         varchar(40) default 'иванов'::character varying not null
-- );
--
--
--
-- create table IF NOT EXISTS users_to_be_confirmed
-- (
--     id              integer generated by default as identity
--         primary key,
--     login           varchar(30) not null
--         unique,
--     password        varchar(80) not null,
--     name            varchar(30),
--     email           varchar(50) not null
--         unique,
--     is_in_ban       boolean,
--     surname         varchar(80) not null
-- );
-- create table IF NOT EXISTS houses(
--                        id  integer generated by default as identity primary key,
--                        description varchar(512),
--                        city varchar(32) not null ,
--                        address varchar(128) not null,
--                        user_id int references users(id)
-- );
--
-- create table IF NOT EXISTS trades(
--                        id  integer generated by default as identity primary key,
--                        given_house int references houses(id),
--                        received_house int references houses(id),
--                        start_date date,
--                        end_date date,
--                        status VARCHAR(20) NOT NULL DEFAULT 'ожидает ответа'
-- );

---------------

-- create type user_roles as enum ('MODERATOR', 'USER');
--
-- create type house_status as enum ('MODERATED', 'UP_FOR_SALE');



create table if not exists users
(
    id            integer generated by default as identity
        primary key,
    login         varchar(30)                                     not null
        unique,
    password      varchar(80)                                     not null,
    name          varchar(30),
    email         varchar(50)                                     not null
        unique,
    is_in_ban     boolean                                         not null,
    surname       varchar(40) default 'иванов'::character varying not null,
    user_role     user_roles  default 'USER'::user_roles,
    total_reviews integer     default 0,
    rating_sum    integer     default 0,
    description   varchar(1024)
);



create table if not exists users_to_be_confirmed
(
    id        integer generated by default as identity
        primary key,
    login     varchar(30) not null
        unique,
    password  varchar(80) not null,
    name      varchar(30),
    email     varchar(50) not null
        unique,
    is_in_ban boolean,
    surname   varchar(80) not null,
    description varchar(1024)
);



create table if not exists houses
(
    id           integer generated by default as identity
        primary key,
    description  varchar(512),
    city         varchar(32)  not null,
    address      varchar(128) not null,
    user_id      integer
        references public.users,
    house_status house_status default 'MODERATED'::house_status
);

create table if not exists trades
(
    id             integer generated by default as identity
        primary key,
    given_house    integer
        references public.houses,
    received_house integer
        references public.houses,
    start_date     date,
    end_date       date,
    status         varchar(20) default 'ожидает ответа'::character varying not null
);

create table if not exists houses_to_be_moderated
(
    id          integer generated by default as identity
        primary key,
    house_id    integer
        references public.houses,
    description varchar(512),
    city        varchar(32),
    address     varchar(128),
    user_id     integer
        references public.users,
    is_approved boolean,
    decision    varchar(256)
);


create table if not exists reported_users
(
    id               integer generated by default as identity
        primary key,
    reported_user_id integer
        references public.users,
    reporter_id      integer
        references public.users,
    complaint_reason varchar(256) not null,
    is_rejected      boolean
);


create table if not exists house_reviews
(
    id          integer generated by default as identity
        primary key,
    house_id    integer
        references public.houses,
    rating      integer,
    description varchar(256),
    author_id   integer
        references public.users
);















-- INSERT INTO users (login, password, name, email, is_in_ban)
-- SELECT 'nikitos',
--        '$2a$10$u7JP6SAu7jqlK3SrEI4o4eQnBkdXU8lw4usGH3/f72mOS12d8jguW',
--        'nikitos',
--        'nikitos@mail.ru',
--        false
-- Where not exists(select * from users where id = 1)
-- ON CONFLICT DO NOTHING;
-- INSERT INTO users (login, password, name, email, is_in_ban)
-- SELECT 'n',
--        '$2a$10$u7JP6SAu7jqlK3SrEI4o4eQnBkdXU8lw4usGH3/f72mOS12d8jguW',
--        'n',
--        'nikitos123@mail.ru',
--        false
-- Where not exists(select * from users where id = 2)
-- ON CONFLICT DO NOTHING;