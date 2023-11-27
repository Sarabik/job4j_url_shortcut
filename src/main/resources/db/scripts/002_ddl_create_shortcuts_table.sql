create table if not exists shortcuts (
    id serial primary key not null,
    url varchar(300) unique,
    shortcut varchar(50) unique,
    counter integer default 0,
    site_id integer references sites(id)
    );