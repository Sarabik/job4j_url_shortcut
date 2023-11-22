create table if not exists sites (
    id serial primary key not null,
    login varchar(50) unique,
    password varchar(50) unique,
    registration boolean default true,
    site varchar(2000) unique
    );