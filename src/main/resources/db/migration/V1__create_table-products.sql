create table if not exists products
(
    id serial primary key,
    price real,
    description varchar(255),
    title varchar(255)
);

