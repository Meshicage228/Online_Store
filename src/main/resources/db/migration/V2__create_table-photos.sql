create table if not exists images
(
    image_id serial primary key,
    product_id integer constraint fkghwsjbjo7mg3iufxruvq6iu3q references products,
    image oid
);

