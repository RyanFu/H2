DROP DATABASE IF EXISTS girls;
CREATE DATABASE girls;
use girls;

CREATE TABLE vote(
    id INT NOT NULL AUTO_INCREMENT,
    pid INT,
    pids varchar(10),    
    level INT,
    ip VARCHAR(15),
    score INT,
    time TIMESTAMP,
    PRIMARY KEY (id)
);

insert into vote(pid, level, pids, ip, score) values 
(1, 0, '0-1', '1.1.1.1', 1), 
(1, 0, '0-1', '1.1.1.2', 1), 
(1, 0, '0-1', '1.1.1.2', -1), 
(1, 0, '0-1', '1.1.1.2', -1), 
(2, 0, '0-2', '1.1.1.2', 1),
(2, 0, '0-2', '1.1.1.3', 1),
(2, 0, '0-2', '1.1.1.3', 1)
;

select * from vote;

select sum(score) as score, pids from vote where score>0 group by pid order by score desc limit 100;

