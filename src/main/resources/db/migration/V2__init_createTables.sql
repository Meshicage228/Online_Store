create table users(
    user_id uuid not null primary key,
    name varchar(255) unique,
    password varchar(255),
    role varchar(255)
        constraint users_role_check
            check ((role)::text = ANY ((ARRAY ['USER':: character varying, 'ADMIN':: character varying])::text[])),
    avatar oid
);
create table products(
    count integer,
    creation_time date,
    price real,
    product_id serial primary key,
    version integer,
    update_time timestamp(6),
    description varchar(255),
    title varchar(255)
);
create table images(
    image_id serial primary key,
    product_product_id integer
        constraint fkiq15xiv4oib9u8jo07tw5dcji
            references products,
    image oid
);
create table comments(
    id serial primary key,
    product_product_id integer
        constraint fkaaki4swospsr6lo2nsiib0d4i
            references products,
    date timestamp(6),
    user_user_id uuid
        constraint fkcc96r34gkial9e3fxphcr3abt
            references users,
    comment varchar(255)
);
create table purchases(
    bill real,
    count_of_product integer,
    id serial primary key,
    price_at_moment_buying real,
    product_product_id     integer
        constraint fkdfv2xugvsklh8py4sqv8fyf0m
            references products,
    date_of_purchase timestamp(6),
    user_user_id uuid
        constraint fk297yrt40v989s9w1in24jwt4g
            references users,
    status varchar(255)
        constraint purchases_status_check
            check ((status)::text = ANY
        ((ARRAY ['WAITING'::character varying, 'IN_PROGRESS'::character varying, 'DONE'::character varying])::text[]))
    );
create table user_carts(
    cart_id serial primary key,
    count_to_buy integer,
    product_id integer
        constraint fk8t3i6dq8dndgcj39bkxtfmd90
            references products,
    user_id uuid
        constraint fk71eql61lq7h25ivtn0eq7mdsc
            references users,
    unique (user_id, product_id),
    constraint ukd2lbcw4tqvlo5av8ct4vm0s4d
        unique (user_id, product_id)
);
create table users_cards(
    id serial primary key,
    money real,
    user_user_id uuid
        unique
        constraint fkostpfy3h2src0dhn7osfnian8
            references users
);
create table users_favorite_products(
    product_id integer not null
        constraint fklysemabkl8xiievg7vow5atng
            references products,
    user_id uuid not null
        constraint fkbgh9jwkhld80atucdab8rjaht
            references users,
    primary key (product_id, user_id)
);