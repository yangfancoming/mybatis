

drop table person if exists;
drop table address if exists;

CREATE TABLE address
(
    id int NOT NULL,
    CONSTRAINT pk_address PRIMARY KEY (id),
)
;


CREATE TABLE person
(
    id int NOT NULL,
    id_address integer NOT NULL,
    CONSTRAINT pk_person PRIMARY KEY (id),
    CONSTRAINT fk_person_id_address FOREIGN KEY (id_address) REFERENCES address (id) ON UPDATE RESTRICT ON DELETE RESTRICT
)
;

INSERT INTO address (id) VALUES  (1);
INSERT INTO address (id) VALUES  (2);
INSERT INTO address (id) VALUES  (3);
INSERT INTO person (id, id_address) VALUES (1, 1);
INSERT INTO person (id, id_address) VALUES (2, 2);
INSERT INTO person (id, id_address) VALUES (3, 3);
