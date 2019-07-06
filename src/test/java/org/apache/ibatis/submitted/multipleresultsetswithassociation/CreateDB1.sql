

    DROP PROCEDURE GetOrderDetailsAndHeaders IF EXISTS;
    DROP TABLE order_detail IF EXISTS;
    DROP TABLE order_header IF EXISTS;

    CREATE TABLE order_detail
    (
        order_id integer NOT NULL,
        line_number integer NOT NULL,
        quantity integer NOT NULL,
        item_description varchar(50) NOT NULL,
        PRIMARY KEY (order_id, line_number)
    );

    CREATE TABLE order_header
    (
        order_id integer NOT NULL,
        cust_name varchar(50) NOT NULL,
        PRIMARY KEY (order_id)
    );
