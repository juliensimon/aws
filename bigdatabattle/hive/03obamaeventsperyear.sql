SELECT year,
       COUNT(globaleventid) AS nb_events
FROM gdelt.eventsorc
WHERE actor1name='BARACK OBAMA'
GROUP BY year
ORDER BY year ASC;
