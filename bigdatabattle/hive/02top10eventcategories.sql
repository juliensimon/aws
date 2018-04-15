use gdelt;
CREATE TABLE tmp AS 
SELECT eventcode, COUNT(globaleventid) AS nb_events FROM eventsorc
GROUP BY eventcode
ORDER BY nb_events DESC LIMIT 10;

SELECT eventcode, eventcodes.description, nb_events FROM tmp 
JOIN eventcodes ON eventcode=eventcodes.code ORDER BY nb_events DESC;

DROP TABLE tmp;
