

    INSERT INTO order_header(order_id, cust_name)
        VALUES (1, 'Fred');
    INSERT INTO order_header(order_id, cust_name)
        VALUES (2, 'Barney');
    INSERT INTO order_header(order_id, cust_name)
        VALUES (3, 'Homer');        

    INSERT INTO order_detail(order_id, line_number, quantity, item_description)
        VALUES (1, 1, 1, 'Pen');
    INSERT INTO order_detail(order_id, line_number, quantity, item_description)
        VALUES (1, 2, 3, 'Pencil');
    INSERT INTO order_detail(order_id, line_number, quantity, item_description)
        VALUES (1, 3, 2, 'Notepad');
    INSERT INTO order_detail(order_id, line_number, quantity, item_description)
        VALUES (2, 1, 1, 'Compass');
    INSERT INTO order_detail(order_id, line_number, quantity, item_description)
        VALUES (2, 2, 1, 'Protractor');
    INSERT INTO order_detail(order_id, line_number, quantity, item_description)
        VALUES (2, 3, 2, 'Pencil');
