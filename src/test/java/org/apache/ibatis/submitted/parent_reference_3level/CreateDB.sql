

DROP TABLE blog;
DROP TABLE post;
DROP TABLE comment;

CREATE TABLE blog (
id          INT,
title       VARCHAR(255)
);

CREATE TABLE post (
id          INT,
blog_id     INT,
body        VARCHAR(255)
);

CREATE TABLE comment (
id          INT,
post_id     INT,
comment     VARCHAR(255)
);

-- Data load
INSERT INTO blog (id,title) VALUES (1,'Blog with posts');
INSERT INTO blog (id,title) VALUES (2,'Blog without posts');

INSERT INTO post (id,blog_id,body) VALUES (1,1,'I think if I never smelled another corn nut it would be too soon...');
INSERT INTO post (id,blog_id,body) VALUES (2,1,'That''s not a dog.  THAT''s a dog!');

INSERT INTO comment (id,post_id,comment) VALUES (1,1,'I disagree and think...');
INSERT INTO comment (id,post_id,comment) VALUES (2,1,'I agree and think troll is an...');
INSERT INTO comment (id,post_id,comment) VALUES (3,2,'I don not agree and still think troll is an...');
