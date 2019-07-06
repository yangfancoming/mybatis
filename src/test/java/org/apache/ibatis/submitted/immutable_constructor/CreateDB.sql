

CREATE TABLE immutables (
    immutable_id          INTEGER PRIMARY KEY,
    immutable_description VARCHAR (30) NOT NULL
);

INSERT INTO immutables (immutable_id, immutable_description) VALUES (1, 'Description of immutable');
INSERT INTO immutables (immutable_id, immutable_description) VALUES (2, 'Another immutable');
