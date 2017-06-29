SELECT year,
       COUNT(globaleventid) AS nb_events
FROM gdelt.eventsparquet
WHERE actor1name='BARACK OBAMA'
GROUP BY year
ORDER BY year ASC;
