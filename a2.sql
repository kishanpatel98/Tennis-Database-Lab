SET search_path TO A2;
\i a2.ddl
-- Add below your SQL statements.
-- For each of the queries below, your final statement should populate the respective answer table (queryX) with the correct tuples. It should look something like:
-- INSERT INTO queryX (SELECT … <complete your SQL query here> …)
-- where X is the correct index [1, …,10].
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables query1, query2, ...
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.
-- Good Luck!


--Query 1 statements
INSERT INTO query1
SELECT DISTINCT P.pname, C.cname, T.tname
FROM (player P JOIN (champion CH JOIN tournament T ON CH.tid = T.tid) ON CH.pid = p.pid) JOIN country C on P.cid = C.cid
WHERE P.cid = T.cid
ORDER BY pname ASC;

-- --Query 2 statements
--Find the tournament that has the most seats in all courts (sum of capacity of its courts). Report the name of the tournament and the total capacity.
INSERT INTO query2
SELECT tname, SUM(capacity)
FROM tournament T JOIN court C ON (T.tid = C.tid)
GROUP BY T.tname
HAVING SUM(c.capacity) = (
  SELECT MAX(tot_capicity)
  FROM (
    SELECT SUM(c.capacity) AS tot_capicity
    FROM tournament T JOIN court C ON (T.tid = C.tid)
    GROUP BY T.tid
  ) AS table1);


-- --Query 3 statements
-- CREATE OR REPLACE VIEW playPair AS
-- SELECT E.winid, E.lossid
-- FROM event E
-- WHERE MAX
-- GROUP BY E.winid;

 --INSERT INTO query3
 --SELECT P.pid AS p1id, P.pname AS p1name, P.
--
--Query 4 statements
--Find the players that have been a champion in every tournament. Report the id and name of the player
CREATE OR REPLACE VIEW playAll AS
SELECT  champion.pid, COUNT (DISTINCT champion.tid) AS num
FROM champion
GROUP BY champion.pid;

INSERT INTO query4
SELECT player.pid, player.pname
FROM playAll JOIN player ON playALL.pid = player.pid
WHERE playALL.num =
  (
    SELECT COUNT(*) AS tCount
    FROM tournament
  )
ORDER BY pname ASC;

DROP VIEW playAll CASCADE;


--
-- --Query 5 statements
INSERT INTO query5
SELECT P.pid, P.pname, AVG(R.wins)
FROM player P JOIN record R ON P.pid = R.pid
WHERE R.year >= 2011 AND R.year <= 2014
GROUP BY P.pid
ORDER BY AVG(R.wins) DESC
LIMIT 10;
--
-- --Query 6 statements

CREATE OR REPLACE VIEW view2011 AS
SELECT record.pid, record.wins
FROM record
WHERE record.year = 2011;

CREATE OR REPLACE VIEW view2012 AS
SELECT record.pid, record.wins
FROM record
WHERE record.year = 2012;

CREATE OR REPLACE VIEW view2013 AS
SELECT record.pid, record.wins
FROM record
WHERE record.year = 2013;

CREATE OR REPLACE VIEW view2014 AS
SELECT record.pid, record.wins
FROM record
WHERE record.year = 2014;

INSERT INTO query6
SELECT P.pid, P.pname
FROM player P
  JOIN view2011 ON P.pid=view2011.pid
  JOIN view2012 ON view2011.pid=view2012.pid
  JOIN view2013 ON view2012.pid=view2013.pid
  JOIN view2014 ON view2013.pid=view2014.pid
WHERE view2011.wins < view2012.wins
  AND view2012.wins < view2013.wins
  AND view2013.wins < view2014.wins
ORDER BY P.pname ASC;

DROP VIEW view2014 CASCADE;
DROP VIEW view2013 CASCADE;
DROP VIEW view2012 CASCADE;
DROP VIEW view2011 CASCADE;

--
-- --Query 7 statements
INSERT INTO query7
SELECT P.pname, C.year
FROM champion C JOIN player P ON C.pid = P.pid
GROUP BY C.year, P.pname
HAVING COUNT (DISTINCT C.tid)>1
ORDER BY P.pname DESC, C.year DESC;

-- --Query 8 statements
CREATE OR REPLACE VIEW win AS
SELECT A.winid, A.eid
FROM event A;

CREATE OR REPLACE VIEW win_play AS
SELECT win.winid, win.eid, P.pname, P.cid
FROM win JOIN player P ON win.winid = P.pid;

CREATE OR REPLACE VIEW loss AS
SELECT B.lossid, B.eid
FROM event B;

CREATE OR REPLACE VIEW loss_play AS
SELECT loss.lossid, loss.eid, P.pname, P.cid
FROM loss JOIN player P ON loss.lossid = P.pid;

CREATE OR REPLACE VIEW fin_table AS
SELECT win_play.pname AS p1name, loss_play.pname AS p2name, C.cname
FROM win_play
  JOIN loss_play ON (win_play.eid=loss_play.eid AND win_play.cid = loss_play.cid)
  JOIN country C ON (win_play.cid = C.cid AND loss_play.cid = C.cid)
UNION
SELECT loss_play.pname AS p1name, win_play.pname AS p2name, C.cname
FROM loss_play
  JOIN win_play ON (win_play.eid=loss_play.eid AND win_play.cid = loss_play.cid)
  JOIN country C ON (win_play.cid = C.cid AND loss_play.cid = C.cid);

INSERT INTO query8
SELECT *
FROM fin_table
ORDER BY 3 ASC, 1 DESC;

DROP VIEW loss_play CASCADE;
DROP VIEW loss CASCADE;
DROP VIEW win_play CASCADE;
DROP VIEW win CASCADE;


--
-- --Query 9 statements
INSERT INTO query9
SELECT C.cname, COUNT(*) AS champions
FROM champion CH
  JOIN player P ON CH.pid=P.pid
  JOIN country C ON P.cid=C.cid
GROUP BY C.cname
HAVING COUNT(*) =
  (
  SELECT MAX(champs)
  FROM
    (
    SELECT C.cname, COUNT(*) AS champs
    FROM champion CH
      JOIN player P ON CH.pid=P.pid
      JOIN country C ON P.cid=C.cid
    GROUP BY C.cname
  ) AS tab)
ORDER BY C.cname DESC;
--
-- --
-- -- --Query 10 statements
CREATE OR REPLACE VIEW matches AS
SELECT E.eid, E.duration, E.winid, E.lossid
FROM event E;

CREATE OR REPLACE VIEW over200 AS
SELECT matches.eid
FROM matches
GROUP BY matches.eid, matches.winid, matches.lossid
HAVING AVG(matches.duration)>200;

CREATE OR REPLACE VIEW playOver AS
SELECT E.winid AS pid
FROM over200 JOIN event E ON over200.eid = E.eid
UNION
SELECT E.lossid AS pid
FROM over200 JOIN event E ON over200.eid = E.eid;

INSERT INTO query10
SELECT P.pname
FROM (playOver JOIN record R ON playOver.pid=R.pid) JOIN player P ON P.pid = playOver.pid
WHERE R.year = 2014 AND R.wins > R.losses
ORDER BY pname DESC;

DROP VIEW playOver CASCADE;
DROP VIEW over200 CASCADE;
DROP VIEW matches CASCADE;
