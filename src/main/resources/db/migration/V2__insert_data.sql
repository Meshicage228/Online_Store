INSERT INTO users (user_id, name, password, role, avatar) values
    ('0144f56e-026a-4a29-8be1-9a85ba8cff3f', 'Admin1', '$2a$10$huajDyb2j4GBceapFVU9Quux/uFuET7bkf00ytafm3tksxBsP.55m', 'ADMIN', '27141'),
    ('48b32169-1c18-441e-a36b-55b4d8c02e93', 'Admin2', '$2a$10$huajDyb2j4GBceapFVU9Quux/uFuET7bkf00ytafm3tksxBsP.55m', 'ADMIN', '27141'),
    ('48b32169-1c18-441e-a36b-55b4d8c02f54', 'Admin', '$2a$10$N1Hn2MqKgGgL5e7KF5e4iedJI/VArG80wzWNMx61x7PKbmBwURkfi', 'ADMIN', '27141'),
    ('5687ec85-29f1-4859-895a-d182c738d627', 'Admin3', '$2a$10$huajDyb2j4GBceapFVU9Quux/uFuET7bkf00ytafm3tksxBsP.55m', 'ADMIN', '27141');

INSERT INTO products (product_id, title, description, count, price, creation_time, update_time, version) values
    (1, 'test1', 'good', 500, 200, '2024-03-07', '2024-03-07', 0),
    (2, 'test2', 'good', 500, 200, '2024-03-07', '2024-03-07', 0),
    (3, 'test3', 'good', 500, 200, '2024-03-07', '2024-03-07', 0),
    (4, 'test4', 'good', 500, 200, '2024-03-07', '2024-03-07', 0);

INSERT INTO images (image_id, product_product_id, image) values
    (1, 1, 27145),
    (2, 2, 27145),
    (3, 3, 27145),
    (4, 2, 27145),
    (5, 1, 27145);

INSERT INTO users_cards (id, money, user_user_id) values
    (1, 99999, '0144f56e-026a-4a29-8be1-9a85ba8cff3f'),
    (2, 99999, '48b32169-1c18-441e-a36b-55b4d8c02e93'),
    (3, 12, '5687ec85-29f1-4859-895a-d182c738d627');

INSERT INTO comments (id, product_product_id, user_user_id, comment, date) values
    (1, 1, '5687ec85-29f1-4859-895a-d182c738d627', 'test comment1', '2024-03-01 12:44:29.755000'),
    (2, 2, '0144f56e-026a-4a29-8be1-9a85ba8cff3f', 'test comment2', '2024-03-01 12:52:29.755000'),
    (3, 2, '48b32169-1c18-441e-a36b-55b4d8c02e93', 'test comment3', '2024-03-01 13:54:29.755000'),
    (4, 3, '0144f56e-026a-4a29-8be1-9a85ba8cff3f', 'test comment4', '2024-03-01 12:44:29.755000'),
    (5, 4, '5687ec85-29f1-4859-895a-d182c738d627', 'test comment5', '2024-03-01 12:44:29.755000');

INSERT INTO users_favorite_products (user_id, product_id) values
    ('5687ec85-29f1-4859-895a-d182c738d627', 1),
    ('5687ec85-29f1-4859-895a-d182c738d627', 2),
    ('0144f56e-026a-4a29-8be1-9a85ba8cff3f', 3),
    ('0144f56e-026a-4a29-8be1-9a85ba8cff3f', 2),
    ('48b32169-1c18-441e-a36b-55b4d8c02e93', 3),
    ('48b32169-1c18-441e-a36b-55b4d8c02e93', 4);